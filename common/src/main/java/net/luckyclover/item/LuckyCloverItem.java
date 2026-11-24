package net.luckyclover.item;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class LuckyCloverItem extends Item {
    private static final Logger LOGGER = LoggerFactory.getLogger(LuckyCloverItem.class);

    // Config suppliers (customizable via TOML config by modpack creators)
    public static DoubleSupplier badLuckChanceSupplier = () -> 0.12;
    public static IntSupplier maxStackSupplier = () -> 8;
    public static Supplier<List<? extends String>> blacklistSupplier = () -> List.of("minecraft:bedrock", "minecraft:barrier", "minecraft:structure_void", "minecraft:command_block");
    public static BooleanSupplier enableLightningSupplier = () -> true;
    public static BooleanSupplier enableCreeperSupplier = () -> true;
    public static DoubleSupplier creeperChargedSupplier = () -> 0.15;
    public static BooleanSupplier enableZombieSupplier = () -> true;
    public static BooleanSupplier enableSkeletonSupplier = () -> true;
    public static BooleanSupplier enableTntSupplier = () -> true;
    public static IntSupplier tntFuseSupplier = () -> 50;

    public LuckyCloverItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    /**
     * Triggered on right-click anywhere (in the air or looking at the horizon).
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (world.isClient()) {
            return TypedActionResult.success(stack);
        }

        ServerWorld serverWorld = (ServerWorld) world;
        Random random = world.getRandom();

        Vec3d look = player.getRotationVector();
        Vec3d spawnVec = player.getEyePos().add(look.multiply(1.5));
        BlockPos spawnPos = BlockPos.ofFloored(spawnVec);

        triggerCloverEffect(serverWorld, spawnVec, spawnPos, player, look, random);

        if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
        }

        return TypedActionResult.consume(stack);
    }

    /**
     * Triggered on right-click when targeting a block.
     */
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos clickedPos = context.getBlockPos();
        BlockPos spawnPos = clickedPos.offset(context.getSide());
        Vec3d spawnVec = Vec3d.ofCenter(spawnPos);

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        ServerWorld serverWorld = (ServerWorld) world;
        Random random = world.getRandom();
        Vec3d look = player != null ? player.getRotationVector() : new Vec3d(0, 0, 0);

        triggerCloverEffect(serverWorld, spawnVec, spawnPos, player, look, random);

        if (player != null && !player.getAbilities().creativeMode) {
            context.getStack().decrement(1);
        }

        return ActionResult.CONSUME;
    }

    private void triggerCloverEffect(ServerWorld world, Vec3d spawnVec, BlockPos spawnPos, PlayerEntity player, Vec3d look, Random random) {
        double badLuckChance = badLuckChanceSupplier.getAsDouble();

        if (random.nextDouble() < badLuckChance) {
            triggerBadLuck(world, spawnPos, player, random);
        } else {
            spawnLuckyEffects(world, spawnVec);
            dropRandomRegisteredItem(world, spawnVec, look, random);
        }
    }

    private void spawnLuckyEffects(ServerWorld world, Vec3d pos) {
        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.8f, 1.2f);
        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, pos.x, pos.y, pos.z, 25, 0.4, 0.4, 0.4, 0.1);
        world.spawnParticles(ParticleTypes.ENCHANT, pos.x, pos.y + 0.3, pos.z, 30, 0.5, 0.5, 0.5, 0.2);
    }

    /**
     * Randomly rolls from ALL registered item IDs in Minecraft vanilla + all installed mods.
     * Features robust try/catch validation to guarantee broken mod items never crash the game.
     */
    private void dropRandomRegisteredItem(ServerWorld world, Vec3d spawnVec, Vec3d look, Random random) {
        Set<String> blacklist = new HashSet<>(blacklistSupplier.get());

        List<Item> allItems = new ArrayList<>(Registries.ITEM.stream()
                .filter(item -> {
                    if (item == null || item == Items.AIR) return false;
                    Identifier id = Registries.ITEM.getId(item);
                    return id == null || !blacklist.contains(id.toString());
                })
                .toList());

        if (allItems.isEmpty()) {
            dropItem(world, spawnVec, look, new ItemStack(Items.DIRT));
            return;
        }

        int maxStack = Math.max(1, maxStackSupplier.getAsInt());
        ItemStack resultStack = ItemStack.EMPTY;

        for (int attempts = 0; attempts < 5 && resultStack.isEmpty(); attempts++) {
            try {
                Item randomItem = allItems.get(random.nextInt(allItems.size()));
                int count = 1;

                if (randomItem.getMaxCount() > 1 && random.nextFloat() < 0.25f) {
                    count = 1 + random.nextInt(Math.min(maxStack, randomItem.getMaxCount()));
                }

                ItemStack stack = new ItemStack(randomItem, count);
                if (!stack.isEmpty()) {
                    resultStack = stack;
                }
            } catch (Throwable t) {
                LOGGER.warn("Safely skipped malformed item while rolling Lucky Clover: {}", t.getMessage());
            }
        }

        if (resultStack.isEmpty()) {
            resultStack = new ItemStack(Items.DIAMOND);
        }

        dropItem(world, spawnVec, look, resultStack);
    }

    /**
     * Executes one of the 5 classic bad luck hazard events.
     */
    private void triggerBadLuck(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {
        List<Integer> availableHazards = new ArrayList<>();
        if (enableLightningSupplier.getAsBoolean()) availableHazards.add(0);
        if (enableCreeperSupplier.getAsBoolean()) availableHazards.add(1);
        if (enableZombieHordeSupplier()) availableHazards.add(2);
        if (enableSkeletonSupplier.getAsBoolean()) availableHazards.add(3);
        if (enableTntSupplier.getAsBoolean()) availableHazards.add(4);

        if (availableHazards.isEmpty()) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0f, 1.0f);
            return;
        }

        int event = availableHazards.get(random.nextInt(availableHazards.size()));
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;

        switch (event) {
            case 0 -> {
                // 1. Lightning bolt near player with dramatic thunder
                LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
                if (bolt != null) {
                    BlockPos targetPos = player != null ? player.getBlockPos() : pos;
                    bolt.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(targetPos));
                    world.spawnEntity(bolt);
                }
            }
            case 1 -> {
                // 2. Surprise Creeper (with charged chance)
                CreeperEntity creeper = EntityType.CREEPER.create(world);
                if (creeper != null) {
                    creeper.refreshPositionAndAngles(x, y, z, random.nextFloat() * 360f, 0f);
                    double chargedChance = creeperChargedSupplier.getAsDouble();
                    if (random.nextDouble() < chargedChance) {
                        LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
                        if (bolt != null) {
                            creeper.onStruckByLightning(world, bolt);
                        }
                    }
                    world.spawnEntity(creeper);
                }
            }
            case 2 -> {
                // 3. Armed Zombie Horde
                int zombieCount = 1 + random.nextInt(2);
                for (int i = 0; i < zombieCount; i++) {
                    ZombieEntity zombie = EntityType.ZOMBIE.create(world);
                    if (zombie != null) {
                        double offsetX = (random.nextDouble() - 0.5) * 2.0;
                        double offsetZ = (random.nextDouble() - 0.5) * 2.0;
                        zombie.refreshPositionAndAngles(x + offsetX, y, z + offsetZ, random.nextFloat() * 360f, 0f);
                        zombie.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                        zombie.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
                        world.spawnEntity(zombie);
                    }
                }
            }
            case 3 -> {
                // 4. Sniper Skeleton with enchanted bow and helmet
                SkeletonEntity skeleton = EntityType.SKELETON.create(world);
                if (skeleton != null) {
                    skeleton.refreshPositionAndAngles(x, y, z, random.nextFloat() * 360f, 0f);
                    ItemStack bow = new ItemStack(Items.BOW);
                    bow.addEnchantment(Enchantments.POWER, 2);
                    bow.addEnchantment(Enchantments.PUNCH, 1);
                    skeleton.equipStack(EquipmentSlot.MAINHAND, bow);
                    skeleton.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
                    world.spawnEntity(skeleton);
                }
            }
            case 4 -> {
                // 5. Primed TNT with short fuse ticking on the ground
                TntEntity tnt = EntityType.TNT.create(world);
                if (tnt != null) {
                    tnt.refreshPositionAndAngles(x, y, z, 0f, 0f);
                    int fuse = tntFuseSupplier.getAsInt();
                    tnt.setFuse(fuse);
                    world.playSound(null, pos, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    world.spawnEntity(tnt);
                }
            }
        }
    }

    private static boolean enableZombieHordeSupplier() {
        return enableZombieSupplier.getAsBoolean();
    }

    private void dropItem(ServerWorld world, Vec3d spawnVec, Vec3d look, ItemStack stack) {
        ItemEntity entity = new ItemEntity(world, spawnVec.x, spawnVec.y, spawnVec.z, stack);
        entity.setToDefaultPickupDelay();
        entity.setVelocity(look.x * 0.15, 0.25, look.z * 0.15);
        world.spawnEntity(entity);
    }
}

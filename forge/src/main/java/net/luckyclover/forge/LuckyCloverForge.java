package net.luckyclover.forge;

import net.luckyclover.item.LuckyCloverItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(LuckyCloverForge.MOD_ID)
public final class LuckyCloverForge {
    public static final String MOD_ID = "lucky_clover_reborn";

    // Native Forge / NeoForge registries (Zero external dependencies)
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<ItemGroup> CREATIVE_TABS = DeferredRegister.create(RegistryKeys.ITEM_GROUP, MOD_ID);

    // The Lucky Clover
    public static final RegistryObject<Item> LUCKY_CLOVER = ITEMS.register("lucky_clover",
            () -> new LuckyCloverItem(new Item.Settings().rarity(Rarity.RARE)));

    // Dedicated Creative Mode Tab
    public static final RegistryObject<ItemGroup> CLOVER_TAB = CREATIVE_TABS.register("clovers",
            () -> ItemGroup.builder()
                    .displayName(Text.translatable("itemGroup." + MOD_ID + ".clovers"))
                    .icon(() -> new ItemStack(LUCKY_CLOVER.get()))
                    .entries((displayContext, entries) -> {
                        entries.add(new ItemStack(LUCKY_CLOVER.get()));
                    })
                    .build()
    );

    public LuckyCloverForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register native TOML configuration in config/lucky_clover_reborn-common.toml
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LuckyCloverConfig.SPEC);

        // Link configuration values directly to the item logic
        LuckyCloverItem.badLuckChanceSupplier = LuckyCloverConfig.BAD_LUCK_CHANCE::get;
        LuckyCloverItem.maxStackSupplier = LuckyCloverConfig.MAX_STACK_COUNT::get;
        LuckyCloverItem.blacklistSupplier = LuckyCloverConfig.ITEM_BLACKLIST::get;
        LuckyCloverItem.enableLightningSupplier = LuckyCloverConfig.ENABLE_LIGHTNING::get;
        LuckyCloverItem.enableCreeperSupplier = LuckyCloverConfig.ENABLE_CREEPER::get;
        LuckyCloverItem.creeperChargedSupplier = LuckyCloverConfig.CREEPER_CHARGED_CHANCE::get;
        LuckyCloverItem.enableZombieSupplier = LuckyCloverConfig.ENABLE_ZOMBIE_HORDE::get;
        LuckyCloverItem.enableSkeletonSupplier = LuckyCloverConfig.ENABLE_SNIPER_SKELETON::get;
        LuckyCloverItem.enableTntSupplier = LuckyCloverConfig.ENABLE_PRIMED_TNT::get;
        LuckyCloverItem.tntFuseSupplier = LuckyCloverConfig.TNT_FUSE_TICKS::get;

        // Register items and creative tabs
        ITEMS.register(modBus);
        CREATIVE_TABS.register(modBus);

        // Also add to vanilla Tools and Ingredients creative tabs
        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ItemGroups.TOOLS || event.getTabKey() == ItemGroups.INGREDIENTS) {
            event.add(new ItemStack(LUCKY_CLOVER.get()));
        }
    }
}

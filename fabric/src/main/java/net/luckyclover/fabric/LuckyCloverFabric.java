package net.luckyclover.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.luckyclover.item.LuckyCloverItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public final class LuckyCloverFabric implements ModInitializer {
    public static final String MOD_ID = "lucky_clover_reborn";

    public static final Item LUCKY_CLOVER = new LuckyCloverItem(
            new Item.Settings().maxCount(64).rarity(Rarity.RARE)
    );

    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Identifier.of(MOD_ID, "general")
    );

    @Override
    public void onInitialize() {
        // Register item
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "lucky_clover"), LUCKY_CLOVER);

        // Register custom creative tab
        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(LUCKY_CLOVER))
                .displayName(Text.translatable("itemGroup.lucky_clover_reborn.general"))
                .entries((context, entries) -> entries.add(LUCKY_CLOVER))
                .build()
        );

        // Register to vanilla creative tabs
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(LUCKY_CLOVER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.add(LUCKY_CLOVER));

        // Grass break drop event
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient() || player == null || player.isCreative()) {
                return;
            }

            if (state.isOf(Blocks.SHORT_GRASS) || state.isOf(Blocks.TALL_GRASS)
                    || state.isOf(Blocks.FERN) || state.isOf(Blocks.LARGE_FERN)) {
                // 1 in 200 chance (0.005)
                if (world.getRandom().nextFloat() < 0.005f) {
                    Block.dropStack(world, pos, new ItemStack(LUCKY_CLOVER));
                }
            }
        });
    }
}

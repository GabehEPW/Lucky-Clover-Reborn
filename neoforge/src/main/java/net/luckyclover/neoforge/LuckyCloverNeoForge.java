package net.luckyclover.neoforge;

import net.luckyclover.item.LuckyCloverItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(LuckyCloverNeoForge.MOD_ID)
public final class LuckyCloverNeoForge {
    public static final String MOD_ID = "lucky_clover_reborn";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(RegistryKeys.ITEM, MOD_ID);
    public static final DeferredRegister<ItemGroup> CREATIVE_TABS = DeferredRegister.create(RegistryKeys.ITEM_GROUP, MOD_ID);

    public static final DeferredHolder<Item, Item> LUCKY_CLOVER = ITEMS.register("lucky_clover",
            () -> new LuckyCloverItem(new Item.Settings().rarity(Rarity.RARE)));

    public static final DeferredHolder<ItemGroup, ItemGroup> CLOVER_TAB = CREATIVE_TABS.register("clovers",
            () -> ItemGroup.builder()
                    .displayName(Text.translatable("itemGroup." + MOD_ID + ".clovers"))
                    .icon(() -> new ItemStack(LUCKY_CLOVER.get()))
                    .entries((displayContext, entries) -> entries.add(new ItemStack(LUCKY_CLOVER.get())))
                    .build()
    );

    public LuckyCloverNeoForge(IEventBus modBus, ModContainer modContainer) {
        // Register native TOML configuration in config/lucky_clover_reborn-common.toml
        modContainer.registerConfig(ModConfig.Type.COMMON, LuckyCloverConfig.SPEC);

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

        ITEMS.register(modBus);
        CREATIVE_TABS.register(modBus);

        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ItemGroups.TOOLS || event.getTabKey() == ItemGroups.INGREDIENTS) {
            event.add(new ItemStack(LUCKY_CLOVER.get()));
        }
    }
}

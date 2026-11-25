package net.luckyclover.neoforge;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class LuckyCloverConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue GRASS_DROP_CHANCE;
    public static final ModConfigSpec.DoubleValue BAD_LUCK_CHANCE;
    public static final ModConfigSpec.IntValue MAX_STACK_COUNT;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_BLACKLIST;

    // Hazards
    public static final ModConfigSpec.BooleanValue ENABLE_LIGHTNING;
    public static final ModConfigSpec.BooleanValue ENABLE_CREEPER;
    public static final ModConfigSpec.DoubleValue CREEPER_CHARGED_CHANCE;
    public static final ModConfigSpec.BooleanValue ENABLE_ZOMBIE_HORDE;
    public static final ModConfigSpec.BooleanValue ENABLE_SNIPER_SKELETON;
    public static final ModConfigSpec.BooleanValue ENABLE_PRIMED_TNT;
    public static final ModConfigSpec.IntValue TNT_FUSE_TICKS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("General settings for Lucky Clover Reborn").push("general");
        GRASS_DROP_CHANCE = builder
                .comment("Chance of dropping a Lucky Clover when breaking grass or ferns.",
                         "Default: 0.005 (0.5% or 1 in 200). Example: 0.02 for 2%, 0.05 for 5%.")
                .defineInRange("grassDropChance", 0.005, 0.0, 1.0);
        builder.pop();

        builder.comment("Item roll settings").push("clover");
        BAD_LUCK_CHANCE = builder
                .comment("Chance of triggering a bad luck hazard instead of dropping an item.",
                         "Default: 0.12 (12%). Set to 0.0 to disable all hazards and have 100% item drops!")
                .defineInRange("badLuckChance", 0.12, 0.0, 1.0);

        MAX_STACK_COUNT = builder
                .comment("Maximum stack size when a stackable item is rolled.",
                         "Default: 8 (min: 1, max: 64).")
                .defineInRange("maxStackCount", 8, 1, 64);

        ITEM_BLACKLIST = builder
                .comment("List of item registry IDs that should NEVER be dropped by the Lucky Clover.",
                         "Modpack creators can add any broken or overpowered items here (e.g. 'minecraft:bedrock').")
                .defineListAllowEmpty(
                        "itemBlacklist",
                        List.of("minecraft:bedrock", "minecraft:barrier", "minecraft:structure_void", "minecraft:command_block"),
                        () -> "",
                        obj -> obj instanceof String
                );
        builder.pop();

        builder.comment("Individual hazard event toggles").push("hazards");
        ENABLE_LIGHTNING = builder.comment("Enable Lightning Bolt hazard event.").define("enableLightning", true);
        ENABLE_CREEPER = builder.comment("Enable Surprise Creeper hazard event.").define("enableCreeper", true);
        CREEPER_CHARGED_CHANCE = builder.comment("Chance for the creeper to be electric/charged. Default: 0.15 (15%).").defineInRange("creeperChargedChance", 0.15, 0.0, 1.0);
        ENABLE_ZOMBIE_HORDE = builder.comment("Enable Armed Zombie Horde hazard event.").define("enableZombieHorde", true);
        ENABLE_SNIPER_SKELETON = builder.comment("Enable Sniper Skeleton hazard event.").define("enableSniperSkeleton", true);
        ENABLE_PRIMED_TNT = builder.comment("Enable Primed TNT hazard event.").define("enablePrimedTnt", true);
        TNT_FUSE_TICKS = builder.comment("Fuse duration in ticks for primed TNT (20 ticks = 1 second). Default: 50 (2.5s).").defineInRange("tntFuseTicks", 50, 10, 200);
        builder.pop();

        SPEC = builder.build();
    }
}

package net.luckyclover.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Lightweight event hooks allowing addons (such as Cobblemon integrations)
 * to intercept or extend Lucky Clover activations without Mixins.
 */
public final class LuckyCloverEvents {
    private static final List<BeforeRollHandler> BEFORE_ROLL_HANDLERS = new CopyOnWriteArrayList<>();
    private static final List<BadLuckHandler> BAD_LUCK_HANDLERS = new CopyOnWriteArrayList<>();
    private static final List<AfterItemDropHandler> AFTER_ITEM_DROP_HANDLERS = new CopyOnWriteArrayList<>();

    private LuckyCloverEvents() {
    }

    /**
     * Registers a handler invoked before the default item/hazard roll.
     * Return {@code true} to mark the activation as handled and skip the default roll.
     */
    public static void registerBeforeRoll(BeforeRollHandler handler) {
        if (handler != null) {
            BEFORE_ROLL_HANDLERS.add(handler);
        }
    }

    /**
     * Registers a handler invoked when a bad luck hazard is about to trigger.
     * Return {@code true} to replace the default vanilla hazard with a custom one.
     */
    public static void registerBadLuck(BadLuckHandler handler) {
        if (handler != null) {
            BAD_LUCK_HANDLERS.add(handler);
        }
    }

    /**
     * Registers a listener notified after a random item has been rolled and dropped.
     */
    public static void registerAfterItemDrop(AfterItemDropHandler handler) {
        if (handler != null) {
            AFTER_ITEM_DROP_HANDLERS.add(handler);
        }
    }

    public static boolean fireBeforeRoll(CloverContext context) {
        for (BeforeRollHandler handler : BEFORE_ROLL_HANDLERS) {
            if (handler.onBeforeRoll(context)) {
                return true;
            }
        }
        return false;
    }

    public static boolean fireBadLuck(CloverContext context) {
        for (BadLuckHandler handler : BAD_LUCK_HANDLERS) {
            if (handler.onBadLuck(context)) {
                return true;
            }
        }
        return false;
    }

    public static void fireAfterItemDrop(CloverContext context, ItemStack droppedStack) {
        for (AfterItemDropHandler handler : AFTER_ITEM_DROP_HANDLERS) {
            handler.onAfterItemDrop(context, droppedStack);
        }
    }

    @FunctionalInterface
    public interface BeforeRollHandler {
        boolean onBeforeRoll(CloverContext context);
    }

    @FunctionalInterface
    public interface BadLuckHandler {
        boolean onBadLuck(CloverContext context);
    }

    @FunctionalInterface
    public interface AfterItemDropHandler {
        void onAfterItemDrop(CloverContext context, ItemStack droppedStack);
    }

    public record CloverContext(
            ServerWorld world,
            Vec3d spawnVec,
            BlockPos spawnPos,
            @Nullable PlayerEntity player,
            Vec3d look,
            Random random
    ) {
    }
}

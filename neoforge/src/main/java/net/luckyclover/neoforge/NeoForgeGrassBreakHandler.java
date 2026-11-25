package net.luckyclover.neoforge;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = LuckyCloverNeoForge.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class NeoForgeGrassBreakHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeoForgeGrassBreakHandler.class);

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || event.getPlayer() == null || event.getPlayer().isCreative()) {
            return;
        }

        BlockState state = event.getState();
        if (isGrassBlock(state)) {
            if (event.getLevel() instanceof ServerWorld serverWorld) {
                Random random = serverWorld.getRandom();
                double dropChance = LuckyCloverConfig.GRASS_DROP_CHANCE.get();

                // Configurable drop chance (Default: 0.005 = 0.5% or 1 in 200)
                if (random.nextDouble() < dropChance) {
                    BlockPos pos = event.getPos();
                    Block.dropStack(serverWorld, pos, new ItemStack(LuckyCloverNeoForge.LUCKY_CLOVER.get()));
                    serverWorld.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    LOGGER.info("Lucky Clover dropped for player {} at {}", event.getPlayer().getName().getString(), pos);
                }
            }
        }
    }

    private static boolean isGrassBlock(BlockState state) {
        return state.isOf(Blocks.SHORT_GRASS)
                || state.isOf(Blocks.TALL_GRASS)
                || state.isOf(Blocks.FERN)
                || state.isOf(Blocks.LARGE_FERN);
    }
}

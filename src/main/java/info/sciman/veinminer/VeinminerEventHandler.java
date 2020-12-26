package info.sciman.veinminer;

import info.sciman.veinminer.setup.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class VeinminerEventHandler {
    @SubscribeEvent
    public void onBlockBroken(BlockEvent.BreakEvent event) {
        // Check if player exists and is sneaking
        PlayerEntity player = event.getPlayer();
        if (player != null) {
            if (player.isSneaking()) {

                // Get item in hand and make sure it exists
                ItemStack stack = player.getHeldItemMainhand();
                if (stack.getItem() != null) {
                    // Are we holding a tool|
                    Set<ToolType> types = stack.getToolTypes();
                    if (!types.isEmpty()) {

                        // What block are we trying to mine?
                        BlockState state = event.getState();

                        // Are both of these in the block/toollist?
                        if (VeinminerMod.blockList.contains(state.getBlock()) && VeinminerMod.toolList.contains(stack.getItem())) {

                            // Did we find a valid tool/blockstate combo?
                            if (stack.canHarvestBlock(state)) {

                                // Get the number of blocks we can mine, maximum
                                int maxBlocksToMine = stack.getMaxDamage() - stack.getDamage();
                                if (maxBlocksToMine > Config.MAX_VEIN_SIZE.get()) {
                                    maxBlocksToMine = Config.MAX_VEIN_SIZE.get();
                                }

                                BlockPos pos = event.getPos();
                                World world = (World) event.getWorld();
                                Block block = state.getBlock();

                                // Get enchantment levels
                                int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE,stack);
                                int silktouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH,stack);

                                // Generate sets to explore
                                HashSet<BlockPos> blocksToDestroy = new HashSet<>();
                                HashSet<BlockPos> blocksToConsider = new HashSet<>();
                                HashSet<BlockPos> newBlocksToConsider = new HashSet<>();
                                blocksToConsider.add(pos);

                                // Determine every block we should break
                                while (!blocksToConsider.isEmpty() && blocksToDestroy.size() < maxBlocksToMine) {

                                    // Iterate over considered block
                                    for (Iterator<BlockPos> iter = blocksToConsider.iterator(); iter.hasNext(); ) {

                                        BlockPos b = iter.next();
                                        blocksToDestroy.add(b);

                                        for (int x = -1; x < 2; x++) {
                                            for (int y = -1; y < 2; y++) {
                                                for (int z = -1; z < 2; z++) {
                                                    BlockPos p = b.add(x, y, z);
                                                    if (world.getBlockState(p).getBlock() == block && !blocksToDestroy.contains(p)) {
                                                        newBlocksToConsider.add(p);
                                                    }
                                                }
                                            }
                                        }

                                        if (blocksToDestroy.size() >= maxBlocksToMine) {
                                            break;
                                        }
                                    }

                                    // Swap
                                    blocksToConsider.clear();
                                    blocksToConsider.addAll(newBlocksToConsider);
                                    newBlocksToConsider.clear();
                                }

                                // Actually break them
                                int xpAmount = 0;

                                for (BlockPos b : blocksToDestroy) {
                                    if (!player.isCreative()) {
                                        xpAmount += block.getExpDrop(world.getBlockState(b),world,b,fortune,silktouch);
                                    }
                                    // Destroy block
                                    world.destroyBlock(b, !player.isCreative(), player);
                                }
                                if (!player.abilities.isCreativeMode) {
                                    // Damage tool
                                    if (!world.isRemote) {
                                        stack.damageItem(blocksToDestroy.size() - 1, player, (e) -> e.sendBreakAnimation(Hand.MAIN_HAND));
                                    }
                                    // Drop xp
                                    while (xpAmount > 0) {
                                        int amt = ExperienceOrbEntity.getXPSplit(xpAmount);
                                        xpAmount -= amt;
                                        world.addEntity(new ExperienceOrbEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, amt));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

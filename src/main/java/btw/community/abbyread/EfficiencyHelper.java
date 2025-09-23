package btw.community.abbyread;

import btw.block.blocks.*;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import net.minecraft.src.*;


public class EfficiencyHelper {
    // Store last computed efficiency (1.0 if ineffective)
    private static boolean lastEffective = false;

    public static void setLastEffective(boolean effective) {
        lastEffective = effective;
    }
    public static boolean getLastEffective() {
        return lastEffective;
    }

    public static boolean isToolItemEfficientVsBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        int meta = world != null ? world.getBlockMetadata(x, y, z) : 0;
        return isToolItemEfficientVsBlock(stack, block, meta);
    }

    public static boolean isToolItemEfficientVsBlock(ItemStack stack, Block block, int metadata) {
        boolean effective = false;

        // --- Pointy Stick (wood chisel) ---
        if (stack.getItem() instanceof ChiselItemWood) {
            // Add efficiency toward non-loose, and non-full grass soil types
            final int DIRTSLAB_DIRT = 0;
            final int PACKED_EARTH = 6;
            if (block instanceof BlockDirt)  {
                return true;
            }
            if (block instanceof BlockStone)  {
                return true;
            }
            if (block instanceof DirtSlabBlock && metadata == DIRTSLAB_DIRT)  {
                return true;
            }
            if (block instanceof BlockGrass && (((BlockGrass) block).isSparse(metadata))) {
                return true;
            }
            if (block instanceof GrassSlabBlock && ((GrassSlabBlock) block).isSparse(metadata))  {
                return true;
            }
            if (block instanceof AestheticOpaqueEarthBlock && metadata == PACKED_EARTH)  {
                return true;
            }

            // Prevent efficiency toward full-grass blocks and glass-likes, wood, and rough stone
            final int DIRTSLAB_GRASS = 1;
            if (block instanceof DirtSlabBlock && metadata == DIRTSLAB_GRASS)  {
                return false;
            }
            if (block instanceof BlockGrass && !(((BlockGrass) block).isSparse(metadata)))  {
                return false;
            }
            if (block instanceof GrassSlabBlock && !((GrassSlabBlock) block).isSparse(metadata))  {
                return false;
            }
            if (block instanceof BlockLog)  {
                return false;
            }
            if (block instanceof ChewedLogBlock)  {
                return false;
            }
            if (block instanceof LogSpikeBlock)  {
                return false;
            }
            if (block instanceof RoughStoneBlock /* && ((RoughStoneBlock) block).strataLevel != 0 */)  {
                return false;
            }
            if (block instanceof BlockGlass)  {
                return false;
            }
            if (block instanceof BlockGlowStone)  {
                return false;
            }
            if (block instanceof BlockIce)  {
                return false;
            }
            if (block instanceof BlockPane)  {
                return false;
            }
            if (block instanceof BlockRedstoneLight)  {
                return false;
            }
            if (block instanceof LightBlock)  {
                return false;
            }
        }

        // --- Sharp Stone (stone chisel) ---
        if (stack.getItem() instanceof ChiselItemStone) {
            // Add efficiency toward full-grass blocks and glass-likes
            final int DIRTSLAB_GRASS = 1;
            if (block instanceof DirtSlabBlock && metadata == DIRTSLAB_GRASS)  {
                return true;
            }
            if (block instanceof BlockGrass && !((BlockGrass) block).isSparse(metadata)) {
                return true;
            }
            if (block instanceof GrassSlabBlock && !((GrassSlabBlock) block).isSparse(metadata))  {
                return true;
            }
            if (block instanceof BlockLog)  {
                return true;
            }
            if (block instanceof ChewedLogBlock)  {
                return true;
            }
            if (block instanceof LogSpikeBlock)  {
                return true;
            }
            if (block instanceof RoughStoneBlock &&
                    ((RoughStoneBlock) block).strataLevel == 0)  {
                return true;
            }
            if (block instanceof BlockGlass)  {
                return true;
            }
            if (block instanceof BlockGlowStone)  {
                return true;
            }
            if (block instanceof BlockIce)  {
                return true;
            }
            if (block instanceof BlockPane)  {
                return true;
            }
            if (block instanceof BlockRedstoneLight)  {
                return true;
            }
            if (block instanceof LightBlock)  {
                return true;
            }

            // Prevent efficiency toward non-loose, and non-full grass soil types
            final int DIRTSLAB_DIRT = 0;
            final int PACKED_EARTH = 6;
            if (block instanceof DirtSlabBlock && metadata == DIRTSLAB_DIRT)  {
                return false;
            }
            if (block instanceof BlockGrass && ((BlockGrass) block).isSparse(metadata))  {
                return false;
            }
            if (block instanceof GrassSlabBlock && ((GrassSlabBlock) block).isSparse(metadata))  {
                return false;
            }
            if (block instanceof BlockDirt)  {
                return false;
            }
            if (block instanceof AestheticOpaqueEarthBlock && metadata == PACKED_EARTH)  {
                return false;
            }
        }
        return effective;
    }

}

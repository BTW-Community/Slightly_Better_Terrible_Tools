package btw.community.abbyread;

import btw.block.blocks.*;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import net.minecraft.src.*;

public class EfficiencyHelper {
    public static boolean isToolItemEfficientVsBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        if (stack.getItem() instanceof ChiselItemWood) {
            // Add efficiency toward non-loose, and non-full grass soil types
            final int GRASS_SPARSE = 1;
            final int DIRTSLAB_DIRT = 0;
            final int PACKED_EARTH = 6;
            if (block instanceof BlockDirt) return true;
            if (block instanceof DirtSlabBlock && world.getBlockMetadata(x, y, z) == DIRTSLAB_DIRT) return true;
            if (block instanceof BlockGrass && world.getBlockMetadata(x, y, z) == GRASS_SPARSE) return true;
            if (block instanceof GrassSlabBlock && ((GrassSlabBlock) block).isSparse(world, x, y, z)) return true;
            if (block instanceof AestheticOpaqueEarthBlock && world.getBlockMetadata(x, y, z) == PACKED_EARTH)
                return true;

            // Prevent efficiency toward full-grass blocks and glass-likes, wood, and rough stone
            final int GRASS_FULL = 0;
            final int DIRTSLAB_GRASS = 1;
            if (block instanceof DirtSlabBlock && world.getBlockMetadata(x, y, z) == DIRTSLAB_GRASS) return false;
            if (block instanceof BlockGrass && world.getBlockMetadata(x, y, z) == GRASS_FULL) return false;
            if (block instanceof RoughStoneBlock /* && ((RoughStoneBlock) block).strataLevel != 0 */) return false;
            if (block instanceof BlockGlass) return false;
            if (block instanceof BlockGlowStone) return false;
            if (block instanceof BlockIce) return false;
            if (block instanceof BlockPane) return false;
            if (block instanceof BlockRedstoneLight) return false;
            if (block instanceof LightBlock) return false;
            if (block instanceof ChewedLogBlock) return false;
            if (block instanceof BlockWood) return false;
        }
        if (stack.getItem() instanceof ChiselItemStone) {
            // Add efficiency toward full-grass blocks and glass-likes
            final int GRASS_FULL = 0;
            final int DIRTSLAB_GRASS = 1;
            if (block instanceof DirtSlabBlock && world.getBlockMetadata(x, y, z) == DIRTSLAB_GRASS) return true;
            if (block instanceof BlockGrass && world.getBlockMetadata(x, y, z) == GRASS_FULL) return true;
            if (block instanceof RoughStoneBlock && ((RoughStoneBlock) block).strataLevel == 0) return true;
            if (block instanceof BlockGlass) return true;
            if (block instanceof BlockGlowStone) return true;
            if (block instanceof BlockIce) return true;
            if (block instanceof BlockPane) return true;
            if (block instanceof BlockRedstoneLight) return true;
            if (block instanceof LightBlock) return true;

            // Prevent efficiency toward non-loose, and non-full grass soil types
            final int GRASS_SPARSE = 1;
            final int DIRTSLAB_DIRT = 0;
            final int PACKED_EARTH = 6;
            if (block instanceof DirtSlabBlock && world.getBlockMetadata(x, y, z) == DIRTSLAB_DIRT) return true;
            if (block instanceof BlockGrass && world.getBlockMetadata(x, y, z) == GRASS_SPARSE) return true;
            if (block instanceof GrassSlabBlock && ((GrassSlabBlock) block).isSparse(world, x, y, z)) return true;
            if (block instanceof BlockDirt) return true;
            if (block instanceof AestheticOpaqueEarthBlock && world.getBlockMetadata(x, y, z) == PACKED_EARTH)
                return true;
        }
        return false;
    }
}

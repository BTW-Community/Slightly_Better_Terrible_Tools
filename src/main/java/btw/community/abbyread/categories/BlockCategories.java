package btw.community.abbyread.categories;

import btw.block.blocks.AestheticOpaqueEarthBlock;
import btw.block.blocks.DirtSlabBlock;
import btw.community.abbyread.categories.BlockCategory;
import net.minecraft.src.Block;
import net.minecraft.src.BlockGrass;
import btw.block.BTWBlocks;
import net.minecraft.src.World;

import java.util.HashSet;
import java.util.Set;

// The place to handle only static, intrinsic block properties.
public class BlockCategories {

    public static Set<BlockCategory> of(Block block, int meta) {
        Set<BlockCategory> categories = new HashSet<>();

        if (block == Block.dirt) {
            categories.add(BlockCategory.DIRTLIKE);
            categories.add(BlockCategory.DIRTLIKE_FIRM);
        }

        if (block == BTWBlocks.looseDirt) {
            categories.add(BlockCategory.DIRTLIKE);
            categories.add(BlockCategory.DIRTLIKE_LOOSE);
            categories.add(BlockCategory.LOOSE_BLOCK);
        }

        // Fixed: call isSparse on the instance, not the class
        if (block instanceof BlockGrass && ((BlockGrass) block).isSparse(meta)) {
            categories.add(BlockCategory.GRASSLIKE);
            categories.add(BlockCategory.GRASSLIKE_SPARSE);
        }

        // Optional: add full grass case
        if (block instanceof BlockGrass && !((BlockGrass) block).isSparse(meta)) {
            categories.add(BlockCategory.GRASSLIKE);
            categories.add(BlockCategory.GRASSLIKE_FULL);
        }

        return categories;
    }

    private static boolean isPackedEarth(Block block, int meta) {
        return block instanceof AestheticOpaqueEarthBlock && meta == 6; // PACKED_EARTH
    }

    private static boolean isDirtSlabDirt(Block block, int meta) {
        return block instanceof DirtSlabBlock && meta == 0; // DIRTSLAB_DIRT
    }

    private static boolean isDirtSlabGrass(Block block, int meta) {
        return block instanceof DirtSlabBlock && meta == 1; // DIRTSLAB_GRASS
    }

    private static boolean isSparseGrass(Block block, int meta) {
        return block instanceof BlockGrass && ((BlockGrass) block).isSparse(meta);
    }

    private static boolean isFullGrass(Block block, int meta) {
        return block instanceof BlockGrass && !((BlockGrass) block).isSparse(meta);
    }

}

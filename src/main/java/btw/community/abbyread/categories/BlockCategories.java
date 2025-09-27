package btw.community.abbyread.categories;

import btw.block.blocks.AestheticOpaqueEarthBlock;
import btw.block.blocks.DirtSlabBlock;
import net.minecraft.src.Block;
import net.minecraft.src.BlockGrass;
import btw.block.BTWBlocks;

import java.util.HashSet;
import java.util.Set;

// The place to handle only static, intrinsic block properties.
public class BlockCategories {

    public static Set<BlockCategory> of(Block block, int meta) {
        Set<BlockCategory> categories = new HashSet<>();

        if (block == Block.dirt) {
            categories.add(BlockCategory.CUBE);
            categories.add(BlockCategory.DIRTLIKE);
            categories.add(BlockCategory.DIRT);
        }

        if (block == BTWBlocks.looseDirt) {
            categories.add(BlockCategory.CUBE);
            categories.add(BlockCategory.DIRTLIKE);
            categories.add(BlockCategory.LOOSE);
        }

        // Fixed: call isSparse on the instance, not the class
        if (block instanceof BlockGrass && ((BlockGrass) block).isSparse(meta)) {
            categories.add(BlockCategory.CUBE);
            categories.add(BlockCategory.GRASS);
            categories.add(BlockCategory.SPARSE);
            categories.add(BlockCategory.DIRTLIKE);
        }

        // Optional: add full grass case
        if (block instanceof BlockGrass && !((BlockGrass) block).isSparse(meta)) {
            categories.add(BlockCategory.CUBE);
            categories.add(BlockCategory.GRASS);
            categories.add(BlockCategory.LUSH);
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

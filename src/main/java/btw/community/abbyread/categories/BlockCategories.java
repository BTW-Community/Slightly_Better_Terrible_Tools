package btw.community.abbyread.categories;

import btw.block.blocks.*;
import net.minecraft.src.Block;
import net.minecraft.src.BlockDirt;
import net.minecraft.src.BlockGrass;
import btw.block.BTWBlocks;

import java.util.HashSet;
import java.util.Set;

// The place to handle only static, intrinsic block properties.
public class BlockCategories {

    private static final Set<Block> LOOSE_BLOCKS = Set.of(
            BTWBlocks.looseDirt,
            BTWBlocks.looseDirtSlab,
            BTWBlocks.looseSparseGrass,
            BTWBlocks.looseSparseGrassSlab,
            Block.sand,
            Block.gravel

    );

    private static final Set<Block> DIRT_BLOCKS = Set.of(
            Block.dirt,
            BTWBlocks.dirtSlab,
            BTWBlocks.looseDirt,
            BTWBlocks.looseDirtSlab
    );

    private static final Set<Block> GRASS_BLOCKS = Set.of(
            Block.grass,
            BTWBlocks.grassSlab,
            BTWBlocks.looseSparseGrass,
            BTWBlocks.looseSparseGrassSlab
    );

    private static final Set<Block> DIRTLIKE_BLOCKS;
    static {
        Set<Block> temp = new HashSet<>();
        temp.addAll(DIRT_BLOCKS);   // include all dirt blocks
        temp.addAll(GRASS_BLOCKS);  // include all grass blocks
        // optionally add more dirtlike blocks if needed
        DIRTLIKE_BLOCKS = Set.copyOf(temp); // immutable
    }

    public static Set<BlockCategory> of(Block block, int metadata) {
        Set<BlockCategory> categories = new HashSet<>();

        if (DIRTLIKE_BLOCKS.contains(block)) {
            categories.add(BlockCategory.DIRTLIKE);
        }

        if (LOOSE_BLOCKS.contains(block)) {
            categories.add(BlockCategory.LOOSE);
        }

        if (block == Block.dirt) {
            categories.add(BlockCategory.DIRT);
            categories.add(BlockCategory.CUBE);
        }

        if (block == BTWBlocks.dirtSlab) {
            categories.add(BlockCategory.DIRT);
            categories.add(BlockCategory.SLAB);
        }

        if (block == BTWBlocks.looseDirt) {
            categories.add(BlockCategory.LOOSE);
            categories.add(BlockCategory.DIRT);
            categories.add(BlockCategory.CUBE);
        }

        if (block == Block.grass) {
            categories.add(BlockCategory.GRASS);
            categories.add(BlockCategory.CUBE);
        }

        if (block == BTWBlocks.grassSlab) {
            categories.add(BlockCategory.GRASS);
            categories.add(BlockCategory.SLAB);
        }

        if (block == BTWBlocks.looseSparseGrass) {
            categories.add(BlockCategory.LOOSE);
            categories.add(BlockCategory.SPARSE);
            categories.add(BlockCategory.GRASS);
            categories.add(BlockCategory.CUBE);
        }

        if (block == BTWBlocks.looseSparseGrassSlab) {
            categories.add(BlockCategory.LOOSE);
            categories.add(BlockCategory.SPARSE);
            categories.add(BlockCategory.GRASS);
            categories.add(BlockCategory.SLAB);
        }

        return categories;
    }

    private static boolean isPackedEarth(Block block, int metadata) {
        if (block instanceof AestheticOpaqueEarthBlock) return metadata == AestheticOpaqueEarthBlock.SUBTYPE_PACKED_EARTH;
        if (block instanceof DirtSlabBlock) return metadata == DirtSlabBlock.SUBTYPE_PACKED_EARTH;

        assert false : "isPackedEarth used on invalid block type";
        return false;
    }

    private static boolean isLoose(Block block, int metadata) {
        return LOOSE_BLOCKS.contains(block);
    }

    private static boolean isGrass(Block block, int metadata) {
        return block instanceof DirtSlabBlock && metadata == DirtSlabBlock.SUBTYPE_GRASS;
    }

    private static boolean isSparse(Block block, int metadata) {
        if (block instanceof BlockGrass) return ((BlockGrass) block).isSparse(metadata);
        if (block instanceof GrassSlabBlock) return ((GrassSlabBlock) block).isSparse(metadata);
        if (block instanceof LooseSparseGrassBlock) return true;
        return false;
    }
}

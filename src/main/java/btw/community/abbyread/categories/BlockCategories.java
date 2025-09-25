package com.example.sbtt.categories;

import btw.community.abbyread.categories.BlockCategory;
import net.minecraft.src.Block;
import btw.block.BTWBlocks;
import java.util.Set;

public class BlockCategories {
    public static BlockCategory of(Block block, int meta) {
        if (isDirt(block, meta)) return BlockCategory.DIRTLIKE;
        if (isGrass(block, meta)) return BlockCategory.GRASSLIKE;
        if (isSparseGrass(block, meta)) return BlockCategory.SPARSE_GRASSLIKE;
        return null;
    }

    private static boolean isDirt(Block block, int meta) {
        return block == Block.dirt || block == Block.tilledField || block == BTWBlocks.looseDirt;
    }

    private static boolean isGrass(Block block, int meta) {
        return block == Block.grass && !Block.grass.isSparse(meta);
    }

    private static boolean isSparseGrass(Block block, int meta) {
        return block == Block.grass && Block.grass.isSparse(meta);
    }
}

package btw.community.abbyread;

import btw.block.blocks.*;
import net.minecraft.src.Block;
import net.minecraft.src.BlockLog;
import net.minecraft.src.BlockSand;

public class BlockBreakingOverrides {
    static final float effMod = EfficiencyHelper.effMod;

    public static boolean isUniversallyEasyBlock(Block block) {
        if (block == null) return false;
        if (    // Regular minimum efficiency
                block instanceof BlockSand ||
                block instanceof ChewedLogBlock ||
                block instanceof BlockLog ||
                block instanceof LogSpikeBlock ||

                // Extra minimum efficiency
                block instanceof LooseDirtBlock ||
                block instanceof LooseSparseGrassBlock ||
                block instanceof LooseSparseGrassSlabBlock ||
                block instanceof LooseDirtSlabBlock) {
            return true;
        }
        return false;
    }

    // The minimum efficiency at which anything should break these blocks
    public static float baselineEfficiency(Block block) {
        if (block == null) return 1.0F;

        if (    // Regular minimum efficiency
                block instanceof BlockSand ||
                block instanceof ChewedLogBlock ||
                block instanceof BlockLog ||
                block instanceof LogSpikeBlock) {
            return effMod;
        }

        if (    // Extra minimum efficiency
                block instanceof LooseDirtBlock ||
                block instanceof LooseSparseGrassBlock ||
                block instanceof LooseSparseGrassSlabBlock ||
                block instanceof LooseDirtSlabBlock) {
            return effMod * 2;
        }

        // No boost by default
        return 1.0F;
    }
}

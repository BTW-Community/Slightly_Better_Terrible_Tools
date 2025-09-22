package btw.community.abbyread;

import btw.block.blocks.*;
import net.minecraft.src.Block;
import net.minecraft.src.BlockLog;
import net.minecraft.src.BlockSand;

public class BlockBreakingOverrides {
    static final float effMod = UniformEfficiencyModifier.VALUE;

    // The minimum efficiency at which anything should break these blocks
    public static float baselineEfficiency(Block block) {
        if (block == null) return 1.0F;

        // Small boosts
        if (block instanceof BlockSand ||
                block instanceof ChewedLogBlock ||
                block instanceof BlockLog ||
                block instanceof LogSpikeBlock) {
            return effMod;
        }

        // Bigger boost for dirt/grass/log type blocks
        if (block instanceof LooseDirtBlock ||
                block instanceof LooseSparseGrassBlock ||
                block instanceof LooseSparseGrassSlabBlock ||
                block instanceof LooseDirtSlabBlock) {
            return effMod * 2;
        }

        // No boost by default
        return 1.0F;
    }
}

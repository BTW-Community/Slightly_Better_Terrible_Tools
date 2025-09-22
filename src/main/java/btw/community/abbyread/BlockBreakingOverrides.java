package btw.community.abbyread;

import btw.block.blocks.*;
import net.minecraft.src.Block;
import net.minecraft.src.BlockLog;

public class BlockBreakingOverrides {
    static final float effMod = UniformEfficiencyModifier.VALUE;

    // The minimum efficiency at which anything should break these blocks
    public static float baselineEfficiency(Block block) {
        if (block == null) return 1.0F;

        // Small boost for sandy blocks
        if (block instanceof net.minecraft.src.BlockSand) {
            return effMod;
        }

        // Bigger boost for dirt/grass/log type blocks
        if (block instanceof LooseDirtBlock ||
                block instanceof LooseSparseGrassBlock ||
                block instanceof LooseSparseGrassSlabBlock ||
                block instanceof LooseDirtSlabBlock ||
                block instanceof ChewedLogBlock ||
                block instanceof BlockLog ||
                block instanceof LogSpikeBlock) {
            return effMod * 1.5F;
        }

        // No boost by default
        return 1.0F;
    }
}

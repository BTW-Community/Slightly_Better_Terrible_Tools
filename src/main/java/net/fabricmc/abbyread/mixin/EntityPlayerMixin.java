package net.fabricmc.abbyread.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import btw.community.abbyread.BlockBreakingOverrides;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {

    @ModifyVariable(
            method = "getCurrentPlayerStrVsBlock",
            at = @At("RETURN"),
            ordinal = 0
    )
    private float abbyread$applyBoostedStrength(float original, Block block, int meta) {
        if (block == null) return original;

        // Compute boosted strength
        float boosted = BlockBreakingOverrides.getBoostedStrength(block);

        // Only apply boost if vanilla strength is lower
        return Math.max(original, boosted);

    }
}

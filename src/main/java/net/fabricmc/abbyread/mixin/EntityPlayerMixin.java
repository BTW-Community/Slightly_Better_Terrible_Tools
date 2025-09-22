package net.fabricmc.abbyread.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import btw.community.abbyread.BlockBreakingOverrides;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {

    @Shadow public InventoryPlayer inventory;

    @ModifyVariable(
            method = "getCurrentPlayerStrVsBlock",
            at = @At("RETURN"),
            ordinal = 0
    )
    private float abbyread$getMinimumStrVsBlock(float current, Block block, int meta) {
        if (block == null) return current;

        float minimum = BlockBreakingOverrides.baselineEfficiency(block);
        return Math.max(current, minimum);

    }
}

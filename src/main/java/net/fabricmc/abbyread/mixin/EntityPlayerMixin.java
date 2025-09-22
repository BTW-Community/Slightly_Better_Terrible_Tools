package net.fabricmc.abbyread.mixin;

import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
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
    private float abbyread$getMinimumStrVsBlock(float original, Block block, int meta) {
        if (block == null) return original;

        ItemStack stack = this.inventory.getCurrentItem();
        // Should I check with EfficiencyHelper prior to doing this?
        if (    stack != null &&
                (stack.getItem() instanceof ChiselItemWood ||
                stack.getItem() instanceof ChiselItemStone)) {

            float minimum = BlockBreakingOverrides.baselineEfficiency(block);
            return Math.max(original, minimum);
        }
        // Only apply boost if vanilla strength is lower
        return original;

    }
}

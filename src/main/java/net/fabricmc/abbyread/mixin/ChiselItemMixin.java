package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.block.blocks.FallingBlock;
import btw.block.blocks.LooseDirtBlock;
import btw.community.abbyread.LooseningHelper;
import btw.item.items.ChiselItem;
import btw.item.items.ChiselItemWood;
import btw.item.items.ToolItem;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.tools.Tool;

@Mixin(ChiselItem.class)
public class ChiselItemMixin {

    @Inject(method = "isEfficientVsBlock", at = @At("RETURN"), remap = false, cancellable = true)
    public void abby$conditionalBoost(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof ChiselItemWood &&
            LooseningHelper.canLoosenBlock(block.blockID))
        {
            cir.setReturnValue(true);
        }
    }
}
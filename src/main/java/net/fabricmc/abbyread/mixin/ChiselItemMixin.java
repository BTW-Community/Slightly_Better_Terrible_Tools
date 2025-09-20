package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.block.blocks.FallingBlock;
import btw.block.blocks.LooseDirtBlock;
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

    //@Inject(method = "isEfficientVsBlock", at = @At("RETURN"), remap = false)
    public void abby$displayRelativeStats(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        String itemName = stack.getDisplayName();
        String blockName = block.getLocalizedName();
        int abby$iToolLevel = ((ToolItem) stack.getItem()).toolMaterial.getHarvestLevel();

        int abby$iBlockToolLevel = block.getEfficientToolLevel(world, i, j, k);
        System.out.println(itemName + "'s iToolLevel: " + abby$iToolLevel +
                " vs. " + blockName + "'s iBlockToolLevel: " + abby$iBlockToolLevel);
    }

    @Inject(method = "isEfficientVsBlock", at = @At("RETURN"), remap = false, cancellable = true)
    public void abby$conditionalBoost(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof ChiselItemWood) {
            if (block instanceof BlockDirt) cir.setReturnValue(true);
            if (block instanceof BlockGrass) cir.setReturnValue(true);
            if (block.blockID == BTWBlocks.dirtSlab.blockID) cir.setReturnValue(true);
            if (block.blockID == BTWBlocks.grassSlab.blockID) cir.setReturnValue(true);


            if (block.blockID == BTWBlocks.looseDirt.blockID) cir.setReturnValue(true);
            if (block.blockID == BTWBlocks.looseDirtSlab.blockID) cir.setReturnValue(true);
            if (block.blockID == BTWBlocks.looseSparseGrass.blockID) cir.setReturnValue(true);
            if (block.blockID == BTWBlocks.looseSparseGrassSlab.blockID) cir.setReturnValue(true);
        }

    }
}
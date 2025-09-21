package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.EfficiencyHelper;
import btw.community.abbyread.UniformEfficiencyModifier;
import btw.item.BTWItems;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import btw.item.items.ToolItem;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public abstract class ToolItemMixin {
    @Unique
    float effMod = UniformEfficiencyModifier.VALUE;

    @Unique
    protected float effOnProp = 4F;

    // Because neither ChiselItem nor ChiselItemWood override this method
    @Inject(method = "getStrVsBlock", at = @At("HEAD"), cancellable = true, require = 1)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        // Mimic ToolItem's getStrVsBlock method but with special treatment
        if (    stack != null && (
                stack.getItem().itemID == BTWItems.pointyStick.itemID ||
                stack.getItem().itemID == BTWItems.sharpStone.itemID)) {
            if (world != null && EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z))
            {
                cir.setReturnValue(effOnProp);
            }
            cir.setReturnValue(1F);
        }
    }

    // DURABILITY
    @Inject(method = "onBlockDestroyed", at = @At("RETURN"), cancellable = true)
    private void abbyread$preventWastedUses(ItemStack stack, World world, int blockID, int x, int y, int z,
                                            EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        boolean effective;
        Block block = Block.blocksList[blockID];
        if (world != null &&
                stack.getItem() instanceof ChiselItemWood ||
                stack.getItem() instanceof ChiselItemStone) {
            effective = EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z);
        } else {
            effective = stack.getItem().isEfficientVsBlock(stack, world, block, x, y, z);
        }
        if (effective) stack.damageItem(1, entity);
        cir.setReturnValue(true); // always return true to cancel vanilla handling
    }
}
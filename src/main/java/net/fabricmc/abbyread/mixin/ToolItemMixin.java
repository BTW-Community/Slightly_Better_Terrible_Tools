package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.BlockBreakingOverrides;
import btw.community.abbyread.EfficiencyHelper;
import btw.item.items.ChiselItemWood;
import btw.item.items.ToolItem;
import net.minecraft.src.Block;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public abstract class ToolItemMixin {
    // ThreadLocal cache of effectiveness between getStrVsBlock and onBlockDestroyed

    @Inject(method = "getStrVsBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block,
                                        int x, int y, int z,
                                        CallbackInfoReturnable<Float> cir) {
        if (stack == null || block == null) return;

        if (world != null) {
            boolean effective = EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z);
            if (effective) {
                EfficiencyHelper.setLastEffective(true);
                ToolItemAccessor accessor = (ToolItemAccessor) this;
                cir.setReturnValue(accessor.getEfficiencyOnProperMaterial());
            } else {
                // Not effective -> maybe boosted fallback
                EfficiencyHelper.setLastEffective(false);
                float baselineStrength = BlockBreakingOverrides.baselineEfficiency(block);
                cir.setReturnValue(Math.max(baselineStrength, 1.0F));
            }
        }

    }

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void abbyread$preventWastedUses(ItemStack stack, World world, int blockID,
                                            int x, int y, int z,
                                            EntityLivingBase entity,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        boolean effective = false;
        if (!world.isRemote) {
            System.out.println("Handling check for item damage.");
            System.out.println("EfficiencyHelper.getLastEffective(): " + EfficiencyHelper.getLastEffective());
            if (EfficiencyHelper.getLastEffective()) {
                System.out.println("Is effective.  Damaging.");
                stack.damageItem(1, entity);
            }
        }

        EfficiencyHelper.setLastEffective(false); // cleanup for next call

        cir.setReturnValue(true);
    }
}

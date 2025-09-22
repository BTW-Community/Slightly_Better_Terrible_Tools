package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.BlockBreakingOverrides;
import btw.community.abbyread.EfficiencyHelper;

import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import btw.item.items.ToolItem;
import net.minecraft.src.Block;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public abstract class ToolItemMixin {
    /**
     * Adjust getStrVsBlock to enforce boosted efficiency on blocks defined
     * in BlockBreakingOverrides, ignoring ChiselItemWood's /=4 divisor.
     */
    @Inject(
            method = "getStrVsBlock",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void abbyread$boostInefficientBlock(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if (block == null) return;

        if (stack.getItem() instanceof ChiselItemWood){
            float originalStrength = cir.getReturnValueF();
            float boostedStrength = BlockBreakingOverrides.baselineEfficiency(block);

            // Only override if the block is meant to be boosted and original is less than boosted
            if (boostedStrength > 1.0F && originalStrength < boostedStrength) {
                cir.setReturnValue(boostedStrength);
            }
        }
    }

    // --- Efficiency override for pointy stick ---
    @Inject(method = "getStrVsBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block, int x, int y, int z,
                                        CallbackInfoReturnable<Float> cir) {
        if (stack == null) return;

        if (stack.getItem() instanceof ChiselItemWood) {
            if (world != null && EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z)) {
                float efficiency = ((ToolItemAccessor) this).getEfficiencyOnProperMaterial();
                cir.setReturnValue(efficiency);
            } else {
                cir.setReturnValue(1.0F);
            }
        }
    }

    // --- Durability only when effective ---
    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true, remap = false)
    private void abbyread$preventWastedUses(ItemStack stack, World world, int blockID, int x, int y, int z,
                                            EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || world == null) return;

        Block block = Block.blocksList[blockID];
        boolean effective;

        if (stack.getItem() instanceof ChiselItemWood || stack.getItem() instanceof ChiselItemStone) {
            effective = EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z);
        } else {
            effective = stack.getItem().isEfficientVsBlock(stack, world, block, x, y, z);
        }

        if (!world.isRemote && effective) {
            stack.damageItem(1, entity);
        }

        // Always cancel vanilla handling — we’ve replaced it fully.
        cir.setReturnValue(true);
    }
}

package net.fabricmc.abbyread.mixin;

import btw.crafting.util.FurnaceBurnTime;
import btw.item.items.ToolItem;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(btw.item.items.ToolItem.class)
public abstract class ToolItemMixin {

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void onlyDamageWhenEffective(ItemStack stack, World world, int blockId, int x, int y, int z, EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        Block block = Block.blocksList[blockId];
        ToolItem self = (ToolItem) (Object) this;

        // Check effectiveness
        boolean effective = self.isEfficientVsBlock(stack, world, block, x, y, z);
        float speed = self.getStrVsBlock(stack, world, block, x, y, z);
        // Debug prints

        // Only apply durability if effective OR has speed > bare hands
        if (effective || speed > 1.0F) {
            stack.damageItem(1, entity);

        }
        // Always return true (tool worked), cancel vanilla handling
        cir.setReturnValue(true);
    }

    @Inject(
            method = "getStrVsBlock",
            at = @At("RETURN"),
            cancellable = true
    )
    private void abbyread$modifyWoodBurnTime(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {

    }

    /**
     * Add extra behavior *after* ToolItem’s constructor finishes.
     * Only applies to wood tools.
     */
    @Shadow public EnumToolMaterial toolMaterial;

    @Shadow public abstract boolean hitEntity(ItemStack stack, EntityLivingBase defendingEntity, EntityLivingBase attackingEntity);

}
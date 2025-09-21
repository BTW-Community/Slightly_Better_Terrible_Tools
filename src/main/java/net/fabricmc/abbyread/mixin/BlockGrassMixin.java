package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.item.items.ChiselItemWood;
import btw.item.util.ItemUtils;
import net.minecraft.src.BlockGrass;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockGrass.class)
public abstract class BlockGrassMixin {
    @Inject(
        method = "onNeighborDirtDugWithImproperTool",
        at = @At("HEAD"),
        cancellable = true
    )
    private void abby$reimplementGrassDrop(World world, int x, int y, int z, int toFacing, CallbackInfo ci) {
        if (toFacing == 0) {
            world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
        }
        ci.cancel();
    }

    @Inject(
        method = "canConvertBlock", at = @At("RETURN"), cancellable = true
    )
    private void abby$addCanConvertByPointyStick(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        if (stack != null && stack.getItem() instanceof ChiselItemWood) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "convertBlock", at = @At("HEAD"), cancellable = true
    )
    private void abby$addConvertByPointyStick(ItemStack stack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir){
        if (stack != null && stack.getItem() instanceof ChiselItemWood) {
            world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
            if (!world.isRemote) {
                world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
            }
            cir.setReturnValue(true);
        }
    }
}
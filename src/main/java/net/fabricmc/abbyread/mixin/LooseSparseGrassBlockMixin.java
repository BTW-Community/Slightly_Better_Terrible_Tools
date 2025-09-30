package net.fabricmc.abbyread.mixin;

import btw.block.blocks.LooseSparseGrassBlock;
import btw.community.abbyread.sbtt.Convert;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LooseSparseGrassBlock.class)
public class LooseSparseGrassBlockMixin {

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$canConvertBlock(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        Block block = (Block)(Object)this;
        int meta = world.getBlockMetadata(x, y, z);
        boolean canConvert = Convert.canConvert(stack, block, meta);
        if (canConvert) cir.setReturnValue(true);
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$convertBlock(ItemStack stack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir) {
        Block block = (Block)(Object)this;
        int meta = world.getBlockMetadata(x, y, z);

        boolean swapped = Convert.convert(stack, block, meta, world, x, y, z, fromSide);
        if (swapped) cir.setReturnValue(true);
    }
}

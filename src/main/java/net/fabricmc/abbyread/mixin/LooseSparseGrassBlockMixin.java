package net.fabricmc.abbyread.mixin;

import btw.block.blocks.LooseSparseGrassBlock;
import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.categories.ItemTags;
import btw.community.abbyread.sbtt.Helper;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("PointlessBooleanExpression")
@Mixin(LooseSparseGrassBlock.class)
public class LooseSparseGrassBlockMixin {
    @Inject(method = "canConvertBlock", at = @At("RETURN"), cancellable = true)
    private void abbyread$canConvertWithSharpStone(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() == false) {
            if (ItemTags.isAll(stack, ItemTag.STONE, ItemTag.CHISEL)) cir.setReturnValue(true);
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$convertWithSharpStone(ItemStack stack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir) {
        if (ItemTags.isNotAll(stack, ItemTag.STONE, ItemTag.CHISEL)) return;

        boolean swapped = false;
        Block block = (LooseSparseGrassBlock)(Object)this;
        int meta = world.getBlockMetadata(x, y, z);

        swapped = Helper.sparsen(stack, block, meta, world, x, y, z, fromSide);
        if (swapped) cir.setReturnValue(true);
    }
}

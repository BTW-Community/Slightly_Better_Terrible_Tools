package btw.community.abbyread.sbtt.mixin;

import btw.block.blocks.LooseDirtBlock;
import btw.community.abbyread.sbtt.Convert;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LooseDirtBlock.class)
public class LooseDirtBlockMixin {
    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$canConvertBlock(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        Block block = (Block)(Object)this;
        int meta = world.getBlockMetadata(x, y, z);
        if (Convert.canConvert(stack, block, meta)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$convertBlock(ItemStack stack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        Block block = (Block)(Object)this;
        int meta = world.getBlockMetadata(x, y, z);
        if (Convert.convert(stack, block, meta, world, x, y, z, fromSide)) {
            cir.setReturnValue(true);
        }
    }
}

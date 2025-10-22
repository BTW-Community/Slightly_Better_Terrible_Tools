package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.sbtt.Efficiency;
import btw.community.abbyread.sbtt.Convert;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$unifiedCanConvertBlock(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        Block block = (Block) (Object) this;
        int meta = world.getBlockMetadata(x, y, z);

        if (Convert.canConvert(stack, block, meta)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$unifiedConvertBlock(ItemStack stack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        Block block = (Block) (Object) this;
        int meta = world.getBlockMetadata(x, y, z);

        if (Convert.convert(stack, null, block, meta, world, x, y, z, fromSide)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "getPlayerRelativeBlockHardness",
            at = @At("RETURN"),
            cancellable = true
    )
    private void abbyread$getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        Block self = (Block)(Object)this;
        int meta = world.getBlockMetadata(x, y, z);

        Set<BlockTag> tags = BlockTags.of(self, meta);
        if (tags.contains(BlockTag.LOG)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier);
        }

        if (tags.contains(BlockTag.LOOSE_DIRTLIKE)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 1.5F);
        }

        if (tags.contains(BlockTag.SAND)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 1.25F);
        }

        if (tags.contains(BlockTag.GRAVEL)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier);
        }

    }
}

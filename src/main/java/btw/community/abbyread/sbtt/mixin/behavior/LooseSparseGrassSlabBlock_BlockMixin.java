package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.block.blocks.LooseSparseGrassSlabBlock;
import btw.community.abbyread.categories.ItemType;
import btw.community.abbyread.categories.ThisItem;
import btw.item.items.ChiselItemStone;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class LooseSparseGrassSlabBlock_BlockMixin {

    @Unique
    private static final int SPARSE = 2; // a metadata value

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canClubConvert(ItemStack stack, World world, int x, int y, int z,
                                CallbackInfoReturnable<Boolean> cir) {
        Block self = (Block) (Object) this;
        if (!(self instanceof LooseSparseGrassSlabBlock)) return;

        // Clubs can convert loose dirtlikes to their firm counterparts
        if (stack != null && ThisItem.is(ItemType.CLUB, stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void clubConvert(ItemStack stack, World world, int x, int y, int z, int iFromSide,
                             CallbackInfoReturnable<Boolean> cir) {
        Block self = (Block) (Object) this;
        if (!(self instanceof LooseSparseGrassSlabBlock)) return;

        // Only handle club conversions here
        if (stack == null || !ThisItem.is(ItemType.CLUB, stack)) {
            return;
        }

        // Convert loose sparse grass to firm sparse grass
        world.setBlockAndMetadataWithNotify(x, y, z, BTWBlocks.grassSlab.blockID, SPARSE);

        cir.setReturnValue(true);
    }

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canSparsenWithSharpStone(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemStone)) return;

        Block block = (Block) (Object) this;
        if (block instanceof LooseSparseGrassSlabBlock) cir.setReturnValue(true);

        // Continue with the rest of the method's logic otherwise
    }
    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void sparsenWithSharpStone(ItemStack stack, World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemStone)) return;

        Block block = (Block) (Object) this;
        if (!(block instanceof LooseSparseGrassSlabBlock)) return;

        world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirtSlab.blockID);
        cir.setReturnValue(true);

    }

}

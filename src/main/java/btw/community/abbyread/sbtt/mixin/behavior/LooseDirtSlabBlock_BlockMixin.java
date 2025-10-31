package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.block.blocks.LooseDirtSlabBlock;
import btw.community.abbyread.categories.ItemType;
import btw.community.abbyread.categories.ThisItem;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class LooseDirtSlabBlock_BlockMixin {

    @Unique
    private static final int DIRT = 0; // a metadata value

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canClubConvert(ItemStack stack, World world, int x, int y, int z,
                                CallbackInfoReturnable<Boolean> cir) {
        Block self = (Block) (Object) this;
        if (!(self instanceof LooseDirtSlabBlock)) return;

        // Clubs can convert loose dirtlikes to their firm counterparts
        if (stack != null && ThisItem.is(ItemType.CLUB, stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void clubConvert(ItemStack stack, World world, int x, int y, int z, int iFromSide,
                             CallbackInfoReturnable<Boolean> cir) {
        Block self = (Block) (Object) this;
        if (!(self instanceof LooseDirtSlabBlock)) return;

        // Only handle club conversions here
        if (stack == null || !ThisItem.is(ItemType.CLUB, stack)) {
            return;
        }

        // Convert loose dirt slab to firm dirt slab
        world.setBlockAndMetadataWithNotify(x, y, z, BTWBlocks.dirtSlab.blockID, DIRT);

        cir.setReturnValue(true);
    }

}

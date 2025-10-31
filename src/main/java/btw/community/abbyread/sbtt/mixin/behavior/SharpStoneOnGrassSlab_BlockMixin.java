package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.block.blocks.GrassSlabBlock;
import btw.community.abbyread.categories.QualifiedBlock;
import btw.item.items.ChiselItemStone;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class SharpStoneOnGrassSlab_BlockMixin {

    @Shadow @Final public int blockID;

    @Unique private static final int FIRM_DIRT = 0;
    @Unique private static final int FULLY_GROWN = 0;
    @Unique private static final int SPARSE = 2;

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canSparsenWithSharpStone(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemStone)) return;

        Block block = (Block) (Object) this;
        if (block instanceof GrassSlabBlock) cir.setReturnValue(true);

        // Continue with the rest of the method's logic otherwise
    }
    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void sparsenWithSharpStone(ItemStack stack, World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemStone)) return;

        Block block = (Block) (Object) this;
        if (!(block instanceof GrassSlabBlock)) return;

        int metadata = world.getBlockMetadata(x, y, z);

        QualifiedBlock toBlock = switch (metadata) {
            case FULLY_GROWN -> new QualifiedBlock(BTWBlocks.grassSlab.blockID, SPARSE);
            case SPARSE -> new QualifiedBlock(BTWBlocks.dirtSlab.blockID, FIRM_DIRT);
            default -> null;
        };
        if (toBlock != null) {
            world.setBlockAndMetadataWithNotify(x, y, z, toBlock.blockID, toBlock.metadata);

            cir.setReturnValue(true);
        }
    }

}

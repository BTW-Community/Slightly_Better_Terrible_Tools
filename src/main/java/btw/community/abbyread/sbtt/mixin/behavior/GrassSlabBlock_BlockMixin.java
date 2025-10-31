package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.block.blocks.GrassSlabBlock;
import btw.community.abbyread.categories.QualifiedBlock;
import btw.item.BTWItems;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import btw.item.util.ItemUtils;
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

import static btw.community.abbyread.sbtt.util.Globals.OUT_OF_CHANCE;

@Mixin(Block.class)
public class GrassSlabBlock_BlockMixin {

    @Shadow @Final public int blockID;

    @Unique private static final int DIRT = 0;
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
            case SPARSE -> new QualifiedBlock(BTWBlocks.dirtSlab.blockID, DIRT);
            default -> null;
        };
        if (toBlock != null) {
            world.setBlockAndMetadataWithNotify(x, y, z, toBlock.blockID, toBlock.metadata);

            // Process seed chance once (just on server)
            if (!world.isRemote) maybeGetSeeds(world, x, y, z, side);

            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canLoosenWithPointyStick(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemWood)) return;

        Block block = (Block) (Object) this;
        if (!(block instanceof GrassSlabBlock)) return;

        // Only loosen if grass is sparse
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == SPARSE) cir.setReturnValue(true);

        // Continue with the rest of the method's logic otherwise
    }
    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void loosenWithPointyStick(ItemStack stack, World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemWood)) return;

        Block block = (Block) (Object) this;
        if (!(block instanceof GrassSlabBlock)) return;

        int metadata = world.getBlockMetadata(x, y, z);

        // Only loosen if grass is sparse
        if (metadata == SPARSE) {
            world.setBlockAndMetadataWithNotify(x, y, z, BTWBlocks.looseSparseGrassSlab.blockID, DIRT);

            world.playSoundEffect((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f, block.getStepSound(world, x, y, z).getBreakSound(), block.getStepSound(world, x, y, z).getPlaceVolume() + 2.0f, block.getStepSound(world, x, y, z).getPlacePitch() * 0.7f);
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void maybeGetSeeds(World world, int x, int y, int z, int side) {
        if (world.rand.nextInt(OUT_OF_CHANCE) == 0) {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.hempSeeds), side);
        }
    }
}

package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.block.blocks.LooseSparseGrassBlock;
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

@Mixin(LooseSparseGrassBlock.class)
public class LooseSparseGrassBlockMixin {

    @Unique
    private static final int SPARSE = 1;

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canClubConvert(ItemStack stack, World world, int x, int y, int z,
                                CallbackInfoReturnable<Boolean> cir) {
        // Clubs can convert loose dirtlikes to their firm counterparts
        if (stack != null && ThisItem.is(ItemType.CLUB, stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void clubConvert(ItemStack stack, World world, int x, int y, int z, int iFromSide,
                             CallbackInfoReturnable<Boolean> cir) {
        // Only handle club conversions here
        if (stack == null || !ThisItem.is(ItemType.CLUB, stack)) {
            return;
        }

        // Convert loose sparse grass to firm sparse grass
        world.setBlockAndMetadataWithNotify(x, y, z, Block.grass.blockID, SPARSE);

        if (!world.isRemote) {
            Block block = BTWBlocks.looseSparseGrass;
            world.playSoundEffect((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f, block.stepSound.getBreakSound(), block.getStepSound(world, x, y, z).getPlaceVolume() + 2.0f, block.getStepSound(world, x, y, z).getPlacePitch());
        }

        cir.setReturnValue(true);
    }
}

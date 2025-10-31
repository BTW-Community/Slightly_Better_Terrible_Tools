package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.block.blocks.LooseDirtBlock;
import btw.community.abbyread.categories.*;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LooseDirtBlock.class)
public abstract class LooseDirtBlockMixin {

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canClubConvert(ItemStack stack, World world, int x, int y, int z,
                                CallbackInfoReturnable<Boolean> cir) {
        // Clubs can convert loose dirt
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

        // Convert loose dirt to firm dirt
        world.setBlockAndMetadataWithNotify(x, y, z, Block.dirt.blockID, 0);

        // Play the tilling effect
        if (!world.isRemote) {
            Block block = BTWBlocks.looseDirt;
            world.playSoundEffect((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f, block.stepSound.getBreakSound(), block.getStepSound(world, x, y, z).getPlaceVolume() + 2.0f, block.getStepSound(world, x, y, z).getPlacePitch());
        }

        cir.setReturnValue(true);
    }
}
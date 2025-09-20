package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import net.minecraft.src.BlockGrass;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockGrass.class)
public abstract class BlockGrassMixin {
    @Inject(
        method = "onNeighborDirtDugWithImproperTool",
        at = @At("HEAD"),
        cancellable = true
    )
    private void abby$restoreGrassLoosening(World world, int x, int y, int z, int toFacing, CallbackInfo ci) {
        if (toFacing == 0) {
            world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
        }
        ci.cancel(); // Prevents the original method
    }
}
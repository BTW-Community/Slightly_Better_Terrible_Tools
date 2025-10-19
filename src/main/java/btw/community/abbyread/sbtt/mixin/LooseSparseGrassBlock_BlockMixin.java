package btw.community.abbyread.sbtt.mixin;

import btw.block.BTWBlocks;
import btw.block.blocks.LooseSparseGrassBlock;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class LooseSparseGrassBlock_BlockMixin {

    @Inject(method = "checkForFall", at = @At("HEAD"))
    private void convertLooseSparseGrassToDirt(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        Block self = (Block)(Object)this;

        if (!(self instanceof LooseSparseGrassBlock)) return;

        // Trigger conversion if the block would fall
        if (self.canFallIntoBlockAtPos(world, x, y - 1, z)) {
            System.out.println("[SBTT DEBUG] LooseSparseGrassBlock converting to LooseDirt at " + x + "," + y + "," + z);
            world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
        }
    }
}

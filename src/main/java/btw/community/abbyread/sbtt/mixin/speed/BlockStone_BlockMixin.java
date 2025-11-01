package btw.community.abbyread.sbtt.mixin.speed;

import net.minecraft.src.Block;
import net.minecraft.src.BlockStone;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockStone_BlockMixin {
    @Inject(method = "arechiselseffectiveon(Lnet/minecraft/src/World;III)Z", at = @At("RETURN"), cancellable = true)
    public void chiselsAreEffectiveOnStone(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        Block self = (Block) (Object) this;
        if (!(self instanceof BlockStone block)) return;
        int strataLevel = block.getStrata(world, x, y, z);

        // All chisels effective on strata 0 stone
        if (strataLevel == 0) {
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }
    }
}

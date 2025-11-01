package btw.community.abbyread.sbtt.mixin.speed;

import btw.block.blocks.RoughStoneBlock;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class RoughStoneBlock_BlockMixin {

    // Changes hardness of last bits of rough stone block harvest since chisels
    //  were made effective on that part too.  Still gotta be slower to disincentivize mining mostly.
    @Inject(method = "getBlockHardness", at = @At("RETURN"), cancellable = true)
    public void roughStoneIsNotAsSlowToDestroy(World world, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        Block self = (Block)(Object)this;
        if (!(self instanceof RoughStoneBlock block)) return;
        if (block.strataLevel != 0) return;

        float hardness = cir.getReturnValue();

        // Increase block hardness to counteract the boost from chisels being effective-on,
        //   just for the last 8 stages of rough stone before breaking completely
        if (world.getBlockMetadata(x, y, z) >= 8) {
            cir.setReturnValue(hardness * 2);
        }
    }
}

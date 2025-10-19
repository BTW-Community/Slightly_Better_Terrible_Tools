package btw.community.abbyread.sbtt.mixin;

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
    public void abbyread$getBlockHardness(World world, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        Block block = (Block)(Object)this;
        if (!(block instanceof RoughStoneBlock)) return;

        float hardness = cir.getReturnValue();
        if (world.getBlockMetadata(x, y, z) >= 8) cir.setReturnValue(hardness * 2);
    }
}

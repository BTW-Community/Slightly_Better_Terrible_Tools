package btw.community.abbyread.sbtt.mixin;

import btw.block.blocks.RoughStoneBlock;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RoughStoneBlock.class)
public class RoughStoneBlockMixin {
    @Inject(method = "arechiselseffectiveon", at = @At("RETURN"), cancellable = true)
    public void abbyread$yasChisels(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}

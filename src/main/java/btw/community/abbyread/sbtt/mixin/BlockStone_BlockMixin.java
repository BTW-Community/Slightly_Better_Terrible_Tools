package btw.community.abbyread.sbtt.mixin;

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
    public void abbyread$yasChisels(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        Block self = (Block)(Object)this;
        if (self instanceof BlockStone) cir.setReturnValue(true);
    }
}

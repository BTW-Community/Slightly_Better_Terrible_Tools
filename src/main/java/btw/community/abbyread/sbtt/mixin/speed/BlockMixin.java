package btw.community.abbyread.sbtt.mixin.speed;

import btw.community.abbyread.categories.ThisBlock;
import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.sbtt.util.Globals;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(
            method = "getPlayerRelativeBlockHardness",
            at = @At("RETURN"),
            cancellable = true
    )
    private void boostSpeedRegardlessOfTool(EntityPlayer player, World world, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        Block self = (Block)(Object)this;
        int meta = world.getBlockMetadata(x, y, z);

        Set<BlockType> tags = ThisBlock.of(self, meta);

        // Regardless of tool (including nothing), boost by global modifier
        if (tags.contains(BlockType.LOG)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Globals.modifier);
            return;
        }

        if (tags.contains(BlockType.LOOSE_DIRTLIKE)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Globals.modifier * 1.5F);
            return;
        }

        if (tags.contains(BlockType.SAND)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Globals.modifier * 1.25F);
            return;
        }

        if (tags.contains(BlockType.GRAVEL)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Globals.modifier);
            return;
        }

    }
}

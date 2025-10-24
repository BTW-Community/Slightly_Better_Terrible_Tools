package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.BlockSide;
import btw.community.abbyread.categories.BlockSet;
import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.sbtt.helper.Efficiency;
import btw.community.abbyread.sbtt.helper.InteractionHandler;
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
    private void abbyread$getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        Block self = (Block)(Object)this;
        int meta = world.getBlockMetadata(x, y, z);

        Set<BlockType> tags = BlockSet.of(self, meta);
        if (tags.contains(BlockType.LOG)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier);
        }

        if (tags.contains(BlockType.LOOSE_DIRTLIKE)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 1.5F);
        }

        if (tags.contains(BlockType.SAND)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 1.25F);
        }

        if (tags.contains(BlockType.GRAVEL)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier);
        }

    }

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$unifiedCanConvertBlock(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        Block block = (Block) (Object) this;
        int meta = world.getBlockMetadata(x, y, z);

        if (InteractionHandler.canInteract(stack, block, meta, InteractionHandler.InteractionType.PRIMARY_LEFT_CLICK)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$unifiedConvertBlock(ItemStack stack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        Block block = (Block) (Object) this;
        int meta = world.getBlockMetadata(x, y, z);
        EntityPlayer player = null; // fromSide doesn't give us the player; passing null is safe for conversions

        BlockSide side = BlockSide.fromId(fromSide);
        if (InteractionHandler.interact(stack, player, block, meta, world, x, y, z, side, InteractionHandler.InteractionType.PRIMARY_LEFT_CLICK)) {
            cir.setReturnValue(true);
        }
    }

}

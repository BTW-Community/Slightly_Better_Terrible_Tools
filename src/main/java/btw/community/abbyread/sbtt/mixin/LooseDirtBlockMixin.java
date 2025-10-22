package btw.community.abbyread.sbtt.mixin;

import btw.block.blocks.LooseDirtBlock;
import btw.community.abbyread.categories.BlockSide;
import btw.community.abbyread.sbtt.api.InteractionHandler;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LooseDirtBlock.class)
public class LooseDirtBlockMixin {

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

package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.block.blocks.DirtSlabBlock;
import btw.item.items.ChiselItemWood;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class PointyStickOnDirtSlab_BlockMixin {

    @Shadow @Final public int blockID;
    @Unique
    private static final int FIRM_DIRT = 0;
    @Unique
    private static final int PACKED_EARTH = 6;

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canLoosenWithPointyStick(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemWood)) return;

        Block block = (Block) (Object) this;
        if (!(block instanceof DirtSlabBlock)) return;
        int metadata = world.getBlockMetadata(x, y, z);

        boolean canConvert = switch (metadata) {
            case PACKED_EARTH, FIRM_DIRT -> true;
            default -> false;
        };
        if (canConvert) cir.setReturnValue(true);

        // Continue with the rest of the method's logic otherwise
    }
    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void loosenWithPointyStick(ItemStack stack, World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemWood)) return;

        Block block = (Block) (Object) this;
        if (!(block instanceof DirtSlabBlock)) return;
        int metadata = world.getBlockMetadata(x, y, z);

        int toBlockID = switch (metadata) {
            case PACKED_EARTH -> Block.dirt.blockID;
            case FIRM_DIRT -> BTWBlocks.looseDirtSlab.blockID;
            default -> 0;
        };
        if (toBlockID != 0) {
            world.setBlockWithNotify(x, y, z, toBlockID);
            cir.setReturnValue(true);
        }
    }

}

package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.items.ChiselItemWood;
import btw.item.util.ItemUtils;
import net.minecraft.src.Block;
import net.minecraft.src.BlockClay;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockClay_BlockMixin {

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canHarvestAndLoosen(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {

        Block block = (Block) (Object) this;

        // Can convert if using pointy stick
        if (block instanceof BlockClay && stack.getItem() instanceof ChiselItemWood) cir.setReturnValue(true);

        // Otherwise resume processing of the normal method
    }
    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void harvestAndLoosen(ItemStack stack, World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        Block block = (Block) (Object) this;

        // Return early if not pointy stick used on clay block
        if (!(stack.getItem() instanceof ChiselItemWood) || !(block instanceof BlockClay)) return;

        world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);

        if (!world.isRemote) {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.clayPile), side);
        }

        cir.setReturnValue(true);
    }
}

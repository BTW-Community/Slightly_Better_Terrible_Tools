package btw.community.abbyread.sbtt.mixin.speed;

import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.categories.ThisBlock;
import btw.item.items.ChiselItem;
import btw.item.items.ChiselItemWood;
import net.minecraft.src.Block;
import net.minecraft.src.BlockClay;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiselItem.class)
public class ChiselItemWood_ChiselItemMixin {

    @Inject(method = "isEfficientVsBlock", at = @At("RETURN"), cancellable = true)
    private void addEfficienciesToPointyStick(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        // Note: Because BTW nerfs the efficiency modifier on pointy sticks, this is mostly
        //       intended to be used as a flag saying that pointy sticks are useful on these blocks.
        //       The plan is for Prevent Wasted Uses to pick up on that and handle things appropriately.

        if (!(stack.getItem() instanceof ChiselItemWood)) return;

        int metadata = world.getBlockMetadata(x, y, z);

        if (block instanceof BlockClay) {
            cir.setReturnValue(true);
            return;
        }
        if (ThisBlock.is(BlockType.LOOSE_STONELIKE, block, metadata)) {
            cir.setReturnValue(true);
            return;
        }
        if (ThisBlock.isAnd(BlockType.FIRM_DIRTLIKE, BlockType.SPARSE, block, metadata)) {
            cir.setReturnValue(true);
            return;
        }
        if (ThisBlock.is(BlockType.PACKED_EARTH, block, metadata)) {
            cir.setReturnValue(true);
            //noinspection UnnecessaryReturnStatement
            return;
        }

    }

}

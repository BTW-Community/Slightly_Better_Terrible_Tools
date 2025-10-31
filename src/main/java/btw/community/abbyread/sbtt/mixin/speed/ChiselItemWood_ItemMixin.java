package btw.community.abbyread.sbtt.mixin.speed;

import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.categories.ThisBlock;
import btw.community.abbyread.sbtt.util.Globals;
import btw.item.BTWItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ChiselItemWood_ItemMixin {

    @Inject(
            method = "getStrVsBlock",
            at = @At("RETURN"),
            cancellable = true
    )
    private void pointyStickEfficiencyBoosts(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        if (stack == null || block == null) return;

        // Return early if not pointy stick
        if (stack.getItem().itemID != BTWItems.pointyStick.itemID) return;

        int metadata = world.getBlockMetadata(x, y, z);

        if (block instanceof BlockStone) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Globals.modifier);
        }

        float mod = 6F; // TODO: Mention added boost in release changes

        // Loose masonry blocks easier to pry up with Pointy Stick
        if (ThisBlock.is(BlockType.LOOSE_STONELIKE, block, metadata)) {
            float base = cir.getReturnValue();
            float modifier = Globals.modifier * mod;
            cir.setReturnValue(base * modifier);
        }

        // Boost clay harvest
        if (block instanceof BlockClay) {
            float base = cir.getReturnValue();
            float modifier = Globals.modifier * 2;
            cir.setReturnValue(base * modifier);
        }

        // Boost toward loosening blocks
        if (ThisBlock.isButNot(BlockType.FIRM_DIRTLIKE, BlockType.FULLY_GROWN, block, metadata)) {
            float base = cir.getReturnValue();
            float modifier = Globals.modifier * 2;
            cir.setReturnValue(base * modifier);
        }

        // Boost toward unpacking blocks
        if (ThisBlock.is(BlockType.PACKED_EARTH, block, metadata)) {
            float base = cir.getReturnValue();
            float modifier = Globals.modifier * 2;
            cir.setReturnValue(base * modifier);
        }

    }

    @Inject(method = "isEfficientVsBlock", at = @At("HEAD"), cancellable = true)
    private void addEfficienciesToPointyStick(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        // Note: Because BTW nerfs the efficiency modifier on pointy sticks, this is mostly
        //       intended to be used as a flag saying that pointy sticks are useful on these blocks.
        //       The plan is for Prevent Wasted Uses to pick up on that and handle things appropriately.

        int metadata = world.getBlockMetadata(x, y, z);

        if (block instanceof BlockClay) cir.setReturnValue(true);
        if (ThisBlock.is(BlockType.LOOSE_STONELIKE, block, metadata)) cir.setReturnValue(true);
        if (ThisBlock.isButNot(BlockType.FIRM_DIRTLIKE, BlockType.FULLY_GROWN, block, metadata)) {
            cir.setReturnValue(true);
        }
        if (ThisBlock.is(BlockType.PACKED_EARTH, block, metadata)) cir.setReturnValue(true);
    }

}

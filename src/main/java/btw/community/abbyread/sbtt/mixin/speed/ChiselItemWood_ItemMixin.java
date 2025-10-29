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
    private void abbyread$pryLooseMasonry(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if (stack == null || block == null) return;

        // Return early if not pointy stick
        if (stack.getItem().itemID != BTWItems.pointyStick.itemID) return;

        int meta = world.getBlockMetadata(i, j, k);

        float mod = 6F; // TODO: Mention added boost in release changes

        // Loose masonry blocks easier to pry up with Pointy Stick
        if (ThisBlock.is(BlockType.LOOSE_STONELIKE, block, meta)) {
            float base = cir.getReturnValue();
            float modifier = Globals.modifier * mod;
            cir.setReturnValue(base * modifier);
        }
    }

}

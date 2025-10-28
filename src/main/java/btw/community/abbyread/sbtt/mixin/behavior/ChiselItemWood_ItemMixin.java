package btw.community.abbyread.sbtt.mixin.behavior;

import btw.community.abbyread.categories.BlockSet;
import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.categories.ItemSet;
import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.sbtt.Globals;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
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
    private void abbyread$loosenDirtWithPointyStick(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if (stack == null || block == null) return;

        // Return early if not pointy stick
        if (stack.getItem().itemID != BTWItems.pointyStick.itemID) return;

        // Check if block is valid for loosening
        int meta = world.getBlockMetadata(i, j, k); // meta is ignored here; expand if needed
        if (BlockSet.hasAll(block, meta, BlockTag.GRASS, BlockTag.SPARSE, BlockTag.FIRM)
                || BlockSet.hasAll(block, meta, BlockTag.DIRT, BlockTag.FIRM)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Globals.modifier * 1.5F);
        }
    }

}

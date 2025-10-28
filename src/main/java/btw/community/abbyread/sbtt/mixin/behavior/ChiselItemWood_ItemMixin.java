package btw.community.abbyread.sbtt.mixin.behavior;

import btw.community.abbyread.categories.ThisBlock;
import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.sbtt.util.Globals;
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
        if (ThisBlock.isAll(block, meta, BlockType.GRASS, BlockType.SPARSE, BlockType.FIRM)
                || ThisBlock.isAll(block, meta, BlockType.DIRT, BlockType.FIRM)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Globals.modifier * 1.5F);
        }
    }

}

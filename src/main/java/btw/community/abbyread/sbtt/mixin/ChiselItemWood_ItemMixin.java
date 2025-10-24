package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.categories.BlockSet;
import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.categories.ItemTags;
import btw.community.abbyread.sbtt.helper.Efficiency;
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
    private void abbyread$loosenDirtWithPointyStick(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if (stack == null || block == null) return;

        // Return early if not pointy stick
        if (ItemTags.isNotAll(stack, ItemTag.WOOD, ItemTag.CHISEL)) return;

        // Check if block is valid for loosening
        int meta = world.getBlockMetadata(i, j, k); // meta is ignored here; expand if needed
        if (BlockSet.isAll(block, meta, BlockType.GRASS, BlockType.SPARSE, BlockType.FIRM)
                || BlockSet.isAll(block, meta, BlockType.DIRT, BlockType.FIRM)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 1.5F);
        }
    }

    @Inject(
            method = "getStrVsBlock",
            at = @At("RETURN"),
            cancellable = true
    )
    private void abbyread$pryLooseMasonry(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if (stack == null || block == null) return;

        // Return early if not pointy stick
        if (ItemTags.isNotAll(stack, ItemTag.WOOD, ItemTag.CHISEL)) return;


        int meta = world.getBlockMetadata(i, j, k);

        float mod = 4F;

        // Loose masonry blocks easier to pry up with Pointy Stick
        if (BlockSet.is(block, meta, BlockType.LOOSE_STONELIKE)) {
            float base = cir.getReturnValue();
            float modifier = Efficiency.modifier * mod;
            cir.setReturnValue(base * modifier);
        }
    }


}

package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.categories.ItemTags;
import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.sbtt.Efficiency;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(
            method = "getStrVsBlock",
            at = @At("RETURN"),
            cancellable = true
    )
    private void abbyread$cutGrassWithSharpStone(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if (stack == null || block == null) return;

        // Check if item is a STONE CHISEL
        if (ItemTags.isNotAll(stack, ItemTag.STONE, ItemTag.CHISEL)) return;

        // Check if block is GRASS
        int meta = world.getBlockMetadata(i, j, k);
        if (BlockTags.is(block, meta, BlockTag.GRASS)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 1.5F);
        }
    }

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
        if (BlockTags.isAll(block, meta, BlockTag.GRASS, BlockTag.SPARSE, BlockTag.FIRM)
                || BlockTags.isAll(block, meta, BlockTag.DIRT, BlockTag.FIRM)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 1.5F);
        }
    }
}

package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.categories.BlockCategories;
import btw.community.abbyread.categories.BlockCategory;
import btw.community.abbyread.categories.ItemCategories;
import btw.community.abbyread.categories.ItemCategory;
import btw.community.abbyread.sbtt.Efficiency;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(
            method = "getStrVsBlock",
            at = @At("RETURN"),
            cancellable = true
    )
    private void abbyread$cutGrassWithSharpStone(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if (stack == null || block == null) return;

        // Check if item is a CHISEL
        Set<ItemCategory> itemCats = ItemCategories.getCategories(stack);
        if (!itemCats.contains(ItemCategory.CHISEL) || !itemCats.contains(ItemCategory.STONE)) return;

        // Check if block is DIRT-like
        Set<BlockCategory> blockCats = BlockCategories.of(block, 0); // meta is ignored here; expand if needed
        if (blockCats.contains(BlockCategory.GRASS)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 2);
        }
    }

    @Inject(
            method = "getStrVsBlock",
            at = @At("RETURN"),
            cancellable = true
    )
    private void abbyread$loosenDirtWithPointyStick(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if (stack == null || block == null) return;

        // Check if item is a CHISEL
        Set<ItemCategory> itemCats = ItemCategories.getCategories(stack);
        if (!itemCats.contains(ItemCategory.CHISEL) || !itemCats.contains(ItemCategory.WOOD)) return;

        // Check if block is DIRT-like
        int meta = world.getBlockMetadata(i, j, k);
        Set<BlockCategory> blockCats = BlockCategories.of(block, meta); // meta is ignored here; expand if needed
        if (blockCats.contains(BlockCategory.DIRTLIKE) && !blockCats.contains(BlockCategory.LOOSE)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 2); // TODO: figure out why this isn't working
        }
    }
}

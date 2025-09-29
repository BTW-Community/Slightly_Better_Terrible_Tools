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

        // Check if item is a STONE CHISEL
        Set<ItemTag> itemCats = ItemTags.getTags(stack);
        if (!itemCats.contains(ItemTag.CHISEL) || !itemCats.contains(ItemTag.STONE)) return;

        // Check if block is GRASS
        int meta = world.getBlockMetadata(i, j, k);
        Set<BlockTag> blockCats = BlockTags.of(block, meta);
        if (blockCats.contains(BlockTag.GRASS)) {
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
        Set<ItemTag> itemCats = ItemTags.getTags(stack);
        if (!itemCats.contains(ItemTag.CHISEL) || !itemCats.contains(ItemTag.WOOD)) return;

        // Check if block is DIRT-like
        int meta = world.getBlockMetadata(i, j, k);
        Set<BlockTag> blockCats = BlockTags.of(block, meta); // meta is ignored here; expand if needed
        if (blockCats.contains(BlockTag.DIRTLIKE) && !blockCats.contains(BlockTag.LOOSE)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 2);
        }
    }
}

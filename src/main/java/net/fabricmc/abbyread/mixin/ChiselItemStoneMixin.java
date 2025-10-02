package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.sbtt.Efficiency;
import btw.item.items.ChiselItemStone;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiselItemStone.class)
public class ChiselItemStoneMixin {

    @Inject(method = "getStrVsBlock", at = @At("RETURN"), cancellable = true)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if (block == null) return;

        // Boost to sparsen grass
        int meta = world.getBlockMetadata(i, j, k);
        if (BlockTags.is(block, meta, BlockTag.GRASS)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier);
        }

        // Boost to web harvest (over the BTW vanilla boost)
        if (BlockTags.is(block, meta, BlockTag.WEB)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier);
        }

        // Shatterables shattered or picked up faster by chisels
        if (BlockTags.is(block, meta, BlockTag.SHATTERABLE)) {
            float base = cir.getReturnValue();
            float modifier = (Efficiency.modifier - 1) * 2 + 1; // (percent boost * 2)
            cir.setReturnValue(base * modifier);
        }

        // Loose masonry blocks easier to pick up with chisels
        if (BlockTags.is(block, meta, BlockTag.LOOSE_STONELIKE)) {
            float base = cir.getReturnValue();
            float modifier = (Efficiency.modifier - 1) * 2 + 1; // (percent boost * 2)
            cir.setReturnValue(base * modifier);
        }

        // Mortared masonry blocks easier to pick up with chisels
        if (BlockTags.is(block, meta, BlockTag.LOOSE_STONELIKE)) {
            float base = cir.getReturnValue();
            float modifier = Efficiency.modifier;
            cir.setReturnValue(base * modifier);
        }
    }
}

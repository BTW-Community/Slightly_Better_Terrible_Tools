package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.sbtt.api.Efficiency;
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

        int meta = world.getBlockMetadata(i, j, k);

        float mod = 1.5F;

        // *** Specific-to-sharp-stone use boost (cutting grass) ***
        if (BlockTags.isAll(block, meta, BlockTag.GRASS, BlockTag.FIRM)) {
            float base = cir.getReturnValue();
            float modifier = Efficiency.modifier * mod;
            cir.setReturnValue(base * modifier);
        }

        // ** Boost categories common to chisels stone and above **

        // Web harvesting is already doubled in the source code.
        //   Boosting just a little more.
        if (BlockTags.is(block, meta, BlockTag.WEB)) {
            mod = 0.75F; // Reduce Efficiency.modifier just a bit
            float base = cir.getReturnValue();
            float modifier = (Efficiency.modifier - 1) * mod + 1;
            cir.setReturnValue(base * modifier);
        }

        // Solid, single-harvest stonelike blocks easier to pick up with chisels
        if (BlockTags.is(block, meta, BlockTag.EASY_SOLID_STONELIKE)) {
            float base = cir.getReturnValue();
            float modifier = (Efficiency.modifier - 1) * mod + 1;
            cir.setReturnValue(base * modifier);
        }

        // Shatterables shattered or picked up faster by chisels
        if (BlockTags.is(block, meta, BlockTag.SHATTERABLE)) {
            float base = cir.getReturnValue();
            float modifier = (Efficiency.modifier - 1) * mod + 1;
            cir.setReturnValue(base * modifier);
        }
    }
}

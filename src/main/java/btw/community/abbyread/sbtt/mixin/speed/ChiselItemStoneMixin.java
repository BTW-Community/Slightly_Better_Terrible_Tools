package btw.community.abbyread.sbtt.mixin.speed;

import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.categories.ThisBlock;
import btw.community.abbyread.sbtt.util.Globals;
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
    private void sharpStoneSpeedBoosts(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if (block == null) return;

        int meta = world.getBlockMetadata(i, j, k);

        float mod = 1.5F;

        // Unnecessary due to isEfficientVsBlock being set to true, boosting it.
        /*
        if (ThisBlock.isAll(block, meta, BlockType.GRASS)) {
            float base = cir.getReturnValue();
            float modifier = Globals.modifier;
            cir.setReturnValue(base * modifier);
        }
        */

        // ** Boost categories common to chisels stone and above **

        // Web harvesting is already doubled in the source code.
        //   Boosting just a little more.
        if (ThisBlock.is(BlockType.WEB, block, meta)) {
            mod = 0.75F; // Reduce Efficiency.modifier just a bit
            float base = cir.getReturnValue();
            float modifier = (Globals.modifier - 1) * mod + 1;
            cir.setReturnValue(base * modifier);
        }

        // Solid, single-harvest stonelike blocks easier to pick up with chisels
        if (ThisBlock.is(BlockType.EASY_SOLID_STONELIKE, block, meta)) {
            float base = cir.getReturnValue();
            float modifier = (Globals.modifier - 1) * mod + 1;
            cir.setReturnValue(base * modifier);
        }

        // Shatterables shattered or picked up faster by chisels
        if (ThisBlock.is(BlockType.SHATTERABLE, block, meta)) {
            float base = cir.getReturnValue();
            float modifier = (Globals.modifier - 1) * mod + 1;
            cir.setReturnValue(base * modifier);
        }
    }
}

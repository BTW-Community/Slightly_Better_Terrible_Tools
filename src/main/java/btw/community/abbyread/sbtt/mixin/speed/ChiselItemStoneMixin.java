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

        // Nerf efficiency boost from marking can-convert on grass, making it come up
        //   as efficient on, causing a too-high speed boost.
        if (ThisBlock.is(BlockType.GRASS, block, meta)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * 0.75F);
            System.out.println("cir.getReturnValue()" + cir.getReturnValue());
            // Fall-through to also nerf loose sparse grass if it is that
        }
        // Nerf loose sparse grass sparsening speed, as the speed-up from loose dirtlike
        //   is only supposed to be for breaking the block.
        if (ThisBlock.isAll(block, meta, BlockType.LOOSE_DIRTLIKE, BlockType.SPARSE, BlockType.GRASS)) {
            float base = cir.getReturnValue();
            System.out.println("cir.getReturnValue()" + cir.getReturnValue());
            // Since universal speed-up on loose dirtlike is base * (Globals.modifier * 1.5F),
            //   undo that by dividing by it.
            cir.setReturnValue(base / (Globals.modifier * 1.5F));
            // Still gets worked faster somehow, but not terribly much.  Feels
            //   right for it taking an extra step to loosen if not already by pig, etc.
            return;
        }

        // ** Boost categories common to chisels stone and above **

        // Web harvesting is already doubled in the source code.
        //   Boosting just a little more.
        if (ThisBlock.is(BlockType.WEB, block, meta)) {
            mod = 0.75F; // Reduce Efficiency.modifier just a bit
            float base = cir.getReturnValue();
            float modifier = (Globals.modifier - 1) * mod + 1;
            cir.setReturnValue(base * modifier);
            return;
        }

        // Solid, single-harvest stonelike blocks easier to pick up with chisels
        if (ThisBlock.is(BlockType.EASY_SOLID_STONELIKE, block, meta)) {
            float base = cir.getReturnValue();
            float modifier = (Globals.modifier - 1) * mod + 1;
            cir.setReturnValue(base * modifier);
            return;
        }

        // Shatterables shattered or picked up faster by chisels
        if (ThisBlock.is(BlockType.SHATTERABLE, block, meta)) {
            float base = cir.getReturnValue();
            float modifier = (Globals.modifier - 1) * mod + 1;
            cir.setReturnValue(base * modifier);
            return;
        }
    }
}

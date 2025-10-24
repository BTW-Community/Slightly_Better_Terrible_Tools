package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.BlockSet;
import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.categories.ItemTags;
import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.sbtt.helper.Efficiency;
import btw.item.items.ChiselItemDiamond;
import btw.item.items.ChiselItemIron;
import btw.item.items.ToolItem;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public class ChiselIronAndDiamond_ToolItemMixin {
    @Inject(
            method = "getStrVsBlock",
            at = @At("RETURN"),
            cancellable = true
    )
    private void abbyread$getStrVsBlock_IronAndDiamondChisels(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        // Note: ChiselItemStone overrides this method, but also calls the super's implementation.
        //          So, we need to avoid doing anything with stone chisels here.

        if (stack == null || block == null) return;

        // Check if item is an iron or diamond chisel
        if (ItemTags.isAny(stack, ItemTag.WOOD, ItemTag.STONE)) return;

        int meta = world.getBlockMetadata(i, j, k);

        float mod = 1;
        if (stack.getItem() instanceof ChiselItemIron) mod = 4;
        if (stack.getItem() instanceof ChiselItemDiamond) mod = 6;

        // Efficiency.modifier: 1.5 or 1.25
        //  (and how the "mod" value would affect it):

        // mod = 2:
        // 1.5  - 1 = 0.5       0.5 * 2 = 1          1 + 1 = 2
        // 1.25 - 1 = 0.25     0.25 * 2 = 0.5      0.5 + 1 = 1.5

        // mod = 3:
        // 1.5  - 1 = 0.5       0.5 * 3 = 1.5      1.5 + 1 = 2.5
        // 1.25 - 1 = 0.25     0.25 * 3 = 0.75    0.75 + 1 = 1.75

        // mod = 4:
        // 1.5  - 1 = 0.5       0.5 * 4 = 2          2 + 1 = 3
        // 1.25 - 1 = 0.25     0.25 * 4 = 1          1 + 1 = 2

        // mod = 5:
        // 1.5  - 1 = 0.5       0.5 * 5 = 2.5      2.5 + 1 = 3.5
        // 1.25 - 1 = 0.25     0.25 * 5 = 1.25    1.25 + 1 = 2.25

        // mod = 6:
        // 1.5  - 1 = 0.5       0.5 * 6 = 3          3 + 1 = 4
        // 1.25 - 1 = 0.25     0.25 * 6 = 1.5      1.5 + 1 = 2.5


        // diamond chisel base multiplier toward web blocks is already high (8).
        if (BlockSet.is(block, meta, BlockType.WEB)
            && stack.getItem() instanceof ChiselItemIron) {
            float base = cir.getReturnValue();
            float modifier = (Efficiency.modifier - 1) * mod + 1;
            cir.setReturnValue(base * modifier);
        }

        // Loose masonry blocks easier to pick up with chisels
        if (BlockSet.is(block, meta, BlockType.LOOSE_STONELIKE)) {
            float base = cir.getReturnValue();
            float modifier = Efficiency.modifier * mod;
            cir.setReturnValue(base * modifier);
        }

        // Solid, single-harvest stonelike blocks easier to pick up with chisels
        if (BlockSet.is(block, meta, BlockType.EASY_SOLID_STONELIKE)) {
            float base = cir.getReturnValue();
            float modifier = (Efficiency.modifier - 1) * mod + 1;
            cir.setReturnValue(base * modifier);
        }

        // Shatterables shattered or picked up faster by chisels
        if (BlockSet.is(block, meta, BlockType.SHATTERABLE)) {
            float base = cir.getReturnValue();
            float modifier = (Efficiency.modifier - 1) * mod + 1;
            cir.setReturnValue(base * modifier);
        }

    }


}

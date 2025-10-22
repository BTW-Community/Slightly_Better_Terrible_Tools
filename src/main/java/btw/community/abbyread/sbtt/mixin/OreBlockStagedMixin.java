package btw.community.abbyread.sbtt.mixin;

import btw.block.blocks.OreBlockStaged;
import btw.item.items.ChiselItem;
import btw.item.items.ToolItem;
import btw.item.util.ItemUtils;
import btw.world.util.difficulty.DifficultyParam;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OreBlockStaged.class)
public abstract class OreBlockStagedMixin {

    @Inject(
            method = "ejectItemsOnChiselConversion",
            at = @At("HEAD"),
            cancellable = true
    )
    private void sbtt$ironChiselsDropWholeOre(
            ItemStack stack, World world, int i, int j, int k,
            int iOldMetadata, int iFromSide,
            CallbackInfo ci
    ) {
        if (stack == null || !(stack.getItem() instanceof ChiselItem)) return;

        OreBlockStaged self = (OreBlockStaged)(Object)this;
        int toolLevel = ((ToolItem) stack.getItem()).toolMaterial.getHarvestLevel();
        int oreRequiredLevel = self.getRequiredToolLevelForOre(world, i, j, k);

        if (toolLevel < oreRequiredLevel) {
            ci.cancel();
            return;
        }

        boolean dropPiles;

        if (toolLevel >= 2) {
            // Iron or better chisel → full ore
            dropPiles = false;
        } else {
            // Weaker chisel → follow difficulty parameter
            dropPiles = world.getDifficultyParameter(
                    DifficultyParam.ShouldOresDropPilesWhenChiseled.class
            );
        }

        // --- Drop the item ---
        ItemUtils.ejectStackFromBlockTowardsFacing(
                world, i, j, k,
                new ItemStack(
                        self.idDroppedOnConversion(dropPiles, iOldMetadata),
                        self.quantityDroppedOnConversion(world.rand),
                        self.damageDroppedOnConversion(iOldMetadata)
                ),
                iFromSide
        );

        ci.cancel(); // Prevent the original method from running
    }
}

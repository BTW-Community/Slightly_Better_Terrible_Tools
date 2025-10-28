package btw.community.abbyread.sbtt.mixin.behavior;

import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.categories.ThisBlock;
import btw.item.items.ClubItem;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClubItem.class)
public class ClubItemMixin {

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void abbyread$convertLooseToFirm(ItemStack stack, World world, int iBlockID, int x, int y, int z, EntityLivingBase usingEntity, CallbackInfoReturnable<Boolean> cir) {
        Block block = Block.blocksList[iBlockID];
        int meta = world.getBlockMetadata(x, y, z);

        if (world.isRemote) return;

        if (ThisBlock.is(block, meta, BlockType.LOOSE_DIRTLIKE)) {

            // Convert to firm block counterpart

            // stack.damageItem(2, usingEntity);

        }
        // Prevent normal onBlockDestroyed routine from wasting use on other blocks
        cir.setReturnValue(true);
    }
}

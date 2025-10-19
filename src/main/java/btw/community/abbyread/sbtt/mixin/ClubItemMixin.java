package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.sbtt.Convert;
import btw.item.items.ClubItem;
import net.minecraft.src.Block;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
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
        System.out.println("ClubItem.onBlockDestroyed() called.");

        if (BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE)) {
            System.out.println("Block is dirtlike loose.");
            if (Convert.convert(stack, block, meta, world, x, y, z, 0)) {
                System.out.println("Convert.convert() returned true.");
                stack.damageItem(2, usingEntity);
            }
        }
        // Prevent normal onBlockDestroyed routine from wasting use on other blocks
        cir.setReturnValue(true);
    }
}

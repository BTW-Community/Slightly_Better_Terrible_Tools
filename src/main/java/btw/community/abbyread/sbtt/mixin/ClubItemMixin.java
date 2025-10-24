package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.categories.BlockSet;
import btw.community.abbyread.categories.BlockSide;
import btw.community.abbyread.sbtt.helper.InteractionHandler;
import btw.community.abbyread.sbtt.helper.InteractionHandler.InteractionType;
import btw.item.items.ClubItem;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClubItem.class)
public class ClubItemMixin {

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void abbyread$convertLooseToFirm(ItemStack stack, World world, int blockID, int x, int y, int z, EntityLivingBase usingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (world.isRemote || !(usingEntity instanceof EntityPlayer player)) return;

        Block block = Block.blocksList[blockID];
        if (block == null) return;

        int meta = world.getBlockMetadata(x, y, z);

        // Only react for club → dirtlike conversions
        if (BlockSet.isAll(block, meta, BlockType.DIRTLIKE, BlockType.LOOSE_DIRTLIKE)) {
            boolean didConvert = InteractionHandler.interact(stack, player, block, meta, world, x, y, z,
                    BlockSide.DOWN, InteractionType.PRIMARY_LEFT_CLICK);
            if (didConvert) {
                stack.damageItem(2, player);
                cir.setReturnValue(true);
            }
        }

        // Prevent default block-destroy behavior (so we don't waste durability)
        cir.setReturnValue(true);
    }
}
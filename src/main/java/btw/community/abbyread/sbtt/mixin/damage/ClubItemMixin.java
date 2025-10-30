package btw.community.abbyread.sbtt.mixin.damage;

import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.categories.ItemType;
import btw.community.abbyread.categories.ThisBlock;
import btw.community.abbyread.categories.ThisItem;
import btw.item.items.ClubItem;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClubItem.class)
public abstract class ClubItemMixin {

    @Unique
    private static final int FIRMING_COST = 2;
    @Unique
    private static final int PACKING_COST = 4;

    @Inject(
            method = "onBlockDestroyed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/ItemStack;damageItem(ILnet/minecraft/src/EntityLivingBase;)V"
            ),
            cancellable = true)
    private void sbtt$onBlockDestroyed(ItemStack stack, World world, int blockID, int x, int y, int z, EntityLivingBase usingEntity, CallbackInfoReturnable<Boolean> cir) {
        Block block = Block.blocksList[blockID];
        int metadata = world.getBlockMetadata(x, y, z);

        // Damage based on packing with bone club
        if (ThisBlock.is(BlockType.FIRM_DIRTLIKE, block, metadata) &&
                ThisItem.is(ItemType.BONE, stack) &&
                usingEntity instanceof EntityPlayer) {

            // Verify conversion was possible (no solid block above, for instance)
            if (!block.canConvertBlock(stack, world, x, y, z)) return;

            // Damage more if packing dirt
            stack.damageItem(PACKING_COST, usingEntity);
            cir.setReturnValue(true);

        } // Damage based on firming with either club
        else if (ThisBlock.is(BlockType.LOOSE_DIRTLIKE, block, metadata) &&
            usingEntity instanceof EntityPlayer) {
            stack.damageItem(FIRMING_COST, usingEntity);
            cir.setReturnValue(true);
        }

        // Proceed normally if it didn't fall into the conditions above
    }

}

package btw.community.abbyread.sbtt.mixin.damage;

import btw.block.BTWBlocks;
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
    @Unique
    private static final int PACKED_EARTH = 6; // a metadata value

    @Inject(
            method = "onBlockDestroyed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/ItemStack;damageItem(ILnet/minecraft/src/EntityLivingBase;)V"
            ),
            cancellable = true)
    private void determineAndApplyDamageAmount(ItemStack stack, World world, int blockID, int x, int y, int z, EntityLivingBase usingEntity, CallbackInfoReturnable<Boolean> cir) {
        Block block = Block.blocksList[blockID];
        int metadata = world.getBlockMetadata(x, y, z);

        if (!(usingEntity instanceof EntityPlayer)) return;

        if // Damage based on packing with bone club
        (
            (   // Starting from a firm dirtlike:
                ThisBlock.is(BlockType.FIRM_DIRTLIKE, block, metadata) ||
                // Starting from a packed earth slab:
                (blockID == BTWBlocks.dirtSlab.blockID && metadata == PACKED_EARTH)
            ) // Bone club is required.
            && ThisItem.is(ItemType.BONE, stack)
        ) {
            // Verify conversion was possible; assume it happened
            if (!block.canConvertBlock(stack, world, x, y, z)) return;

            // Apply damage to club
            stack.damageItem(PACKING_COST, usingEntity);

            // DEBUG:
            if (!world.isRemote) System.out.println("ClubItemMixin::stack.damageItem(" + PACKING_COST + ", usingEntity);");

            cir.setReturnValue(true);
        }
        // Damage based on firming with either club
        else if (ThisBlock.is(BlockType.LOOSE_DIRTLIKE, block, metadata)) {
            stack.damageItem(FIRMING_COST, usingEntity);

            // DEBUG:
            if (!world.isRemote) System.out.println("ClubItemMixin::stack.damageItem(" + FIRMING_COST + ", usingEntity);");

            cir.setReturnValue(true);
        }
        // Proceed normally if it didn't fall into the conditions above
    }

}

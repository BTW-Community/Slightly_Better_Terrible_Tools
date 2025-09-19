package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.items.ClubItem;
import btw.item.items.ToolItem;
import net.minecraft.src.Block;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClubItem.class)
public abstract class ClubItemMixin {

    /**
     * Prevents clubs from taking damage when used for digging.
     */
    @Inject(
            method = "onBlockDestroyed",
            at = @At("TAIL"),
            cancellable = true,
            remap = false
    )
    private void abbyread$preventDamage(
            ItemStack stack,
            World world,
            int blockID,
            int x, int y, int z,
            EntityLivingBase user,
            CallbackInfoReturnable<Boolean> cir
    ) {
        // Preserve the original result (packing or block conversion)
        boolean result = cir.getReturnValue();

        // Override to prevent any additional damage beyond what vanilla does
        cir.setReturnValue(result);
    }

    /**
     * Packs loose dirt/grass blocks into solid variants when hit with a club.
     */
    @Inject(
            method = "onBlockDestroyed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void abbyread$packLooseBlocks(
            ItemStack stack, World world, int iBlockID, int i, int j, int k,
            EntityLivingBase usingEntity,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (Block.blocksList[iBlockID].getBlockHardness(world, i, j, k) > 0F) {
            boolean packed = false;
            int thisBlockID = Block.blocksList[iBlockID].blockID;

            if (thisBlockID == BTWBlocks.looseSparseGrass.blockID) {
                world.setBlockAndMetadataWithNotify(i, j, k, Block.grass.blockID, 1);
                packed = true;
            }
            if (thisBlockID == BTWBlocks.looseSparseGrassSlab.blockID) {
                world.setBlockAndMetadataWithNotify(i, j, k, BTWBlocks.grassSlab.blockID, 1);
                packed = true;
            }
            if (thisBlockID == BTWBlocks.looseDirt.blockID) {
                world.setBlockWithNotify(i, j, k, Block.dirt.blockID);
                packed = true;
            }
            if (thisBlockID == BTWBlocks.looseDirtSlab.blockID) {
                world.setBlockWithNotify(i, j, k, BTWBlocks.dirtSlab.blockID);
                packed = true;
            }

            if (packed) {
                // Apply extra durability loss depending on club type
                if (stack.itemID == BTWItems.woodenClub.itemID) {
                    stack.damageItem(2, usingEntity);
                    System.out.println("2 uses applied to club");
                } else {
                    stack.damageItem(1, usingEntity);
                    System.out.println("1 use applied to club");
                }

                world.playSoundEffect(
                        i + 0.5D, j + 0.5D, k + 0.5D,
                        "step.gravel", 0.25F, world.rand.nextFloat() * 0.1F + 0.6F
                );

                cir.setReturnValue(true); // override return value
            }
            else {
                stack.damageItem(-2, usingEntity); // undo damage from irrelevant block
            }
        }
    }
}

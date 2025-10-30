package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.community.abbyread.categories.*;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInWorldManager.class)
public abstract class ClubItem_ItemInWorldManagerMixin {
    @Shadow
    public EntityPlayerMP thisPlayerMP;

    @Shadow
    public World theWorld;

    @Inject(method = "survivalTryHarvestBlock", at = @At("HEAD"), cancellable = true)
    private void clubConvertBlock(int x, int y, int z, int iFromSide,
                                  CallbackInfoReturnable<Boolean> cir) {
        if (theWorld.isRemote) return;

        EntityPlayerMP player = thisPlayerMP;
        int blockID = theWorld.getBlockId(x, y, z);
        Block block = Block.blocksList[blockID];

        if (block == null) return;

        ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem == null || ThisItem.isNot(ItemType.CLUB, heldItem)) return;

        // Check if this block can be converted by a club
        if (block.canConvertBlock(heldItem, theWorld, x, y, z)) {
            // Try to convert it
            boolean converted = block.convertBlock(heldItem, theWorld, x, y, z, iFromSide);

            if (converted) {
                // Determine damage based on what the block converted TO
                int newBlockID = theWorld.getBlockId(x, y, z);
                int newMetadata = theWorld.getBlockMetadata(x, y, z);
                int damageAmount = 1; // Default: firming costs 1 durability

                // Check if we packed (converted to packed earth slab)
                if (newBlockID == BTWBlocks.dirtSlab.blockID && newMetadata == 6) {
                    damageAmount = 2; // Packing costs 2 durability
                }

                // Damage the item - this handles breaking it if needed
                heldItem.damageItem(damageAmount, player);

                // If item is now broken (stackSize became 0), it will be removed from inventory
                // by vanilla logic, so we don't need to do anything special

                // Tell Minecraft we handled it
                cir.setReturnValue(true);
            }
        }
    }
}
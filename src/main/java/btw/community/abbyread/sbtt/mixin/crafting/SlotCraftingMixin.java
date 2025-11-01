package btw.community.abbyread.sbtt.mixin.crafting;

import net.minecraft.src.SlotCrafting;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Item;
import net.minecraft.src.IInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotCrafting.class)
public class SlotCraftingMixin {

    @Final
    @Shadow private IInventory craftMatrix;
    @Shadow private EntityPlayer thePlayer;

    // Track whether we already played sound for this craft action
    @Unique
    private boolean soundPlayed = false;

    @Inject(method = "onCrafting(Lnet/minecraft/src/ItemStack;)V", at = @At("HEAD"))
    private void playFirmDirtSound(ItemStack stack, CallbackInfo ci) {
        // Only run once per craft batch
        if (soundPlayed) return;

        boolean hasLooseDirt = false;
        boolean hasLooseDirtSlab = false;
        boolean hasDirtPiles = false;
        boolean hasSlime = false;

        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            ItemStack s = craftMatrix.getStackInSlot(i);
            if (s == null) continue;

            if (s.itemID == Item.slimeBall.itemID) hasSlime = true;
            if (s.itemID == btw.block.BTWBlocks.looseDirt.blockID) hasLooseDirt = true;
            if (s.itemID == btw.block.BTWBlocks.looseDirtSlab.blockID) hasLooseDirtSlab = true;
            if (s.itemID == btw.item.BTWItems.dirtPile.itemID) hasDirtPiles = true;
        }

        // Check for any of your firm dirt recipes
        if (hasSlime && (hasLooseDirt || hasLooseDirtSlab || hasDirtPiles)) {
            thePlayer.worldObj.playSoundAtEntity(thePlayer, "mob.slime.attack", 1.0f, 1.0f);
            soundPlayed = true;
        }
    }

    // Reset flag after crafting completes
    @Inject(method = "onPickupFromSlot(Lnet/minecraft/src/EntityPlayer;Lnet/minecraft/src/ItemStack;)V", at = @At("TAIL"))
    private void resetSoundFlag(EntityPlayer player, ItemStack stack, CallbackInfo ci) {
        soundPlayed = false;
    }
}

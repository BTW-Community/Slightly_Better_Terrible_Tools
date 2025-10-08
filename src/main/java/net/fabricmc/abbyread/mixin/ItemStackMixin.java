package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.sbtt.Convert;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Unique boolean DEBUG = false;

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    public void onBlockDestroyedIntercept(World world, int blockId, int x, int y, int z, EntityPlayer player, CallbackInfo ci) {
        if (world == null || player == null) return;
        ItemStack self = (ItemStack)(Object)this;

        Block block = Block.blocksList[blockId];
        if (block == null) return;

        // --- Compare block destruction speeds ---
        // Tool speed
        float toolSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);

        // Bare hand speed (simulate no tool by nulling out the held stack temporarily)
        ItemStack held = player.inventory.mainInventory[player.inventory.currentItem];
        player.inventory.mainInventory[player.inventory.currentItem] = null;
        float bareHandSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
        player.inventory.mainInventory[player.inventory.currentItem] = held;

        float multiplier = (bareHandSpeed > 0.0f) ? (toolSpeed / bareHandSpeed) : toolSpeed;

        boolean convertedBlock = Convert.justConverted;
        // --- Only damage the tool if it was faster than bare hands ---
        if (multiplier > 1.0f || convertedBlock) {
            Convert.justConverted = false;
            boolean didAffectTool = Item.itemsList[self.itemID].onBlockDestroyed(self, world, blockId, x, y, z, player);
            int itemDamage = Convert.itemDamageAmount; // in case it has been set to more damage temporarily
            Convert.itemDamageAmount = 1; // reset to the default after assigning to local.
            if (didAffectTool) {
                player.addStat(StatList.objectUseStats[self.itemID], itemDamage);
            }

            if (DEBUG) {
                System.out.printf(
                        "[Tool Check] %s vs %s at (%d,%d,%d): tool=%.3f, bare=%.3f, x%.2f%n",
                        self.getDisplayName(),
                        block.getLocalizedName(),
                        x, y, z,
                        toolSpeed, bareHandSpeed, multiplier
                );
            }
        }

        // Cancel original to prevent double-calling
        ci.cancel();
    }
}
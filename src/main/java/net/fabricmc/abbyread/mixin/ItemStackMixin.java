package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.categories.ItemTags;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {


    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    public void onBlockDestroyedIntercept(World world, int blockId, int x, int y, int z, EntityPlayer player, CallbackInfo ci) {
        ItemStack self = (ItemStack)(Object)this;

        Block block = Block.blocksList[blockId];
        if (block == null) {
            // Nothing to do if the block ID is invalid
            ci.cancel();
            return;
        }

        // --- Compare destruction speeds ---
        // Tool speed
        float toolSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);

        // Bare hand speed (simulate no tool by nulling out the held stack temporarily)
        ItemStack held = player.inventory.mainInventory[player.inventory.currentItem];
        player.inventory.mainInventory[player.inventory.currentItem] = null;
        float bareHandSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
        player.inventory.mainInventory[player.inventory.currentItem] = held;

        float multiplier = (bareHandSpeed > 0.0f) ? (toolSpeed / bareHandSpeed) : toolSpeed;

        // --- Only damage the tool if it was faster than bare hands ---
        if (multiplier > 1.0f) {
            boolean didAffectTool = Item.itemsList[self.itemID].onBlockDestroyed(self, world, blockId, x, y, z, player);

            if (didAffectTool) {
                player.addStat(StatList.objectUseStats[self.itemID], 1);
            }
/*
            // Optional: log for debugging
            System.out.printf(
                    "[Tool Check] %s vs %s at (%d,%d,%d): tool=%.3f, bare=%.3f, x%.2f%n",
                    self.getDisplayName(),
                    block.getLocalizedName(),
                    x, y, z,
                    toolSpeed, bareHandSpeed, multiplier
            );

 */
        // Damage override for converting to firm using club (because it's slower than punching)
        } else if (!world.isRemote && ItemTags.is(self, ItemTag.CLUB)) {
            int meta = world.getBlockMetadata(x, y, z);
            if (BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE)) {
                player.addStat(StatList.objectUseStats[self.itemID], 1);
                int itemDamage = 2;
                self.damageItem(itemDamage, player);
            }
        }

        // Cancel original to prevent double-calling
        ci.cancel();
    }
}
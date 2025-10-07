package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.categories.ItemTags;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Unique
    private static final boolean DEBUG = true; // Turn this on for debugging

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    public void onBlockDestroyedIntercept(World world, int blockId, int x, int y, int z, EntityPlayer player, CallbackInfo ci) {
        debug("=== onBlockDestroyedIntercept START ===");
        debug(String.format("Params: blockId=%d, pos=(%d,%d,%d), player=%s, world.isRemote=%s",
                blockId, x, y, z, player != null ? player.username : "null", world != null && world.isRemote));

        ItemStack self = (ItemStack) (Object) this;
        Block block = Block.blocksList[blockId];

        if (block == null) {
            debug("Invalid blockId: " + blockId + " (block is null). Cancelling.");
            ci.cancel();
            debug("=== onBlockDestroyedIntercept END (invalid block) ===");
            return;
        }

        debug("Valid block: " + block.getLocalizedName());

        // --- Compute metadata-aware tool effectiveness ---
        float toolSpeed = self.getItem().getStrVsBlock(self, world, block, x, y, z);
        debug("Tool speed computed: " + toolSpeed);

        float bareHandSpeed = computeBareHandSpeed(block, player, world, x, y, z);
        debug("Bare hand speed computed: " + bareHandSpeed);

        float multiplier = (bareHandSpeed > 0.0f) ? (toolSpeed / bareHandSpeed) : toolSpeed;
        debug(String.format("Multiplier = %.2f (toolSpeed/bareHandSpeed)", multiplier));

        debug(String.format("Tool=%s, Block=%s at (%d,%d,%d): toolSpeed=%.3f, bareHandSpeed=%.3f, multiplier=%.2f",
                self.getDisplayName(), block.getLocalizedName(), x, y, z, toolSpeed, bareHandSpeed, multiplier));

        // --- Only damage the tool if it is more effective than bare hands ---
        if (multiplier > 1.0f) {
            debug("Tool is more effective than bare hands. Applying normal damage...");
            damageToolNormally(self, player, blockId, world, x, y, z);
        }
        // --- Special case for CLUB, which is slower than punching ---
        else if (!world.isRemote && ItemTags.is(self, ItemTag.CLUB)) {
            debug("Tool is CLUB and slower than bare hands. Applying club damage override...");
            applyClubDamageOverride(self, player, block, world, x, y, z);
        } else {
            debug("Tool is not more effective and not a CLUB. No damage applied.");
        }

        // Prevent original method from executing
        ci.cancel();
        debug("Cancelled original onBlockDestroyed.");
        debug("=== onBlockDestroyedIntercept END ===");
    }

    // ---------- Helper Methods ----------

    @Unique
    private float computeBareHandSpeed(Block block, EntityPlayer player, World world, int x, int y, int z) {
        debug("computeBareHandSpeed() called for block: " + block.getLocalizedName());
        ItemStack held = player.inventory.mainInventory[player.inventory.currentItem];
        player.inventory.mainInventory[player.inventory.currentItem] = null;
        float speed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
        player.inventory.mainInventory[player.inventory.currentItem] = held;
        debug("Bare hand block hardness computed: " + speed);
        return speed;
    }

    @Unique
    private void damageToolNormally(ItemStack stack, EntityPlayer player, int blockId, World world, int x, int y, int z) {
        debug("damageToolNormally() for tool: " + stack.getDisplayName());
        boolean didAffectTool = Item.itemsList[stack.itemID].onBlockDestroyed(stack, world, blockId, x, y, z, player);
        debug("didAffectTool result: " + didAffectTool);
        if (didAffectTool) {
            player.addStat(StatList.objectUseStats[stack.itemID], 1);
            debug("Tool damaged normally and stat updated for: " + stack.getDisplayName());
        } else {
            debug("Tool did NOT affect onBlockDestroyed.");
        }
    }

    @Unique
    private void applyClubDamageOverride(ItemStack stack, EntityPlayer player, Block block, World world, int x, int y, int z) {
        debug("applyClubDamageOverride() for block: " + block.getLocalizedName());
        int meta = world.getBlockMetadata(x, y, z);
        debug("Block metadata: " + meta);

        if (BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE)) {
            debug("Block matches DIRTLIKE or LOOSE_DIRTLIKE. Applying club damage override.");
            int itemDamage = 2; // Club damage when converting to firm
            stack.damageItem(itemDamage, player);
            player.addStat(StatList.objectUseStats[stack.itemID], 1);
            debug("Club applied manual damage: " + stack.getDisplayName() + " for block " + block.getLocalizedName());
        } else {
            debug("Block is NOT DIRTLIKE/LOOSE_DIRTLIKE. No club damage applied.");
        }
    }

    @Unique
    private void debug(String message) {
        if (DEBUG) System.out.println("[ItemStackMixin] " + message);
    }
}

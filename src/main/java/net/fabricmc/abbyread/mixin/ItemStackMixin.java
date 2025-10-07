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
    private static final boolean DEBUG = false;

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    public void onBlockDestroyedIntercept(World world, int blockId, int x, int y, int z, EntityPlayer player, CallbackInfo ci) {
        ItemStack self = (ItemStack) (Object) this;
        Block block = Block.blocksList[blockId];

        if (block == null) {
            debug("Invalid blockId: " + blockId);
            ci.cancel();
            return;
        }

        // --- Compute metadata-aware tool effectiveness ---
        float toolSpeed = getEffectiveBlockHardness(self, world, x, y, z, player);
        float bareHandSpeed = computeBareHandSpeed(block, player, world, x, y, z);

        float multiplier = (bareHandSpeed > 0.0f) ? (toolSpeed / bareHandSpeed) : toolSpeed;
        debug(String.format("Tool=%s, Block=%s at (%d,%d,%d): toolSpeed=%.3f, bareHandSpeed=%.3f, multiplier=%.2f",
                self.getDisplayName(),
                block.getLocalizedName(),
                x, y, z,
                toolSpeed, bareHandSpeed, multiplier
        ));

        // --- Only damage the tool if it is more effective than bare hands ---
        if (multiplier > 1.0f) {
            damageToolNormally(self, player, blockId, world, x, y, z);
        }
        // --- Special case for CLUB, which is slower than punching ---
        else if (!world.isRemote && ItemTags.is(self, ItemTag.CLUB)) {
            applyClubDamageOverride(self, player, block, world, x, y, z);
        }

        // Prevent original method from executing
        ci.cancel();
    }

    // ---------- Helper Methods ----------

    @Unique
    private float computeBareHandSpeed(Block block, EntityPlayer player, World world, int x, int y, int z) {
        ItemStack held = player.inventory.mainInventory[player.inventory.currentItem];
        player.inventory.mainInventory[player.inventory.currentItem] = null;
        float speed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
        player.inventory.mainInventory[player.inventory.currentItem] = held;
        return speed;
    }

    @Unique
    private float getEffectiveBlockHardness(ItemStack stack, World world, int x, int y, int z, EntityPlayer player) {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        int meta = world.getBlockMetadata(x, y, z);

        if (block == null) return 0.0f;

        float baseHardness = block.getPlayerRelativeBlockHardness(player, world, x, y, z);

        // --- Adjust for wood chisel ---
        if (ItemTags.isAll(stack, ItemTag.WOOD, ItemTag.CHISEL)) {
            if (BlockTags.is(block, meta, BlockTag.FIRM) &&
                    (BlockTags.is(block, meta, BlockTag.DIRT) || BlockTags.isAll(block, meta, BlockTag.GRASS, BlockTag.SPARSE))) {
                return baseHardness * 1.5f;
            }
        }

        // --- Adjust for stone chisel ---
        if (ItemTags.isAll(stack, ItemTag.STONE, ItemTag.CHISEL)) {
            if (BlockTags.is(block, meta, BlockTag.GRASS)) {
                return baseHardness * 2.0f;
            }
        }

        // --- Adjust for club ---
        if (ItemTags.is(stack, ItemTag.CLUB)) {
            if (BlockTags.isAll(block, meta, BlockTag.LOOSE_DIRTLIKE, BlockTag.DIRTLIKE)) {
                return baseHardness * 0.75f;
            }
        }

        // Default to vanilla hardness
        return baseHardness;
    }

    @Unique
    private void damageToolNormally(ItemStack stack, EntityPlayer player, int blockId, World world, int x, int y, int z) {
        boolean didAffectTool = Item.itemsList[stack.itemID].onBlockDestroyed(stack, world, blockId, x, y, z, player);
        if (didAffectTool) {
            player.addStat(StatList.objectUseStats[stack.itemID], 1);
            debug("Tool damaged normally: " + stack.getDisplayName());
        }
    }

    @Unique
    private void applyClubDamageOverride(ItemStack stack, EntityPlayer player, Block block, World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE)) {
            int itemDamage = 2; // Club damage when converting to firm
            stack.damageItem(itemDamage, player);
            player.addStat(StatList.objectUseStats[stack.itemID], 1);
            debug("Club applied manual damage: " + stack.getDisplayName() + " for block " + block.getLocalizedName());
        }
    }

    @Unique
    private void debug(String message) {
        if (DEBUG) System.out.println("[ItemStackMixin] " + message);
    }
}

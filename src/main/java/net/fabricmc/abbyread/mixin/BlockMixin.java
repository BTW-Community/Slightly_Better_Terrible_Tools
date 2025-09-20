package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.community.abbyread.ToolState;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Unique
    private int getLoosenedBlockId(int fromBlockId) {
        if (fromBlockId == Block.dirt.blockID) {
            return BTWBlocks.looseDirt.blockID;
        } else if (fromBlockId == BTWBlocks.dirtSlab.blockID) {
            return BTWBlocks.looseDirtSlab.blockID;
        } else if (fromBlockId == Block.grass.blockID) {
            return BTWBlocks.looseSparseGrass.blockID;
        } else if (fromBlockId == BTWBlocks.grassSlab.blockID) {
            return BTWBlocks.looseSparseGrassSlab.blockID;
        }
        return -1; // no match
    }

    /**
     * Replaces the vanilla drop behavior if the player is using a pointy stick.
     */
    @Inject(
            method = "dropBlockAsItemWithChance",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventDropsIfLoosened(World world, int x, int y, int z, int metadata, float chance, int fortune, CallbackInfo ci) {
        ItemStack tool = ToolState.getCurrentTool();
        if (tool != null && tool.getItem() == BTWItems.pointyStick) {
            int blockID = world.getBlockId(x, y, z);
            int toBlockID = getLoosenedBlockId(blockID);

            if (toBlockID != -1) {
                world.setBlockWithNotify(x, y, z, toBlockID); // straight replacement
                ci.cancel(); // prevent vanilla drops
            }
        }
    }
}

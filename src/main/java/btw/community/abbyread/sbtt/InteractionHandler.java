package btw.community.abbyread.sbtt;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.categories.ItemTags;
import btw.community.abbyread.categories.ItemTag;
import btw.item.items.ChiselItemWood;
import btw.item.items.ShovelItemStone;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import java.util.Set;

public class InteractionHandler {

    public static boolean handleBlockDestroyed(ItemStack stack, Block block, int meta, World world, int x, int y, int z) {
        Set<BlockTag> blockTags = BlockTags.of(block, meta);
        Set<ItemTag> itemTags = ItemTags.getTags(stack);

        // Example: pointy stick loosens dirtlike blocks
        if (itemTags.contains(ItemTag.CHISEL) && itemTags.contains(ItemTag.WOOD)
                && blockTags.contains(BlockTag.DIRTLIKE)
                && !blockTags.contains(BlockTag.LOOSE)) {

            world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
            return true;
        }

        return false; // not handled
    }


    /**
     * Called when a block is destroyed with a tool that may be improper.
     * Returns true if the event was handled and the original method should be cancelled.
     */
    public static boolean handleBlockBreak(EntityPlayer player, World world, Block block, int meta, int x, int y, int z) {
        if (player == null || block == null) return false;

        ItemStack held = player.inventory.getCurrentItem();
        if (held == null) return false;

        Set<BlockTag> categories = BlockTags.of(block, meta);

        // --- Stone shovel effect ---
        if (held.getItem() instanceof ShovelItemStone) {
            // Loosen nearby blocks? For now just do the "improper tool" effect
            world.playAuxSFX(BTWEffectManager.BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID,
                    x, y, z, world.getBlockId(x, y, z) + (meta << 12));
            block.dropComponentItemsOnBadBreak(world, x, y, z, meta, 1F);
            return true;
        }

        // --- Pointy stick (wood chisel) loosens dirtlike blocks ---
        if (held.getItem() instanceof ChiselItemWood && categories.contains(BlockTag.DIRTLIKE)) {
            if (!world.isRemote) {
                world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
                world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
            }
            return true;
        }

        return false; // not handled
    }

    /**
     * Called from canConvertBlock mixins.
     * Returns true if the block can be converted by the held item.
     */
    public static boolean handleCanConvert(ItemStack stack, World world, Block block, int x, int y, int z) {
        if (stack == null || block == null) return false;

        // Wood chisel can convert dirtlike blocks
        if (stack.getItem() instanceof ChiselItemWood) {
            Set<BlockTag> categories = BlockTags.of(block, world.getBlockMetadata(x, y, z));
            return categories.contains(BlockTag.DIRTLIKE);
        }

        // Add additional checks for other tools later
        return false;
    }

    /**
     * Called from convertBlock mixins.
     * Returns true if the conversion was handled.
     */
    public static boolean handleConvert(ItemStack stack, World world, Block block, int x, int y, int z, int fromSide) {
        if (stack == null || block == null) return false;

        // Pointy stick / wood chisel effect
        if (stack.getItem() instanceof ChiselItemWood) {
            Set<BlockTag> categories = BlockTags.of(block, world.getBlockMetadata(x, y, z));
            if (categories.contains(BlockTag.DIRTLIKE)) {
                if (!world.isRemote) {
                    world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
                    world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
                }
                return true;
            }
        }

        // Could add stone chisel converting grass to sparse, etc.

        return false;
    }
}

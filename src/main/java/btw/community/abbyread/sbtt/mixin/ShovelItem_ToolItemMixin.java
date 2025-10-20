package btw.community.abbyread.sbtt.mixin;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.categories.ItemTags;
import btw.community.abbyread.sbtt.Convert;
import btw.item.items.ToolItem;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public class ShovelItem_ToolItemMixin {

    @Inject(method = "onItemUse", at = @At("RETURN"), cancellable = true)
    private void firmDirtlikes(ItemStack stack, EntityPlayer player, World world,
                               int x, int y, int z, int iFacing,
                               float fClickX, float fClickY, float fClickZ,
                               CallbackInfoReturnable<Boolean> cir) {
        if (ItemTags.isNot(stack, ItemTag.SHOVEL)) return;

        int blockID = world.getBlockId(x, y, z);
        Block clickedBlock = Block.blocksList[blockID];
        int clickedBlockMeta = world.getBlockMetadata(x, y, z);

        if (BlockTags.isAll(clickedBlock, clickedBlockMeta, BlockTag.LOOSE_DIRTLIKE, BlockTag.DIRTLIKE)) {
            if (Convert.firm(stack, clickedBlock, clickedBlockMeta, world, x, y, z, iFacing)) {
                if (!world.isRemote) {
                    stack.damageItem(1, player);
                }
                cir.setReturnValue(true); // Cancel vanilla use only if we firmed the block
            }
        }
    }

    @Inject(method = "onItemUse", at = @At("RETURN"), cancellable = true, remap = false)
    private void pushDirtBlock(ItemStack stack, EntityPlayer player, World world,
                               int x, int y, int z, int side,
                               float hitX, float hitY, float hitZ,
                               CallbackInfoReturnable<Boolean> cir) {

        // Only Iron Shovel or better
        if (stack == null || ItemTags.isNot(stack, ItemTag.SHOVEL)) return;
        if (ItemTags.is(stack, ItemTag.STONE)) return;

        if (world.isRemote) return; // server-side only

        // Push direction: opposite the clicked face
        int dx = -Facing.offsetsXForSide[side];
        int dy = -Facing.offsetsYForSide[side];
        int dz = -Facing.offsetsZForSide[side];

        int targetX = x + dx;
        int targetY = y + dy;
        int targetZ = z + dz;

        // Check world height bounds
        if (targetY < 0 || targetY >= world.getHeight()) return;

        int clickedID = world.getBlockId(x, y, z);
        int targetID = world.getBlockId(targetX, targetY, targetZ);

        // Only push if both clicked and target blocks are dirt
        if (clickedID == Block.dirt.blockID && targetID == Block.dirt.blockID) {
            // Convert target block to aestheticEarth variant 6
            world.setBlock(targetX, targetY, targetZ, BTWBlocks.aestheticEarth.blockID, 6, 3);

            // Remove clicked block to simulate movement
            world.setBlockToAir(x, y, z);

            // Notify clients
            world.markBlockForUpdate(x, y, z);
            world.markBlockForUpdate(targetX, targetY, targetZ);

            // Play tilling effect
            world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);

            Convert.justConverted = true;

            // Damage the shovel slightly
            stack.damageItem(1, player);

            // Cancel vanilla behavior only after successful push
            cir.setReturnValue(true);

        } else {
            // Could not push: play dirt-step sound for feedback
            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5,
                    "tile.grass.step", 0.5F, 0.8F);
        }
    }
}

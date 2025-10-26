package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.BlockSide;
import btw.community.abbyread.categories.ItemType;
import btw.community.abbyread.categories.ItemSet;
import btw.community.abbyread.sbtt.helper.InteractionHandler;
import btw.community.abbyread.sbtt.helper.InteractionHandler.InteractionType;
import btw.item.items.ToolItem;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public class ShovelItem_ToolItemMixin {

    @Inject(method = "onItemUse", at = @At("RETURN"), cancellable = true)
    private void shovelRightClickOnBlock(ItemStack stack, EntityPlayer player, World world,
                                         int x, int y, int z, int iFacing,
                                         float fClickX, float fClickY, float fClickZ,
                                         CallbackInfoReturnable<Boolean> cir) {

        // Only continue if this is actually a shovel
        if (ItemSet.isNot(stack, ItemType.SHOVEL)) return;

        // Prevent conversion when placing in a block
        if (player.isUsingSpecialKey()) return;

        int blockID = world.getBlockId(x, y, z);
        Block clickedBlock = Block.blocksList[blockID];
        if (clickedBlock == null) return;

        int meta = world.getBlockMetadata(x, y, z);
        BlockSide side = BlockSide.fromId(iFacing);

        // Call your new conversion system (SECONDARY right-click)
        boolean didConvert = InteractionHandler.interact(stack, player, clickedBlock, meta, world,
                x, y, z, side, InteractionType.SECONDARY_RIGHT_CLICK);

        // If conversion happened, override return
        if (didConvert) {
            cir.setReturnValue(true);
        }
    }

}
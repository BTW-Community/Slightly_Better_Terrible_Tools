package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.categories.ItemTags;
import btw.community.abbyread.sbtt.Convert;
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
        if (ItemTags.isNot(stack, ItemTag.SHOVEL)) return;
        int blockID = world.getBlockId(x, y, z);
        Block clickedBlock = Block.blocksList[blockID];
        if (clickedBlock == null) return;

        int clickedBlockMeta = world.getBlockMetadata(x, y, z);
        cir.setReturnValue(Convert.trySecondaryConvert(stack, player, clickedBlock, clickedBlockMeta, world, x, y, z, iFacing));
    }
}

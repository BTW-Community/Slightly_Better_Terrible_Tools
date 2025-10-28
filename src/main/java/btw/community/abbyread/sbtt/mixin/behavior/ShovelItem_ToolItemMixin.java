package btw.community.abbyread.sbtt.mixin.behavior;


import btw.community.abbyread.categories.ItemSet;
import btw.community.abbyread.categories.ItemTag;
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
public abstract class ShovelItem_ToolItemMixin {

    @Inject(method = "onItemUse", at = @At("RETURN"))
    private void shovelRightClickOnBlock(ItemStack stack, EntityPlayer player, World world,
                                         int x, int y, int z, int iFacing,
                                         float fClickX, float fClickY, float fClickZ,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (ItemSet.hasNot(stack, ItemTag.SHOVEL)) return;
        int blockID = world.getBlockId(x, y, z);
        Block clickedBlock = Block.blocksList[blockID];
        if (clickedBlock == null) return;

        int clickedBlockMeta = world.getBlockMetadata(x, y, z);

        // Convert loose dirtlike to firm dirtlike with 2 damage to shovel
        // or...
        // Convert firm dirtlike to packed earth slab if targeting topside.  2 damage.
        // or...
        // Convert packed earth slab and firm dirtlike below to air and packed earth, respectively.

    }
}
package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.ThisItem;
import btw.community.abbyread.categories.ItemType;
import btw.community.abbyread.categories.QualifiedBlock;
import btw.community.abbyread.sbtt.util.SwapContext;
import btw.item.items.ToolItem;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(ToolItem.class)
public abstract class ShovelItem_ToolItemMixin {

    @Unique
    private static Map<QualifiedBlock, QualifiedBlock> FROM_TO;

    @Unique
    private static Map<QualifiedBlock, QualifiedBlock> getFromToMap() {
        if (FROM_TO == null) {
            FROM_TO = new HashMap<>();
            FROM_TO.put(
                    new QualifiedBlock(BTWBlocks.looseDirt.blockID, 0),
                    new QualifiedBlock(Block.dirt.blockID, 0)
            );
            FROM_TO.put(
                    new QualifiedBlock(BTWBlocks.looseDirtSlab.blockID, 0),
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, 0)
            );
            FROM_TO.put(
                    new QualifiedBlock(BTWBlocks.looseSparseGrass.blockID, 0),
                    new QualifiedBlock(Block.grass.blockID, 1)
            );
            FROM_TO.put(
                    new QualifiedBlock(BTWBlocks.looseSparseGrassSlab.blockID, 0),
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, 2)
            );
        }
        return FROM_TO;
    }

    @Inject(method = "onItemUse", at = @At("HEAD"), cancellable = true)
    private void shovelRightClickOnBlock(ItemStack stack, EntityPlayer player, World world,
                                         int x, int y, int z, int iFacing,
                                         float fClickX, float fClickY, float fClickZ,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (ThisItem.isNot(stack, ItemType.SHOVEL)) return;

        // Skip swapping logic entirely if the special key is held
        if (player.isUsingSpecialKey()) return;

        int blockID = world.getBlockId(x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);
        QualifiedBlock from = new QualifiedBlock(blockID, metadata);
        QualifiedBlock to = getFromToMap().get(from);

        if (to == null) return;

        SwapContext ctx = new SwapContext(stack, player, world, x, y, z);
        swapTo(to.blockID, to.metadata, ctx);

        // Tell Minecraft the use was successful so client plays SFX/animation
        cir.setReturnValue(true);
    }

    @Unique
    private static void swapTo(int blockID, int metadata, SwapContext ctx) {

        ctx.world.setBlockAndMetadataWithNotify(ctx.x, ctx.y, ctx.z, blockID, metadata);
        ctx.stack.damageItem(2, ctx.player);

        // Play tilled dirt effect client-side
        if (ctx.world.isRemote) {
            ctx.world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, ctx.x, ctx.y, ctx.z, 0);
        }
    }
}

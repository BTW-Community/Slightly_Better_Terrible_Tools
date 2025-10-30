package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueEarthBlock;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.*;
import btw.community.abbyread.sbtt.util.SwapContext;
import btw.item.items.ToolItem;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;
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
    private static final int TRY_PACKING = 0; // a blockID sentinel value

    @Unique
    private static final int PACKED_EARTH = 6; // the metadata value for it

    @Unique
    private static final int FIRMING_COST = 2;

    @Unique
    private static final int PACKING_COST = 4;

    @Unique
    private static Map<QualifiedBlock, QualifiedBlock> FROM_TO;

    @Unique
    private static Map<QualifiedBlock, QualifiedBlock> getFromToMap() {
        if (FROM_TO == null) {
            FROM_TO = new HashMap<>();
            FROM_TO.put( // Firm loose dirt blocks
                    new QualifiedBlock(BTWBlocks.looseDirt.blockID, 0), // loose dirt block
                    new QualifiedBlock(Block.dirt.blockID, 0) // firm dirt block
            );
            FROM_TO.put( // Firm loose dirt slabs
                    new QualifiedBlock(BTWBlocks.looseDirtSlab.blockID, 0), // loose dirt slab
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, 0) // firm dirt slab
            );
            FROM_TO.put( // Firm loose sparse grass blocks
                    new QualifiedBlock(BTWBlocks.looseSparseGrass.blockID, 0), // loose sparse grass
                    new QualifiedBlock(Block.grass.blockID, 1) // sparse grass block
            );
            FROM_TO.put( // Firm loose sparse grass slabs
                    new QualifiedBlock(BTWBlocks.looseSparseGrassSlab.blockID, 0), // as named
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, 2) // sparse grass slab
            );
            FROM_TO.put( // Pack firm dirt downwards
                    new QualifiedBlock(Block.dirt.blockID, 0), // firm dirt
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, 6) // packed earth slab
            );
            FROM_TO.put( // Pack fully-grown grass blocks downward
                    new QualifiedBlock(Block.grass.blockID, 0), // fully-grown grass
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, 6) // packed earth slab
            );
            FROM_TO.put( // Pack sparse grass blocks downward
                    new QualifiedBlock(Block.grass.blockID, 1), // sparse grass
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, 6) // packed earth slab
            );
            FROM_TO.put( // Pack downward; needs extra check for lower neighbor to be a firm dirtlike block
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, 6), // packed earth slab
                    new QualifiedBlock(TRY_PACKING, 0) // sentinel value to indicate making air
            );
        }
        return FROM_TO;
    }

    @Inject(method = "onItemUse", at = @At("HEAD"), cancellable = true)
    private void shovelRightClickOnBlock(ItemStack stack, EntityPlayer player, World world,
                                         int x, int y, int z, int iFacing,
                                         float fClickX, float fClickY, float fClickZ,
                                         CallbackInfoReturnable<Boolean> cir) {
        // Only handle shovels in this mixin
        if (ThisItem.isNot(ItemType.SHOVEL, stack)) return;

        int blockID = world.getBlockId(x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);

        // Destroy tall grass as if punching it
        if (blockID == Block.tallGrass.blockID) {
            if (!world.isRemote) world.destroyBlock(x, y, z, false);
            player.addExhaustion(0.025f);
            cir.setReturnValue(true);
        }

        // Skip block conversion logic entirely if the special key is held
        if (player.isUsingSpecialKey()) return;

        QualifiedBlock from = new QualifiedBlock(blockID, metadata);
        QualifiedBlock to = getFromToMap().get(from);

        // Return early if no possible conversion found via getFromToMap
        if (to == null) return;

        // Require air above the clicked-on block in order to pack firm dirtlike into slab
        Block block = Block.blocksList[from.blockID];
        if (block != null && ThisBlock.is(BlockType.FIRM_DIRTLIKE, block, metadata)) {
            // Prevent packing downward if there is a solid block above
            if (WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, x, y + 1, z, 0) ) return;
        }

        if (to.blockID == TRY_PACKING) { // triggered if clicked block was a packed earth slab
            // Check lower neighbor to determine if/how to pack

            int lowerNeighborID = world.getBlockId(x, y - 1, z);
            int lowerNeighborMetadata = world.getBlockMetadata(x, y - 1, z);
            Block lowerNeighbor = Block.blocksList[lowerNeighborID];

            if (lowerNeighbor == null) return;

            // Pack downward if the lower neighbor is a firm dirtlike block
            if (
                    ThisItem.isNot(ItemType.STONE, stack) &&
                    ThisBlock.isAnd(
                            BlockType.FIRM_DIRTLIKE,
                            BlockType.CUBE,
                            lowerNeighbor, lowerNeighborMetadata)
                ) {
                // Prepare conversion context for lower neighbor block
                SwapContext lowerCtx = new SwapContext(stack, player, world, x, y - 1, z);

                // Set clicked on block to air
                world.setBlockToAir(x, y, z);

                // Make the lower neighbor a packed earth cube
                convertBlock(
                        BTWBlocks.aestheticEarth.blockID,
                        AestheticOpaqueEarthBlock.SUBTYPE_PACKED_EARTH,
                        lowerCtx, PACKING_COST);
                cir.setReturnValue(true);
            }

            // Firm the dirtlike block below (basically, to prepare for packing next round)
            else if (ThisBlock.isAnd(BlockType.LOOSE_DIRTLIKE, BlockType.CUBE, lowerNeighbor, lowerNeighborMetadata)) {
                // Change lower block to firm dirt; leave upper block as-is
                SwapContext lowerCtx = new SwapContext(stack, player, world, x, y - 1, z);
                convertBlock(Block.dirt.blockID, 0, lowerCtx, FIRMING_COST);
                cir.setReturnValue(true);
            }

        }
        else { // Firm or pack the dirtlike block that was clicked
            SwapContext ctx = new SwapContext(stack, player, world, x, y, z);
            if (to.metadata == PACKED_EARTH) {
                // Only pack if shovel is better than stone
                if (ThisItem.isNot(ItemType.STONE, stack)) {
                    convertBlock(to.blockID, to.metadata, ctx, PACKING_COST);
                    cir.setReturnValue(true);
                }
            }
            else {
                convertBlock(to.blockID, to.metadata, ctx, FIRMING_COST);
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private static void convertBlock(int toBlockID, int toMetadata, SwapContext ctx, int damageToItem) {

        ctx.world.setBlockAndMetadataWithNotify(ctx.x, ctx.y, ctx.z, toBlockID, toMetadata);
        ctx.stack.damageItem(damageToItem, ctx.player);

        // Play tilled dirt effect client-side
        if (ctx.world.isRemote) {
            ctx.world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, ctx.x, ctx.y, ctx.z, 0);
        }
    }

}

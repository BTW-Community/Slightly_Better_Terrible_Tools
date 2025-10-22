package btw.community.abbyread.sbtt;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.*;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import java.util.*;

/**
 * Unified item-on-block interaction handler with caching.
 * Consolidates: combo matching, conversion logic, and result application.
 */
public class InteractionHandler {

    // Cache key: (itemId, blockId, metadata, side, clickType) -> InteractionResult
    private static final Map<InteractionCacheKey, Optional<InteractionResult>> INTERACTION_CACHE =
            new LinkedHashMap<>() {
                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > 512; // LRU cache, max 512 entries
                }
            };

    private static boolean justConverted = false;

    // ===== Cache Key =====

    @SuppressWarnings("PatternVariableCanBeUsed")
    private static class InteractionCacheKey {
        final int itemId;
        final int blockId;
        final int metadata;
        final BlockSide side;
        final InteractionType type;

        InteractionCacheKey(ItemStack stack, Block block, int metadata, BlockSide side, InteractionType type) {
            this.itemId = stack != null ? stack.getItem().itemID : -1;
            this.blockId = block != null ? block.blockID : -1;
            this.metadata = metadata;
            this.side = side;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InteractionCacheKey)) return false;
            InteractionCacheKey key = (InteractionCacheKey) o;
            return itemId == key.itemId && blockId == key.blockId &&
                    metadata == key.metadata && side == key.side && type == key.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemId, blockId, metadata, side, type);
        }
    }

    // ===== Interaction Type =====

    public enum InteractionType {
        PRIMARY_LEFT_CLICK,
        SECONDARY_RIGHT_CLICK
    }

    // ===== Interaction Result =====

    private static class InteractionResult {
        final InteractionType type;
        final Block resultBlock;
        final int resultMeta;
        final ConversionAction action;

        InteractionResult(InteractionType type, Block resultBlock, int resultMeta, ConversionAction action) {
            this.type = type;
            this.resultBlock = resultBlock;
            this.resultMeta = resultMeta;
            this.action = action;
        }
    }

    @FunctionalInterface
    private interface ConversionAction {
        boolean apply(ItemStack stack, EntityPlayer player, Block oldBlock, int oldMeta,
                      World world, int x, int y, int z, BlockSide side);
    }

    // ===== Unified Definition of All Interactions =====

    private static final List<InteractionDefinition> INTERACTIONS = List.of(
            // PRIMARY: Wood chisel + firm dirtlike/grass -> loosen
            new InteractionDefinition(
                    InteractionType.PRIMARY_LEFT_CLICK,
                    Set.of(ItemTag.WOOD, ItemTag.CHISEL),
                    Set.of(BlockTag.FIRM, BlockTag.DIRTLIKE, BlockTag.GRASS),
                    null,
                    InteractionHandler::loosen
            ),
            // PRIMARY: Stone chisel + grass -> sparsen
            new InteractionDefinition(
                    InteractionType.PRIMARY_LEFT_CLICK,
                    Set.of(ItemTag.STONE, ItemTag.CHISEL),
                    Set.of(BlockTag.GRASS),
                    null,
                    InteractionHandler::sparsen
            ),
            // PRIMARY: Club + dirtlike -> firm
            new InteractionDefinition(
                    InteractionType.PRIMARY_LEFT_CLICK,
                    Set.of(ItemTag.CLUB),
                    Set.of(BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE),
                    null,
                    InteractionHandler::firm
            ),
            // SECONDARY: Shovel + loose dirtlike -> firm
            new InteractionDefinition(
                    InteractionType.SECONDARY_RIGHT_CLICK,
                    Set.of(ItemTag.SHOVEL),
                    Set.of(BlockTag.LOOSE_DIRTLIKE),
                    null,
                    InteractionHandler::firm
            ),
            // SECONDARY: Iron+ shovel + dirt, top side only -> pack
            new InteractionDefinition(
                    InteractionType.SECONDARY_RIGHT_CLICK,
                    Set.of(ItemTag.SHOVEL),
                    Set.of(BlockTag.DIRTLIKE, BlockTag.FIRM, BlockTag.CUBE),
                    Set.of(BlockSide.UP),
                    InteractionHandler::pack
            ),
            // SECONDARY: Shears + tall grass -> harvest
            new InteractionDefinition(
                    InteractionType.PRIMARY_LEFT_CLICK,
                    Set.of(ItemTag.SHEARS),
                    Set.of(BlockTag.TALL_GRASS),
                    null,
                    (stack, player, block, meta, world, x, y, z, side) -> true // no-op, game handles it
            ),
            // SECONDARY: Hoe + grass -> till
            new InteractionDefinition(
                    InteractionType.PRIMARY_LEFT_CLICK,
                    Set.of(ItemTag.HOE),
                    Set.of(BlockTag.GRASS),
                    null,
                    (stack, player, block, meta, world, x, y, z, side) -> true // no-op, game handles it
            ),
            // SECONDARY: Hoe + dirt -> till
            new InteractionDefinition(
                    InteractionType.PRIMARY_LEFT_CLICK,
                    Set.of(ItemTag.HOE),
                    Set.of(BlockTag.DIRT),
                    null,
                    (stack, player, block, meta, world, x, y, z, side) -> true // no-op, game handles it
            )
    );

    @SuppressWarnings("SimplifyStreamApiCallChains")
    private static class InteractionDefinition {
        final InteractionType type;
        final Set<ItemTag> itemTags;
        final Set<BlockTag> blockTags;
        final Set<BlockSide> validSides;
        final ConversionAction action;

        InteractionDefinition(InteractionType type, Set<ItemTag> itemTags, Set<BlockTag> blockTags,
                              Set<BlockSide> validSides, ConversionAction action) {
            this.type = type;
            this.itemTags = itemTags;
            this.blockTags = blockTags;
            this.validSides = validSides;
            this.action = action;
        }

        boolean matches(ItemStack stack, Block block, int meta, BlockSide side) {
            // Item must match
            if (stack == null || !itemTags.stream().anyMatch(tag -> ItemTags.is(stack, tag))) {
                return false;
            }
            // Block must match
            if (block == null || !blockTags.stream().anyMatch(tag -> BlockTags.is(block, meta, tag))) {
                return false;
            }
            // Side must match (if specified)
            return validSides == null || side == null || validSides.contains(side);
        }
    }

    // ===== Public API =====

    /**
     * Check if an interaction can occur without executing it.
     */
    public static boolean canInteract(ItemStack stack, Block block, int meta, InteractionType type) {
        return findInteraction(stack, block, meta, null, type) != null;
    }

    /**
     * Execute an interaction (primary use, left-click, etc.)
     */
    public static boolean interact(ItemStack stack, EntityPlayer player, Block block, int meta,
                                   World world, int x, int y, int z, BlockSide side, InteractionType type) {
        if (stack == null || block == null) return false;

        justConverted = false;
        InteractionDefinition def = findInteraction(stack, block, meta, side, type);
        if (def == null) return false;

        return def.action.apply(stack, player, block, meta, world, x, y, z, side);
    }

    public static boolean hasJustConverted() {
        return justConverted;
    }

    @SuppressWarnings("unused")
    public static void clearCache() {
        INTERACTION_CACHE.clear();
    }

    // ===== Internal Helpers =====

    /**
     * Find matching interaction definition with caching.
     */
    private static InteractionDefinition findInteraction(ItemStack stack, Block block, int meta,
                                                         BlockSide side, InteractionType type) {
        // Try to find matching definition (bypass cache for now, could add later)
        for (InteractionDefinition def : INTERACTIONS) {
            if (def.type == type && def.matches(stack, block, meta, side)) {
                return def;
            }
        }
        return null;
    }

    /**
     * Common block transformation and notification.
     */
    private static boolean swapBlock(World world, int x, int y, int z,
                                     Block oldBlock, int oldMeta, Block newBlock, int newMeta) {
        if (newBlock == null) return false;
        boolean swapped = false;

        if (newBlock != oldBlock || newMeta != oldMeta) {
            world.setBlockAndMetadataWithNotify(x, y, z, newBlock.blockID, newMeta);
            swapped = true;
        }

        if (!world.isRemote && swapped) {
            world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
        }

        return swapped;
    }

    // ===== Conversion Actions =====

    private static boolean loosen(ItemStack stack, EntityPlayer player, Block block,
                                  int meta, World world, int x, int y, int z, BlockSide side) {
        if (!BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.FIRM)
                || BlockTags.is(block, meta, BlockTag.LOOSE_DIRTLIKE)) return false;

        Block newBlock = null;
        int newMeta = meta;

        if (BlockTags.is(block, meta, BlockTag.DIRT)) {
            newBlock = BlockTags.is(block, meta, BlockTag.CUBE) ? BTWBlocks.looseDirt : BTWBlocks.looseDirtSlab;
        } else if (BlockTags.isAll(block, meta, BlockTag.SPARSE, BlockTag.GRASS)) {
            if (BlockTags.is(block, meta, BlockTag.CUBE)) {
                newBlock = BTWBlocks.looseSparseGrass;
                newMeta = 0;
            } else if (BlockTags.is(block, meta, BlockTag.SLAB)) {
                newBlock = BTWBlocks.looseSparseGrassSlab;
                newMeta = 0;
            }
        }

        justConverted = swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
        return justConverted;
    }

    private static boolean firm(ItemStack stack, EntityPlayer player, Block block,
                                int meta, World world, int x, int y, int z, BlockSide side) {
        if (!BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE)) return false;

        Block newBlock = null;
        int newMeta = meta;
        boolean toSwap = false;

        if (block == BTWBlocks.looseDirt) {
            newBlock = Block.dirt;
            toSwap = true;
        } else if (block == BTWBlocks.looseDirtSlab) {
            newBlock = BTWBlocks.dirtSlab;
            toSwap = true;
        } else if (block == BTWBlocks.looseSparseGrass) {
            newBlock = Block.grass;
            newMeta = 1;
            toSwap = true;
        } else if (block == BTWBlocks.looseSparseGrassSlab) {
            newBlock = BTWBlocks.grassSlab;
            newMeta = 2;
            toSwap = true;
        }

        if (toSwap && ItemTags.is(stack, ItemTag.CLUB)) {
            ItemDamage.amount = 2; // TODO: Verify this reference works
        }

        justConverted = swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
        return justConverted;
    }

    private static boolean sparsen(ItemStack stack, EntityPlayer player, Block block,
                                   int meta, World world, int x, int y, int z, BlockSide side) {
        if (!BlockTags.is(block, meta, BlockTag.GRASS)) return false;

        Block newBlock = null;
        int newMeta = meta;

        if (BlockTags.is(block, meta, BlockTag.CUBE)) {
            if (BlockTags.isAll(block, meta, BlockTag.FIRM, BlockTag.FULLY_GROWN)) {
                newBlock = block;
                newMeta = 1;
            } else if (BlockTags.isAll(block, meta, BlockTag.FIRM, BlockTag.SPARSE)) {
                newBlock = Block.dirt;
                newMeta = 0;
            } else if (BlockTags.isAll(block, meta, BlockTag.LOOSE_DIRTLIKE, BlockTag.SPARSE)) {
                newBlock = BTWBlocks.looseDirt;
            }
        } else if (BlockTags.is(block, meta, BlockTag.SLAB)) {
            if (BlockTags.isAll(block, meta, BlockTag.FIRM, BlockTag.FULLY_GROWN)) {
                newBlock = block;
                newMeta = 2;
            } else if (BlockTags.isAll(block, meta, BlockTag.FIRM, BlockTag.SPARSE)) {
                newBlock = BTWBlocks.dirtSlab;
                newMeta = 0;
            } else if (BlockTags.isAll(block, meta, BlockTag.LOOSE_DIRTLIKE, BlockTag.SPARSE)) {
                newBlock = BTWBlocks.looseDirtSlab;
            }
        }

        final int VERY_LOW_HEMP_SEED_CHANCE = 1000;
        if (!world.isRemote && world.rand.nextInt(VERY_LOW_HEMP_SEED_CHANCE) == 0) {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z,
                    new ItemStack(BTWItems.hempSeeds), side.getId());
        }

        justConverted = swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
        return justConverted;
    }

    @SuppressWarnings("UnusedAssignment")
    private static boolean pack(ItemStack stack, EntityPlayer player, Block block,
                                int meta, World world, int x, int y, int z, BlockSide side) {
        if (block == null) return false;

        Block newBlock = null;
        int newMeta = meta;

        if (BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.FIRM) && side == BlockSide.UP) {
            newBlock = BTWBlocks.dirtSlab;
            newMeta = 6;
            swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
            return true;
        }

        if (block == BTWBlocks.dirtSlab && meta == 6 && side == BlockSide.UP) {
            if (world.getBlockId(x, y - 1, z) == Block.dirt.blockID) {
                swapBlock(world, x, y - 1, z, block, meta, BTWBlocks.aestheticEarth, 6);
                world.setBlockToAir(x, y, z);
            }
            return true;
        }

        return false;
    }
}
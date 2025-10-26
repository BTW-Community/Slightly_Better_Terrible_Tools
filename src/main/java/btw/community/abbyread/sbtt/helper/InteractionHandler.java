package btw.community.abbyread.sbtt.helper;

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

    // Cache key: (itemId, blockId, metadata, side, clickType) -> InteractionDefinition (or null)
    private static final Map<InteractionCacheKey, Optional<InteractionDefinition>> INTERACTION_CACHE =
            new LinkedHashMap<>() {
                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > 512; // LRU cache, max 512 entries
                }
            };

    // ===== Cache Key =====

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

        @SuppressWarnings("PatternVariableCanBeUsed")
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

    @FunctionalInterface
    private interface ConversionAction {
        boolean apply(ItemStack stack, EntityPlayer player, Block oldBlock, int oldMeta,
                      World world, int x, int y, int z, BlockSide side);
    }

    // ===== Unified Definition of All Interactions =====

    private static final List<InteractionDefinition> INTERACTIONS = List.of(
            new InteractionDefinition(
                    InteractionType.PRIMARY_LEFT_CLICK,
                    Set.of(ItemType.WOOD, ItemType.CHISEL),
                    Set.of(BlockType.FIRM, BlockType.DIRTLIKE, BlockType.GRASS),
                    null,
                    InteractionHandler::loosen
            ),
            new InteractionDefinition(
                    InteractionType.PRIMARY_LEFT_CLICK,
                    Set.of(ItemType.STONE, ItemType.CHISEL),
                    Set.of(BlockType.GRASS),
                    null,
                    InteractionHandler::sparsen
            ),
            new InteractionDefinition(
                    InteractionType.PRIMARY_LEFT_CLICK,
                    Set.of(ItemType.CLUB),
                    Set.of(BlockType.DIRTLIKE, BlockType.LOOSE_DIRTLIKE),
                    null,
                    InteractionHandler::firm,
                    2 // clubs take double damage when firming loose dirt
            ),
            new InteractionDefinition(
                    InteractionType.SECONDARY_RIGHT_CLICK,
                    Set.of(ItemType.SHOVEL),
                    Set.of(BlockType.LOOSE_DIRTLIKE),
                    null,
                    InteractionHandler::firm
            ),
            new InteractionDefinition(
                    InteractionType.SECONDARY_RIGHT_CLICK,
                    Set.of(ItemType.SHOVEL),
                    Set.of(BlockType.DIRTLIKE, BlockType.FIRM, BlockType.CUBE),
                    Set.of(BlockSide.UP),
                    InteractionHandler::pack
            ),
            new InteractionDefinition(
                    InteractionType.PRIMARY_LEFT_CLICK,
                    Set.of(ItemType.SHEARS),
                    Set.of(BlockType.TALL_GRASS),
                    null,
                    (stack, player, block, meta, world, x, y, z, side) -> true
            ),
            new InteractionDefinition(
                    InteractionType.PRIMARY_LEFT_CLICK,
                    Set.of(ItemType.HOE),
                    Set.of(BlockType.GRASS),
                    null,
                    (stack, player, block, meta, world, x, y, z, side) -> true
            ),
            new InteractionDefinition(
                    InteractionType.PRIMARY_LEFT_CLICK,
                    Set.of(ItemType.HOE),
                    Set.of(BlockType.DIRT),
                    null,
                    (stack, player, block, meta, world, x, y, z, side) -> true
            )
    );

    private static class InteractionDefinition {
        final InteractionType type;
        final Set<ItemType> itemTags;
        final Set<BlockType> blockTags;
        final Set<BlockSide> validSides;
        final ConversionAction action;
        final int damageAmount;

        InteractionDefinition(InteractionType type, Set<ItemType> itemTags, Set<BlockType> blockTags,
                              Set<BlockSide> validSides, ConversionAction action, int damageAmount) {
            this.type = type;
            this.itemTags = itemTags;
            this.blockTags = blockTags;
            this.validSides = validSides;
            this.action = action;
            this.damageAmount = damageAmount;
        }

        // Optional default-damage constructor (keeps old definitions valid)
        InteractionDefinition(InteractionType type, Set<ItemType> itemTags, Set<BlockType> blockTags,
                              Set<BlockSide> validSides, ConversionAction action) {
            this(type, itemTags, blockTags, validSides, action, 1);
        }

        boolean matches(ItemStack stack, Block block, int meta, BlockSide side) {
            if (stack == null || itemTags.stream().noneMatch(tag -> ItemSet.is(stack, tag))) return false;
            if (block == null || blockTags.stream().noneMatch(tag -> BlockSet.is(block, meta, tag))) return false;
            return validSides == null || side == null || validSides.contains(side);
        }
    }


    // ===== Public API =====

    public static boolean canInteract(ItemStack stack, Block block, int meta, InteractionType type) {
        return findInteraction(stack, block, meta, null, type).isPresent();
    }

    public static boolean interact(ItemStack stack, EntityPlayer player, Block block, int meta,
                                   World world, int x, int y, int z, BlockSide side, InteractionType type) {
        if (stack == null || block == null) return false;

        Optional<InteractionDefinition> defOpt = findInteraction(stack, block, meta, side, type);
        if (defOpt.isEmpty()) return false;

        InteractionDefinition def = defOpt.get();
        boolean success = def.action.apply(stack, player, block, meta, world, x, y, z, side);

        if (success && player != null) {
            ((SBTTPlayerExtension) player).sbtt_setItemUsedFlag(true, def.damageAmount);
        }

        return success;
    }

    @SuppressWarnings("unused")
    public static void clearCache() {
        INTERACTION_CACHE.clear();
    }

    // ===== Internal Helpers =====

    private static Optional<InteractionDefinition> findInteraction(ItemStack stack, Block block, int meta,
                                                                   BlockSide side, InteractionType type) {
        InteractionCacheKey key = new InteractionCacheKey(stack, block, meta, side, type);

        // Check cache first (works with both present and empty optionals)
        if (INTERACTION_CACHE.containsKey(key)) {
            return INTERACTION_CACHE.get(key);
        }

        // Compute result if not cached
        for (InteractionDefinition def : INTERACTIONS) {
            if (def.type == type && def.matches(stack, block, meta, side)) {
                INTERACTION_CACHE.put(key, Optional.of(def));
                return Optional.of(def);
            }
        }

        // Cache negative result
        INTERACTION_CACHE.put(key, Optional.empty());
        return Optional.empty();
    }

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
        if (!BlockSet.isAll(block, meta, BlockType.DIRTLIKE, BlockType.FIRM)
                || BlockSet.is(block, meta, BlockType.LOOSE_DIRTLIKE)) return false;

        Block newBlock = null;
        int newMeta = meta;

        if (BlockSet.is(block, meta, BlockType.DIRT)) {
            if (BlockSet.is(block, meta, BlockType.CUBE)) newBlock = BTWBlocks.looseDirt;
            else if (BlockSet.is(block, meta, BlockType.SLAB)) newBlock = BTWBlocks.looseDirtSlab;
        } else if (BlockSet.isAll(block, meta, BlockType.SPARSE, BlockType.GRASS)) {
            if (BlockSet.is(block, meta, BlockType.CUBE)) newBlock = BTWBlocks.looseSparseGrass;
            else if (BlockSet.is(block, meta, BlockType.SLAB)) newBlock = BTWBlocks.looseSparseGrassSlab;
            newMeta = 0;
        }

        return swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
    }

    private static boolean firm(ItemStack stack, EntityPlayer player, Block block,
                                int meta, World world, int x, int y, int z, BlockSide side) {
        if (!BlockSet.isAll(block, meta, BlockType.DIRTLIKE, BlockType.LOOSE_DIRTLIKE)) return false;
        Block newBlock = null;
        int newMeta = meta;

        if (block == BTWBlocks.looseDirt) newBlock = Block.dirt;
        else if (block == BTWBlocks.looseDirtSlab) newBlock = BTWBlocks.dirtSlab;
        else if (block == BTWBlocks.looseSparseGrass) {
            newBlock = Block.grass;
            newMeta = 1;
        } else if (block == BTWBlocks.looseSparseGrassSlab) {
            newBlock = BTWBlocks.grassSlab;
            newMeta = 2;
        }

        return swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
    }

    private static boolean sparsen(ItemStack stack, EntityPlayer player, Block block,
                                   int meta, World world, int x, int y, int z, BlockSide side) {
        if (!BlockSet.is(block, meta, BlockType.GRASS)) return false;

        Block newBlock = null;
        int newMeta = meta;

        if (BlockSet.is(block, meta, BlockType.CUBE)) {
            if (BlockSet.isAll(block, meta, BlockType.FIRM, BlockType.FULLY_GROWN)) {
                newBlock = block;
                newMeta = 1;
            } else if (BlockSet.isAll(block, meta, BlockType.FIRM, BlockType.SPARSE)) {
                newBlock = Block.dirt;
                newMeta = 0;
            } else if (BlockSet.isAll(block, meta, BlockType.LOOSE_DIRTLIKE, BlockType.SPARSE)) {
                newBlock = BTWBlocks.looseDirt;
            }
        } else if (BlockSet.is(block, meta, BlockType.SLAB)) {
            if (BlockSet.isAll(block, meta, BlockType.FIRM, BlockType.FULLY_GROWN)) {
                newBlock = block;
                newMeta = 2;
            } else if (BlockSet.isAll(block, meta, BlockType.FIRM, BlockType.SPARSE)) {
                newBlock = BTWBlocks.dirtSlab;
                newMeta = 0;
            } else if (BlockSet.isAll(block, meta, BlockType.LOOSE_DIRTLIKE, BlockType.SPARSE)) {
                newBlock = BTWBlocks.looseDirtSlab;
            }
        }

        final int VERY_LOW_HEMP_SEED_CHANCE = 1000;
        if (!world.isRemote && world.rand.nextInt(VERY_LOW_HEMP_SEED_CHANCE) == 0) {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z,
                    new ItemStack(BTWItems.hempSeeds), side.getValue());
        }

        return swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
    }

    private static boolean pack(ItemStack stack, EntityPlayer player, Block block,
                                int meta, World world, int x, int y, int z, BlockSide side) {
        if (block == null) return false;

        Block newBlock;
        int newMeta;
        boolean swapped = false;

        if (BlockSet.isAll(block, meta, BlockType.DIRTLIKE, BlockType.FIRM) && side == BlockSide.UP) {
            newBlock = BTWBlocks.dirtSlab;
            newMeta = 6;
            swapped = swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
        } else if (block == BTWBlocks.dirtSlab && meta == 6 && side == BlockSide.UP) {
            if (world.getBlockId(x, y - 1, z) == Block.dirt.blockID) {
                swapped = swapBlock(world, x, y - 1, z, block, meta, BTWBlocks.aestheticEarth, 6);
                world.setBlockToAir(x, y, z);
            }
        }

        return swapped;
    }

}
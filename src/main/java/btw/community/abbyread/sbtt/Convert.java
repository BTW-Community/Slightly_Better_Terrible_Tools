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

import java.util.List;
import java.util.Set;

public class Convert {

    private static final boolean DEBUG = false;
    private static final int VERY_LOW_HEMP_SEED_CHANCE = 1000;
    private static boolean justConverted = false;

    private static class ConversionRule {
        private final ItemUseCombo combo;
        private final ConversionAction action;

        ConversionRule(ItemUseCombo combo, ConversionAction action) {
            this.combo = combo;
            this.action = action;
        }

        boolean matches(ItemStack stack, Block block, int meta, BlockSide side) {
            return combo.matches(stack, block, meta, side);
        }

        boolean execute(ItemStack stack, EntityPlayer player, Block block, int meta,
                        World world, int x, int y, int z, int side) {
            return action.apply(stack, player, block, meta, world, x, y, z, side);
        }
    }

    @FunctionalInterface
    private interface ConversionAction {
        boolean apply(ItemStack stack, EntityPlayer player, Block block, int meta,
                      World world, int x, int y, int z, int side);
    }

    private static final List<ConversionRule> PRIMARY_RULES = List.of(
            new ConversionRule(
                    new ItemUseCombo(Set.of(ItemTag.WOOD, ItemTag.CHISEL),
                            Set.of(BlockTag.FIRM, BlockTag.DIRTLIKE, BlockTag.GRASS)),
                    Convert::loosen
            ),
            new ConversionRule(
                    new ItemUseCombo(Set.of(ItemTag.STONE, ItemTag.CHISEL),
                            Set.of(BlockTag.GRASS)),
                    Convert::sparsen
            ),
            new ConversionRule(
                    new ItemUseCombo(Set.of(ItemTag.CLUB),
                            Set.of(BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE)),
                    Convert::firm
            )
    );

    private static final List<ConversionRule> SECONDARY_RULES = List.of(
            new ConversionRule(
                    new ItemUseCombo(Set.of(ItemTag.SHOVEL),
                            Set.of(BlockTag.LOOSE_DIRTLIKE)),
                    Convert::firm
            ),
            new ConversionRule(
                    new ItemUseCombo(Set.of(ItemTag.SHOVEL),
                            Set.of(BlockTag.DIRTLIKE, BlockTag.FIRM, BlockTag.CUBE),
                            Set.of(BlockSide.UP)),
                    Convert::pack
            )
    );

    public static boolean canConvert(ItemStack stack, Block block, int meta) {
        return stack != null && block != null &&
                PRIMARY_RULES.stream().anyMatch(rule -> rule.matches(stack, block, meta, null));
    }

    public static boolean convert(ItemStack stack, EntityPlayer player,
                                  Block block, int meta, World world,
                                  int x, int y, int z, int side) {
        if (stack == null || block == null) return false;
        justConverted = false;
        for (ConversionRule rule : PRIMARY_RULES) {
            if (rule.matches(stack, block, meta, BlockSide.fromId(side))) {
                justConverted = rule.execute(stack, player, block, meta, world, x, y, z, side);
                if (justConverted) return true;
            }
        }
        return false;
    }

    public static boolean secondaryCanConvert(ItemStack stack, Block block, int meta) {
        return stack != null && block != null &&
                SECONDARY_RULES.stream().anyMatch(rule -> rule.matches(stack, block, meta, null));
    }

    public static boolean secondaryConvert(ItemStack stack, EntityPlayer player,
                                           Block block, int meta, World world,
                                           int x, int y, int z, int side) {
        if (stack == null || block == null) return false;
        justConverted = false;
        for (ConversionRule rule : SECONDARY_RULES) {
            if (rule.matches(stack, block, meta, BlockSide.fromId(side))) {
                justConverted = rule.execute(stack, player, block, meta, world, x, y, z, side);
                if (justConverted) return true;
            }
        }
        return false;
    }

    public static boolean hasJustConverted() {
        return justConverted;
    }

    private static boolean loosen(ItemStack stack, EntityPlayer player, Block block,
                                  int meta, World world, int x, int y, int z, int side) {

        // Only firm dirt should be loosened
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

        return swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
    }

    private static boolean firm(ItemStack stack, EntityPlayer player, Block block,
                                int meta, World world, int x, int y, int z, int side) {

        // Only loose dirt should be firmed
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
            ItemDamage.amount = 2;
        }

        return swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
    }

    private static boolean sparsen(ItemStack stack, EntityPlayer player, Block block,
                                   int meta, World world, int x, int y, int z, int side) {

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

        if (!world.isRemote && world.rand.nextInt(VERY_LOW_HEMP_SEED_CHANCE) == 0) {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z,
                    new ItemStack(BTWItems.hempSeeds), side);
        }

        return swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
    }

    private static boolean pack(ItemStack stack, EntityPlayer player, Block block,
                                int meta, World world, int x, int y, int z, int side) {

        if (block == null) return false;
        Block newBlock = null;
        int newMeta = meta;

        if (BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.FIRM) && side == 1) {
            newBlock = BTWBlocks.dirtSlab;
            newMeta = 6;
            swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
            return true;
        }

        if (block == BTWBlocks.dirtSlab && meta == 6 && side == 1) {
            if (world.getBlockId(x, y - 1, z) == Block.dirt.blockID) {
                newBlock = BTWBlocks.aestheticEarth;
                swapBlock(world, x, y - 1, z, block, meta, newBlock, 6);
                world.setBlockToAir(x, y, z);
            }
            return true;
        }

        return false;
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

    private static void debug(String message) {
        if (DEBUG) System.out.println(message);
    }
}

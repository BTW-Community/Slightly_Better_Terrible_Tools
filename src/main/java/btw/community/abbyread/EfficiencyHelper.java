package btw.community.abbyread;

import btw.block.blocks.*;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import btw.community.abbyread.sbtt.oldMixins.ToolItemAccessor;
import net.minecraft.src.*;

// Important notes:
// - These checks influence what the game considers "proper material"
//    when it checks for strength of the item against a block.
// - Being "effective" against a block as determined here means the
//    item will be damaged when it destroys or harvests from the block.
public class EfficiencyHelper {

    public static final float effMod = 1.5F;

    // Store last computed efficiency (1.0 if ineffective) for damage check
    private static boolean lastEffective = false;

    public static void setLastEffective(boolean effective) {
        lastEffective = effective;
    }
    public static boolean getLastEffective() {
        return lastEffective;
    }

    public static float genericGetStrVsBlock(ItemStack stack, World world, Block block,
                                             int x, int y, int z) {
        // 1F is the default getStrVsBlock
        float minimum = 1F;
        if (stack == null || block == null) return minimum;

        if (world != null) {
            ToolItemAccessor accessor = (ToolItemAccessor) stack.getItem();
            float effProp = accessor.getEfficiencyOnProperMaterial();
            // Check efficiency between tool and the block it's used on.
            boolean effective = EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z);
            if (effective) {
                EfficiencyHelper.setLastEffective(true);
                return effProp;
            } else {
                // Not effective: Shouldn't boost, shouldn't damage item
                float potentialOverride = minimum;
                if (BlockBreakingOverrides.isUniversallyEasyBlock(block)) {
                    potentialOverride = BlockBreakingOverrides.baselineEfficiency(block);
                }
                EfficiencyHelper.setLastEffective(false);
                // Prevent boost by picking minimum.
                //   (universally easy blocks already max to potentialOverride)
                return Math.min(potentialOverride, minimum); // 1F is the default getStrVsBlock
            }
        }
        return minimum;
    }

    public static float getStrVsBlock(ItemStack stack, World world, Block block,
                                      int x, int y, int z) {
        float minimum = 1F;

        if (world != null) {
            float strength = EfficiencyHelper.genericGetStrVsBlock(stack, world, block, x, y, z);

            // Specific boosts and nerfs
            if (getLastEffective()) {
                int metadata = world.getBlockMetadata(x, y, z);

                // Pointy stick loosening blocks
                if (stack.getItem() instanceof ChiselItemWood &&
                        EfficiencyHelper.firmDirt(block, metadata)) {
                    float boost = 8F;
                    strength *= boost;
                } else

                // Sharp stone for grass cutting
                if (stack.getItem() instanceof ChiselItemStone &&
                        fullGrass(block, metadata)) {
                    float boost = 6F;
                    strength *= boost;
                } else

                // Sharp stone to mine the last bits of upper-strata stone faster
                if (stack.getItem() instanceof ChiselItemStone &&
                        roughStoneExtra(block, metadata)) {
                    float boost = 2.5F;  // halve the slowness to be less painful
                    strength *= boost;
                } else

                // Sharp stone Sandstone boost
                if (stack.getItem() instanceof ChiselItemStone &&
                        (   block instanceof BlockSandStone
                        ||  block instanceof SandstoneStairsBlock   )
                ) {
                    float boost = 4F;
                    strength *= boost;
                } else

                //  Sharp stone loose and hard efficiency boost
                if (stack.getItem() instanceof ChiselItemStone &&
                        (   block instanceof LooseCobblestoneBlock
                                ||  block instanceof LooseCobblestoneSlabBlock
                                ||  block instanceof LooseCobblestoneStairsBlock
                                ||  block instanceof LooseBrickBlock
                                ||  block instanceof LooseBrickSlabBlock
                                ||  block instanceof LooseBrickStairsBlock   )
                ) {
                    float boost = 2F;
                    strength *= boost;
                } else

                // Sharp stone to be fast on glass-likes maybe
                //    (only "proper material" boost implemented right now)
                if (glassLike(block, metadata) && stack.getItem() instanceof ChiselItemStone) {
                    float boost = 4F;
                    strength *= boost;
                }
            }
            return strength;
        }
        return minimum;
    }

    public static boolean isToolItemEfficientVsBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        if (stack == null || world == null || block == null) return false;
        int meta = world.getBlockMetadata(x, y, z);
        return isToolItemEfficientVsBlock(stack, block, meta);
    }

    public static boolean isToolItemEfficientVsBlock(ItemStack stack, Block block, int metadata) {
        if (stack == null || block == null) return false;

// --- Pointy Stick (wood chisel) ---
        if (stack.getItem() instanceof ChiselItemWood) {
            final int DIRTSLAB_DIRT = 0;
            final int DIRTSLAB_GRASS = 1;
            final int PACKED_EARTH = 6;

            if (        block instanceof BlockDirt
                    ||  block instanceof BlockStone
                    || (block instanceof RoughStoneBlock && ((RoughStoneBlock) block).strataLevel == 0)
                    || (block instanceof OreBlock && ((OreBlock) block).getStrata(metadata) == 0)
                    || (block instanceof DirtSlabBlock && metadata == DIRTSLAB_DIRT)
                    || (block instanceof BlockGrass && ((BlockGrass) block).isSparse(metadata))
                    || (block instanceof GrassSlabBlock && ((GrassSlabBlock) block).isSparse(metadata))
                    || (block instanceof AestheticOpaqueEarthBlock && metadata == PACKED_EARTH)) {
                return true;
            }

            if (       (block instanceof DirtSlabBlock && metadata == DIRTSLAB_GRASS)
                    || (block instanceof BlockGrass && !((BlockGrass) block).isSparse(metadata))
                    || (block instanceof GrassSlabBlock && !((GrassSlabBlock) block).isSparse(metadata))
                    ||  block instanceof BlockLog
                    ||  block instanceof LogSpikeBlock
                    ||  block instanceof ChewedLogBlock
                    ||  block instanceof BlockGlass
                    ||  block instanceof BlockGlowStone
                    ||  block instanceof BlockIce
                    ||  block instanceof BlockPane
                    ||  block instanceof BlockRedstoneLight
                    ||  block instanceof LightBlock   ) {
                return false;
            }
        }

// --- Sharp Stone (stone chisel) ---
        if (stack.getItem() instanceof ChiselItemStone) {
            final int DIRTSLAB_DIRT = 0;
            final int DIRTSLAB_GRASS = 1;
            final int PACKED_EARTH = 6;

            if (       (block instanceof BlockWeb) // Hacky buff in ChiselItemStone replaced with this
                    || (block instanceof DirtSlabBlock && metadata == DIRTSLAB_GRASS)
                    || (block instanceof BlockGrass && !((BlockGrass) block).isSparse(metadata))
                    || (block instanceof GrassSlabBlock && !((GrassSlabBlock) block).isSparse(metadata))
                    || (block instanceof RoughStoneBlock && ((RoughStoneBlock) block).strataLevel == 0)
                    || (block instanceof OreBlock && ((OreBlock) block).getStrata(metadata) == 0)
                    ||  block instanceof BlockLog
                    ||  block instanceof ChewedLogBlock
                    ||  block instanceof LogSpikeBlock
                    ||  block instanceof BlockStone
                    ||  block instanceof BlockSandStone
                    ||  block instanceof SandstoneStairsBlock
                    ||  block instanceof LooseCobblestoneBlock
                    ||  block instanceof LooseCobblestoneSlabBlock
                    ||  block instanceof LooseCobblestoneStairsBlock
                    ||  block instanceof LooseBrickBlock
                    ||  block instanceof LooseBrickSlabBlock
                    ||  block instanceof LooseBrickStairsBlock
                    ||  block instanceof BlockGlass
                    ||  block instanceof BlockGlowStone
                    ||  block instanceof BlockIce
                    ||  block instanceof BlockPane
                    ||  block instanceof BlockRedstoneLight
                    ||  block instanceof LightBlock   ) {
                return true;
            }

            if (        block instanceof BlockDirt
                    || (block instanceof DirtSlabBlock && metadata == DIRTSLAB_DIRT)
                    || (block instanceof BlockGrass && ((BlockGrass) block).isSparse(metadata))
                    || (block instanceof GrassSlabBlock && ((GrassSlabBlock) block).isSparse(metadata))
                    || (block instanceof AestheticOpaqueEarthBlock && metadata == PACKED_EARTH)   ) {
                return false;
            }
        }
        return false;
    }

    // For extra efficiency boosts (more than efficiency on proper material *effMod)
    public static boolean firmDirt(Block block, int metadata) {
        final int DIRTSLAB_DIRT = 0;
        final int PACKED_EARTH = 6;

        return (   (block instanceof BlockDirt)
                || (block instanceof DirtSlabBlock && metadata == DIRTSLAB_DIRT)
                || (block instanceof BlockGrass && ((BlockGrass) block).isSparse(metadata))
                || (block instanceof GrassSlabBlock && ((GrassSlabBlock) block).isSparse(metadata))
                || (block instanceof AestheticOpaqueEarthBlock && metadata == PACKED_EARTH)   );
    }
    public static boolean fullGrass(Block block, int metadata) {
        final int DIRTSLAB_GRASS = 1;

        return (   (block instanceof DirtSlabBlock && metadata == DIRTSLAB_GRASS)
                || (block instanceof BlockGrass && !((BlockGrass) block).isSparse(metadata))
                || (block instanceof GrassSlabBlock && !((GrassSlabBlock) block).isSparse(metadata))   );
    }

    // Check if it's the last (normally very slow) hits of upper-strata stone
    public static boolean roughStoneExtra(Block block, int metadata) {
        return (block instanceof RoughStoneBlock &&
                (((RoughStoneBlock) block).strataLevel == 0) &&
                metadata >= 8   );
    }

    @SuppressWarnings("unused")
    public static boolean glassLike(Block block, int ignoredMetadata) {
        return (   (block instanceof BlockGlass)
                || (block instanceof BlockGlowStone)
                || (block instanceof BlockIce)
                || (block instanceof BlockPane)
                || (block instanceof BlockRedstoneLight)
                || (block instanceof LightBlock)   );
    }
}

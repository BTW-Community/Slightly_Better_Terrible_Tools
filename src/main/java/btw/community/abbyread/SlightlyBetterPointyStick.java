package btw.community.abbyread;

import net.minecraft.src.Block;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import btw.block.BTWBlocks;
import btw.item.items.ChiselItemWood;

public class SlightlyBetterPointyStick extends ChiselItemWood {

    public SlightlyBetterPointyStick(int iItemID) {
        super(iItemID);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, World world, Block block, int i, int j, int k )
    {
        float fStrength = super.getStrVsBlock( stack, world, block, i, j, k );

        if (
            block.blockID == Block.glass.blockID ||
            block.blockID == Block.glowStone.blockID ||
            block.blockID == Block.ice.blockID ||
            block.blockID == Block.redstoneLampActive.blockID ||
            block.blockID == Block.redstoneLampIdle.blockID ||
            block.blockID == Block.thinGlass.blockID ||
            block.blockID == BTWBlocks.lens.blockID ||
            block.blockID == BTWBlocks.lightBlockOn.blockID ||
            block.blockID == BTWBlocks.lightBlockOff.blockID
        ) {
            return fStrength *= 1.25;
        }
        return fStrength;
    }

    @Override
    public boolean onBlockDestroyed(
        ItemStack stack,
        World world,
        int iBlockID,
        int i,
        int j,
        int k,
        EntityLivingBase usingEntity
    ) {
        // Apply damage from blocks approximately efficiency on
        // (to get around inefficient use no longer causing damage
        // and because I can't be bothered to have a bunch of other
        // files doing it for real).
        if (
            iBlockID == Block.glass.blockID ||
            iBlockID == Block.glowStone.blockID ||
            iBlockID == Block.ice.blockID ||
            iBlockID == Block.redstoneLampActive.blockID ||
            iBlockID == Block.redstoneLampIdle.blockID ||
            iBlockID == Block.thinGlass.blockID ||
            iBlockID == BTWBlocks.lens.blockID ||
            iBlockID == BTWBlocks.lightBlockOn.blockID ||
            iBlockID == BTWBlocks.lightBlockOff.blockID
        ) {
            stack.damageItem(1, usingEntity); // apply durability loss
            return true;
        }
        return true;
    }

}

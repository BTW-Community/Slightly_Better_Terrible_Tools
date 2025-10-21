package btw.community.abbyread.sbtt;

import net.minecraft.src.Block;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ItemDamage {

    public static int amount = 1;
    public boolean tryDamage(ItemStack stack, World world, int iBlockID, int x, int y, int z, EntityLivingBase usingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (Block.blocksList[iBlockID].getBlockHardness(world, x, y, z) > 0.0f) {



            // default damage amount
            doDamage(stack, usingEntity, 1);
        }
        return true;
    }

    private void doDamage(ItemStack stack, EntityLivingBase usingEntity, int amount) {
        stack.damageItem(amount, usingEntity);
    }
}

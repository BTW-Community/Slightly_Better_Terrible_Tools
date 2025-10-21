package btw.community.abbyread.sbtt.mixin;

import btw.item.items.ToolItem;
import net.minecraft.src.Block;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public class ToolItemMixin {
    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void damageCalculationOverride(ItemStack stack, World world, int iBlockID, int x, int y, int z, EntityLivingBase usingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (Block.blocksList[iBlockID].getBlockHardness(world, x, y, z) > 0.0f) {
            stack.damageItem(1, usingEntity);
        }
        cir.setReturnValue(true);
    }
}

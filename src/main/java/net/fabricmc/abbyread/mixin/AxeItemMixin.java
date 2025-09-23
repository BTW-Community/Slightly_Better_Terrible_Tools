package net.fabricmc.abbyread.mixin;

import btw.item.items.AxeItem;
import net.minecraft.src.Block;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Shadow
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        throw new AssertionError(); // shadowed
    }

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void abbyread$preventWastedUses(ItemStack stack, World world, int blockID,
                                            int x, int y, int z,
                                            EntityLivingBase entity,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || world == null) return;
        Block block = Block.blocksList[blockID];
        if (this.isEfficientVsBlock(stack, world, block, x, y, z)) {
            if (!world.isRemote) {
                stack.damageItem(1, entity);
                cir.setReturnValue(true);
            }
        }
    }
}

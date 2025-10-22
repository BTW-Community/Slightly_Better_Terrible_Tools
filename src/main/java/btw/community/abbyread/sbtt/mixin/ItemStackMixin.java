package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.sbtt.ItemDamage;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void itemDamageIntercept(World world, int blockID, int x, int y, int z, EntityPlayer player, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        ItemDamage.tryDamage(stack, world, blockID, x, y, z, player);
        ci.cancel(); // to prevent the normal processing of item damage
    }
}

package btw.community.abbyread.sbtt.mixin.damage;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
/*
    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void interceptOnBlockDestroyed(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer, CallbackInfo ci) {
        System.out.println("Block destroyed.");
    }
*/

}

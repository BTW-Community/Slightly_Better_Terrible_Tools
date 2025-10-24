package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.sbtt.helper.ItemDamage;
import btw.community.abbyread.sbtt.helper.SBTTPlayerExtension;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void sbtt$onBlockDestroyedIntercept(World world, int blockID, int x, int y, int z, EntityPlayer player, CallbackInfo ci) {
        if (world.isRemote || player == null) return;

        ItemStack stack = (ItemStack) (Object) this;
        SBTTPlayerExtension ext = (SBTTPlayerExtension) player;

        // Check if a special interaction already marked this tool use
        boolean wasCustomInteraction = ext.sbtt_consumeItemUsedFlag();
        int damageFromInteraction = ext.sbtt_consumeItemUsedDamage();

        int appliedDamage;
        if (wasCustomInteraction) {
            appliedDamage = ItemDamage.damageByAmount(stack, player,
                    damageFromInteraction > 0 ? damageFromInteraction : ItemDamage.amount);
        } else {
            // Fallback to normal logic (break speed or basic damage)
            appliedDamage = ItemDamage.tryDamage(stack, world, blockID, x, y, z, player);
        }

        if (appliedDamage > 0) {
            // Cancel vanilla logic only if we applied our own damage
            ci.cancel();
        }
    }
}

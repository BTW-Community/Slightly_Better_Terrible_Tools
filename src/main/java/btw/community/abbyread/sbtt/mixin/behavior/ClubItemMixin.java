package btw.community.abbyread.sbtt.mixin.behavior;

import btw.item.items.ClubItem;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClubItem.class)
public class ClubItemMixin {

    @Inject(
            method = "onBlockDestroyed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void sbtt$onBlockDestroyed(ItemStack stack, World world, int iBlockID, int i, int j, int k, EntityLivingBase usingEntity, CallbackInfoReturnable<Boolean> cir) {
        // Prevent useless damage
        cir.setReturnValue(true);
    }

}

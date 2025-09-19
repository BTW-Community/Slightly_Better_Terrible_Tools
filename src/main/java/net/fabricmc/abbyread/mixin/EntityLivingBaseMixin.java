package net.fabricmc.abbyread.mixin;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin {
    @Shadow public abstract boolean isClientWorld();

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void abby$debugDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // Print to console for debugging
        if (isClientWorld()) {
            System.out.println("Entity " + ((EntityLivingBase)(Object)this).getClass().getSimpleName() + " is taking " + amount + " damage from " + source.damageType);
        }
        // If a player is the source, we can also send chat
        if (isClientWorld() && source.getEntity() instanceof EntityPlayer player) {
            player.sendChatToPlayer(
                    net.minecraft.src.ChatMessageComponent.createFromText(
                            "Player dealt " + amount + " damage to " + ((EntityLivingBase)(Object)this).getClass().getSimpleName()
                    )
            );
        }
    }
}

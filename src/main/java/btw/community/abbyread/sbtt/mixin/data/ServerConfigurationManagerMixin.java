package btw.community.abbyread.sbtt.mixin.data;

import btw.community.abbyread.sbtt.util.PlayerDataExtension;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ServerConfigurationManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerConfigurationManager.class)
public class ServerConfigurationManagerMixin {

    @Inject(method = "respawnPlayer", at = @At("TAIL"))
    private void onRespawnPlayer(EntityPlayerMP oldPlayer, int iDefaultDimension, boolean playerLeavingTheEnd, CallbackInfoReturnable<EntityPlayerMP> cir) {
        EntityPlayer newPlayer = cir.getReturnValue();

        // Copy your custom data
        PlayerDataExtension oldData = (PlayerDataExtension) oldPlayer;
        PlayerDataExtension newData = (PlayerDataExtension) newPlayer;

        newData.setSeedAttempts(oldData.getSeedAttempts());
    }
}

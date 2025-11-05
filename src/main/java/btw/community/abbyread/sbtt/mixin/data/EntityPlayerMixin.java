package btw.community.abbyread.sbtt.mixin.data;

import btw.community.abbyread.sbtt.util.PlayerDataExtension;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
@Implements(@Interface(iface = PlayerDataExtension.class, prefix = "sbtt$"))
public class EntityPlayerMixin {

    @Unique
    private static final String NBT_TAG = "SBTTPlayerData";

    @Unique
    private int playerSeedAttempts = 0;

    public int sbtt$getSeedAttempts() {
        return playerSeedAttempts;
    }

    public void sbtt$setSeedAttempts(int value) {
        this.playerSeedAttempts = value;
    }

   @Inject(method = "writeModDataToNBT", at = @At("TAIL"))
    private void onWriteModDataToNBT(NBTTagCompound tag, CallbackInfo ci) {
       NBTTagCompound myData = new NBTTagCompound();
       myData.setInteger("playerSeedAttempts", playerSeedAttempts);
       tag.setTag(NBT_TAG, myData);
   }

    @Inject(method = "readModDataFromNBT", at = @At("TAIL"))
    private void onReadModDataFromNBT(NBTTagCompound tag, CallbackInfo ci) {
        if (tag.hasKey(NBT_TAG)) {
            NBTTagCompound myData = tag.getCompoundTag(NBT_TAG);
            playerSeedAttempts = myData.hasKey("playerSeedAttempts")
                    ? myData.getInteger("playerSeedAttempts")
                    : 0;
        }
    }

}

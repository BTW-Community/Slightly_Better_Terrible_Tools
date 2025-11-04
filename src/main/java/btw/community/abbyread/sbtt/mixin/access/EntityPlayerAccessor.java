package btw.community.abbyread.sbtt.mixin.access;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityPlayer.class)
public interface EntityPlayerAccessor {

    @Invoker("readModDataFromNBT")
    void invokeReadModDataFromNBT(NBTTagCompound tag);

    @Invoker("writeModDataToNBT")
    void invokeWriteModDataToNBT(NBTTagCompound tag);
}

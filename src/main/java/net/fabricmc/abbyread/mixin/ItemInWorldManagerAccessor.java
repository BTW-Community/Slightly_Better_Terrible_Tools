package net.fabricmc.abbyread.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.src.ItemInWorldManager;

@Mixin(ItemInWorldManager.class)
public interface ItemInWorldManagerAccessor {

    @Accessor("curblockDamage")
    int getCurblockDamage();

    @Accessor("curblockDamage")
    void setCurblockDamage(int value);

    @Accessor("field_73093_n")
    int getInitialDamage();

    @Accessor("field_73093_n")
    void setInitialDamage(int value);

    @Accessor("receivedFinishDiggingPacket")
    boolean getReceivedFinishDiggingPacket();

    @Accessor("receivedFinishDiggingPacket")
    void setReceivedFinishDiggingPacket(boolean value);

    @Accessor("durabilityRemainingOnBlock")
    int getDurabilityRemainingOnBlock();

    @Accessor("durabilityRemainingOnBlock")
    void setDurabilityRemainingOnBlock(int value);

    @Accessor("posX")
    int getPosX();

    @Accessor("posX")
    void setPosX(int value);

    @Accessor("posY")
    int getPosY();

    @Accessor("posY")
    void setPosY(int value);

    @Accessor("posZ")
    int getPosZ();

    @Accessor("posZ")
    void setPosZ(int value);

    @Accessor("harvestingFromFacing")
    int getHarvestingFromFacing();

    @Accessor("harvestingFromFacing")
    void setHarvestingFromFacing(int value);
    
    @Accessor("isDestroyingBlock")
    boolean getIsDestroyingBlock();

    @Accessor("isDestroyingBlock")
    void setIsDestroyingBlock(boolean value);

    @Accessor("partiallyDestroyedBlockX")
    int getPartiallyDestroyedBlockX();

    @Accessor("partiallyDestroyedBlockX")
    void setPartiallyDestroyedBlockX(int value);

    @Accessor("partiallyDestroyedBlockY")
    int getPartiallyDestroyedBlockY();

    @Accessor("partiallyDestroyedBlockY")
    void setPartiallyDestroyedBlockY(int value);

    @Accessor("partiallyDestroyedBlockZ")
    int getPartiallyDestroyedBlockZ();

    @Accessor("partiallyDestroyedBlockZ")
    void setPartiallyDestroyedBlockZ(int value);
}

package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.sbtt.api.SBTTPlayerExtension;
import net.minecraft.src.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin implements SBTTPlayerExtension {

    @Unique
    private boolean sbtt_itemUsed = false;

    @Unique
    private int sbtt_pendingItemDamage = 0;

    @Override
    public void sbtt_setItemUsedFlag(boolean value, int damageAmount) {
        this.sbtt_itemUsed = value;
        this.sbtt_pendingItemDamage = value ? damageAmount : 0;
    }

    @Override
    public boolean sbtt_consumeItemUsedFlag() {
        boolean value = this.sbtt_itemUsed;
        this.sbtt_itemUsed = false; // reset the flag
        this.sbtt_pendingItemDamage = 0; // clear damage after consumption
        return value;
    }

    @Override
    public int sbtt_getPendingItemDamage() {
        return this.sbtt_pendingItemDamage;
    }
}

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
    private int sbtt_itemDamageAmount = 0;

    @Override
    public void sbtt_setItemUsedFlag(boolean value, int damageAmount) {
        this.sbtt_itemUsed = value;
        this.sbtt_itemDamageAmount = damageAmount;
    }

    @Override
    public boolean sbtt_consumeItemUsedFlag() {
        boolean result = this.sbtt_itemUsed;
        this.sbtt_itemUsed = false; // reset
        return result;
    }

    @Override
    public int sbtt_consumeItemUsedDamage() {
        int result = this.sbtt_itemDamageAmount;
        this.sbtt_itemDamageAmount = 0; // reset
        return result;
    }
}

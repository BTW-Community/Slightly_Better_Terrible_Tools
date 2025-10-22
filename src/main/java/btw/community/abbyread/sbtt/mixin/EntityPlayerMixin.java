package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.sbtt.api.SBTTPlayerExtension;
import net.minecraft.src.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin implements SBTTPlayerExtension {

    @Unique
    private boolean sbtt_justConverted = false;

    @Override
    public void sbtt_setJustConvertedFlag(boolean value) {
        this.sbtt_justConverted = value;
    }

    @Override
    public boolean sbtt_consumeJustConvertedFlag() {
        boolean value = this.sbtt_justConverted;
        this.sbtt_justConverted = false;
        return value;
    }
}
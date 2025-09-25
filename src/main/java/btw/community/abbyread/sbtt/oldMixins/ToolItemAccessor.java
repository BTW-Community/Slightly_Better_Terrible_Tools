package btw.community.abbyread.sbtt.oldMixins;

import btw.item.items.ToolItem;
import net.minecraft.src.EnumToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ToolItem.class)
public interface ToolItemAccessor {

    // BTW's /= 4 toward pointy stick's efficiency means
    //   0.5F on proper material and 0.25F on improper since
    //   EnumToolMaterial has the WOOD enum correspond to 2.0F for that.
    // BTW's /= 2 toward sharp stone's efficiency means
    //   2.0F on proper material and 1.0F on improper since
    //   EnumToolMaterial has the STONE enum correspond to 4.0F for that.

    @Accessor(remap = false)
    float getEfficiencyOnProperMaterial();

    @Accessor(remap = false)
    void setEfficiencyOnProperMaterial(float efficiencyOnProperMaterial);

    @Accessor(remap = false)
    EnumToolMaterial getToolMaterial();

    @Accessor(remap = false)
    void setToolMaterial(EnumToolMaterial toolMaterial);
}

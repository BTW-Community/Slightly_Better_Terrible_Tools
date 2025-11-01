package btw.community.abbyread.sbtt.mixin.access;

import btw.item.items.ToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ToolItem.class)
public interface ToolItemAccessor {

    @Accessor(value = "efficiencyOnProperMaterial", remap = false)
    float getEfficiencyOnProperMaterial();

    @Accessor(value = "efficiencyOnProperMaterial", remap = false)
    void setEfficiencyOnProperMaterial(float value);

}

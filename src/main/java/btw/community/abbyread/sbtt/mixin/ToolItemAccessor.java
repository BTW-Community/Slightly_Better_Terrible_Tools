package btw.community.abbyread.sbtt.mixin;

import btw.item.items.ToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ToolItem.class)
public interface ToolItemAccessor {

    @Accessor(value = "efficiencyOnProperMaterial", remap = false)
    float abbyread$getEfficiencyOnProperMaterial();

    @Accessor(value = "efficiencyOnProperMaterial", remap = false)
    void abbyread$setEfficiencyOnProperMaterial(float value);
}

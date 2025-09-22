package net.fabricmc.abbyread.mixin;

import btw.item.items.ToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ToolItem.class)
public interface ToolItemAccessor {
    @Accessor(remap = false)
    float getEfficiencyOnProperMaterial();

    @Accessor(remap = false)
    public void setEfficiencyOnProperMaterial(float efficiencyOnProperMaterial);
}

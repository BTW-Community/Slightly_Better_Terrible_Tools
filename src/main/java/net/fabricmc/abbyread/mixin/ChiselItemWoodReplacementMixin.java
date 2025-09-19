package net.fabricmc.abbyread.mixin;

import btw.item.BTWItems;
import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import btw.community.abbyread.SlightlyBetterPointyStick;

@Mixin(BTWItems.class)
public class ChiselItemWoodReplacementMixin {

    // Shadow the static field
    @Shadow @Final @Mutable
    public static Item pointyStick; // field in BTWItems

    @Inject(method = "instantiateModItems", at = @At("TAIL"), remap = false)
    private static void replacePointyStick(CallbackInfo ci) {
        int id = pointyStick.itemID; // get the old ID
        SlightlyBetterPointyStick replacement = new SlightlyBetterPointyStick(id);

        // Swap the field and the global item array
        pointyStick = replacement;
        Item.itemsList[id] = replacement;
    }
}

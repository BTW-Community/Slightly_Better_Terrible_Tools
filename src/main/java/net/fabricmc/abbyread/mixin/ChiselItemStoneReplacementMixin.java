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

import btw.community.abbyread.SlightlyBetterSharpStone;

@Mixin(BTWItems.class)
public class ChiselItemStoneReplacementMixin {

    // Shadow the static field
    @Shadow @Final @Mutable
    public static Item sharpStone; // field in BTWItems

    @Inject(method = "instantiateModItems", at = @At("TAIL"), remap = false)
    private static void replaceSharpStone(CallbackInfo ci) {
        int id = sharpStone.itemID; // get the old ID
        SlightlyBetterSharpStone replacement = new SlightlyBetterSharpStone(id);

        // Swap the field and the global item array
        sharpStone = replacement;
        Item.itemsList[id] = replacement;
    }
}

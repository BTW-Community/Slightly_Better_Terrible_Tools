package btw.community.abbyread.sbtt.oldMixins;

import btw.community.abbyread.EfficiencyHelper;
import btw.item.items.ChiselItemStone;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiselItemStone.class)
public class ChiselItemStoneMixin {

    /*
    // Follow-up BTW's efficiencyOnProperMaterial /= 2 with a *= effMod
    @Inject(
            method = "applyStandardEfficiencyModifiers",
            at = @At("TAIL"),
            remap = false
    )
    private void abbyread$effModApplication(CallbackInfo ci) {
        ToolItemAccessor accessor = (ToolItemAccessor) this;
        float original = accessor.getEfficiencyOnProperMaterial();
        final float effMod = EfficiencyHelper.effMod;
        accessor.setEfficiencyOnProperMaterial(original * effMod);
    }
    */

    // Remove the special case for webs in favor of making sharp stone efficient toward them elsewhere
    @Inject(method = "getStrVsBlock", at = @At("HEAD"), cancellable = true)
    private void overrideTheOverride(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(EfficiencyHelper.getStrVsBlock(stack, world, block, x, y, z));
    }
}

package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.community.abbyread.UniformEfficiencyModifier;
import btw.item.items.ChiselItemStone;
import btw.item.items.ToolItem;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiselItemStone.class)
public abstract class ChiselItemStoneMixin
{
    @Unique
    private final float effMod = UniformEfficiencyModifier.UNIFORM_EFFICIENCY_MODIFIER;

    @Inject(
            method = "applyStandardEfficiencyModifiers",
            at = @At("RETURN"),
            remap = false
    )
    private void tweakEfficiency(CallbackInfo ci)
    {
        ((ToolItem)(Object)this).addCustomEfficiencyMultiplier(effMod);
    }
    @Inject(
            method = "getStrVsBlock",
            at = @At("RETURN"),
            cancellable = true
    )
    public void sbtt$modifyEfficiency(ItemStack stack, net.minecraft.src.World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        float efficiency = cir.getReturnValue();
        if (
            block.blockID == Block.glass.blockID ||
            block.blockID == Block.glowStone.blockID ||
            block.blockID == Block.ice.blockID ||
            block.blockID == Block.redstoneLampActive.blockID ||
            block.blockID == Block.redstoneLampIdle.blockID ||
            block.blockID == Block.thinGlass.blockID ||
            block.blockID == BTWBlocks.upperStrataRoughStone.blockID
        ) {
            efficiency *= effMod; // to nerf the boost from effectiveness
        }
        cir.setReturnValue(efficiency);
    }
}

package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.UniformEfficiencyModifier;

import btw.block.BTWBlocks;
import net.minecraft.src.BlockLog;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {
    @Unique private final float effMod = UniformEfficiencyModifier.UNIFORM_EFFICIENCY_MODIFIER;

    @Inject(
            method = "getCurrentPlayerStrVsBlock",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private void boostSpeedConditionally(Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        ItemStack held = ((EntityPlayer)(Object)this).getCurrentEquippedItem();

        if (held == null) {
            boolean modified = false;

            // Vanilla logs
            if (block.blockID == Block.wood.blockID) {
                cir.setReturnValue(effMod);
                modified = true;
            } else {
                // Chewed logs
                for (Block chewed : BlockLog.chewedLogArray) {
                    if (block == chewed) {
                        cir.setReturnValue(effMod);
                        modified = true;
                        break;
                    }
                }
            }

            // Loose Dirt and Sand
            if (
                block.blockID == BTWBlocks.looseSparseGrass.blockID ||
                block.blockID == BTWBlocks.looseSparseGrassSlab.blockID ||
                block.blockID == BTWBlocks.looseDirt.blockID ||
                block.blockID == BTWBlocks.looseDirtSlab.blockID ||
                block.blockID == Block.sand.blockID
            ) {
               // cir.setReturnValue(effMod);
               // modified = true;
            } else {
                // Chewed logs
                for (Block chewed : BlockLog.chewedLogArray) {
                    if (block == chewed) {
                        cir.setReturnValue(effMod);
                        modified = true;
                        break;
                    }
                }
            }
            /* Debug logging
            System.out.println("[AbbyTweaks] Called getCurrentPlayerStrVsBlock");
            System.out.println("[AbbyTweaks] Block: " + block + " (ID " + block.blockID + ")");
            System.out.println("[AbbyTweaks] Held item: " + held);
            System.out.println("[AbbyTweaks] Original strength: " + cir.getReturnValue());
            if (modified) {
                System.out.println("[AbbyTweaks] Modified bare-hand strength to: " + cir.getReturnValue());
            }
            */
        }
    }
}


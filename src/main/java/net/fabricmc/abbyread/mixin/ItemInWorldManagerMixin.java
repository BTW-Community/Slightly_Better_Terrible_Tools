package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.BlockTimer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemInWorldManager;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// I'm only using this currently with my BlockTimer

@Mixin(ItemInWorldManager.class)
public class ItemInWorldManagerMixin {
    @Shadow private int durabilityRemainingOnBlock;
    @Shadow private int partiallyDestroyedBlockX;
    @Shadow private int partiallyDestroyedBlockY;
    @Shadow private int partiallyDestroyedBlockZ;
    @Shadow private boolean isDestroyingBlock;
    @Shadow private World theWorld;

    @Shadow public EntityPlayerMP thisPlayerMP;
    // --- Our custom state (per ItemInWorldManager instance) ---
    @Unique private BlockTimer abbyread$activeTimer;

    // --- Hook when block clicking begins ---
    @Inject(method = "onBlockClicked", at = @At("HEAD"))
    private void abbyread$startTimer(int x, int y, int z, int side, CallbackInfo ci) {
        abbyread$activeTimer = new BlockTimer(x, y, z, thisPlayerMP);
    }

    // --- Hook when block is removed fully ---
    @Inject(method = "removeBlock", at = @At("RETURN"), cancellable = true)
    private void abbyread$finishTimer(int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (abbyread$activeTimer != null) {
            EntityPlayerMP player = ((ItemInWorldManager)(Object)this).thisPlayerMP;
            if (player != null && abbyread$activeTimer.finish(x, y, z, player)) {
                abbyread$activeTimer = null; // reset after successful finish
            }
        }
    }

    @Inject(
            method = "updateBlockRemoving",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/World;destroyBlockInWorldPartially(IIIII)V",
                    shift = At.Shift.AFTER
            )
    )
    private void onPartialHarvest(CallbackInfo ci) {
        if (isDestroyingBlock && abbyread$activeTimer != null) {
            int x = partiallyDestroyedBlockX;
            int y = partiallyDestroyedBlockY;
            int z = partiallyDestroyedBlockZ;

            // Only fire if this is the block we are tracking
            if (abbyread$activeTimer.x == x && abbyread$activeTimer.y == y && abbyread$activeTimer.z == z) {
                EntityPlayerMP player = thisPlayerMP;
                if (player != null && abbyread$activeTimer.finishPartial(x, y, z, player)) {
                    // optionally reset or track multiple partial events
                }
            }
        }
    }
}

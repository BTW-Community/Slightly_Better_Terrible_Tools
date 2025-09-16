package net.fabricmc.abbyread.mixin;

import btw.BTWMod;
import net.minecraft.src.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BTWMod.class)
public class SBTTMixin {
	@Inject(at = @At("HEAD"), method = "initialize", remap = false)
	private void init(CallbackInfo info) {
		System.out.println("This line is printed by an SBTT mixin!");
	}
}

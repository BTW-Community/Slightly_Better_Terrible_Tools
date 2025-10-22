package btw.community.abbyread.sbtt.mixin;

import net.minecraft.src.GuiWinGame;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiWinGame.class)
public class GuiWinGameMixin {
    @Shadow
    @Final
    @Mutable
    private static ResourceLocation minecraftLogoTexture;

    static {
        // Replace with your own resource
        minecraftLogoTexture =
                new ResourceLocation("sbtt", "textures/gui/title/minecraft.png");
    }
}

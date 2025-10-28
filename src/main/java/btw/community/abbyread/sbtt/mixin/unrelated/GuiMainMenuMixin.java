package btw.community.abbyread.sbtt.mixin.unrelated;

import net.minecraft.src.GuiMainMenu;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiMainMenu.class)
public class GuiMainMenuMixin {
    @Shadow
    @Final
    @Mutable
    private static ResourceLocation minecraftTitleTextures;

    static {
        // Replace with your own resource
        minecraftTitleTextures =
                new ResourceLocation("sbtt", "textures/gui/title/minecraft.png");
    }
}

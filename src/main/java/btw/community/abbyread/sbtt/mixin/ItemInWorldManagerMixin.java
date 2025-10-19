package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.BlockTags;
import net.minecraft.src.Block;
import net.minecraft.src.ItemInWorldManager;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInWorldManager.class)
public class ItemInWorldManagerMixin {

    @Unique boolean DEBUG = false;

    @Shadow public World theWorld;

    @Inject(method = "onBlockClicked", at = @At("RETURN"))
    private void tellMeWhatTheTagsAre(int x, int y, int z, int side, CallbackInfo ci) {
        if (DEBUG) {
            Block block = Block.blocksList[theWorld.getBlockId(x, y, z)];
            int metadata = theWorld.getBlockMetadata(x, y, z);
            System.out.println("onBlockClicked reports block tags: " + BlockTags.getTags(block, metadata));
        }
    }
}

package btw.community.abbyread.sbtt.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("FieldCanBeLocal")
@Mixin(PlayerControllerMP.class)
public class PlayerControllerMPMixin {

    @Unique private boolean DEBUG = true;

    @Unique private boolean checking = false;

    @Inject(method = "onPlayerRightClick",
            at = @At("RETURN"))
    public void sbtt$onPlayerRightClick(EntityPlayer player, World world, ItemStack stack,
                                         int x, int y, int z, int side, Vec3 hitVec,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        if (DEBUG) {
            Block block = Block.blocksList[world.getBlockId(x, y, z)];
            if (block == null) return;
            int meta = world.getBlockMetadata(x, y, z);
            System.out.println(block.getClass() + " with meta: " + meta);
            System.out.println(stack.getDisplayName() + " durability " + (stack.getMaxDamage() - stack.getItemDamage()));
        }
    }
}

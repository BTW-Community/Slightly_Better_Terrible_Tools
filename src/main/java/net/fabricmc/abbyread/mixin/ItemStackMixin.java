package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Shadow
    public int itemID;

    // helper to avoid ugly casts
    private ItemStack self() {
        return (ItemStack) (Object) this;
    }

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void onlyDamageIfEffective(World world, int blockID, int x, int y, int z, EntityPlayer player, CallbackInfo ci) {
        System.out.println("[MixinDebug] onBlockDestroyed called:");
        System.out.println("  itemID = " + itemID);
        System.out.println("  blockID = " + blockID + " (x=" + x + ", y=" + y + ", z=" + z + ")");
        Block block = Block.blocksList[blockID];
        System.out.println("  block: " + block);
        Item item = Item.itemsList[itemID];
        System.out.println("  item: " + item);
        System.out.println("  player = " + (player != null ? player.username : "null"));

        float base = Item.stick.getStrVsBlock(new ItemStack(Item.stick), world, block, x, y, z);
        float tool = item.getStrVsBlock(self(), world, block, x, y, z);
        System.out.println("  base: " + base);
        System.out.println("  tool: " + tool);
        if (tool > base) {
            System.out.println("[MixinDebug] Tool is effective, damaging item.");
            self().damageItem(1, player);
        } else {
            System.out.println("[MixinDebug] Tool not more effective than base, preventing damage.");
            ci.cancel();
        }

    }
}

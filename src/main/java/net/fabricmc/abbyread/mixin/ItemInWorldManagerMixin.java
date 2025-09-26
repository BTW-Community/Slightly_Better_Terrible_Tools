package net.fabricmc.abbyread.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInWorldManager.class)
public class ItemInWorldManagerMixin {

    // --- Shadows of private fields ---
    @Shadow private int curblockDamage;
    @Shadow private int initialDamage;
    @Shadow private boolean isDestroyingBlock;
    @Shadow private int partiallyDestroyedBlockX;
    @Shadow private int partiallyDestroyedBlockY;
    @Shadow private int partiallyDestroyedBlockZ;
    @Shadow private boolean receivedFinishDiggingPacket;
    @Shadow private int posX;
    @Shadow private int posY;
    @Shadow private int posZ;
    @Shadow private int field_73093_n;
    @Shadow private int durabilityRemainingOnBlock;

    // --- Shadows of private fields ---
    @Shadow private EnumGameType gameType;
    @Shadow public EntityPlayerMP thisPlayerMP;
    @Shadow public World theWorld;

    // --- Shadow methods ---
    @Shadow private boolean removeBlock(int x, int y, int z) { return false; }
    @Shadow public boolean isCreative() { return false; }

    @Inject(method = "updateBlockRemoving", at = @At("HEAD"), cancellable = true)
    private void onUpdateBlockRemoving(CallbackInfo ci) {
        ItemInWorldManager self = (ItemInWorldManager) (Object) this;

        // ++this.curblockDamage;
        curblockDamage++;

        int elapsedTicks; // var1
        float relativeHardnessProgress; // var4
        int damageStage; // var5

        // if (this.receivedFinishDiggingPacket)
        if (receivedFinishDiggingPacket) {
            // var1 = this.curblockDamage - this.field_73093_n;
            elapsedTicks = curblockDamage - field_73093_n;

            // int var2 = this.theWorld.getBlockId(this.posX, this.posY, this.posZ);
            int blockId = self.theWorld.getBlockId(posX, posY, posZ);

            // if (var2 == 0)
            if (blockId == 0) {
                // this.receivedFinishDiggingPacket = false;
                receivedFinishDiggingPacket = false;
            } else {
                // Block var3 = Block.blocksList[var2];
                Block block = Block.blocksList[blockId];

                // var4 = var3.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.posX, this.posY, this.posZ) * (float)(var1 + 1);
                relativeHardnessProgress =
                        block.getPlayerRelativeBlockHardness(self.thisPlayerMP,
                                self.thisPlayerMP.worldObj,
                                posX, posY, posZ)
                                * (elapsedTicks + 1);

                // var5 = (int)(var4 * 10.0F);
                damageStage = (int) (relativeHardnessProgress * 10.0F);

                // if (var5 != this.durabilityRemainingOnBlock)
                if (damageStage != durabilityRemainingOnBlock) {
                    // this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, this.posX, this.posY, this.posZ, var5);
                    self.theWorld.destroyBlockInWorldPartially(self.thisPlayerMP.entityId,
                            posX, posY, posZ,
                            damageStage);

                    // this.durabilityRemainingOnBlock = var5;
                    durabilityRemainingOnBlock = damageStage;
                }

                // if (var4 >= 1.0F)
                if (relativeHardnessProgress >= 1.0F) {
                    // this.receivedFinishDiggingPacket = false;
                    receivedFinishDiggingPacket = false;

                    // this.tryHarvestBlock(this.posX, this.posY, this.posZ, this.harvestingFromFacing);
                    self.tryHarvestBlock(posX, posY, posZ);

                }
            }
        }
        // else if (this.isDestroyingBlock)
        else if (isDestroyingBlock) {
            // var1 = this.theWorld.getBlockId(this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ);
            int blockId = self.theWorld.getBlockId(partiallyDestroyedBlockX, partiallyDestroyedBlockY, partiallyDestroyedBlockZ);

            // Block var6 = Block.blocksList[var1];
            Block block = Block.blocksList[blockId];

            // if (var6 == null)
            if (block == null) {
                // this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ, -1);
                self.theWorld.destroyBlockInWorldPartially(self.thisPlayerMP.entityId,
                        partiallyDestroyedBlockX, partiallyDestroyedBlockY, partiallyDestroyedBlockZ,
                        -1);

                // this.durabilityRemainingOnBlock = -1;
                durabilityRemainingOnBlock = -1;

                // this.isDestroyingBlock = false;
                isDestroyingBlock = false;
            } else {
                // int var7 = this.curblockDamage - this.initialDamage;
                elapsedTicks = curblockDamage - initialDamage;

                // var4 = var6.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ) * (float)(var7 + 1);
                relativeHardnessProgress =
                        block.getPlayerRelativeBlockHardness(self.thisPlayerMP,
                                self.thisPlayerMP.worldObj,
                                partiallyDestroyedBlockX, partiallyDestroyedBlockY, partiallyDestroyedBlockZ)
                                * (elapsedTicks + 1);

                // var5 = (int)(var4 * 10.0F);
                damageStage = (int) (relativeHardnessProgress * 10.0F);

                // if (var5 != this.durabilityRemainingOnBlock)
                if (damageStage != durabilityRemainingOnBlock) {
                    // this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ, var5);
                    self.theWorld.destroyBlockInWorldPartially(self.thisPlayerMP.entityId,
                            partiallyDestroyedBlockX, partiallyDestroyedBlockY, partiallyDestroyedBlockZ,
                            damageStage);

                    // this.durabilityRemainingOnBlock = var5;
                    durabilityRemainingOnBlock = damageStage;
                }
            }
        }

        // Cancel the original so it doesn't run
        ci.cancel();
    }

    @Inject(method = "onBlockClicked", at = @At("HEAD"), cancellable = true)
    private void onOnBlockClicked(int par1, int par2, int par3, int par4, CallbackInfo ci) {
        ItemInWorldManager self = (ItemInWorldManager) (Object) this;

        // if (!this.gameType.isAdventure() || this.thisPlayerMP.isCurrentToolAdventureModeExempt(par1, par2, par3))
        if (!gameType.isAdventure() || thisPlayerMP.isCurrentToolAdventureModeExempt(par1, par2, par3)) {
            // if (this.isCreative())
            if (isCreative()) {
                // if (!this.theWorld.extinguishFire((EntityPlayer)null, par1, par2, par3, par4))
                if (!theWorld.extinguishFire((EntityPlayer)null, par1, par2, par3, par4)) {
                    // this.tryHarvestBlock(par1, par2, par3);
                    self.tryHarvestBlock(par1, par2, par3);
                }
            } else {
                // this.theWorld.extinguishFire((EntityPlayer)null, par1, par2, par3, par4);
                theWorld.extinguishFire((EntityPlayer)null, par1, par2, par3, par4);

                // this.initialDamage = this.curblockDamage;
                initialDamage = curblockDamage;

                // float var5 = 1.0F;
                float blockHardness = 1.0F;

                // int var6 = this.theWorld.getBlockId(par1, par2, par3);
                int blockId = theWorld.getBlockId(par1, par2, par3);

                // if (var6 > 0)
                if (blockId > 0) {
                    // Block.blocksList[var6].onBlockClicked(this.theWorld, par1, par2, par3, this.thisPlayerMP);
                    Block.blocksList[blockId].onBlockClicked(theWorld, par1, par2, par3, thisPlayerMP);

                    // var5 = Block.blocksList[var6].getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, par1, par2, par3);
                    blockHardness = Block.blocksList[blockId].getPlayerRelativeBlockHardness(thisPlayerMP, thisPlayerMP.worldObj, par1, par2, par3);
                }

                // if (var6 > 0 && var5 >= 1.0F)
                if (blockId > 0 && blockHardness >= 1.0F) {
                    // this.tryHarvestBlock(par1, par2, par3);
                    self.tryHarvestBlock(par1, par2, par3);
                } else {
                    // this.isDestroyingBlock = true;
                    isDestroyingBlock = true;

                    // this.partiallyDestroyedBlockX = par1;
                    partiallyDestroyedBlockX = par1;

                    // this.partiallyDestroyedBlockY = par2;
                    partiallyDestroyedBlockY = par2;

                    // this.partiallyDestroyedBlockZ = par3;
                    partiallyDestroyedBlockZ = par3;

                    // int var7 = (int)(var5 * 10.0F);
                    int damageStage = (int)(blockHardness * 10.0F);

                    // this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, par1, par2, par3, var7);
                    theWorld.destroyBlockInWorldPartially(thisPlayerMP.entityId, par1, par2, par3, damageStage);

                    // this.durabilityRemainingOnBlock = var7;
                    durabilityRemainingOnBlock = damageStage;
                }
            }
        }

        // Cancel the original so it doesn't run
        ci.cancel();
    }

    @Inject(method = "uncheckedTryHarvestBlock(III)V", at = @At("HEAD"), cancellable = true)
    private void onUncheckedTryHarvestBlock(int par1, int par2, int par3, CallbackInfo ci) {
        ItemInWorldManager self = (ItemInWorldManager) (Object) this;

        // if (par1 == this.partiallyDestroyedBlockX && par2 == this.partiallyDestroyedBlockY && par3 == this.partiallyDestroyedBlockZ)
        if (par1 == partiallyDestroyedBlockX && par2 == partiallyDestroyedBlockY && par3 == partiallyDestroyedBlockZ) {
            // int var4 = this.curblockDamage - this.initialDamage;
            int elapsedDamage = curblockDamage - initialDamage;

            // int var5 = this.theWorld.getBlockId(par1, par2, par3);
            int blockId = theWorld.getBlockId(par1, par2, par3);

            // if (var5 != 0)
            if (blockId != 0) {
                // Block var6 = Block.blocksList[var5];
                Block block = Block.blocksList[blockId];

                // float var7 = var6.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, par1, par2, par3) * (float)(var4 + 1);
                float totalProgress = block.getPlayerRelativeBlockHardness(thisPlayerMP, thisPlayerMP.worldObj, par1, par2, par3) * (float)(elapsedDamage + 1);

                // if (var7 >= 0.7F)
                if (totalProgress >= 0.7F) {
                    // this.isDestroyingBlock = false;
                    isDestroyingBlock = false;

                    // this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, par1, par2, par3, -1);
                    theWorld.destroyBlockInWorldPartially(thisPlayerMP.entityId, par1, par2, par3, -1);

                    // this.tryHarvestBlock(par1, par2, par3);
                    self.tryHarvestBlock(par1, par2, par3);
                }
                // else if (!this.receivedFinishDiggingPacket)
                else if (!receivedFinishDiggingPacket) {
                    // this.isDestroyingBlock = false;
                    isDestroyingBlock = false;

                    // this.receivedFinishDiggingPacket = true;
                    receivedFinishDiggingPacket = true;

                    // this.posX = par1;
                    posX = par1;

                    // this.posY = par2;
                    posY = par2;

                    // this.posZ = par3;
                    posZ = par3;

                    // this.field_73093_n = this.initialDamage;
                    field_73093_n = initialDamage;
                }
            }
        }

        // Cancel the original so it doesn't run
        ci.cancel();
    }

    @Inject(method = "cancelDestroyingBlock", at = @At("HEAD"), cancellable = true)
    private void onCancelDestroyingBlock(int par1, int par2, int par3, CallbackInfo ci) {
        ItemInWorldManager self = (ItemInWorldManager) (Object) this;

        // this.isDestroyingBlock = false;
        isDestroyingBlock = false;

        // this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ, -1);
        theWorld.destroyBlockInWorldPartially(thisPlayerMP.entityId, partiallyDestroyedBlockX, partiallyDestroyedBlockY, partiallyDestroyedBlockZ, -1);

        // Cancel the original so it doesn't run
        ci.cancel();
    }

    @Inject(method = "removeBlock", at = @At("HEAD"), cancellable = true)
    private void onRemoveBlock(int par1, int par2, int par3, CallbackInfoReturnable<Boolean> cir) {
        ItemInWorldManager self = (ItemInWorldManager) (Object) this;

        // Block var4 = Block.blocksList[this.theWorld.getBlockId(par1, par2, par3)];
        Block block = Block.blocksList[theWorld.getBlockId(par1, par2, par3)];

        // int var5 = this.theWorld.getBlockMetadata(par1, par2, par3);
        int blockMetadata = theWorld.getBlockMetadata(par1, par2, par3);

        // if (var4 != null)
        if (block != null) {
            // var4.onBlockHarvested(this.theWorld, par1, par2, par3, var5, this.thisPlayerMP);
            block.onBlockHarvested(theWorld, par1, par2, par3, blockMetadata, thisPlayerMP);
        }

        // boolean var6 = this.theWorld.setBlockToAir(par1, par2, par3);
        boolean blockRemoved = theWorld.setBlockToAir(par1, par2, par3);

        // if (var4 != null && var6)
        if (block != null && blockRemoved) {
            // var4.onBlockDestroyedByPlayer(this.theWorld, par1, par2, par3, var5);
            block.onBlockDestroyedByPlayer(theWorld, par1, par2, par3, blockMetadata);
        }

        // return var6;
        cir.setReturnValue(blockRemoved);
        return;
    }

    @Inject(method = "tryHarvestBlock(III)Z", at = @At("HEAD"), cancellable = true)
    private void onTryHarvestBlock(int par1, int par2, int par3, CallbackInfoReturnable<Boolean> cir) {
        ItemInWorldManager self = (ItemInWorldManager) (Object) this;

        // if (this.gameType.isAdventure() && !this.thisPlayerMP.isCurrentToolAdventureModeExempt(par1, par2, par3))
        if (gameType.isAdventure() && !thisPlayerMP.isCurrentToolAdventureModeExempt(par1, par2, par3)) {
            // return false;
            cir.setReturnValue(false);
            return;
        }
        // else if (this.gameType.isCreative() && this.thisPlayerMP.getHeldItem() != null && this.thisPlayerMP.getHeldItem().getItem() instanceof ItemSword)
        else if (gameType.isCreative() && thisPlayerMP.getHeldItem() != null && thisPlayerMP.getHeldItem().getItem() instanceof ItemSword) {
            // return false;
            cir.setReturnValue(false);
            return;
        } else {
            // int var4 = this.theWorld.getBlockId(par1, par2, par3);
            int blockId = theWorld.getBlockId(par1, par2, par3);

            // int var5 = this.theWorld.getBlockMetadata(par1, par2, par3);
            int blockMetadata = theWorld.getBlockMetadata(par1, par2, par3);

            // this.theWorld.playAuxSFXAtEntity(this.thisPlayerMP, 2001, par1, par2, par3, var4 + (this.theWorld.getBlockMetadata(par1, par2, par3) << 12));
            theWorld.playAuxSFXAtEntity(thisPlayerMP, 2001, par1, par2, par3, blockId + (theWorld.getBlockMetadata(par1, par2, par3) << 12));

            // boolean var6 = this.removeBlock(par1, par2, par3);
            boolean blockRemoved = removeBlock(par1, par2, par3);

            // if (this.isCreative())
            if (isCreative()) {
                // this.thisPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(par1, par2, par3, this.theWorld));
                thisPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(par1, par2, par3, theWorld));
            } else {
                // ItemStack var7 = this.thisPlayerMP.getCurrentEquippedItem();
                ItemStack currentTool = thisPlayerMP.getCurrentEquippedItem();

                // boolean var8 = this.thisPlayerMP.canHarvestBlock(Block.blocksList[var4]);
                boolean canHarvest = self.thisPlayerMP.canHarvestBlock(Block.blocksList[blockId], posX, posY, posZ);

                // if (var7 != null)
                if (currentTool != null) {
                    // var7.onBlockDestroyed(this.theWorld, var4, par1, par2, par3, this.thisPlayerMP);
                    currentTool.onBlockDestroyed(theWorld, blockId, par1, par2, par3, thisPlayerMP);

                    // if (var7.stackSize == 0)
                    if (currentTool.stackSize == 0) {
                        // this.thisPlayerMP.destroyCurrentEquippedItem();
                        thisPlayerMP.destroyCurrentEquippedItem();
                    }
                }

                // if (var6 && var8)
                if (blockRemoved && canHarvest) {
                    // Block.blocksList[var4].harvestBlock(this.theWorld, this.thisPlayerMP, par1, par2, par3, var5);
                    Block.blocksList[blockId].harvestBlock(theWorld, thisPlayerMP, par1, par2, par3, blockMetadata);
                }
            }

            // return var6;
            cir.setReturnValue(blockRemoved);
            return;
        }
    }

    @Inject(method = "tryUseItem", at = @At("HEAD"), cancellable = true)
    private void onTryUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack, CallbackInfoReturnable<Boolean> cir) {
        ItemInWorldManager self = (ItemInWorldManager) (Object) this;

        // int var4 = par3ItemStack.stackSize;
        int originalStackSize = par3ItemStack.stackSize;

        // int var5 = par3ItemStack.getItemDamage();
        int originalDamage = par3ItemStack.getItemDamage();

        // ItemStack var6 = par3ItemStack.useItemRightClick(par2World, par1EntityPlayer);
        ItemStack resultStack = par3ItemStack.useItemRightClick(par2World, par1EntityPlayer);

        // if (var6 == par3ItemStack && (var6 == null || var6.stackSize == var4 && var6.getMaxItemUseDuration() <= 0 && var6.getItemDamage() == var5))
        if (resultStack == par3ItemStack && (resultStack == null || resultStack.stackSize == originalStackSize && resultStack.getMaxItemUseDuration() <= 0 && resultStack.getItemDamage() == originalDamage)) {
            // return false;
            cir.setReturnValue(false);
            return;
        } else {
            // par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = var6;
            par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = resultStack;

            // if (this.isCreative())
            if (self.isCreative()) {
                // var6.stackSize = var4;
                resultStack.stackSize = originalStackSize;

                // if (var6.isItemStackDamageable())
                if (resultStack.isItemStackDamageable()) {
                    // var6.setItemDamage(var5);
                    resultStack.setItemDamage(originalDamage);
                }
            }

            // if (var6.stackSize == 0)
            if (resultStack.stackSize == 0) {
                // par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = null;
                par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = null;
            }

            // if (!par1EntityPlayer.isUsingItem())
            if (!par1EntityPlayer.isUsingItem()) {
                // ((EntityPlayerMP)par1EntityPlayer).sendContainerToPlayer(par1EntityPlayer.inventoryContainer);
                ((EntityPlayerMP)par1EntityPlayer).sendContainerToPlayer(par1EntityPlayer.inventoryContainer);
            }

            // return true;
            cir.setReturnValue(true);
            return;
        }
    }

    @Inject(method = "activateBlockOrUseItem", at = @At("HEAD"), cancellable = true)
    private void onActivateBlockOrUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack, int par4, int par5, int par6, int par7, float par8, float par9, float par10, CallbackInfoReturnable<Boolean> cir) {
        ItemInWorldManager self = (ItemInWorldManager) (Object) this;

        // int var11;
        int blockId;

        // if (!par1EntityPlayer.isSneaking() || par1EntityPlayer.getHeldItem() == null)
        if (!par1EntityPlayer.isSneaking() || par1EntityPlayer.getHeldItem() == null) {
            // var11 = par2World.getBlockId(par4, par5, par6);
            blockId = par2World.getBlockId(par4, par5, par6);

            // if (var11 > 0 && Block.blocksList[var11].onBlockActivated(par2World, par4, par5, par6, par1EntityPlayer, par7, par8, par9, par10))
            if (blockId > 0 && Block.blocksList[blockId].onBlockActivated(par2World, par4, par5, par6, par1EntityPlayer, par7, par8, par9, par10)) {
                // return true;
                cir.setReturnValue(true);
                return;
            }
        }

        // if (par3ItemStack == null)
        if (par3ItemStack == null) {
            // return false;
            cir.setReturnValue(false);
            return;
        }
        // else if (this.isCreative())
        else if (self.isCreative()) {
            // var11 = par3ItemStack.getItemDamage();
            int originalDamage = par3ItemStack.getItemDamage();

            // int var12 = par3ItemStack.stackSize;
            int originalStackSize = par3ItemStack.stackSize;

            // boolean var13 = par3ItemStack.tryPlaceItemIntoWorld(par1EntityPlayer, par2World, par4, par5, par6, par7, par8, par9, par10);
            boolean placeResult = par3ItemStack.tryPlaceItemIntoWorld(par1EntityPlayer, par2World, par4, par5, par6, par7, par8, par9, par10);

            // par3ItemStack.setItemDamage(var11);
            par3ItemStack.setItemDamage(originalDamage);

            // par3ItemStack.stackSize = var12;
            par3ItemStack.stackSize = originalStackSize;

            // return var13;
            cir.setReturnValue(placeResult);
            return;
        } else {
            // return par3ItemStack.tryPlaceItemIntoWorld(par1EntityPlayer, par2World, par4, par5, par6, par7, par8, par9, par10);
            boolean result = par3ItemStack.tryPlaceItemIntoWorld(par1EntityPlayer, par2World, par4, par5, par6, par7, par8, par9, par10);
            cir.setReturnValue(result);
            return;
        }
    }
}

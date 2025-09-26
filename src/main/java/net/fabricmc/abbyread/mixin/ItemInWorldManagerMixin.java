package net.fabricmc.abbyread.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.ItemInWorldManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInWorldManager.class)
public class ItemInWorldManagerMixin {

    @Shadow
    private int curblockDamage;
    @Shadow
    private int initialDamage;
    @Shadow
    private boolean isDestroyingBlock;
    @Shadow
    private int partiallyDestroyedBlockX;
    @Shadow
    private int partiallyDestroyedBlockY;
    @Shadow
    private int partiallyDestroyedBlockZ;
    @Shadow
    private boolean receivedFinishDiggingPacket;
    @Shadow
    private int posX;
    @Shadow
    private int posY;
    @Shadow
    private int posZ;
    @Shadow
    private int field_73093_n;
    @Shadow
    private int durabilityRemainingOnBlock;

    @Unique public int getCurblockDamage() { return ((ItemInWorldManagerAccessor)this).getCurblockDamage(); }
    @Unique public void setCurblockDamage(int v) { ((ItemInWorldManagerAccessor)this).setCurblockDamage(v); }

    @Unique public int getInitialDamage() { return ((ItemInWorldManagerAccessor)this).getInitialDamage(); }
    @Unique public void setInitialDamage(int v) { ((ItemInWorldManagerAccessor)this).setInitialDamage(v); }

    @Unique public boolean getReceivedFinishDiggingPacket() { return ((ItemInWorldManagerAccessor)this).getReceivedFinishDiggingPacket(); }
    @Unique public void setReceivedFinishDiggingPacket(boolean v) { ((ItemInWorldManagerAccessor)this).setReceivedFinishDiggingPacket(v); }

    @Unique public int getDurabilityRemainingOnBlock() { return ((ItemInWorldManagerAccessor)this).getDurabilityRemainingOnBlock(); }
    @Unique public void setDurabilityRemainingOnBlock(int v) { ((ItemInWorldManagerAccessor)this).setDurabilityRemainingOnBlock(v); }

    @Unique public int getPosX() { return ((ItemInWorldManagerAccessor)this).getPosX(); }
    @Unique public void setPosX(int v) { ((ItemInWorldManagerAccessor)this).setPosX(v); }
    @Unique public int getPosY() { return ((ItemInWorldManagerAccessor)this).getPosY(); }
    @Unique public void setPosY(int v) { ((ItemInWorldManagerAccessor)this).setPosY(v); }
    @Unique public int getPosZ() { return ((ItemInWorldManagerAccessor)this).getPosZ(); }
    @Unique public void setPosZ(int v) { ((ItemInWorldManagerAccessor)this).setPosZ(v); }

    @Unique public int getHarvestingFromFacing() { return ((ItemInWorldManagerAccessor)this).getHarvestingFromFacing(); }
    @Unique public void setHarvestingFromFacing(int v) { ((ItemInWorldManagerAccessor)this).setHarvestingFromFacing(v); }

    @Unique public boolean getIsDestroyingBlock() { return ((ItemInWorldManagerAccessor)this).getIsDestroyingBlock(); }
    @Unique public void setIsDestroyingBlock(boolean v) { ((ItemInWorldManagerAccessor)this).setIsDestroyingBlock(v); }

    @Unique public int getPartiallyDestroyedBlockX() { return ((ItemInWorldManagerAccessor)this).getPartiallyDestroyedBlockX(); }
    @Unique public void setPartiallyDestroyedBlockX(int v) { ((ItemInWorldManagerAccessor)this).setPartiallyDestroyedBlockX(v); }
    @Unique public int getPartiallyDestroyedBlockY() { return ((ItemInWorldManagerAccessor)this).getPartiallyDestroyedBlockY(); }
    @Unique public void setPartiallyDestroyedBlockY(int v) { ((ItemInWorldManagerAccessor)this).setPartiallyDestroyedBlockY(v); }
    @Unique public int getPartiallyDestroyedBlockZ() { return ((ItemInWorldManagerAccessor)this).getPartiallyDestroyedBlockZ(); }
    @Unique public void setPartiallyDestroyedBlockZ(int v) { ((ItemInWorldManagerAccessor)this).setPartiallyDestroyedBlockZ(v); }

    @Inject(method = "updateBlockRemoving", at = @At("HEAD"), cancellable = true)
    private void MixinCopyOfUpdateBlockRemoving(CallbackInfo ci) {
        // Create "self" in place of using "this"
        ItemInWorldManager self = (ItemInWorldManager) (Object) this;

        setCurblockDamage(getCurblockDamage() + 1);  // ++this.curblockDamage;
        int var1; // int var1;
        float relativeHardnessProgress; //float var4;
        int damageStage; int var5;

        if (getReceivedFinishDiggingPacket()) { // if (this.receivedFinishDiggingPacket) {
            var1 = getCurblockDamage() - getInitialDamage(); // var1 = this.curblockDamage - this.field_73093_n;
            int blockID = self.theWorld.getBlockId(getPosX(), getPosY(), getPosZ()); // int var2 = this.theWorld.getBlockId(this.posX, this.posY, this.posZ);

            if (blockID == 0) { // if (var2 == 0) {
                setReceivedFinishDiggingPacket(false); // this.receivedFinishDiggingPacket = false;
            } else {
                Block block = Block.blocksList[blockID]; // Block var3 = Block.blocksList[var2];
                // var4 =
                //      var3.getPlayerRelativeBlockHardness(this.thisPlayerMP,
                //              this.thisPlayerMP.worldObj,
                //              this.posX, this.posY, this.posZ)
                //              * (float)(var1 + 1);
                relativeHardnessProgress =
                        block.getPlayerRelativeBlockHardness(self.thisPlayerMP,
                                self.thisPlayerMP.worldObj,
                                getPosX(), getPosY(), getPosZ())
                                * (float) (var1 + 1);

                damageStage = (int) (relativeHardnessProgress * 10.0F); //var5 = (int)(var4 * 10.0F);

                if (damageStage != getDurabilityRemainingOnBlock()) { // if (var5 != this.durabilityRemainingOnBlock) {
                    // this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId,
                    //      this.posX, this.posY, this.posZ,
                    //      var5);
                    self.theWorld.destroyBlockInWorldPartially(self.thisPlayerMP.entityId,
                            getPosX(), getPosY(), getPosZ(),
                            damageStage);
                    setDurabilityRemainingOnBlock(damageStage); // this.durabilityRemainingOnBlock = var5;
                }

                if (relativeHardnessProgress >= 1.0F) { // if (var4 >= 1.0F) {
                    setReceivedFinishDiggingPacket(false); // this.receivedFinishDiggingPacket = false;
                    self.tryHarvestBlock(getPosX(), getPosY(), getPosZ()); // this.tryHarvestBlock(this.posX, this.posY, this.posZ);
                }
            }
        } else if (getIsDestroyingBlock()) { // } else if (this.isDestroyingBlock) {
            // var1 = this.theWorld.getBlockId(this.partiallyDestroyedBlockX,
            //      this.partiallyDestroyedBlockY,
            //      this.partiallyDestroyedBlockZ);
            var1 = self.theWorld.getBlockId(getPartiallyDestroyedBlockX(),
                    getPartiallyDestroyedBlockY(),
                    getPartiallyDestroyedBlockZ());

            Block blockVar6 = Block.blocksList[var1]; // Block var6 = Block.blocksList[var1];

            if (blockVar6 == null) { // if (var6 == null) {
                // this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId,
                //      this.partiallyDestroyedBlockX,
                //      this.partiallyDestroyedBlockY,
                //      this.partiallyDestroyedBlockZ, -1);
                self.theWorld.destroyBlockInWorldPartially(self.thisPlayerMP.entityId,
                        getPartiallyDestroyedBlockX(),
                        getPartiallyDestroyedBlockY(),
                        getPartiallyDestroyedBlockZ(), -1);
                setDurabilityRemainingOnBlock(-1); // this.durabilityRemainingOnBlock = -1;
                setIsDestroyingBlock(false); // this.isDestroyingBlock = false;
            } else {
                int var7 = getCurblockDamage() - getInitialDamage(); // int var7 = this.curblockDamage - this.initialDamage;
                // var4 =
                //      var6.getPlayerRelativeBlockHardness(this.thisPlayerMP,
                //              this.thisPlayerMP.worldObj,
                //              this.partiallyDestroyedBlockX,
                //              this.partiallyDestroyedBlockY,
                //              this.partiallyDestroyedBlockZ)
                //              * (float)(var7 + 1);
                relativeHardnessProgress =
                        blockVar6.getPlayerRelativeBlockHardness(self.thisPlayerMP,
                                self.thisPlayerMP.worldObj,
                                getPartiallyDestroyedBlockX(),
                                getPartiallyDestroyedBlockY(),
                                getPartiallyDestroyedBlockZ())
                                * (float) (var7 + 1);

                damageStage = (int) (relativeHardnessProgress * 10.0F); // var5 = (int)(var4 * 10.0F);

                if (damageStage != getDurabilityRemainingOnBlock()) { // if (var5 != this.durabilityRemainingOnBlock) {
                    // this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId,
                    //      this.partiallyDestroyedBlockX,
                    //      this.partiallyDestroyedBlockY,
                    //      this.partiallyDestroyedBlockZ,
                    //      var5);
                    self.theWorld.destroyBlockInWorldPartially(self.thisPlayerMP.entityId,
                            getPartiallyDestroyedBlockX(),
                            getPartiallyDestroyedBlockY(),
                            getPartiallyDestroyedBlockZ(),
                            damageStage);
                    setDurabilityRemainingOnBlock(damageStage); // this.durabilityRemainingOnBlock = var5;
                }
            }
        }

        // Cancel the original so it doesn't run
        ci.cancel();
    }
}
package btw.community.abbyread;

import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.EntityPlayerMP;
import btw.BTWMod;

public class BlockTimer {
    public final int x, y, z;
    public final long startTime;
    public int counter;

    public BlockTimer(int x, int y, int z, EntityPlayerMP player) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.startTime = System.currentTimeMillis();
        this.counter = 0;
    }

    public boolean finish(int x, int y, int z, EntityPlayerMP player) {
        if (this.x == x && this.y == y && this.z == z) {
            long elapsed = System.currentTimeMillis() - startTime;
            double seconds = elapsed / 1000.0;
            player.sendChatToPlayer(ChatMessageComponent.createFromText(
                    "Block destroyed in about " + String.format("%.2f", seconds) + " seconds."
            ));
            return true; // success, timer finished
        }
        return false; // not the same block
    }
    public boolean finishPartial(int x, int y, int z, EntityPlayerMP player) {
        counter++;
        if (counter == 10 && this.x == x && this.y == y && this.z == z) {
            long elapsed = System.currentTimeMillis() - startTime;
            double seconds = elapsed / 1000.0;
            player.sendChatToPlayer(ChatMessageComponent.createFromText(
                    "Block harvested in about " + String.format("%.1f", seconds) + " seconds."
            ));
            return true; // success, timer finished
        }
        return false; // not the same block
    }
}

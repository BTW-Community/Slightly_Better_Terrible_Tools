package btw.community.abbyread;

import btw.AddonHandler;
import btw.BTWAddon;

public class SBTT extends BTWAddon {
    private static SBTT instance;

    public SBTT() {
        super();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }
}
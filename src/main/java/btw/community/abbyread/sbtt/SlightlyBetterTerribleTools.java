package btw.community.abbyread.sbtt;

import btw.AddonHandler;
import btw.BTWAddon;

public class SlightlyBetterTerribleTools extends BTWAddon {
    private final float effMod = EfficiencyHelper.effMod;
    private final String percentage = String.format("%.0f%%", effMod * 100 - 100);
    public SlightlyBetterTerribleTools() {
        super();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " with " + percentage + " Boost Initializing...");
    }

    @Override
    public String getModID() {
        return "sbtt"; // or whatever ID you want for your addon
    }
}
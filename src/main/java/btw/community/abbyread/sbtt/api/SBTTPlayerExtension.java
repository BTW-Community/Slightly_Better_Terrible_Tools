package btw.community.abbyread.sbtt.api;

public interface SBTTPlayerExtension {
    void sbtt_setItemUsedFlag(boolean value, int damageAmount);
    boolean sbtt_consumeItemUsedFlag();
    int sbtt_getPendingItemDamage();
}
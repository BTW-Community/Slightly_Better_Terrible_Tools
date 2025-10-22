package btw.community.abbyread.sbtt.api;

public interface SBTTPlayerExtension {
    void sbtt_setJustConvertedFlag(boolean value);
    boolean sbtt_consumeJustConvertedFlag();
}
package btw.community.abbyread.sbtt.api;

/**
 * Extension interface added to players via mixin to track custom
 * item usage events and variable damage amounts from interactions.
 */
public interface SBTTPlayerExtension {

    /**
     * Sets whether the item was just used by a special interaction,
     * and how much damage that use should apply.
     */
    void sbtt_setItemUsedFlag(boolean value, int damageAmount);

    /**
     * Consumes and clears the "item used" flag, returning whether
     * a tool interaction occurred since the last check.
     */
    boolean sbtt_consumeItemUsedFlag();

    /**
     * Consumes and clears the recorded damage amount, returning
     * how much damage should be applied for the last interaction.
     */
    int sbtt_consumeItemUsedDamage();
}

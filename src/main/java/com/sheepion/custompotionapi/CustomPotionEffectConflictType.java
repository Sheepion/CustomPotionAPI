package com.sheepion.custompotionapi;

/**
 * project name: CustomPotionAPI
 * package: com.sheepion.custompotionapi
 *
 * @author Sheepion
 */
public enum CustomPotionEffectConflictType {
    /**
     * ignore the effect if the player already has the effect with the same type.
     */
    IGNORE,
    /**
     * remove the earlier effect if the player already has the effect with the same type
     * and add the new effect.
     */
    OVERWRITE,
    /**
     * simply add the new effect even already has one with the same type.
     */
    STACK,
    /**
     * add the duration to the earlier effect
     * if the player already has the effect with the same type and same amplifier.
     */
    ADD_DURATION_ON_SAME_AMPLIFIER,
}

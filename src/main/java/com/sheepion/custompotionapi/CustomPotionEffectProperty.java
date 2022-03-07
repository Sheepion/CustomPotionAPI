package com.sheepion.custompotionapi;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * presents a custom potion effect' properties
 *
 * @author Sheepion
 */
public class CustomPotionEffectProperty {
    /**
     * the potion item that carries the effect
     */
    private final ItemStack potion;

    /**
     * the effect's duration
     */
    private final int duration;

    /**
     * the effect's rest duration
     */
    private int restDuration;

    /**
     * the effect's amplifier
     */
    private int amplifier;

    private boolean ambient;

    /**
     * the effect's check interval
     */
    private final int checkInterval;

    /**
     * the ticks before the effect starts.
     */
    private final int delay;

    /**
     * get the mutable potion item that carries the effect
     *
     * @return the potion item
     */
    public ItemStack getPotion() {
        return potion;
    }

    /**
     * @param potion        the potion item that carries the effect
     * @param duration      the effect's duration
     * @param amplifier     the effect's amplifier
     * @param checkInterval the effect's check interval
     * @param delay         the ticks before the effect starts.
     */
    public CustomPotionEffectProperty(@Nullable ItemStack potion, int duration, int amplifier, int checkInterval, int delay) {
        this(potion, duration, duration, amplifier, false, checkInterval, delay);
    }

    /**
     * @param potion        the potion item that carries the effect
     * @param duration      the effect's duration
     * @param restDuration  the effect's rest duration
     * @param amplifier     the effect's amplifier
     * @param ambient       the effect's ambient
     * @param checkInterval the effect's check interval
     * @param delay         the ticks before the effect starts.
     */
    public CustomPotionEffectProperty(@Nullable ItemStack potion, int duration, int restDuration, int amplifier, boolean ambient, int checkInterval, int delay) {
        this.potion = potion;
        this.duration = duration;
        this.restDuration = restDuration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.checkInterval = checkInterval;
        this.delay = delay;
    }

    /**
     * @return the effect's duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @return the effect's rest duration BEFORE this time's effect being called.
     */
    public int getRestDuration() {
        return restDuration;
    }

    /**
     * set the effect's rest duration<br>
     * edit this value to change the effect's rest duration
     *
     * @param restDuration the effect's new rest duration
     */
    public void setRestDuration(int restDuration) {
        this.restDuration = restDuration;
    }

    /**
     * @return the effect's amplifier
     */
    public int getAmplifier() {
        return amplifier;
    }

    /**
     * set the effect's amplifier<br>
     * edit this value to change the effect's amplifier
     *
     * @param amplifier the effect's new amplifier
     */
    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    /**
     * @return if the effect is ambient
     */
    public boolean isAmbient() {
        return ambient;
    }

    /**
     * set if the effect is ambient<br>
     * edit this value to change the effect's ambient
     *
     * @param ambient the effect's new ambient
     */
    public void setAmbient(boolean ambient) {
        this.ambient = ambient;
    }

    /**
     * @return the effect's check interval.
     */
    public int getCheckInterval() {
        return checkInterval;
    }

    /**
     * @return the ticks before the effect starts.
     */
    public int getDelay() {
        return delay;
    }

    public CustomPotionEffectProperty clone() {
        return new CustomPotionEffectProperty(potion, duration, restDuration, amplifier, ambient, checkInterval, delay);
    }
}

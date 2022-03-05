package com.sheepion.custompotionapi;

import io.papermc.paper.potion.PotionMix;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;

/**
 * project name: CustomPotionAPI
 * package: com.sheepion.custompotionapi
 * <p>
 * you need register your effect type by CustomPotionManager#registerPotionEffectType(CustomPotionEffectType) to make it work.
 * to apply this effect to a living entity, call CustomPotionEffectType#apply(LivingEntity)
 *
 * @author Sheepion
 */
public interface CustomPotionEffectType {

    /**
     * @return the namespaced key of the potion effect
     */
    NamespacedKey getKey();

    /**
     * check if the entity can be applied by this potion effect
     *
     * @param entity the entity to check
     * @return true if the entity can be applied by this potion effect
     */
    boolean canBeApplied(LivingEntity entity);

    /**
     * if the effect can be removed by milk
     * this will be automatically called by CustomPotionManager when entity drinks milk.
     *
     * @param entity the entity that drinks milk.
     * @param duration      the duration of the potion effect.
     * @param amplifier     the amplifier of the potion effect.
     * @param checkInterval the check interval of the potion effect.
     * @return true if the effect can be removed by milk
     */
    boolean canBeRemovedByMilk(LivingEntity entity, int duration, int amplifier, int checkInterval);

    /**
     * the potion effect to the entity
     * this method will be called every %checkInterval% ticks for %duration% ticks.
     *
     * @param entity        the entity to apply the potion effect
     * @param duration      the REST duration of the potion effect.
     *                      can be zero if the effect will be removed immediately after this method is called.
     * @param amplifier     the amplifier of the potion effect, you can use this to adjust the effect.
     * @param checkInterval the interval of the potion effect.
     */
    void effect(LivingEntity entity, int duration, int amplifier, int checkInterval);

    /**
     * get all the potion mix recipes.
     * those recipes will be automatically registered when you register this potion effect type.
     *
     * @return the potion mix recipes
     */
    ArrayList<PotionMix> potionMixes();

    /**
     * get the display name of the potion item
     * used when create the potion item by CustomPotionManager#getPotion(...)
     *
     * @param duration      the duration of the potion effect.
     * @param amplifier     the amplifier of the potion effect.
     * @param checkInterval the check interval of the potion effect.
     * @return the display name
     */
    Component potionDisplayName(int duration, int amplifier, int checkInterval);

    /**
     * get the lore of the potion item
     * used when create the potion item by CustomPotionManager#getPotion(...)
     *
     * @param duration      the duration of the potion effect.
     * @param amplifier     the amplifier of the potion effect.
     * @param checkInterval the check interval of the potion effect.
     * @return the lore
     */
    ArrayList<Component> potionLore(int duration, int amplifier, int checkInterval);

    /**
     * get the color of the potion item
     *
     * @param duration      the duration of the potion effect.
     * @param amplifier     the amplifier of the potion effect.
     * @param checkInterval the check interval of the potion effect.
     * @return the color
     */
    Color potionColor(int duration, int amplifier, int checkInterval);

    /**
     * if the potion has enchanted glow
     *
     * @return true if the potion has enchanted glow
     */
    boolean potionEnchanted();

    /**
     * get the lore of the splash potion item
     * used when create the potion item by CustomPotionManager#getSplashPotion(...)
     *
     * @param duration      the duration of the potion effect.
     * @param amplifier     the amplifier of the potion effect.
     * @param checkInterval the check interval of the potion effect.
     * @return the lore
     */
    ArrayList<Component> splashPotionLore(int duration, int amplifier, int checkInterval);

    /**
     * get the lore of the splash potion item
     * used when create the potion item by CustomPotionManager#getSplashPotion(...)
     *
     * @param duration      the duration of the potion effect.
     * @param amplifier     the amplifier of the potion effect.
     * @param checkInterval the check interval of the potion effect.
     * @return the display name
     */
    Component splashPotionDisplayName(int duration, int amplifier, int checkInterval);

    /**
     * get the color of the splash potion item
     *
     * @param duration      the duration of the potion effect.
     * @param amplifier     the amplifier of the potion effect.
     * @param checkInterval the check interval of the potion effect.
     * @return the color
     */
    Color splashPotionColor(int duration, int amplifier, int checkInterval);

    /**
     * if the splash potion has enchanted glow
     *
     * @return true if the splash potion has enchanted glow
     */
    boolean splashPotionEnchanted();
}

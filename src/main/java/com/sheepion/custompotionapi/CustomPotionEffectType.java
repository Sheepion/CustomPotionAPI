package com.sheepion.custompotionapi;

import io.papermc.paper.potion.PotionMix;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;

/**
 * project name: CustomPotionAPI
 * package: com.sheepion.custompotionapi
 *
 * you need register your effect type by CustomPotionManager#registerPotionEffectType(CustomPotionEffectType) to make it work.
 * to apply this effect to a living entity, call CustomPotionEffectType#apply(LivingEntity)
 *
 * @author Sheepion
 * @date 3/4/2022
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
     * the potion effect to the entity
     * this method will be called every %checkInterval% ticks for %duration% ticks.
     *
     * @param entity        the entity to apply the potion effect
     * @param duration      the REST duration of the potion effect.
     *                      can be zero if the effect will be removed immediately after this method is called.
     * @param checkInterval the interval of the potion effect.
     * @param amplifier     the amplifier of the potion effect, you can use this to adjust the effect.
     */
    void effect(LivingEntity entity, int duration, int checkInterval, int amplifier);

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
     * @return the display name
     */
    Component potionDisplayName();

    /**
     * get the lore of the potion item
     * used when create the potion item by CustomPotionManager#getPotion(...)
     *
     * @return the lore
     */
    ArrayList<Component> potionLore();

    /**
     * get the lore of the splash potion item
     * used when create the potion item by CustomPotionManager#getSplashPotion(...)
     *
     * @return the lore
     */
    ArrayList<Component> splashPotionLore();

    /**
     * get the lore of the splash potion item
     * used when create the potion item by CustomPotionManager#getSplashPotion(...)
     *
     * @return the display name
     */
    Component splashPotionDisplayName();

}

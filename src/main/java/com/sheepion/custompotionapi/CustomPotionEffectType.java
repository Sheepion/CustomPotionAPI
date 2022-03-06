package com.sheepion.custompotionapi;

import io.papermc.paper.potion.PotionMix;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;

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
     * the vanilla area effect cloud's duration (600 ticks, 30 seconds)
     */
    int VANILLA_AREA_EFFECT_CLOUD_DURATION = 600;

    /**
     * the vanilla area effect cloud's duration on use (0 tick)
     */
    int VANILLA_AREA_EFFECT_CLOUD_DURATION_ON_USE = 0;

    /**
     * the vanilla area effect cloud's radius (3 blocks)
     */
    float VANILLA_AREA_EFFECT_CLOUD_RADIUS = 3.0f;

    /**
     * the vanilla area effect cloud's radius on use (0.5 blocks)
     */
    float VANILLA_AREA_EFFECT_CLOUD_RADIUS_ON_USE = -0.5f;

    /**
     * the vanilla area effect cloud's radius per tick (0.005 blocks)
     */
    float VANILLA_AREA_EFFECT_CLOUD_RADIUS_PER_TICK = 0.005f;

    /**
     * the default area effect cloud's reapplication delay (5 ticks)
     * note: this value might not be the same as the vanilla one
     */
    int DEFAULT_AREA_EFFECT_CLOUD_REAPPLICATION_DELAY = 5;


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
     * @param entity        the entity that drinks milk.
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

    //TODO:添加药水砸到地面以后的效果

    /**
     * the potion effect when splash potion hit block
     * this method will be called automatically when the splash potion hit the block.
     *
     * @param shooter       the shooter of the splash potion
     * @param block         the block that the potion hit
     * @param duration      the duration of the potion effect
     * @param amplifier     the amplifier of the potion effect
     * @param checkInterval the check interval of the potion effect
     */
    default void splashPotionHitBlockEffect(ProjectileSource shooter, Block block, int duration, int amplifier, int checkInterval) {
    }

    /**
     * the potion effect when lingering potion hit block
     * this method will be called automatically when the lingering potion hit the block.
     *
     * @param shooter       the shooter of the lingering potion
     * @param block         the block that the potion hit
     * @param duration      the duration of the potion effect
     * @param amplifier     the amplifier of the potion effect
     * @param checkInterval the check interval of the potion effect
     */
    default void lingeringPotionHitBlockEffect(ProjectileSource shooter, Block block, int duration, int amplifier, int checkInterval) {
    }

    /**
     * the potion effect when splash potion hit entity
     * this method will be called automatically when the splash potion hit the entity.
     *
     * @param shooter       the shooter of the potion
     * @param entity        the block that the potion hit
     * @param duration      the duration of the potion effect
     * @param amplifier     the amplifier of the potion effect
     * @param checkInterval the check interval of the potion effect
     */
    default void splashPotionHitEntityEffect(ProjectileSource shooter, Entity entity, int duration, int amplifier, int checkInterval) {
    }

    /**
     * the potion effect when lingering potion hit entity
     * this method will be called automatically when the lingering potion hit the entity.
     *
     * @param shooter       the shooter of the potion
     * @param entity        the entity that the potion hit
     * @param duration      the duration of the potion effect
     * @param amplifier     the amplifier of the potion effect
     * @param checkInterval the check interval of the potion effect
     */
    default void lingeringPotionHitEntityEffect(ProjectileSource shooter, Entity entity, int duration, int amplifier, int checkInterval) {
    }

    /**
     * get all the potion mix recipes.
     * those recipes will be automatically registered when you register this potion effect type.
     *
     * @return the potion mix recipes
     */
    @Nullable ArrayList<PotionMix> potionMixes();

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
    default boolean potionEnchanted() {
        return true;
    }

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
    default boolean splashPotionEnchanted() {
        return true;
    }

    /**
     * get the lore of the lingering potion item
     * used when create the potion item by CustomPotionManager#getLingeringPotion(...)
     *
     * @param duration      the duration of the potion effect.
     * @param amplifier     the amplifier of the potion effect.
     * @param checkInterval the check interval of the potion effect.
     * @return the lore
     */
    ArrayList<Component> lingeringPotionLore(int duration, int amplifier, int checkInterval);

    /**
     * get the lore of the lingering potion item
     * used when create the potion item by CustomPotionManager#getLingeringPotion(...)
     *
     * @param duration      the duration of the potion effect.
     * @param amplifier     the amplifier of the potion effect.
     * @param checkInterval the check interval of the potion effect.
     * @return the display name
     */
    Component lingeringPotionDisplayName(int duration, int amplifier, int checkInterval);

    /**
     * get the color of the lingering potion item
     *
     * @param duration      the duration of the potion effect.
     * @param amplifier     the amplifier of the potion effect.
     * @param checkInterval the check interval of the potion effect.
     * @return the color
     */
    Color lingeringPotionColor(int duration, int amplifier, int checkInterval);

    /**
     * if the lingering potion has enchanted glow
     *
     * @return true if the lingering potion has enchanted glow
     */
    default boolean lingeringPotionEnchanted() {
        return true;
    }

    /**
     * the initial duration which this cloud will exist for (in ticks).
     *
     * @return the duration ticks
     */
    default int areaEffectCloudDuration() {
        return VANILLA_AREA_EFFECT_CLOUD_DURATION;
    }

    /**
     * the amount that the duration of this cloud will INCREASE by when it applies an effect to an entity.
     * make this value negative to make the duration decrease.
     *
     * @return the duration ticks on use.
     */
    default int areaEffectCloudDurationOnUse() {
        return VANILLA_AREA_EFFECT_CLOUD_DURATION_ON_USE;
    }

    /**
     * the initial radius of the cloud.
     *
     * @return the radius
     */
    default float areaEffectCloudRadius() {
        return VANILLA_AREA_EFFECT_CLOUD_RADIUS;
    }

    /**
     * the amount that the radius of this cloud will INCREASE by when it applies an effect to an entity.
     * make this value negative to make the radius decrease.
     *
     * @return the radius on use.
     */
    default float areaEffectCloudRadiusOnUse() {
        return VANILLA_AREA_EFFECT_CLOUD_RADIUS_ON_USE;
    }

    /**
     * the amount that the radius of this cloud will INCREASE by each tick.
     * make this value negative to make it decrease.
     *
     * @return the radius on tick.
     */
    default float areaEffectCloudRadiusPerTick() {
        return VANILLA_AREA_EFFECT_CLOUD_RADIUS_PER_TICK;
    }

    /**
     * the time that an entity will be immune from subsequent exposure.
     *
     * @return the time in ticks.
     */
    default int areaEffectCloudReapplicationDelay() {
        return DEFAULT_AREA_EFFECT_CLOUD_REAPPLICATION_DELAY;
    }
}

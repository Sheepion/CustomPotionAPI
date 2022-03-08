package com.sheepion.custompotionapi;

import io.papermc.paper.potion.PotionMix;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
@SuppressWarnings({"unused", "SameReturnValue"})
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
     * @param entity   the entity to check
     * @param property the property of the potion effect
     * @return true if the entity can be applied by this potion effect
     */
    boolean canBeApplied(LivingEntity entity, CustomPotionEffectProperty property);

    /**
     * if the effect can be removed by milk
     * this will be automatically called by CustomPotionManager when entity drinks milk.
     *
     * @param entity   the entity that drinks milk.
     * @param property the property of the potion effect that removed by milk
     * @return true if the effect can be removed by milk
     */
    boolean canBeRemovedByMilk(LivingEntity entity, CustomPotionEffectProperty property);

    /**
     * if the area effect cloud should spawn when the creepers with the effect exploded.
     * @param creeper the creeper that exploded
     * @param property the property of the potion effect that applied to the creeper
     * @return true if the area effect cloud should spawn when the creepers with the effect exploded.
     */
    default boolean spawnAreaEffectCloudOnCreeperExplosion(Creeper creeper,CustomPotionEffectProperty property){return true;}

    /**
     * the things you want to do before the potion effect is applied to the entity<br>
     * this method will be called only once before the effect is applied to the entity<br>
     * if you want to cancel this effect for some reason, try set the rest duration in the property below 0.<br>
     * if you want to cancel other effects, first you need to get the effect by CustomPotionManager#getActivePotionEffects(UUID)<br>
     * <p>
     * you may use this method to do some things like:<br>
     * - deal with the conflict with other potion effects<br>
     *
     * @param entity   the entity that is going to be applied the potion effect
     * @param property the potion effect's property
     */
    void beforeApply(LivingEntity entity, CustomPotionEffectProperty property);

    /**
     * the potion effect to the entity<br>
     * this method will be called every %checkInterval% ticks for %duration% ticks.<br>
     * if you want to make the effect instant, just make the duration and check interval the same.<br>
     *
     * @param entity   the entity to apply the potion effect
     * @param property the property of the potion effect that applied to the entity
     */
    void effect(LivingEntity entity, CustomPotionEffectProperty property);

    /**
     * the potion effect when splash potion hit block
     * this method will be called automatically when the splash potion hit the block.
     *
     * @param block    the block that the potion hit
     * @param property the property of the potion effect that hit the block
     */
    default void splashPotionHitBlockEffect(Block block, CustomPotionEffectProperty property) {
    }

    /**
     * the potion effect when lingering potion hit block
     * this method will be called automatically when the lingering potion hit the block.
     *
     * @param block    the block that the potion hit
     * @param property the property of the potion effect that hit the block
     */
    default void lingeringPotionHitBlockEffect(Block block, CustomPotionEffectProperty property) {
    }

    /**
     * the potion effect when splash potion hit entity
     * this method will be called automatically when the splash potion hit the entity.
     *
     * @param entity   the block that the potion hit
     * @param property the property of the potion effect that hit the entity
     */
    default void splashPotionHitEntityEffect(Entity entity, CustomPotionEffectProperty property) {
    }

    /**
     * the potion effect when lingering potion hit entity<br>
     * this method will be called automatically when the lingering potion hit the entity.
     *
     * @param entity   the entity that the potion hit
     * @param property the property of the potion effect that hit the entity
     */
    default void lingeringPotionHitEntityEffect(Entity entity, CustomPotionEffectProperty property) {
    }

    /**
     * get all the potion mix recipes that need to register.<br>
     * those recipes will be automatically registered to the potion brewer when you register this potion effect type.
     *
     * @return the potion mix recipes
     */
    @Nullable ArrayList<PotionMix> potionMixes();

    /**
     * get the display name of the potion item
     * used when create the potion item by CustomPotionManager#getPotion(...)
     *
     * @param property the property of the potion effect
     * @return the display name
     */
    Component potionDisplayName(CustomPotionEffectProperty property);

    /**
     * get the lore of the potion item
     * used when create the potion item by CustomPotionManager#getPotion(...)
     *
     * @param property the property of the potion effect
     * @return the lore
     */
    ArrayList<Component> potionLore(CustomPotionEffectProperty property);

    /**
     * get the color of the potion item
     *
     * @param property the potion effect property
     * @return the color
     */
    Color potionColor(CustomPotionEffectProperty property);

    /**
     * if the potion has enchanted glow
     *
     * @param property the potion effect property
     * @return true if the potion has enchanted glow
     */
    default boolean potionEnchanted(CustomPotionEffectProperty property) {
        return true;
    }

    /**
     * get the lore of the splash potion item
     * used when create the potion item by CustomPotionManager#getSplashPotion(...)
     *
     * @param property the property of the potion effect
     * @return the lore
     */
    ArrayList<Component> splashPotionLore(CustomPotionEffectProperty property);

    /**
     * get the lore of the splash potion item
     * used when create the potion item by CustomPotionManager#getSplashPotion(...)
     *
     * @param property the property of the potion effect
     * @return the display name
     */
    Component splashPotionDisplayName(CustomPotionEffectProperty property);

    /**
     * get the color of the splash potion item
     *
     * @param property the potion effect property
     * @return the color
     */
    Color splashPotionColor(CustomPotionEffectProperty property);

    /**
     * if the splash potion has enchanted glow
     * used when create the potion item by CustomPotionManager#getSplashPotion(...)
     *
     * @param property the potion effect property
     * @return true if the splash potion has enchanted glow
     */
    default boolean splashPotionEnchanted(CustomPotionEffectProperty property) {
        return true;
    }

    /**
     * get the lore of the lingering potion item
     * used when create the potion item by CustomPotionManager#getLingeringPotion(...)
     *
     * @param property the property of the potion effect
     * @return the lore
     */
    ArrayList<Component> lingeringPotionLore(CustomPotionEffectProperty property);

    /**
     * get the lore of the lingering potion item
     * used when create the potion item by CustomPotionManager#getLingeringPotion(...)
     *
     * @param property the property of the potion effect
     * @return the display name
     */
    Component lingeringPotionDisplayName(CustomPotionEffectProperty property);

    /**
     * get the color of the lingering potion item
     * used when create the potion item by CustomPotionManager#getLingeringPotion(...)
     *
     * @param property the potion effect property
     * @return the color
     */
    Color lingeringPotionColor(CustomPotionEffectProperty property);

    /**
     * if the lingering potion has enchanted glow
     * used when create the potion item by CustomPotionManager#getLingeringPotion(...)
     *
     * @param property the potion effect property
     * @return true if the lingering potion has enchanted glow
     */
    default boolean lingeringPotionEnchanted(CustomPotionEffectProperty property) {
        return true;
    }

    /**
     * the initial duration which this cloud will exist for (in ticks).
     * used when
     *
     * @param property the potion effect property
     * @return the duration ticks
     */
    default int areaEffectCloudDuration(CustomPotionEffectProperty property) {
        return VANILLA_AREA_EFFECT_CLOUD_DURATION;
    }

    /**
     * the amount that the duration of this cloud will INCREASE by when it applies an effect to an entity.
     * make this value negative to make the duration decrease.
     *
     * @param property the potion effect property
     * @return the duration ticks on use.
     */
    default int areaEffectCloudDurationOnUse(CustomPotionEffectProperty property) {
        return VANILLA_AREA_EFFECT_CLOUD_DURATION_ON_USE;
    }

    /**
     * the initial radius of the cloud.
     *
     * @param property the potion effect property
     * @return the radius
     */
    default float areaEffectCloudRadius(CustomPotionEffectProperty property) {
        return VANILLA_AREA_EFFECT_CLOUD_RADIUS;
    }

    /**
     * the amount that the radius of this cloud will INCREASE by when it applies an effect to an entity.
     * make this value negative to make the radius decrease.
     *
     * @param property the potion effect property
     * @return the radius on use.
     */
    default float areaEffectCloudRadiusOnUse(CustomPotionEffectProperty property) {
        return VANILLA_AREA_EFFECT_CLOUD_RADIUS_ON_USE;
    }

    /**
     * the amount that the radius of this cloud will INCREASE by each tick.
     * make this value negative to make it decrease.
     *
     * @param property the potion effect property
     * @return the radius on tick.
     */
    default float areaEffectCloudRadiusPerTick(CustomPotionEffectProperty property) {
        return VANILLA_AREA_EFFECT_CLOUD_RADIUS_PER_TICK;
    }

    /**
     * the time that an entity will be immune from subsequent exposure.
     *
     * @param property the potion effect property
     * @return the time in ticks.
     */
    default int areaEffectCloudReapplicationDelay(CustomPotionEffectProperty property) {
        return DEFAULT_AREA_EFFECT_CLOUD_REAPPLICATION_DELAY;
    }

}

package com.sheepion.custompotionapi;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static com.sheepion.custompotionapi.CustomPotionManager.getActiveEffectsOnEntity;

/**
 * project name: CustomPotionAPI
 * package: com.sheepion.custompotionapi
 * <p>
 * presents a custom potion effect with specified effect type, duration, amplifier and check interval.
 *
 * @author Sheepion
 */
public class CustomPotionEffect implements Runnable {

    private final CustomPotionEffectProperty property;

    private BukkitTask task;
    private LivingEntity entity;
    private final CustomPotionEffectType effectType;

    /**
     * return the effect type of the effect
     *
     * @return effect type
     */
    public @NotNull CustomPotionEffectType getEffectType() {
        return effectType;
    }

    /**
     * get the mutable property of the effect.<br>
     *
     * @return the property
     */
    public CustomPotionEffectProperty getProperty() {
        return property;
    }

    /**
     * get the remaining duration of the effect
     *
     * @return remaining duration in ticks
     */
    public int getDuration() {
        return property.getDuration();
    }

    /**
     * get the amplifier of the effect
     *
     * @return amplifier
     */
    public int getAmplifier() {
        return property.getAmplifier();
    }

    /**
     * get the check interval of the effect
     *
     * @return check interval in ticks
     */
    public int getCheckInterval() {
        return property.getCheckInterval();
    }

    /**
     * get the delay before the effect take effect
     *
     * @return delay in ticks
     */
    public int getDelay() {
        return property.getDelay();
    }

    /**
     * get the entity that has the effect
     * note: a custom potion effect will only be applied to one entity,
     * even two entities have the same effect type, it is two different CustomPotionEffect instances.
     *
     * @return entity
     */
    public @NotNull LivingEntity getEntity() {
        return entity;
    }

    private void setTask(BukkitTask task) {
        this.task = task;
    }

    private void setEntity(LivingEntity entity) {
        this.entity = entity;
    }


    /**
     * create a new CustomPotionEffect
     * make duration and checkInterval to zero for an instant effect
     * never try to create a permanent effect, because the effect will be removed before the first call to effect().
     *
     * @param effectType    effect type
     * @param potion        the potion item that brings the effect to the entity
     * @param shooter       the shooter of the potion
     * @param duration      duration in ticks
     * @param amplifier     amplifier
     * @param checkInterval run the effect() method in the effect type every checkInterval ticks
     * @param delay         delay in ticks
     */
    public CustomPotionEffect(@NotNull CustomPotionEffectType effectType, ItemStack potion, @Nullable ProjectileSource shooter, int duration, int amplifier, int checkInterval, int delay) {
        this.effectType = effectType;
        this.property = new CustomPotionEffectProperty(potion, shooter, duration, duration, amplifier, false, checkInterval, delay);
    }

    /**
     * create a new CustomPotionEffect
     * make duration and checkInterval to zero for an instant effect
     * never try to create a permanent effect, because the effect will be removed before the first call to effect().
     *
     * @param effectType effect type
     * @param property   effect property
     */
    public CustomPotionEffect(@NotNull CustomPotionEffectType effectType, CustomPotionEffectProperty property) {
        this.effectType = effectType;
        this.property = property;
    }

    /**
     * add effect to entity
     *
     * @param entity entity to add effect to
     * @return true if success, false if failed
     */
    public boolean apply(@NotNull LivingEntity entity) {
        if (!effectType.canBeApplied(entity, property)) {
            return false;
        }
        CustomPotionEffect potionEffect = copy();
        potionEffect.setEntity(entity);
        potionEffect.getEffectType().beforeApply(entity, potionEffect.property);
        potionEffect.setTask(CustomPotionAPI.getInstance().getServer().getScheduler().runTaskTimer(CustomPotionAPI.getInstance(), potionEffect, property.getDelay(), property.getCheckInterval()));
        if (!getActiveEffectsOnEntity().containsKey(entity.getUniqueId())) {
            getActiveEffectsOnEntity().put(entity.getUniqueId(), new ArrayList<>());
        }
        getActiveEffectsOnEntity().get(entity.getUniqueId()).add(potionEffect);
        return true;
    }

    /**
     * add effect to entity
     *
     * @param effectType effect type
     * @param entity     entity to add effect to
     * @param property   effect property
     * @return true if success, false if failed
     */
    public static boolean apply(CustomPotionEffectType effectType, LivingEntity entity, CustomPotionEffectProperty property) {
        CustomPotionEffect potionEffect = new CustomPotionEffect(effectType, property);
        return potionEffect.apply(entity);
    }

    /**
     * remove this effect from entity
     */
    public void cancel() {
        if (task != null) {
            task.cancel();
        }
        if (getActiveEffectsOnEntity().containsKey(entity.getUniqueId())) {
            getActiveEffectsOnEntity().get(entity.getUniqueId()).remove(this);
        }
    }

    @Override
    public void run() {
        //skip if player offline
        if (entity instanceof Player && !((Player) entity).isOnline()) {
            //don't use cancel() because it will remove the effect from the list.
            //keep the instance in the list, so that it can be applied again when the player comes back online.
            task.cancel();
            return;
        }
        property.setRestDuration(property.getRestDuration() - property.getCheckInterval());
        if (property.getRestDuration() < 0) {
            cancel();
            return;
        }
        if (entity.isDead() || !entity.isValid()) {
            cancel();
            return;
        }
        effectType.effect(entity, property);
        if (property.getRestDuration() == 0) {
            cancel();
        }
    }

    /**
     * copy a new CustomPotionEffect with the same effect type, duration, amplifier and checkInterval
     *
     * @return the new CustomPotionEffect
     */
    public @NotNull CustomPotionEffect copy() {
        return new CustomPotionEffect(effectType, property.clone());
    }
}

package com.sheepion.custompotionapi;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

import static com.sheepion.custompotionapi.CustomPotionManager.activeEffectsOnEntity;

/**
 * project name: CustomPotionAPI
 * package: com.sheepion.custompotionapi
 *
 * @author Sheepion
 * @date 3/4/2022
 */
public class CustomPotionEffect implements Runnable {

    private int duration;
    private int amplifier;
    private int checkInterval;
    private BukkitTask task;
    private LivingEntity entity;
    private CustomPotionEffectType effectType;
    public CustomPotionEffectType getEffectType() {
        return effectType;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public int getCheckInterval() {
        return checkInterval;
    }

    public LivingEntity getEntity() {
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
     * @param effectType effect type
     * @param duration duration in ticks
     * @param amplifier amplifier
     * @param checkInterval run the effect() method in the effect type every checkInterval ticks
     */
    public CustomPotionEffect(CustomPotionEffectType effectType, int duration, int amplifier, int checkInterval) {
        this.effectType = effectType;
        this.duration = duration;
        this.amplifier = amplifier;
        this.checkInterval = checkInterval;
    }

    /**
     * add effect to entity
     * @param entity entity to add effect to
     * @return true if success, false if failed
     */
    public boolean apply(LivingEntity entity) {
        if (effectType.canBeApplied(entity)) {
            CustomPotionEffect potion = copy();
            potion.setEntity(entity);
            potion.setTask(CustomPotionAPI.getInstance().getServer().getScheduler().runTaskTimer(CustomPotionAPI.getInstance(), potion, 0, checkInterval));
            if(!activeEffectsOnEntity.containsKey(entity.getUniqueId())) {
                activeEffectsOnEntity.put(entity.getUniqueId(), new ArrayList<>());
            }
            activeEffectsOnEntity.get(entity.getUniqueId()).add(potion);
            return true;
        }
        return false;
    }

    /**
     * add effect to entity
     * @param effectType effect type
     * @param entity entity to add effect to
     * @param duration duration in ticks
     * @param amplifier amplifier
     * @param checkInterval run the effect() method in the effect type every checkInterval ticks
     * @return true if success, false if failed
     */
    public static boolean apply(CustomPotionEffectType effectType,LivingEntity entity,int duration,int amplifier,int checkInterval) {
        CustomPotionEffect potion = new CustomPotionEffect(effectType, duration, amplifier, checkInterval);
        return potion.apply(entity);
    }

    @Override
    public void run() {
        //skip if player offline
        if(entity instanceof Player && !((Player) entity).isOnline()) {
            task.cancel();
            return;
        }
        duration -= checkInterval;
        if (duration < 0) {
            task.cancel();
            activeEffectsOnEntity.get(entity.getUniqueId()).remove(this);
            CustomPotionAPI.getInstance().getLogger().info("cancelled: duration < 0");
            CustomPotionAPI.getInstance().getLogger().info("effect left:");
            for (CustomPotionEffect customPotionEffect : activeEffectsOnEntity.get(entity.getUniqueId())) {
                CustomPotionAPI.getInstance().getLogger().info(customPotionEffect.getEffectType().getKey().toString());
            }
            CustomPotionAPI.getInstance().getLogger().info("duration: " + duration);
            return;
        }
        if (entity.isDead() || !entity.isValid()) {
            task.cancel();
            activeEffectsOnEntity.get(entity.getUniqueId()).remove(this);
            CustomPotionAPI.getInstance().getLogger().info("effect left:");
            for (CustomPotionEffect customPotionEffect : activeEffectsOnEntity.get(entity.getUniqueId())) {
                CustomPotionAPI.getInstance().getLogger().info(customPotionEffect.getEffectType().getKey().toString());
            }
            CustomPotionAPI.getInstance().getLogger().info("cancelled: entity is dead or invalid");
            return;
        }
        effectType.effect(entity, duration, checkInterval, amplifier);
        if (duration == 0) {
            task.cancel();
            activeEffectsOnEntity.get(entity.getUniqueId()).remove(this);
            CustomPotionAPI.getInstance().getLogger().info("effect left:");
            for (CustomPotionEffect customPotionEffect : activeEffectsOnEntity.get(entity.getUniqueId())) {
                CustomPotionAPI.getInstance().getLogger().info(customPotionEffect.getEffectType().getKey().toString());
            }
            CustomPotionAPI.getInstance().getLogger().info("cancelled: duration == 0");
        }
    }

    public CustomPotionEffect copy() {
        return new CustomPotionEffect(effectType, duration, amplifier, checkInterval);
    }
}

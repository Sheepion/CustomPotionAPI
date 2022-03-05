package com.sheepion.custompotionapi;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.sheepion.custompotionapi.CustomPotionManager.activeEffectsOnEntity;

/**
 * project name: CustomPotionAPI
 * package: com.sheepion.custompotionapi
 *
 * presents a custom potion effect with specified effect type, duration, amplifier and check interval.
 * @author Sheepion
 * @date 3/4/2022
 */
public class CustomPotionEffect implements Runnable {

    private int duration;
    private final int amplifier;
    private final int checkInterval;
    private BukkitTask task;
    private LivingEntity entity;
    private final CustomPotionEffectType effectType;

    /**
     * return the effect type of the effect
     * @return effect type
     */
    public @NotNull CustomPotionEffectType getEffectType() {
        return effectType;
    }

    /**
     * get the remaining duration of the effect
     * @return remaining duration in ticks
     */
    public int getDuration() {
        return duration;
    }

    /**
     * get the amplifier of the effect
     * @return amplifier
     */
    public int getAmplifier() {
        return amplifier;
    }

    /**
     * get the check interval of the effect
     * @return check interval in ticks
     */
    public int getCheckInterval() {
        return checkInterval;
    }

    /**
     * get the entity that has the effect
     * note: a custom potion effect will only be applied to one entity,
     *       even two entities have the same effect type, it is two different CustomPotionEffect instances.
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
     * @param effectType effect type
     * @param duration duration in ticks
     * @param amplifier amplifier
     * @param checkInterval run the effect() method in the effect type every checkInterval ticks
     */
    public CustomPotionEffect(@NotNull CustomPotionEffectType effectType, int duration, int amplifier, int checkInterval) {
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
    public boolean apply(@NotNull LivingEntity entity) {
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

    /**
     * remove this effect from entity
     */
    public void cancel(){
        if(task != null) {
            task.cancel();
        }
        if(activeEffectsOnEntity.containsKey(entity.getUniqueId())) {
            activeEffectsOnEntity.get(entity.getUniqueId()).remove(this);
        }
    }
    @Override
    public void run() {
        //skip if player offline
        if(entity instanceof Player && !((Player) entity).isOnline()) {
            //don't use cancel() because it will remove the effect from the list.
            //keep the instance in the list, so that it can be applied again when the player comes back online.
            task.cancel();
            return;
        }
        duration -= checkInterval;
        if (duration < 0) {
            cancel();
            return;
        }
        if (entity.isDead() || !entity.isValid()) {
            cancel();
            return;
        }
        effectType.effect(entity, duration, amplifier, checkInterval);
        if (duration == 0) {
            cancel();
        }
    }

    public @NotNull CustomPotionEffect copy() {
        return new CustomPotionEffect(effectType, duration, amplifier, checkInterval);
    }
}

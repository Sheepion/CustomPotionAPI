package com.sheepion.custompotionapi;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

import static com.sheepion.custompotionapi.CustomPotionManager.*;


/**
 * this class is used to listen for custom potion events.<br>
 * generally not to be used from within a plugin.
 *
 * @author Sheepion
 */
public class CustomPotionListener implements Listener {

    /**
     * apply unfinished potion effect to the player
     *
     * @param event the player join event
     */
    @EventHandler(ignoreCancelled = true)
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!getActiveEffectsOnEntity().containsKey(player.getUniqueId())) {
            getActiveEffectsOnEntity().put(player.getUniqueId(), new ArrayList<>());
        }
        ArrayList<CustomPotionEffect> unfinishedEffects = getActiveEffectsOnEntity().get(player.getUniqueId());
        ArrayList<CustomPotionEffect> needsRemoval = new ArrayList<>();
        //apply unfinished potion effects
        for (CustomPotionEffect customPotionEffect : unfinishedEffects) {
            customPotionEffect.apply(player);
            needsRemoval.add(customPotionEffect);
        }
        //remove the unfinished effects from the list
        for (CustomPotionEffect customPotionEffect : needsRemoval) {
            unfinishedEffects.remove(customPotionEffect);
        }
    }

    /**
     * spawn area effect cloud when creeper explodes
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Creeper creeper) {
            if (getActivePotionEffects(creeper.getUniqueId()).size() > 0) {
                for (CustomPotionEffect potionEffect : getActivePotionEffects(creeper.getUniqueId())) {
                    if (potionEffect.getEffectType().spawnAreaEffectCloudOnCreeperExplosion(creeper, potionEffect.getProperty())) {
                        AreaEffectCloud areaEffectCloud = (AreaEffectCloud) creeper.getWorld().spawnEntity(creeper.getLocation(), EntityType.AREA_EFFECT_CLOUD, CreatureSpawnEvent.SpawnReason.EXPLOSION);
                        areaEffectCloud.setColor(potionEffect.getEffectType().lingeringPotionColor(potionEffect.getProperty()));
                        areaEffectCloud.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 0, 0), true);
                        setAreaEffectCloudProperties(potionEffect, areaEffectCloud);
                        getAreaEffectClouds().put(areaEffectCloud, potionEffect);
                    }
                }
            }
        }
    }

    /**
     * apply the potion effect to the player when the player consume the potion
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION) {
            return;
        }
        CustomPotionEffect customEffect = getCustomPotionEffect(event.getItem());
        if (customEffect == null) {
            return;
        }
        customEffect.apply(event.getPlayer());
    }


    /**
     * remove the potion effect when player drinks milk
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDrinksMilk(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.MILK_BUCKET) {
            return;
        }
        Player player = event.getPlayer();
        getActivePotionEffects(event.getPlayer().getUniqueId()).forEach(customPotionEffect -> {
            if (customPotionEffect.getEffectType().canBeRemovedByMilk(player, customPotionEffect.getProperty())) {
                customPotionEffect.cancel();
            }
        });
    }

    /**
     * handle potion hit block effect and potion hit entity effect
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof ThrownPotion thrownPotion)) {
            return;
        }
        CustomPotionEffect customEffect = getCustomPotionEffect(thrownPotion.getItem());
        if (customEffect == null) {
            return;
        }
        CustomPotionEffectType customPotionEffectType = customEffect.getEffectType();
        customEffect.getProperty().setShooter(thrownPotion.getShooter());
        //handle potion hit block effect
        Block block = event.getHitBlock();
        if (block != null) {
            if (thrownPotion.getItem().getType() == Material.SPLASH_POTION) {
                customPotionEffectType.splashPotionHitBlockEffect(block, customEffect.getProperty());
            } else if (thrownPotion.getItem().getType() == Material.LINGERING_POTION) {
                customPotionEffectType.lingeringPotionHitBlockEffect(block, customEffect.getProperty());
            }
        }
        //handle potion hit entity effect
        Entity entity = event.getHitEntity();
        if (entity != null) {
            if (thrownPotion.getItem().getType() == Material.SPLASH_POTION) {
                customPotionEffectType.splashPotionHitEntityEffect(entity, customEffect.getProperty());
            } else if (thrownPotion.getItem().getType() == Material.LINGERING_POTION) {
                customPotionEffectType.lingeringPotionHitEntityEffect(entity, customEffect.getProperty());
            }
        }
    }

    /**
     * apply the potion effect to entities
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        CustomPotionEffect customEffect = getCustomPotionEffect(event.getEntity().getItem());
        if (customEffect == null) {
            return;
        }
        customEffect.getProperty().setShooter(event.getEntity().getShooter());
        event.getAffectedEntities().forEach(customEffect::apply);
    }

    /**
     * store the potion effect on the area effect cloud.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
        CustomPotionEffect customPotionEffect = getCustomPotionEffect(event.getEntity().getItem());
        if (customPotionEffect == null) {
            return;
        }
        customPotionEffect.getProperty().setShooter(event.getEntity().getShooter());
        AreaEffectCloud areaEffectCloud = event.getAreaEffectCloud();
        setAreaEffectCloudProperties(customPotionEffect, areaEffectCloud);
        getAreaEffectClouds().put(event.getAreaEffectCloud(), customPotionEffect);
    }

    /**
     * apply the potion effect to entities that are affected by the area effect cloud
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onAreaEffectCloudApply(AreaEffectCloudApplyEvent event) {
        CustomPotionEffect customPotionEffect = getAreaEffectClouds().get(event.getEntity());
        if (customPotionEffect != null) {
            for (LivingEntity affectedEntity : event.getAffectedEntities()) {
                customPotionEffect.apply(affectedEntity);
            }
        }
    }
}

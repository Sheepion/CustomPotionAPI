package com.sheepion.custompotionapi;

import io.papermc.paper.potion.PotionMix;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * project name: CustomPotionAPI
 * package: com.sheepion.custompotionapi
 *
 * @author Sheepion
 */
public class CustomPotionManager implements Listener {
    /**
     * used to store custom potion effect on area effect clouds.
     */
    private static final HashMap<AreaEffectCloud, CustomPotionEffect> areaEffectClouds = new HashMap<>();

    static {
        CustomPotionAPI.getInstance().getServer().getPluginManager().registerEvents(new CustomPotionManager(), CustomPotionAPI.getInstance());

        //clear dead area effect clouds task
        CustomPotionAPI.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(CustomPotionAPI.getInstance(), () -> {
            ArrayList<AreaEffectCloud> toRemove = new ArrayList<>();
            for (AreaEffectCloud areaEffectCloud : areaEffectClouds.keySet()) {
                if (areaEffectCloud.isDead()) {
                    toRemove.add(areaEffectCloud);
                }
            }
            for (AreaEffectCloud areaEffectCloud : toRemove) {
                areaEffectClouds.remove(areaEffectCloud);
            }
        }, 0L, 40L);
    }

    /**
     * used to store all the custom potion types
     */
    private static final HashSet<CustomPotionEffectType> customPotionEffectTypes = new HashSet<>();

    /**
     * used to store all the custom potion effects an entity has
     */
    private static final HashMap<UUID, ArrayList<CustomPotionEffect>> activeEffectsOnEntity = new HashMap<>();

    /**
     * @return the active effects on entity.
     */
    public static HashMap<UUID, ArrayList<CustomPotionEffect>> getActiveEffectsOnEntity() {
        return activeEffectsOnEntity;
    }

    /**
     * get the specific effect instance applied on the entity
     *
     * @param uuid                   the entity uuid
     * @param customPotionEffectType the potion effect type
     * @return the potion effect if exists, null otherwise
     */
    public static @Nullable CustomPotionEffect getActivePotionEffect(UUID uuid, CustomPotionEffectType customPotionEffectType) {
        ArrayList<CustomPotionEffect> customPotionEffects = activeEffectsOnEntity.get(uuid);
        if (customPotionEffects == null) {
            return null;
        }
        for (CustomPotionEffect customPotionEffect : customPotionEffects) {
            if (customPotionEffect.getEffectType().getKey().equals(customPotionEffectType.getKey())) {
                return customPotionEffect;
            }
        }
        return null;
    }

    /**
     * get a shallow copy of all the custom potion effects applied on the entity
     *
     * @param uuid the entity uuid
     * @return the potion effects. the array list would be empty if the entity has no potion effects
     */
    public static @NotNull ArrayList<CustomPotionEffect> getActivePotionEffects(UUID uuid) {
        ArrayList<CustomPotionEffect> customPotionEffects = activeEffectsOnEntity.get(uuid);
        if (customPotionEffects == null) {
            return new ArrayList<>();
        }
        return (ArrayList<CustomPotionEffect>) customPotionEffects.clone();
    }

    /**
     * judge if the entity has potion effect applied with the specific effect type
     *
     * @param uuid                   the entity uuid
     * @param customPotionEffectType the potion effect type
     * @return true if the entity has the potion effect, false otherwise
     */
    public static boolean isPotionEffectActive(UUID uuid, CustomPotionEffectType customPotionEffectType) {
        return getActivePotionEffect(uuid, customPotionEffectType) != null;
    }

    /**
     * spawn area effect cloud when creeper explodes
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGH,ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Creeper) {
            Creeper creeper = (Creeper) entity;
            if (getActivePotionEffects(creeper.getUniqueId()).size() > 0) {
                for (CustomPotionEffect potionEffect : getActivePotionEffects(creeper.getUniqueId())) {
                    if(potionEffect.getEffectType().spawnAreaEffectCloudOnCreeperExplosion(creeper,potionEffect.getProperty())) {
                        AreaEffectCloud areaEffectCloud = (AreaEffectCloud) creeper.getWorld().spawnEntity(creeper.getLocation(), EntityType.AREA_EFFECT_CLOUD, CreatureSpawnEvent.SpawnReason.EXPLOSION);
                        areaEffectCloud.setColor(potionEffect.getEffectType().lingeringPotionColor(potionEffect.getProperty()));
                        areaEffectCloud.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS,0,0),true);
                        setAreaEffectCloudProperties(potionEffect, areaEffectCloud);
                        areaEffectClouds.put(areaEffectCloud, potionEffect);
                    }
                }
            }
        }

    }

    /**
     * set the properties of the area effect cloud<br>
     * note: this WILL NOT add the area effect cloud to the areaEffectClouds map
     *
     * @param potionEffect the potion effect
     * @param areaEffectCloud the area effect cloud
     */
    private static void setAreaEffectCloudProperties(CustomPotionEffect potionEffect, AreaEffectCloud areaEffectCloud) {
        CustomPotionEffectProperty property = potionEffect.getProperty();
        areaEffectCloud.setDuration(potionEffect.getEffectType().areaEffectCloudDuration(property));
        areaEffectCloud.setDurationOnUse(potionEffect.getEffectType().areaEffectCloudDurationOnUse(property));
        areaEffectCloud.setRadius(potionEffect.getEffectType().areaEffectCloudRadius(property));
        areaEffectCloud.setRadiusOnUse(potionEffect.getEffectType().areaEffectCloudRadiusOnUse(property));
        areaEffectCloud.setRadiusPerTick(potionEffect.getEffectType().areaEffectCloudRadiusPerTick(property));
        areaEffectCloud.setReapplicationDelay(potionEffect.getEffectType().areaEffectCloudReapplicationDelay(property));
    }

    /**
     * register a custom potion effect type
     * this will register the listeners either if your class implemented Listener interface
     *
     * @param customPotionEffectType the custom potion effect type
     */
    public static void registerPotionEffectType(CustomPotionEffectType customPotionEffectType) {
        //add to type list
        customPotionEffectTypes.add(customPotionEffectType);
        //register the listeners if necessary
        if (customPotionEffectType instanceof Listener) {
            CustomPotionAPI.getInstance().getServer().getPluginManager().registerEvents((Listener) customPotionEffectType, CustomPotionAPI.getInstance());
        }
        //register potion mix recipes
        ArrayList<PotionMix> potionMixes = customPotionEffectType.potionMixes();
        if (potionMixes != null) {
            for (PotionMix potionMix : potionMixes) {
                CustomPotionAPI.getInstance().getServer().getPotionBrewer().removePotionMix(potionMix.getKey());
                CustomPotionAPI.getInstance().getServer().getPotionBrewer().addPotionMix(potionMix);
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
            int duration = customPotionEffect.getDuration();
            int checkInterval = customPotionEffect.getCheckInterval();
            int amplifier = customPotionEffect.getAmplifier();
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
        if (!(event.getEntity() instanceof ThrownPotion)) {
            return;
        }
        ThrownPotion thrownPotion = (ThrownPotion) event.getEntity();
        CustomPotionEffect customEffect = getCustomPotionEffect(thrownPotion.getItem());
        if (customEffect == null) {
            return;
        }
        CustomPotionEffectType customPotionEffectType = customEffect.getEffectType();
        //handle potion hit block effect
        Block block = event.getHitBlock();
        if (block != null) {
            if (thrownPotion.getItem().getType() == Material.SPLASH_POTION) {
                customPotionEffectType.splashPotionHitBlockEffect(thrownPotion.getShooter(), block, customEffect.getProperty());
            } else if (thrownPotion.getItem().getType() == Material.LINGERING_POTION) {
                customPotionEffectType.lingeringPotionHitBlockEffect(thrownPotion.getShooter(), block, customEffect.getProperty());
            }
        }
        //handle potion hit entity effect
        Entity entity = event.getHitEntity();
        if (entity != null) {
            if (thrownPotion.getItem().getType() == Material.SPLASH_POTION) {
                customPotionEffectType.splashPotionHitEntityEffect(thrownPotion.getShooter(), entity, customEffect.getProperty());
            } else if (thrownPotion.getItem().getType() == Material.LINGERING_POTION) {
                customPotionEffectType.lingeringPotionHitEntityEffect(thrownPotion.getShooter(), entity, customEffect.getProperty());
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
        AreaEffectCloud areaEffectCloud = event.getAreaEffectCloud();
        setAreaEffectCloudProperties(customPotionEffect, areaEffectCloud);
        areaEffectClouds.put(event.getAreaEffectCloud(), customPotionEffect);
    }

    /**
     * apply the potion effect to entities that are affected by the area effect cloud
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onAreaEffectCloudApply(AreaEffectCloudApplyEvent event) {
        CustomPotionEffect customPotionEffect = areaEffectClouds.get(event.getEntity());
        if (customPotionEffect != null) {
            for (LivingEntity affectedEntity : event.getAffectedEntities()) {
                customPotionEffect.apply(affectedEntity);
            }
        }
    }

    /**
     * get the potion effect from an item
     *
     * @param item the item
     * @return the potion effect, null if not found
     */
    public static @Nullable CustomPotionEffect getCustomPotionEffect(@NotNull ItemStack item) {
        if (item.getItemMeta() == null) {
            return null;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        // check if the potion is a custom potion
        String typeKey = pdc.get(new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_type"), PersistentDataType.STRING);
        if (typeKey == null) {
            return null;
        }
        //get the effect information
        NamespacedKey type = NamespacedKey.fromString(typeKey);
        assert type != null;
        CustomPotionEffectType customPotionEffectType = null;
        for (CustomPotionEffectType potionEffectType : customPotionEffectTypes) {
            if (potionEffectType.getKey().equals(type)) {
                customPotionEffectType = potionEffectType;
            }
        }
        // check if the potion effect type is valid
        if (customPotionEffectType == null) {
            return null;
        }
        Integer duration = pdc.get(new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_duration"), PersistentDataType.INTEGER);
        if (duration == null) {
            duration = 0;
        }
        Integer checkInterval = pdc.get(new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_check_interval"), PersistentDataType.INTEGER);
        if (checkInterval == null) {
            checkInterval = 20;
        }
        Integer amplifier = pdc.get(new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_amplifier"), PersistentDataType.INTEGER);
        if (amplifier == null) {
            amplifier = 0;
        }
        Integer delay = pdc.get(new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_delay"), PersistentDataType.INTEGER);
        if (delay == null) {
            delay = 0;
        }
        return new CustomPotionEffect(customPotionEffectType, item, duration, amplifier, checkInterval, delay);
    }

    /**
     * create a custom potion item use given material
     *
     * @param material               the material of the potion
     * @param customPotionEffectType the custom potion effect type
     * @param duration               the duration of the potion effect in ticks
     * @param amplifier              the amplifier of the potion effect
     * @param checkInterval          the interval of the potion effect in ticks
     * @param delay                  the delay of the potion effect in ticks
     * @return the custom potion item
     */
    public static ItemStack getPotion(Material material, NamespacedKey customPotionEffectType, int duration, int amplifier, int checkInterval, int delay) {
        ItemStack result = new ItemStack(material);
        ItemMeta meta = result.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        //set the name, lore, color, enchant glow.
        for (CustomPotionEffectType potionEffectType : customPotionEffectTypes) {
            if (potionEffectType.getKey().equals(customPotionEffectType)) {
                CustomPotionEffectProperty property = new CustomPotionEffectProperty(result, duration, duration, amplifier, false, checkInterval, delay);
                if (material.equals(Material.POTION)) {
                    ((PotionMeta) meta).setColor(potionEffectType.potionColor(property));
                    meta.displayName(potionEffectType.potionDisplayName(property));
                    meta.lore(potionEffectType.potionLore(property));
                    if (potionEffectType.potionEnchanted(property)) {
                        meta.addEnchant(Enchantment.DURABILITY, 1, true);
                    }
                } else if (material.equals(Material.SPLASH_POTION)) {
                    ((PotionMeta) meta).setColor(potionEffectType.splashPotionColor(property));
                    meta.displayName(potionEffectType.splashPotionDisplayName(property));
                    meta.lore(potionEffectType.splashPotionLore(property));
                    if (potionEffectType.splashPotionEnchanted(property)) {
                        meta.addEnchant(Enchantment.DURABILITY, 1, true);
                    }
                } else if (material.equals(Material.LINGERING_POTION)) {
                    ((PotionMeta) meta).setColor(potionEffectType.lingeringPotionColor(property));
                    ((PotionMeta) meta).addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 0, 0, false, false, false), true);
                    meta.displayName(potionEffectType.lingeringPotionDisplayName(property));
                    meta.lore(potionEffectType.lingeringPotionLore(property));
                    if (potionEffectType.lingeringPotionEnchanted(property)) {
                        meta.addEnchant(Enchantment.DURABILITY, 1, true);
                    }
                }
                break;
            }
        }
        pdc.set(new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_type"), PersistentDataType.STRING, customPotionEffectType.toString());
        pdc.set(new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_duration"), PersistentDataType.INTEGER, duration);
        pdc.set(new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_check_interval"), PersistentDataType.INTEGER, checkInterval);
        pdc.set(new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_amplifier"), PersistentDataType.INTEGER, amplifier);
        pdc.set(new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_delay"), PersistentDataType.INTEGER, delay);
        result.setItemMeta(meta);
        return result;
    }

    /**
     * create a NORMAL VANILLA potion such as water bottle,
     * YOU CAN NOT USE THIS METHOD TO CREATE A POTION WITH CUSTOM EFFECT
     *
     * @param material   the material of the potion.
     *                   you should only use Material.POTION or Material.SPLASH_POTION or Material.LINGERING_POTION
     * @param potionType the potion type of the potion.
     *                   PotionType.WATER presents the water bottle.
     * @return the custom potion item
     */
    public static ItemStack getPotion(Material material, PotionType potionType) {
        ItemStack bottle = new ItemStack(Material.POTION, 1);
        ItemMeta meta = bottle.getItemMeta();
        PotionMeta potionMeta = (PotionMeta) meta;
        PotionData potionData = new PotionData(PotionType.WATER);
        potionMeta.setBasePotionData(potionData);
        bottle.setItemMeta(meta);
        return bottle;
    }

    /**
     * create a custom potion item
     *
     * @param customPotionEffectType the custom potion effect type
     * @param duration               the duration of the potion effect in ticks
     * @param amplifier              the amplifier of the potion effect
     * @param checkInterval          the interval of the potion effect in ticks
     * @param delay                  the delay before the potion effect starts in ticks
     * @return the custom potion item
     */
    public static ItemStack getPotion(NamespacedKey customPotionEffectType, int duration, int amplifier, int checkInterval, int delay) {
        return getPotion(Material.POTION, customPotionEffectType, duration, amplifier, checkInterval, delay);
    }

    /**
     * create a custom splash potion item
     *
     * @param customPotionEffectType the custom potion effect type
     * @param duration               the duration of the potion effect in ticks
     * @param amplifier              the amplifier of the potion effect
     * @param checkInterval          the interval of the potion effect in ticks
     * @param delay                  the delay before the potion effect starts in ticks
     * @return the custom potion item
     */
    public static ItemStack getSplashPotion(NamespacedKey customPotionEffectType, int duration, int amplifier, int checkInterval, int delay) {
        return getPotion(Material.SPLASH_POTION, customPotionEffectType, duration, amplifier, checkInterval, delay);
    }

    /**
     * create a custom lingering potion item
     *
     * @param customPotionEffectType the custom potion effect type
     * @param duration               the duration of the potion effect in ticks
     * @param amplifier              the amplifier of the potion effect
     * @param checkInterval          the interval of the potion effect in ticks
     * @param delay                  the delay before the potion effect starts in ticks
     * @return the custom potion item
     */
    public static ItemStack getLingeringPotion(NamespacedKey customPotionEffectType, int duration, int amplifier, int checkInterval, int delay) {
        return getPotion(Material.LINGERING_POTION, customPotionEffectType, duration, amplifier, checkInterval, delay);
    }

    /**
     * apply unfinished potion effect to the player
     *
     * @param event the player join event
     */
    @EventHandler(ignoreCancelled = true)
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!activeEffectsOnEntity.containsKey(player.getUniqueId())) {
            activeEffectsOnEntity.put(player.getUniqueId(), new ArrayList<>());
        }
        for (CustomPotionEffect customPotionEffect : (ArrayList<CustomPotionEffect>) activeEffectsOnEntity.getOrDefault(player.getUniqueId(), new ArrayList<>()).clone()) {
            customPotionEffect.apply(player);
            activeEffectsOnEntity.get(player.getUniqueId()).remove(customPotionEffect);
        }
    }
}

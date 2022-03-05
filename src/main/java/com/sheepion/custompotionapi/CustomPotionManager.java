package com.sheepion.custompotionapi;

import io.papermc.paper.potion.PotionMix;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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
 * @date 3/4/2022
 */
public class CustomPotionManager implements Listener {
    static {
        CustomPotionAPI.getInstance().getServer().getPluginManager().registerEvents(new CustomPotionManager(), CustomPotionAPI.getInstance());
    }

    /**
     * used to store all the custom potion types
     */
    private static final HashSet<CustomPotionEffectType> customPotionEffectTypes = new HashSet<>();

    /**
     * used to store all the custom potion effects an entity has
     */
    public static final HashMap<UUID, ArrayList<CustomPotionEffect>> activeEffectsOnEntity = new HashMap<>();

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
        for (PotionMix potionMix : customPotionEffectType.potionMixes()) {
            CustomPotionAPI.getInstance().getServer().getPotionBrewer().removePotionMix(potionMix.getKey());
            CustomPotionAPI.getInstance().getServer().getPotionBrewer().addPotionMix(potionMix);
        }
    }

    /**
     * apply the potion effect to the player when the player consume the potion
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        CustomPotionAPI.getInstance().getLogger().info("onPlayerItemConsume");
        if (event.getItem().getType() != Material.POTION) {
            return;
        }
        CustomPotionEffect customEffect = getCustomPotionEffect(event.getItem());
        if (customEffect == null) {
            return;
        }
        boolean success = customEffect.apply(event.getPlayer());
        CustomPotionAPI.getInstance().getLogger().info("success: " + success);
    }


    /**
     * remove the potion effect when player drinks milk
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDrinksMilk(PlayerItemConsumeEvent event) {
        CustomPotionAPI.getInstance().getLogger().info("onPlayerDrinksMilk");
        if (event.getItem().getType() != Material.MILK_BUCKET) {
            return;
        }
        Player player = event.getPlayer();
        getActivePotionEffects(event.getPlayer().getUniqueId()).forEach(customPotionEffect -> {
            int duration = customPotionEffect.getDuration();
            int checkInterval = customPotionEffect.getCheckInterval();
            int amplifier = customPotionEffect.getAmplifier();
            if (customPotionEffect.getEffectType().canBeRemovedByMilk(player, duration, amplifier, checkInterval)) {
                CustomPotionAPI.getInstance().getLogger().info("remove potion effect: " + customPotionEffect.getEffectType().getKey());
                customPotionEffect.cancel();
            }
        });
    }

    /**
     * apply the potion effect to entities
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        CustomPotionAPI.getInstance().getLogger().info("onPotionSplash");

        CustomPotionEffect customEffect = getCustomPotionEffect(event.getEntity().getItem());
        if (customEffect == null) {
            return;
        }
        event.getAffectedEntities().forEach(entity -> {
            boolean success = customEffect.apply(entity);
            CustomPotionAPI.getInstance().getLogger().info("success: " + success);
        });
    }

    /**
     * get the potion effect from an item
     *
     * @param item the item
     * @return the potion effect, null if not found
     */
    public static CustomPotionEffect getCustomPotionEffect(ItemStack item) {
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
        CustomPotionAPI.getInstance().getLogger().info("type: " + type);
        CustomPotionEffectType customPotionEffectType = null;
        for (CustomPotionEffectType potionEffectType : customPotionEffectTypes) {
            CustomPotionAPI.getInstance().getLogger().info("registered type: " + potionEffectType.getKey().toString());
            if (potionEffectType.getKey().equals(type)) {
                CustomPotionAPI.getInstance().getLogger().info("valid potion effect type found");
                customPotionEffectType = potionEffectType;
            }
        }
        // check if the potion effect type is valid
        if (customPotionEffectType == null) {
            CustomPotionAPI.getInstance().getLogger().info("no valid potion effect type found");
            return null;
        }
        int duration = pdc.get(new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_duration"), PersistentDataType.INTEGER);
        int checkInterval = pdc.get(new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_check_interval"), PersistentDataType.INTEGER);
        int amplifier = pdc.get(new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_amplifier"), PersistentDataType.INTEGER);
        return new CustomPotionEffect(customPotionEffectType, duration, checkInterval, amplifier);
    }

    /**
     * create a custom potion item
     *
     * @param customPotionEffectType the custom potion effect type
     * @param duration               the duration of the potion effect in ticks
     * @param amplifier              the amplifier of the potion effect
     * @param checkInterval          the interval of the potion effect in ticks
     * @return the custom potion item
     */
    public static ItemStack getPotion(NamespacedKey customPotionEffectType, int duration, int amplifier, int checkInterval) {
        return getPotion(Material.POTION, customPotionEffectType, duration, amplifier, checkInterval);
    }

    /**
     * create a custom potion item use given material
     *
     * @param material               the material of the potion
     * @param customPotionEffectType the custom potion effect type
     * @param duration               the duration of the potion effect in ticks
     * @param amplifier              the amplifier of the potion effect
     * @param checkInterval          the interval of the potion effect in ticks
     * @return the custom potion item
     */
    public static ItemStack getPotion(Material material, NamespacedKey customPotionEffectType, int duration, int amplifier, int checkInterval) {
        ItemStack result = new ItemStack(material);
        ItemMeta meta = result.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        //set the name, lore, color, enchant glow.
        for (CustomPotionEffectType potionEffectType : customPotionEffectTypes) {
            if (potionEffectType.getKey().equals(customPotionEffectType)) {
                if (material.equals(Material.POTION)) {
                    ((PotionMeta) meta).setColor(potionEffectType.potionColor(duration, amplifier, checkInterval));
                    meta.displayName(potionEffectType.potionDisplayName(duration, amplifier, checkInterval));
                    meta.lore(potionEffectType.potionLore(duration, amplifier, checkInterval));
                    if (potionEffectType.potionEnchanted()) {
                        meta.addEnchant(Enchantment.DURABILITY, 1, true);
                    }
                } else if (material.equals(Material.SPLASH_POTION)) {
                    ((PotionMeta) meta).setColor(potionEffectType.splashPotionColor(duration, amplifier, checkInterval));
                    meta.displayName(potionEffectType.splashPotionDisplayName(duration, amplifier, checkInterval));
                    meta.lore(potionEffectType.splashPotionLore(duration, amplifier, checkInterval));
                    if (potionEffectType.splashPotionEnchanted()) {
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
        result.setItemMeta(meta);
        return result;
    }

    /**
     * create a custom potion item
     *
     * @param customPotionEffectType the custom potion effect type
     * @param duration               the duration of the potion effect in ticks
     * @param amplifier              the amplifier of the potion effect
     * @param checkInterval          the interval of the potion effect in ticks
     * @return the custom potion item
     */
    public static ItemStack getSplashPotion(NamespacedKey customPotionEffectType, int duration, int amplifier, int checkInterval) {
        return getPotion(Material.SPLASH_POTION, customPotionEffectType, duration, amplifier, checkInterval);
    }

    /**
     * apply unfinished potion effect to player
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
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

package com.sheepion.custompotionapi;

import org.bukkit.NamespacedKey;

/**
 * The NamespacedKey that used to identify the information in item's persistent data container.
 *
 * @author Sheepion
 */
public class PropertyKey {
    /**
     * the namespaced key of the potion effect's type
     */
    public static final NamespacedKey EFFECT_TYPE = new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_type");
    /**
     * the namespaced key of the potion effect's duration
     */
    public static final NamespacedKey EFFECT_DURATION = new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_duration");
    /**
     * the namespaced key of the potion effect's check interval
     */
    public static final NamespacedKey EFFECT_CHECK_INTERVAL = new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_check_interval");
    /**
     * the namespaced key of the potion effect's amplifier
     */
    public static final NamespacedKey EFFECT_AMPLIFIER = new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_amplifier");
    /**
     * the namespaced key of the potion effect's delay
     */
    public static final NamespacedKey EFFECT_DELAY = new NamespacedKey(CustomPotionAPI.getInstance(), "custom_potion_effect_delay");
}

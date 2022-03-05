package com.sheepion.custompotionapi;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * To use this api to create your own custom potion,
 * you need to create a new class that implements the CustomPotionEffectType interface,
 * after that, you need to use CustomPotionManager#registerPotionEffectType(CustomPotionEffectType) to register it.
 *
 * Once you have registered it, the potion mixes will be automatically added to your server.
 * You can use CustomPotionManager#getPotion(NamespacedKey, int, int, int) to get the potion.
 */
public final class CustomPotionAPI extends JavaPlugin {
    private static CustomPotionAPI instance;

    /**
     * initializes the plugin
     */
    public CustomPotionAPI() {
        instance = this;
    }

    /**
     * @return the instance of the plugin
     */
    public static JavaPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

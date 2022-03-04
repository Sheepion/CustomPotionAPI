package com.sheepion.custompotionapi;

import org.bukkit.plugin.java.JavaPlugin;

public final class CustomPotionAPI extends JavaPlugin {
    private static CustomPotionAPI instance;
    public CustomPotionAPI() {
        instance = this;
    }
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

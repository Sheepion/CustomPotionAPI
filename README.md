# CustomPotionAPI

create your own potion more easily.

paper-api version: 1.18.1-R0.1-SNAPSHOT  
plugin version: 1.0

## How to create your own potion

First, you need to add this api as your plugin's dependency.  
To create your own potion, you should create a class which implements the CustomPotionEffectType interface, you need
implements all the abstract methods, and write your potion's effect in the CustomPotionEffectType.effect().  
All non-abstract methods are optional, please read the javadoc for more information.

After create your own effect type, simply register the type using the CustomPotionManager.registerPotionEffectType(
CustomPotionEffectType type).  
If you want to obtain a potion item with your custom effect, you can call the CustomPotionManager.getPotion(
org.bukkit.NamespacedKey, int, int, int, int) method.

Most of the methods you will use in your own plugin is in the CustomPotionManager class.  
To make your effect more flexible, you may need to use methods in the CustomPotionEffect class and the
CustomPotionEffectProperty class.

## Commands and permissions

There isn't any commands or permissions in this plugin yet, feel free to use.  
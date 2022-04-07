package com.darksoldier1404.dmw;

import com.darksoldier1404.dmw.commands.DMWCommand;
import com.darksoldier1404.dmw.functions.DMWFunction;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class MultiWorld extends JavaPlugin {
    public static String prefix;
    private static MultiWorld plugin;
    public static YamlConfiguration config;
    public static Map<String, World> customWorlds = new HashMap<>();

    public static MultiWorld getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        config = ConfigUtils.loadDefaultPluginConfig(plugin);
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Settings.prefix"));
        getCommand("dmw").setExecutor(new DMWCommand());
        DMWFunction.loadAllCustomWorlds();
    }

    @Override
    public void onDisable() {
        ConfigUtils.savePluginConfig(plugin, config);
    }
}

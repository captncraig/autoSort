package io.captncraig.autosort;

import org.bukkit.plugin.java.JavaPlugin;

public final class Autosort extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("autosort").setExecutor(new AutoSortCommand());
        getServer().getPluginManager().registerEvents(new Events(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

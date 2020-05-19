package me.c10coding.generatorpvp;

import me.c10coding.coreapi.CoreAPI;
import me.c10coding.generatorpvp.commands.Commands;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class GeneratorPvP extends JavaPlugin {

    private CoreAPI api = new CoreAPI();
    private static Economy econ = null;

    @Override
    public void onEnable() {
        validateConfigs();
        this.getServer().getPluginCommand("menu").setExecutor(new Commands(this));
        if (!setupEconomy() ) {
            this.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public CoreAPI getApi(){
        return api;
    }

    public void validateConfigs(){
        File[] files = {new File(this.getDataFolder(), "config.yml")};
        for(File f : files){
            if(!f.exists()){
                this.saveResource("config.yml",false);
            }
        }
        this.getLogger().info("The config files have been validated!");
    }

    public String getPrefix(){
        return this.getConfig().getString("Prefix");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

}

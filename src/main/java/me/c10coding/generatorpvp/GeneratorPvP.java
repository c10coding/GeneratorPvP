package me.c10coding.generatorpvp;

import me.c10coding.coreapi.CoreAPI;
import me.c10coding.generatorpvp.commands.AdminCommands;
import me.c10coding.generatorpvp.commands.MenuCommand;
import me.c10coding.generatorpvp.listeners.GeneralListener;
import me.c10coding.generatorpvp.listeners.TeleportListener;
import me.c10coding.generatorpvp.listeners.WeaponsListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.logging.Logger;

public final class GeneratorPvP extends JavaPlugin {

    private CoreAPI api = new CoreAPI();
    private static Economy econ = null;
    private Logger logger;
    private EnchantmentRegister enchantmentRegister;

    @Override
    public void onEnable() {

        validateConfigs();
        registerEvents();
        initializeCommands();
        startAmplifierTimer();

        if (!setupEconomy() ) {
            this.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }

        this.logger = this.getLogger();
        this.enchantmentRegister = new EnchantmentRegister(this);

        enchantmentRegister.registerEnchantments();

    }

    @Override
    public void onDisable() {
        enchantmentRegister.unRegisterEnchantments();
    }

    public CoreAPI getApi(){
        return api;
    }

    public void validateConfigs(){
        File[] files = {new File(this.getDataFolder(), "config.yml"), new File(this.getDataFolder(), "equipped.yml"), new File(this.getDataFolder(), "amplifiers.yml"), new File(this.getDataFolder(), "enchants.yml")};
        for(File f : files){
            if(!f.exists()){
                this.saveResource(f.getName(),false);
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

    private void registerEvents(){
        this.getServer().getPluginManager().registerEvents(new WeaponsListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GeneralListener(this), this);
        this.getServer().getPluginManager().registerEvents(new TeleportListener(this), this);
    }

    private void initializeCommands(){
        this.getServer().getPluginCommand("menu").setExecutor(new MenuCommand(this));
        this.getServer().getPluginCommand("genpvp").setExecutor(new AdminCommands(this));
    }

    private void startAmplifierTimer(){
        new AmplifierTimer(this).runTaskTimer(this, 0L, 20L);
    }

    /*
    private void registerEmptyEnchant(Enchantment ench){
        boolean registered = true;
        //Using Reflection
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(ench);
        }catch(Exception e) {
            registered = false;
        }

        if(registered) {
            this.getLogger().info("The empty enchant is registered!");
        }else{
            this.getLogger().info("The empty enchant is already registered. Ignoring...");
        }
    }*/

    public static Economy getEconomy() {
        return econ;
    }

    public Logger getPluginLogger(){
        return logger;
    }



}

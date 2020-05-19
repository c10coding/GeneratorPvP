package me.c10coding.generatorpvp;

import me.c10coding.coreapi.CoreAPI;
import me.c10coding.generatorpvp.commands.Commands;
import me.c10coding.generatorpvp.listeners.GeneralListener;
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

public final class GeneratorPvP extends JavaPlugin {

    private CoreAPI api = new CoreAPI();
    private static Economy econ = null;
    public static Enchantment empty;

    @Override
    public void onEnable() {
        validateConfigs();
        registerEvents();

        empty = new EmptyEnchant(this);
        registerEmptyEnchant(empty);

        if (!setupEconomy() ) {
            this.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        try {
            Field keyField = Enchantment.class.getDeclaredField("byKey");

            keyField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);

            byKey.remove(new NamespacedKey(this, "Empty"));

            Field nameField = Enchantment.class.getDeclaredField("byName");

            nameField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);

            byKey.remove(new NamespacedKey(this, "Empty"));

        } catch (Exception ignored) { }
    }

    public CoreAPI getApi(){
        return api;
    }

    public void validateConfigs(){
        File[] files = {new File(this.getDataFolder(), "config.yml"), new File(this.getDataFolder(), "equipped.yml")};
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
        this.getServer().getPluginCommand("menu").setExecutor(new Commands(this));
        this.getServer().getPluginManager().registerEvents(new WeaponsListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GeneralListener(this), this);
    }

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
    }

    public static Economy getEconomy() {
        return econ;
    }

}

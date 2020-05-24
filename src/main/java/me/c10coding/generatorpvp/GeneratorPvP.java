package me.c10coding.generatorpvp;

import me.c10coding.coreapi.CoreAPI;
import me.c10coding.coreapi.holograms.HologramHelper;
import me.c10coding.generatorpvp.commands.AdminCommands;
import me.c10coding.generatorpvp.commands.MenuCommand;
import me.c10coding.generatorpvp.listeners.GeneralListener;
import me.c10coding.generatorpvp.listeners.TeleportListener;
import me.c10coding.generatorpvp.listeners.WeaponsListener;
import me.c10coding.generatorpvp.managers.Generator;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class GeneratorPvP extends JavaPlugin {

    private CoreAPI api = new CoreAPI();
    private static Economy econ = null;
    private Logger logger;
    private EnchantmentRegister enchantmentRegister;

    @Override
    public void onEnable() {

        this.enchantmentRegister = new EnchantmentRegister(this);

        validateConfigs();
        registerEvents();
        initializeCommands();
        startAmplifierTimer();
        startGenerators();

        if (!setupEconomy() ) {
            this.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }

        this.logger = this.getLogger();
        enchantmentRegister.registerEnchantments();

    }

    @Override
    public void onDisable() {
        enchantmentRegister.unRegisterEnchantments();
        disableHolograms();
    }

    public CoreAPI getApi(){
        return api;
    }

    public void validateConfigs(){
        File[] files = {new File(this.getDataFolder(), "config.yml"), new File(this.getDataFolder(), "equipped.yml"), new File(this.getDataFolder(), "amplifiers.yml"), new File(this.getDataFolder(), "generators.yml")};
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
        getLogger().info("Hooking Economy...");
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
        getLogger().info("Registering events...");
        this.getServer().getPluginManager().registerEvents(new WeaponsListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GeneralListener(this), this);
        this.getServer().getPluginManager().registerEvents(new TeleportListener(this), this);
    }

    private void initializeCommands(){
        getLogger().info("Setting up commands...");
        this.getServer().getPluginCommand("menu").setExecutor(new MenuCommand(this));
        this.getServer().getPluginCommand("genpvp").setExecutor(new AdminCommands(this));
    }

    private void startGenerators(){

        getLogger().info("Starting Generators...");

        Generator coalOre1 = new Generator(this, GeneratorTypes.COAL_ORE, 1);
        Generator coalOre2 = new Generator(this, GeneratorTypes.COAL_ORE, 2);
        Generator coalOre3 = new Generator(this, GeneratorTypes.COAL_ORE, 3);
        coalOre1.startGenerator();
        coalOre2.startGenerator();
        coalOre3.startGenerator();

        Generator coalBlock1 = new Generator(this, GeneratorTypes.COAL_BLOCK, 1);
        Generator coalBlock2 = new Generator(this, GeneratorTypes.COAL_BLOCK, 2);
        coalBlock1.startGenerator();
        coalBlock2.startGenerator();

        Generator ironOre1 = new Generator(this, GeneratorTypes.IRON_ORE, 1);
        Generator ironOre2 = new Generator(this, GeneratorTypes.IRON_ORE, 2);
        ironOre1.startGenerator();
        ironOre2.startGenerator();

        Generator ironBlock1 = new Generator(this, GeneratorTypes.IRON_BLOCK, 1);
        ironBlock1.startGenerator();

        Generator goldOre1 = new Generator(this, GeneratorTypes.GOLD_ORE, 1);
        goldOre1.startGenerator();

        Generator goldBlock1 = new Generator(this, GeneratorTypes.GOLD_BLOCK, 1);
        goldBlock1.startGenerator();

        Generator diamondOre1 = new Generator(this, GeneratorTypes.DIAMOND_ORE, 1);
        diamondOre1.startGenerator();

        Generator diamondBlock1 = new Generator(this, GeneratorTypes.DIAMOND_BLOCK, 1);
        diamondBlock1.startGenerator();

        Generator emeraldOre1 = new Generator(this, GeneratorTypes.EMERALD_ORE, 1);
        emeraldOre1.startGenerator();

    }

    private void disableHolograms(){
        HologramHelper hh = new HologramHelper(this);
        for(String hologramName : hh.getAllNames()){
            hh.removeHologram(hologramName);
        }
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

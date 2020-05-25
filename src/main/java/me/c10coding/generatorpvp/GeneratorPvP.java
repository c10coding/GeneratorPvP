package me.c10coding.generatorpvp;

import me.c10coding.coreapi.CoreAPI;
import me.c10coding.coreapi.holograms.HologramHelper;
import me.c10coding.generatorpvp.commands.AdminCommands;
import me.c10coding.generatorpvp.commands.MenuCommand;
import me.c10coding.generatorpvp.files.GeneratorConfigManager;
import me.c10coding.generatorpvp.listeners.GeneralListener;
import me.c10coding.generatorpvp.listeners.TeleportListener;
import me.c10coding.generatorpvp.listeners.WeaponsListener;
import me.c10coding.generatorpvp.managers.Generator;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class GeneratorPvP extends JavaPlugin {

    private CoreAPI api = new CoreAPI();
    private static Economy econ = null;
    private Logger logger;
    private EnchantmentRegister enchantmentRegister;
    public List<Integer> generatorRunnableIDs = new ArrayList<>();

    @Override
    public void onEnable() {

        this.logger = this.getLogger();

        validateConfigs();
        registerEvents();
        initializeCommands();
        startAmplifierTimer();

        this.enchantmentRegister = new EnchantmentRegister(this);
        enchantmentRegister.registerEnchantments();

        if (!setupEconomy() ) {
            this.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }

        GeneratorConfigManager gcm = new GeneratorConfigManager(this);
        if(gcm.getWorldName() != null){
            //startGenerators();
        }else{
            this.getLogger().info("This is probably your first time running this. Make sure that the GenPvPWorld field in the generatorRunnableIDs.yml file is set!");
        }

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
        File[] files = {new File(this.getDataFolder(), "config.yml"), new File(this.getDataFolder(), "equipped.yml"), new File(this.getDataFolder(), "amplifiers.yml"), new File(this.getDataFolder(), "generators.yml"), new File(this.getDataFolder(), "animations.yml"), new File(this.getDataFolder(), "holograms.yml")};
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

        generatorRunnableIDs.add(coalOre1.getRunnableID());
        generatorRunnableIDs.add(coalOre2.getRunnableID());
        generatorRunnableIDs.add(coalOre3.getRunnableID());
        generatorRunnableIDs.add(coalBlock1.getRunnableID());
        generatorRunnableIDs.add(coalBlock2.getRunnableID());
        generatorRunnableIDs.add(ironOre1.getRunnableID());
        generatorRunnableIDs.add(ironOre2.getRunnableID());
        generatorRunnableIDs.add(ironBlock1.getRunnableID());
        generatorRunnableIDs.add(goldOre1.getRunnableID());
        generatorRunnableIDs.add(goldBlock1.getRunnableID());
        generatorRunnableIDs.add(diamondOre1.getRunnableID());
        generatorRunnableIDs.add(diamondBlock1.getRunnableID());
        generatorRunnableIDs.add(emeraldOre1.getRunnableID());

    }

    private void disableHolograms(){

        File hologramFile = new File(this.getDataFolder(), "holograms.yml");

        if(hologramFile.exists()){
            HologramHelper hh = new HologramHelper(this);

            if(hh.getAllNames() != null){
                for(String hologramName : hh.getAllNames()){
                    hh.removeHologram(hologramName);
                }
            }

        }
    }

    public void restartGenerators(){
        stopGeneratorRunnables();
        disableHolograms();
        startGenerators();
    }

    public void stopGeneratorRunnables(){
        for(Integer id : generatorRunnableIDs){
            Bukkit.getScheduler().cancelTask(id);
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

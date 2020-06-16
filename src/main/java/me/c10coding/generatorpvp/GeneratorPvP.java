package me.c10coding.generatorpvp;

import me.TechsCode.UltraPermissions.hooks.pluginHooks.VaultHook;
import me.c10coding.coreapi.CoreAPI;
import me.c10coding.coreapi.holograms.HologramHelper;
import me.c10coding.generatorpvp.commands.AdminCommands;
import me.c10coding.generatorpvp.commands.ConfirmedCommands;
import me.c10coding.generatorpvp.commands.LeaderboardCommand;
import me.c10coding.generatorpvp.commands.MenuCommand;
import me.c10coding.generatorpvp.files.GeneratorConfigManager;
import me.c10coding.generatorpvp.listeners.GeneralListener;
import me.c10coding.generatorpvp.listeners.TeleportListener;
import me.c10coding.generatorpvp.listeners.WeaponsListener;
import me.c10coding.generatorpvp.managers.Generator;
import me.c10coding.generatorpvp.managers.ScoreboardManager;
import me.c10coding.generatorpvp.runnables.AmplifierTimer;
import me.c10coding.generatorpvp.runnables.ScoreboardUpdater;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
    Plugin: GeneratorPvP
    Finished: 5/27/2020
    Author: Caleb Owens // C10coding
    Plugin for: Minhas
 */

public final class GeneratorPvP extends JavaPlugin {

    private CoreAPI api;
    private static Economy econ = null;
    private static Permission perms = null;
    private Logger logger;
    private EnchantmentRegister enchantmentRegister;
    public List<Generator> generators = new ArrayList<>();

    @Override
    public void onEnable() {

        if(getServer().getPluginManager().getPlugin("CoreAPI") == null){
            getServer().getPluginManager().disablePlugin(this);
        }
        api = (CoreAPI) getServer().getPluginManager().getPlugin("CoreAPI");

        disableHolograms();

        this.logger = this.getLogger();

        this.getLogger().info("==================================================================");
        validateConfigs();
        registerEvents();
        initializeCommands();
        startAmplifierTimer();
        registerEnchants();
        new ScoreboardUpdater(this).runTaskTimer(this, 0L, 20L);

        if (!setupEconomy() ) {
            this.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }

        GeneratorConfigManager gcm = new GeneratorConfigManager(this);

        if(gcm.getWorldName() != null){
            startGenerators();
        }else{
            this.getLogger().info("===========================================");
            this.getLogger().info("=====================================================");
            this.getLogger().info("This is probably your first time running this plugin!");
            this.getLogger().info("Make sure that the GenPvPWorld field in the generatorRunnableIDs.yml file is set!");
            this.getLogger().info("After that, reload the plugin :)");
            this.getLogger().info("===========================================");
            this.getLogger().info("=====================================================");
        }

        this.getLogger().info("==================================================================");

    }

    @Override
    public void onDisable() {
        updatePlayerXP();
        enchantmentRegister.unRegisterEnchantments();
    }

    public CoreAPI getApi(){
        return api;
    }

    public void validateConfigs(){
        File[] files = {new File(this.getDataFolder(), "config.yml"), new File(this.getDataFolder(), "equipped.yml"), new File(this.getDataFolder(), "amplifiers.yml"), new File(this.getDataFolder(), "generators.yml"), new File(this.getDataFolder(), "animations.yml"), new File(this.getDataFolder(), "holograms.yml"), new File(this.getDataFolder(), "itemsaver.yml")};
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
        this.getServer().getPluginManager().registerEvents(new ScoreboardManager(this), this);
    }

    private void initializeCommands(){
        getLogger().info("Setting up commands...");
        this.getServer().getPluginCommand("menu").setExecutor(new MenuCommand(this));
        this.getServer().getPluginCommand("genpvp").setExecutor(new AdminCommands(this));
        this.getServer().getPluginCommand("leaderboard").setExecutor(new LeaderboardCommand(this));
        this.getServer().getPluginCommand("genpvpconfirm").setExecutor(new ConfirmedCommands(this));
    }

    private void registerEnchants(){
        getLogger().info("Registering enchantments...");
        this.enchantmentRegister = new EnchantmentRegister(this);
        enchantmentRegister.registerEnchantments();
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

        generators.add(coalOre1);
        generators.add(coalOre2);
        generators.add(coalOre3);
        generators.add(coalBlock1);
        generators.add(coalBlock2);
        generators.add(ironOre1);
        generators.add(ironOre2);
        generators.add(ironBlock1);
        generators.add(goldOre1);
        generators.add(goldBlock1);
        generators.add(diamondOre1);
        generators.add(diamondBlock1);
        generators.add(emeraldOre1);

    }

    public void disableHolograms(){

        File hologramFile = new File(this.getDataFolder(), "holograms.yml");
        this.getLogger().info("Removing holograms...");
        if(hologramFile.exists()){
            HologramHelper hh = new HologramHelper(this);

            if(hh.getAllNames() != null){
                for(String hologramName : hh.getAllNames()){
                    hh.removeHologram(hologramName);
                }
            }

        }
    }

    private void updatePlayerXP(){
        for(Player p : Bukkit.getOnlinePlayers()){
            p.setExp(0);
            p.setLevel(0);
        }
    }

    public void restartGenerators(){
        this.getLogger().info("Restarting generators!");
        stopGeneratorRunnables();
        disableHolograms();
        startGenerators();
    }

    public void stopGeneratorRunnables(){
        for(Generator generator : generators){
            List<Integer> genRunnables = generator.getRunnableIds();
            for(Integer id : genRunnables){
                Bukkit.getScheduler().cancelTask(id);
            }
        }
    }

    public List<Generator> getGenerators(){
        return generators;
    }

    private void startAmplifierTimer(){
        new AmplifierTimer(this).runTaskTimer(this, 0L, 20L);
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPerms(){
        return perms;
    }

    public Logger getPluginLogger(){
        return logger;
    }



}

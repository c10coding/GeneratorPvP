package me.c10coding.generatorpvp.managers;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.coreapi.holograms.HologramHelper;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.GeneratorTypes;
import me.c10coding.generatorpvp.files.GeneratorConfigManager;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class Generator {

    private GeneratorTypes genType;
    private GeneratorPvP plugin;
    private GeneratorConfigManager gcm;
    private Location genLoc;
    private double amountSpawned;
    private double spawnRate;
    private List<String> hologramLines;
    private Chat chatFactory;
    private ItemStack itemSpawned;
    private int numGen;
    private String hologramConfigName;

    public Generator(GeneratorPvP plugin, GeneratorTypes genType, int numGen){
        this.plugin = plugin;
        this.genType = genType;
        this.gcm = new GeneratorConfigManager(plugin);
        this.genLoc = gcm.getGenLocation(genType, numGen);
        this.amountSpawned = gcm.getAmountSpawned(genType);
        this.spawnRate = gcm.getSpawnRate(genType);
        this.hologramLines = gcm.getHologramLines(genType);
        this.chatFactory = plugin.getApi().getChatFactory();
        this.itemSpawned = new ItemStack(Material.getMaterial(genType.toString()), (int) amountSpawned);
        this.numGen = numGen;
        this.hologramConfigName = GPUtils.enumToConfigKey(genType) + numGen;
    }

    private void setupHolograms(){

        HologramHelper hologramHelper = new HologramHelper(plugin);

        hologramLines = GPUtils.colorLore(hologramLines);

        for(int x = 0; x < hologramLines.size(); x++){
            hologramLines.set(x, replacePlaceholders(hologramLines.get(x)));
        }

        String firstLine = chatFactory.chat(hologramLines.get(0));
        hologramHelper.createHologram(genLoc, firstLine, hologramConfigName);

        for(int lineNum = 1; lineNum < hologramLines.size(); lineNum++){
            hologramHelper.addLine(hologramConfigName, chatFactory.chat(hologramLines.get(lineNum)));
        }

    }

    public void startGenerator(){
        setupHolograms();
        updateName();
        new BukkitRunnable(){

            @Override
            public void run() {

                Collection<Entity> entitiesNearby = genLoc.getWorld().getNearbyEntities(genLoc, 5, 5, 5);
                if(hasPlayer(entitiesNearby)){
                    genLoc.getWorld().dropItem(genLoc, itemSpawned);
                }

            }
        }.runTaskTimer(plugin, 10L, (long) (spawnRate * 20));
    }


    public void updateName(){

        new BukkitRunnable(){

            @Override
            public void run() {

                HologramHelper hologramHelper = new HologramHelper(plugin);

                Collection<Entity> entitiesNearby = genLoc.getWorld().getNearbyEntities(genLoc, 5, 5, 5);
                String newLine;
                String currentLine = chatFactory.chat(hologramHelper.getLine(hologramConfigName, 1));
                if(hasPlayer(entitiesNearby)){
                    newLine = chatFactory.chat(genType.getColorCode() + genType.getDisplayName() + " &fGenerator " + "&aActive");
                }else{
                    newLine = chatFactory.chat(genType.getColorCode() + genType.getDisplayName() + " &fGenerator " + "&cInactive");
                }

                if(!currentLine.equalsIgnoreCase(newLine)){
                    Bukkit.broadcastMessage(hologramConfigName);
                    hologramHelper.editLine(hologramConfigName, newLine, 1);
                }

            }
        }.runTaskTimer(plugin, 11L, 20L);
    }

    private enum HologramPlaceholders{
        SPAWN_RATE("%rate%", "SpawnRateInSeconds"),
        AMOUNT_SPAWNED("%amount%", "AmountSpawned");

        private String placeholder, configKey;
        HologramPlaceholders(String placeholder, String configKey){
            this.placeholder = placeholder;
            this.configKey = configKey;
        }
    }

    private boolean hasPlayer(Collection<Entity> entities){
        Predicate<Entity> entity = e -> e instanceof Player;
        return entities.stream().anyMatch(entity);
    }

    private String replacePlaceholders(String s){
        for(HologramPlaceholders p : HologramPlaceholders.values()){
            if(s.contains(p.placeholder)){
                s = s.replace(p.placeholder, gcm.getValue("Generator Settings." + GPUtils.enumToConfigKey(genType) + "." + p.configKey));
            }
        }
        return s;
    }

}
package me.c10coding.generatorpvp.managers;

import me.c10coding.coreapi.chat.ChatFactory;
import me.c10coding.coreapi.holograms.HologramHelper;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.GeneratorTypes;
import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import me.c10coding.generatorpvp.files.GeneratorConfigManager;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Predicate;

public class Generator {

    private GeneratorTypes genType;
    private GeneratorPvP plugin;
    private GeneratorConfigManager gcm;
    private AmplifiersConfigManager acm;
    private Location genLoc;
    private int amountSpawned;
    private int spawnRate;
    private List<String> hologramLines;
    private ChatFactory chatFactory;
    private int numGen;
    private List<Integer> runnableIds = new ArrayList<>();
    private int runnableID = 0;
    private String hologramConfigName;

    public Generator(GeneratorPvP plugin, GeneratorTypes genType, int numGen){
        this.plugin = plugin;
        this.genType = genType;
        this.gcm = new GeneratorConfigManager(plugin);
        this.acm = new AmplifiersConfigManager(plugin);
        this.genLoc = gcm.getGenLocation(genType, numGen);
        this.amountSpawned = gcm.getAmountSpawned(genType);
        this.spawnRate = gcm.getSpawnRate(genType);
        this.hologramLines = gcm.getHologramLines(genType);
        this.chatFactory = plugin.getAPI().getChatFactory();
        this.numGen = numGen;
        this.hologramConfigName = GPUtils.enumToConfigKey(genType) + numGen;

        boolean isMultiplierActive = acm.isAmplifierActivated("Multipliers");
        boolean isBoosterActivated = acm.isAmplifierActivated("Boosters");

        if(isMultiplierActive){
            double multiplier = acm.getMultiplier();
            int theoreticalAmount = (int) Math.round(multiplier * gcm.getAmountSpawned(genType));

            if(theoreticalAmount != amountSpawned){
                amountSpawned = theoreticalAmount;
            }
        }

        if(isBoosterActivated){
            double multiplier = acm.getBoostersMultiplier();
            int theoreticalSpawnRate = (int) Math.round((gcm.getSpawnRate(genType) / multiplier));

            if(theoreticalSpawnRate <= 0){
                theoreticalSpawnRate = 1;
            }

            if(theoreticalSpawnRate != spawnRate){
                spawnRate = theoreticalSpawnRate;
            }
        }

    }

    private void setupHolograms(){

        HologramHelper hologramHelper = new HologramHelper(plugin);
        hologramLines = GPUtils.colorLore(hologramLines);

        for(int x = 0; x < hologramLines.size(); x++){
            hologramLines.set(x, replacePlaceholders(hologramLines.get(x)));
        }

        String firstLine = chatFactory.colorString(hologramLines.get(0));

        //try{
        hologramHelper.createHologram(genLoc, firstLine, hologramConfigName);
            /*
        }catch(NullPointerException e){
            plugin.getLogger().info("This chunk is unloaded and holograms cannot be spawned here at this moment...");
            plugin.getLogger().info("This isn't an issue with the plugin");
        }*/

        for(int lineNum = 1; lineNum < hologramLines.size(); lineNum++){
            hologramHelper.addLine(hologramConfigName, chatFactory.colorString(hologramLines.get(lineNum)));
        }

    }


    public void startGenerator(){
        setupHolograms();
        updateFirstLine();
        updateSecondLine();
        new BukkitRunnable(){
            int counter = spawnRate;

            @Override
            public void run() {

                if(!runnableIds.contains(this.getTaskId())){
                    runnableIds.add(this.getTaskId());
                }

                Collection<Entity> entitiesNearby = genLoc.getWorld().getNearbyEntities(genLoc, 5, 5, 5);
                ItemStack itemSpawned;

                itemSpawned = new ItemStack(genType.getMaterial(), amountSpawned);

                ItemMeta itemMeta = itemSpawned.getItemMeta();
                itemMeta.setDisplayName(chatFactory.colorString(genType.getColorCode() + genType.getDisplayName()));
                List<String> lore = new ArrayList<>();
                lore.add(chatFactory.colorString("&c[&4!&c] &rUse this item to trade &c[&4!&c]"));
                itemMeta.setLore(lore);
                itemSpawned.setItemMeta(itemMeta);

                if(hasPlayer(entitiesNearby)){
                    genLoc.getWorld().dropItem(genLoc, itemSpawned);
                }
            }
        }.runTaskTimer(plugin, 0L, spawnRate * 20);
    }

    public void updateSecondLine(){
        new BukkitRunnable(){
            int counter = spawnRate;
            public void run() {

                if(!runnableIds.contains(this.getTaskId())){
                    runnableIds.add(this.getTaskId());
                }

                if(counter == 0){
                    counter = spawnRate;
                }

                HologramHelper hologramHelper = new HologramHelper(plugin);
                String newLine = chatFactory.colorString("&7Spawning &c" + amountSpawned + " in &c" + counter + "&7 Seconds");
                hologramHelper.editLine(hologramConfigName, newLine, 2);
                counter--;

            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void updateFirstLine(){

        new BukkitRunnable(){

            @Override
            public void run() {

                if(!runnableIds.contains(this.getTaskId())){
                    runnableIds.add(this.getTaskId());
                }

                HologramHelper hologramHelper = new HologramHelper(plugin);

                Collection<Entity> entitiesNearby = genLoc.getWorld().getNearbyEntities(genLoc, 5, 5, 5);
                String newLine;
                String currentLine = chatFactory.colorString(hologramHelper.getLine(hologramConfigName, 1));
                if(hasPlayer(entitiesNearby)){
                    newLine = chatFactory.colorString(genType.getColorCode() + genType.getDisplayName() + " &fGenerator " + "&aActive");
                }else{
                    newLine = chatFactory.colorString(genType.getColorCode() + genType.getDisplayName() + " &fGenerator " + "&cInactive");
                }

                if(!chatFactory.removeChatColor(currentLine).equalsIgnoreCase(chatFactory.removeChatColor(newLine))){
                    if(hologramHelper.isAHologram(hologramConfigName)){
                        hologramHelper.editLine(hologramConfigName, newLine, 1);
                    }
                }

            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public enum HologramPlaceholders{
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

        HashMap<String, Integer> placeholders = new HashMap<>();
        placeholders.put("%rate%", spawnRate);
        placeholders.put("%amount%", amountSpawned);

        for(Map.Entry placeholder : placeholders.entrySet()){
            String p = (String) placeholder.getKey();
            if(s.contains(p)){
                String value = String.valueOf(placeholder.getValue());
                s = s.replace(p, value);
            }
        }

        return s;
    }

    public List<Integer> getRunnableIds(){
        return runnableIds;
    }

    public void setAmountSpawned(int amountSpawned){
        this.amountSpawned = amountSpawned;
    }

    public void setSpawnRate(int spawnRate){
        this.spawnRate = spawnRate;
    }

    public GeneratorTypes getGenType(){
        return genType;
    }

}
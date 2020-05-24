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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.List;

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
    }

    private void setupHolograms(){
        HologramHelper hologramHelper = new HologramHelper(plugin);
        String hologramName = GPUtils.enumToConfigKey(genType);

        hologramLines = GPUtils.colorLore(hologramLines);

        for(int x = 0; x < hologramLines.size(); x++){
            hologramLines.set(x, replacePlaceholders(hologramLines.get(x)));
        }

        String firstLine = chatFactory.chat(hologramLines.get(0));
        hologramHelper.createHologram(genLoc, firstLine, hologramName);

        for(int lineNum = 1; lineNum < hologramLines.size(); lineNum++){
            hologramHelper.addLine(hologramName, chatFactory.chat(hologramLines.get(lineNum)));
        }

        if(!hologramHelper.hasAnimation(hologramName, 2)){
            hologramHelper.setAsAnimatable(GPUtils.enumToConfigKey(genType), 2);
        }

        hologramHelper.setAnimationStatus(hologramName, 2, true);

    }

    public void startGenerator(){
        setupHolograms();
        new BukkitRunnable(){

            @Override
            public void run() {

                Collection<Entity> entitiesNearby = spawnInvArmorStand().getNearbyEntities(5, 5, 5);
                if(!entitiesNearby.isEmpty()){
                    for(Entity e : entitiesNearby){
                        if(e instanceof Player){
                            genLoc.getWorld().dropItem(genLoc, itemSpawned);
                            Bukkit.broadcastMessage("Player near! Spawning at " + genType.name());
                        }
                    }
                }else{
                    Bukkit.broadcastMessage("Nobody near. Not spawning :(");
                }

            }
        }.runTaskTimer(plugin, 10L, (long) (spawnRate * 20));
    }

    private ArmorStand spawnInvArmorStand(){
        ArmorStand as = (ArmorStand) genLoc.getWorld().spawnEntity(genLoc, EntityType.ARMOR_STAND);
        as.setGravity(false);
        as.setVisible(false);
        return as;
    }

    private enum HologramPlaceholders{
        STATUS("%status%", "IsActive"),
        SPAWN_RATE("%rate%", "SpawnRateInSeconds"),
        AMOUNT_SPAWNED("%amount%", "AmountSpawned");

        private String placeholder, configKey;
        HologramPlaceholders(String placeholder, String configKey){
            this.placeholder = placeholder;
            this.configKey = configKey;
        }
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

package me.c10coding.generatorpvp.files;

import me.c10coding.coreapi.files.ConfigManager;
import me.c10coding.generatorpvp.GeneratorTypes;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class GeneratorConfigManager extends ConfigManager {

    public GeneratorConfigManager(JavaPlugin plugin) {
        super(plugin, "generators.yml");
    }

    public boolean isActive(GeneratorTypes genType){
        return config.getBoolean("Generator Settings." + GPUtils.enumToConfigKey(genType) + ".IsActive");
    }

    public Location getGenLocation(GeneratorTypes genType, int numGen){
        World world = Bukkit.getWorld(config.getString("GenPvPWorld"));
        int x = config.getInt("Generator Settings." + GPUtils.enumToConfigKey(genType) + ".Locations." + numGen + ".X");
        int y = config.getInt("Generator Settings." + GPUtils.enumToConfigKey(genType) + ".Locations." + numGen + ".Y");
        int z = config.getInt("Generator Settings." + GPUtils.enumToConfigKey(genType) + ".Locations." + numGen + ".Z");
        return new Location(world,x,y,z);
    }

    public List<String> getHologramLines(GeneratorTypes genType){
        return config.getStringList("Generator Settings." + GPUtils.enumToConfigKey(genType) + ".Hologram.Lines");
    }

    public int getSpawnRate(GeneratorTypes genType){
        return config.getInt("Generator Settings." + GPUtils.enumToConfigKey(genType) + ".SpawnRateInSeconds");
    }

    public int getAmountSpawned(GeneratorTypes genType){
        return config.getInt("Generator Settings." + GPUtils.enumToConfigKey(genType) + ".AmountSpawned");
    }

    public String getWorldName(){
        return config.getString("GenPvPWorld");
    }



}

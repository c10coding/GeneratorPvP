package me.c10coding.generatorpvp.files;

import me.c10coding.coreapi.files.Config;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class StatsConfigManager extends Config {

    public StatsConfigManager(JavaPlugin plugin) {
        super(plugin, "stats.yml");
    }

    public void addPlayerToFile(UUID u){
        config.set(u.toString() + ".Kills", 0);
        config.set(u.toString() + ".Deaths", 0);
        saveConfig();
    }

    public enum Stats{
        KILLS,
        DEATHS
    }

    public boolean isInFile(UUID u){
        return config.getString(u.toString()) != null;
    }

    public void increaseKills(UUID u){
        int kills = getKills(u);
        config.set(u.toString() + ".Kills", kills + 1);
    }

    public void resetStat(UUID u, Stats stat){
        String configKey = GPUtils.enumToConfigKey(stat);
        config.set(u.toString() + "." + configKey, null);
    }

    public void resetAllStats(){
        ConfigurationSection section = config.getConfigurationSection("");
        for(String uuid : section.getKeys(false)){
            config.set(uuid, null);
        }
    }

    public int getKills(UUID u){
        return config.getInt(u.toString() + ".Kills");
    }

    public int getDeaths(UUID u){
        return config.getInt(u.toString() + ".Deaths");
    }

    public void increaseDeaths(UUID u){
        int deaths = getDeaths(u);
        config.set(u.toString() + ".Deaths", deaths + 1);
    }

}

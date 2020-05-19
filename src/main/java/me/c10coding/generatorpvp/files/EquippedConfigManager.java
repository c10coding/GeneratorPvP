package me.c10coding.generatorpvp.files;

import me.c10coding.coreapi.files.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class EquippedConfigManager extends ConfigManager {

    private UUID u;

    public EquippedConfigManager(JavaPlugin plugin, UUID u) {
        super(plugin, "equipped.yml");
        this.u = u;
    }

    public void setEquipped(String key, String category, boolean isEquipped){
        if(hasSomethingEquipped(category)){
            Bukkit.broadcastMessage("There is something equipped");
            String equippedKey = getThingEquipped(category);
            Bukkit.broadcastMessage("Equipped key: " + equippedKey);
            config.set(category + "." + u + "." + ".IsEquipped." + equippedKey, false);
        }
        config.set(category + "." + u + "." + ".IsEquipped." + key, true);
    }

    public boolean isEquipped(String key, String category){
        return config.getBoolean(category + "." + u + "." + ".IsEquipped." + key);
    }

    public boolean isPurchased(String key, String category){
        return config.getBoolean(category + "." + u + "." + ".IsPurchased." + key);
    }

    public boolean hasSomethingEquipped(String category){
        List<Boolean> values = getCategoryIsEquipped(category);
        return values.contains(true);
    }

    public void addPlayerToFile(UUID u){

        config.set("Chat." + u.toString() + ".IsPurchased.Gray", false);
        config.set("Chat." + u.toString() + ".IsPurchased.Green", false);
        config.set("Chat." + u.toString() + ".IsPurchased.Orange", false);
        config.set("Chat." + u.toString() + ".IsPurchased.Blue", false);
        config.set("Chat." + u.toString() + ".IsPurchased.Orange", false);
        config.set("Chat." + u.toString() + ".IsPurchased.Pink", false);
        config.set("Chat." + u.toString() + ".IsEquipped.Gray", false);
        config.set("Chat." + u.toString() + ".IsEquipped.Green", false);
        config.set("Chat." + u.toString() + ".IsEquipped.Orange", false);
        config.set("Chat." + u.toString() + ".IsEquipped.Blue", false);
        config.set("Chat." + u.toString() + ".IsEquipped.Orange", false);
        config.set("Chat." + u.toString() + ".IsEquipped.Pink", false);

    }

    public Map<Object, Object> mapIsEquipped(String category){
        Map<Object, Object> values = new HashMap<>();
        ConfigurationSection cs = config.getConfigurationSection(category + "." + u + ".IsEquipped");
        for(String key: cs.getKeys(false)){
            values.put(key, config.getBoolean(category + "." + u + "." + ".IsEquipped." + key));
        }
        return values;
    }

    public List<Boolean> getCategoryIsEquipped(String category){
        List<Boolean> values = new ArrayList<>();
        ConfigurationSection cs = config.getConfigurationSection(category);
        for(String k : cs.getKeys(false)){
            values.add(config.getBoolean(category + "." + u + "." + ".IsEquipped." + k));
        }
        return values;
    }

    public String getThingEquipped(String category){
        Map<Object, Object> map = mapIsEquipped(category);
        for(Map.Entry e : map.entrySet()){
            if((boolean)e.getValue()){
                return (String) e.getKey();
            }
        }
        return null;
    }

    public boolean isInFile(){
        return config.getString("Chat." + u.toString() + ".IsPurchased.Gray") == null;
    }


}

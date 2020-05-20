package me.c10coding.generatorpvp.files;

import me.c10coding.coreapi.files.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class EquippedConfigManager extends ConfigManager {

    private UUID u;

    public EquippedConfigManager(JavaPlugin plugin, UUID u) {
        super(plugin, "equipped.yml");
        this.u = u;
    }

    public void setEquipped(String key, String category){
        if(hasSomethingEquipped(category)){
            String equippedKey = getThingEquipped(category);
            config.set(category + "." + u.toString() + ".IsEquipped." + equippedKey, false);
        }
        config.set(category + "." + u.toString() + ".IsEquipped." + key, true);
    }

    public void setEquipped(String key, String category, boolean b){
        config.set(category + "." + u.toString() + ".IsEquipped." + key, b);
    }

    public void setPurchased(String key, String category, boolean b){
        config.set(category + "." + u.toString() + ".IsPurchased." + key, b);
    }

    public boolean isEquipped(String key, String category){
        return config.getBoolean(category + "." + u.toString() + ".IsEquipped." + key);
    }

    public boolean isPurchased(String key, String category){
        return config.getBoolean(category + "." + u.toString() + ".IsPurchased." + key);
    }

    public boolean hasSomethingEquipped(String category){
        List<Boolean> values = getCategoryIsEquipped(category);
        return values.contains(true);
    }

    public void addPlayerToFile(){

        config.set("Chat." + u.toString() + ".IsPurchased.Gray", true);
        config.set("Chat." + u.toString() + ".IsPurchased.Green", false);
        config.set("Chat." + u.toString() + ".IsPurchased.Yellow", false);
        config.set("Chat." + u.toString() + ".IsPurchased.Blue", false);
        config.set("Chat." + u.toString() + ".IsPurchased.Gold", false);
        config.set("Chat." + u.toString() + ".IsPurchased.Purple", false);

        config.set("Chat." + u.toString() + ".IsEquipped.Gray", true);
        config.set("Chat." + u.toString() + ".IsEquipped.Green", false);
        config.set("Chat." + u.toString() + ".IsEquipped.Yellow", false);
        config.set("Chat." + u.toString() + ".IsEquipped.Blue", false);
        config.set("Chat." + u.toString() + ".IsEquipped.Gold", false);
        config.set("Chat." + u.toString() + ".IsEquipped.Purple", false);

    }

    public Map<Object, Object> mapIsEquipped(String category){
        Map<Object, Object> values = new HashMap<>();
        ConfigurationSection cs = config.getConfigurationSection(category + "." + u + ".IsEquipped");
        for(String key: cs.getKeys(false)){
            values.put(key, config.getBoolean(category + "." + u.toString() + ".IsEquipped." + key));
        }
        return values;
    }

    public List<Boolean> getCategoryIsEquipped(String category){
        List<Boolean> values = new ArrayList<>();
        ConfigurationSection cs = config.getConfigurationSection(category + "." + u.toString() + ".IsEquipped");
        for(String k : cs.getKeys(false)){
            values.add(config.getBoolean(category + "." + u.toString() + ".IsEquipped." + k));
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

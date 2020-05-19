package me.c10coding.generatorpvp.files;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.generatorpvp.GeneratorPvP;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class DefaultConfigManager extends me.c10coding.coreapi.files.ConfigManager {

    private Chat chatFactory;

    public DefaultConfigManager(JavaPlugin plugin) {
        super(plugin, "config.yml");
        this.chatFactory = ((GeneratorPvP) plugin).getApi().getChatFactory();
    }

    public Map<String, Object> getSlotInfo(String menuType, int slotNum){
        Map<String, Object> slotInfo = new HashMap<>();

        Material blockMat = Material.valueOf(config.getString(getMenuPath(menuType, slotNum, "Material")));
        String blockDisplayName = config.getString(getMenuPath(menuType, slotNum, "DisplayName"));
        List<String> lore =  config.getStringList(getMenuPath(menuType, slotNum, "Lore"));

        for(String s : lore){
            int indexOfString = lore.indexOf(s);
            lore.set(indexOfString, chatFactory.chat(s));
        }

        slotInfo.put("Lore", lore);
        slotInfo.put("slotNum", slotNum);
        slotInfo.put("Material", blockMat);
        slotInfo.put("DisplayName", chatFactory.chat(blockDisplayName));
        return slotInfo;
    }

    public int getNumMenuSlots(String menuType){
        return getSlots(menuType).size();
    }

    public List<Integer> getSlots(String menuType){
        List<Integer> slotNums = new ArrayList<>();
        Set<String> cs = config.getConfigurationSection("Menus." + menuType).getKeys(false);
        cs.stream().forEach(s -> slotNums.add(Integer.parseInt(s)));
        return slotNums;
    }

    public double getTPCost(String key){
        return config.getDouble("Costs.TP." + key);
    }

    public double getOreTPCost(String key){
        return config.getDouble("Costs.OreTP." + key);
    }

    public double getOreCost(String key, int amount){
        return config.getDouble("Costs.Ores." + amount + "." + key);
    }

    public double getWeaponsCost(String key){
        return config.getDouble("Costs.Weapons." + key);
    }

    public double getChatCost(String key){
        return config.getDouble("Costs.Chat." + key);
    }

    private String getMenuPath(String menuType, int slotNum, String key){
        return "Menus." + menuType + "." + slotNum + "." + key;
    }



}

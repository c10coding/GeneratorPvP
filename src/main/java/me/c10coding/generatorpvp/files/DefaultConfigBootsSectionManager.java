package me.c10coding.generatorpvp.files;

import me.c10coding.coreapi.files.ConfigManager;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultConfigBootsSectionManager extends ConfigManager {

    public DefaultConfigBootsSectionManager(JavaPlugin plugin) {
        super(plugin, "config.yml");
    }

    public enum SuperBootsProperty{
        DURATION,
        COOLDOWN,
        GLOW_BLOCK_RANGE,
        LEVEL,
        EXTRA_HEART_AMOUNT,
        BLINDNESS_BLOCK_RANGE,
    }

    public int getBootsProperty(String configKey, SuperBootsProperty property){
        return config.getInt("SuperBoots Properties." + configKey + "." + GPUtils.enumToString(property));
    }

    public boolean isLevelable(String configKey){
        return config.getString("SuperBoots Properties." + configKey + "." + "Level") != null;
    }

}

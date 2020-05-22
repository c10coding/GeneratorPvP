package me.c10coding.generatorpvp.files;

import me.c10coding.coreapi.files.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class AmplifiersConfigManager extends ConfigManager {

    public AmplifiersConfigManager(JavaPlugin plugin) {
        super(plugin, "amplifiers.yml");
    }

    public boolean isAmplifierActivated(String amplifierName){
        return config.getBoolean("Amplifier Activation Status." + amplifierName + ".Active");
    }

    /*
        I'm aware that these could be the same method
     */

    public void setAmplifierToActive(String amplifierName){
        config.set("Amplifier Activation Status." + amplifierName + ".Active", true);
    }

    public void setAmplifierToNotActive(String amplifierName){
        config.set("Amplifier Activation Status." + amplifierName + ".Active", false);
    }

    public String getWhoActivatedAmplifier(String amplifierName){
        return config.getString("Amplifier Activation Status." + amplifierName + ".Person Who Activated");
    }

    public void setWhoActivatedAmplifier(String amplifierName, String playerName){
        config.set("Amplifier Activation Status." + amplifierName + ".Person Who Activated", playerName);
    }

    public void setAmplifierAsActivated(String amplifierName){
        config.set("Amplifier Activation Status." + amplifierName, true);
    }

    public void setAmplifierTimer(String amplifierName, int levelAmplifier){
        double lengthOfAmplifier = getAmplifierDuration(amplifierName, levelAmplifier);
        double seconds = lengthOfAmplifier * 3600;
        config.set("Amplifier Activation Status." + amplifierName + ".TimeLeft", seconds);
    }

    public void updateAmplifierTime(String amplifierName){
        int secondsLeft = getAmplifierSecondsLeft(amplifierName);
        config.set("Amplifier Activation Status." + amplifierName + ".TimeLeft", secondsLeft - 1);
    }

    /*
        When the amplifier time is at 0
     */
    public void removeAmplifier(String amplifierName){
        setWhoActivatedAmplifier(amplifierName, "");
        setAmplifierToNotActive(amplifierName);
    }

    public int getAmplifierSecondsLeft(String amplifierName){
        return config.getInt("Amplifier Activation Status." + amplifierName + ".TimeLeft");
    }

    public double getAmplifierDuration(String amplifierName, int levelAmplifier){
       return config.getDouble("Amplifier Info." + amplifierName + "." + levelAmplifier + ".Duration");
    }

    public double getBoostersMultiplier(){
        return config.getDouble("Amplifier Info.Boosters.Multiplier");
    }

    public double getCoinsMultiplier(){
        return config.getDouble("Amplifier Info.Coin Multiplier.Multiplier");
    }

}

package me.c10coding.generatorpvp;

import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import org.bukkit.scheduler.BukkitRunnable;

public class AmplifierTimer extends BukkitRunnable {

    private String[] amplifierConfigKeys = {"Multipliers", "Boosters", "Coin Multiplier"};
    private GeneratorPvP plugin;

    public AmplifierTimer(GeneratorPvP plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        AmplifiersConfigManager acm = new AmplifiersConfigManager(plugin);
        for(String amplifierKey : amplifierConfigKeys){
            if(acm.isAmplifierActivated(amplifierKey)){
                int secondsLeft = acm.getAmplifierSecondsLeft(amplifierKey);
                if(secondsLeft != 0){
                    acm.updateAmplifierTime(amplifierKey);
                }else{
                    acm.removeAmplifier(amplifierKey);
                }
                acm.saveConfig();
            }
        }
    }
}

package me.c10coding.generatorpvp.runnables;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import me.c10coding.generatorpvp.managers.AnnouncementsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
                    int amplifierLevel = acm.getActivatedAmplifierLevel(amplifierKey);
                    int duration = (int) acm.getAmplifierDuration(amplifierKey, amplifierLevel);
                    if(duration / 2 == secondsLeft || secondsLeft == 300){
                        AnnouncementsManager am = new AnnouncementsManager(plugin);
                        String whoActivatedAmplifier = acm.getWhoActivatedAmplifier(amplifierKey);
                        am.announceAmplifierActivated(whoActivatedAmplifier, amplifierKey, duration);
                    }
                    acm.updateAmplifierTime(amplifierKey);
                    acm.saveConfig();
                }else{
                    acm.removeAmplifier(amplifierKey);
                    acm.saveConfig();
                    plugin.restartGenerators();
                }
            }
        }
    }
}

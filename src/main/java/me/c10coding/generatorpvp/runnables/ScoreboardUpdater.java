package me.c10coding.generatorpvp.runnables;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.managers.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardUpdater extends BukkitRunnable {

    private GeneratorPvP plugin;
    private ScoreboardManager sm;

    public ScoreboardUpdater(GeneratorPvP plugin){
        this.plugin = plugin;
        this.sm = new ScoreboardManager(plugin);
    }

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            sm.setSB(p);
        }
    }
}

package me.c10coding.generatorpvp.managers;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import me.c10coding.generatorpvp.files.StatsConfigManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.Arrays;
import java.util.List;

public class ScoreboardManager implements Listener {

    private GeneratorPvP plugin;
    private Chat chatFactory;

    public ScoreboardManager(GeneratorPvP plugin){
        this.plugin = plugin;
        this.chatFactory = plugin.getApi().getChatFactory();
    }

    public void setSB(Player p){
        Player player = p;
        org.bukkit.scoreboard.ScoreboardManager scoreboardManager = Bukkit.getServer().getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective obj = scoreboard.registerNewObjective("Stats", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(chatFactory.chat("&b&lHEIGHTS"));
        StatsConfigManager scm = new StatsConfigManager(plugin);

        if(!scm.isInFile(player.getUniqueId())){
            scm.addPlayerToFile(player.getUniqueId());
        }

        AmplifiersConfigManager acm = new AmplifiersConfigManager(plugin);

        Score name = obj.getScore(chatFactory.chat("&fName: &7" + player.getName()));
        name.setScore(11);
        String group = GeneratorPvP.getPerms().getPrimaryGroup(player);
        Score rank = obj.getScore(chatFactory.chat("&fRank: " + group));
        rank.setScore(10);

        Score empty9 = obj.getScore("    ");
        empty9.setScore(9);

        Score kills = obj.getScore(chatFactory.chat("&fKills: &a" + scm.getKills(player.getUniqueId())));
        kills.setScore(8);
        Score deaths = obj.getScore(chatFactory.chat("&fDeaths: &c" + scm.getDeaths(player.getUniqueId())));
        deaths.setScore(7);

        Score empty7 = obj.getScore("   ");
        empty7.setScore(6);

        Score coins = obj.getScore(chatFactory.chat("&fCoins: &6" + (int)plugin.getEconomy().getBalance(player)));
        coins.setScore(5);

        Score empty5 = obj.getScore("  ");
        empty5.setScore(4);

        String isActive = chatFactory.chat("&cInactive");
        if(acm.isAmplifierActivated("Boosters") || acm.isAmplifierActivated("Multipliers") || acm.isAmplifierActivated("Coin Multiplier")){
            isActive = "&aActive";
        }
        Score amplifier = obj.getScore(chatFactory.chat("&fAmplifier: " + isActive));
        amplifier.setScore(3);
        Score warnings = obj.getScore(chatFactory.chat("&fWarnings: " + 0));
        warnings.setScore(2);

        Score empty2 = obj.getScore(" ");
        empty2.setScore(1);

        Score footer = obj.getScore(StringUtils.center(chatFactory.chat("&e&LStore.HeightsMC.com"), 2));
        footer.setScore(0);

        player.setScoreboard(scoreboard);
    }

}



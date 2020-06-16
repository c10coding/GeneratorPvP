package me.c10coding.generatorpvp.runnables;

import me.TechsCode.UltraPunishments.UltraPunishments;
import me.TechsCode.UltraPunishments.types.IndexedPlayer;
import me.c10coding.coreapi.chat.ChatFactory;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import me.c10coding.generatorpvp.files.StatsConfigManager;
import me.c10coding.generatorpvp.managers.ScoreboardManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardUpdater extends BukkitRunnable {

    private GeneratorPvP plugin;
    private ScoreboardManager sm;
    private ChatFactory chatFactory = new ChatFactory();
    private StatsConfigManager scm;
    private AmplifiersConfigManager acm;

    public ScoreboardUpdater(GeneratorPvP plugin){
        this.plugin = plugin;
        this.sm = new ScoreboardManager(plugin);
        this.scm = new StatsConfigManager(plugin);
        this.acm = new AmplifiersConfigManager(plugin);
    }

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){

            // Heights has a weird glitch that has players show as online that aren't really online which is why there is a null check for player and the team playername
            if(p != null){
                acm.reloadConfig();
                scm.reloadConfig();
                Scoreboard scoreboard = p.getScoreboard();

                Team playerName = scoreboard.getTeam("PlayerName");

                if(playerName == null){
                    continue;
                }

                Team rank = scoreboard.getTeam("Rank");
                Team kills = scoreboard.getTeam("Kills");
                Team deaths = scoreboard.getTeam("Deaths");
                Team warnings = scoreboard.getTeam("Warnings");
                Team coins = scoreboard.getTeam("Coins");
                Team amplifier = scoreboard.getTeam("Amplifier");

                playerName.setSuffix(chatFactory.colorString(p.getName()));

                int playerBalance = (int) GeneratorPvP.getEconomy().getBalance(p);
                coins.setSuffix(playerBalance + "");

                String group = chatFactory.colorString(PlaceholderAPI.setPlaceholders(p, "%uperms_rank%"));
                rank.setSuffix(group);

                int numKills = scm.getKills(p.getUniqueId());
                int numDeaths = scm.getDeaths(p.getUniqueId());
                kills.setSuffix(numKills+"");
                deaths.setSuffix(numDeaths+"");

                String isActive = chatFactory.colorString("&cInactive");

                if(acm.isAmplifierActivated("Boosters") || acm.isAmplifierActivated("Multipliers") || acm.isAmplifierActivated("Coin Multiplier")){
                    isActive = "&aActive";
                }

                amplifier.setSuffix(chatFactory.colorString(isActive));

                UltraPunishments up = (UltraPunishments) UltraPunishments.getAPI();
                IndexedPlayer ip = up.getPlayerIndexes().get(p.getUniqueId());
                int numWarnings = up.getWarningStorage().getWarnings().target(ip).count();
                warnings.setSuffix(chatFactory.colorString("&c" + numWarnings+""));

                p.setScoreboard(scoreboard);
            }

        }
    }
}

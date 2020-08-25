package me.c10coding.generatorpvp.runnables;

import me.TechsCode.UltraPunishments.UltraPunishments;
import me.TechsCode.UltraPunishments.storage.types.IndexedPlayer;
import me.c10coding.coreapi.chat.ChatFactory;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import me.c10coding.generatorpvp.files.StatsConfigManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Optional;

public class ScoreboardUpdater extends BukkitRunnable {

    private ChatFactory chatFactory = new ChatFactory();
    private StatsConfigManager scm;
    private AmplifiersConfigManager acm;

    public ScoreboardUpdater(GeneratorPvP plugin){
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
                Optional<IndexedPlayer> opIndexedPlayer = up.getPlayerIndexes().get(p.getUniqueId());
                if(opIndexedPlayer.isPresent()){
                    IndexedPlayer ip = opIndexedPlayer.get();
                    int numWarnings = up.getWarningStorage().getWarnings().target(ip).size();
                    warnings.setSuffix(chatFactory.colorString("&c" + numWarnings+""));
                }

                p.setScoreboard(scoreboard);
            }
        }
    }
}

package me.c10coding.generatorpvp.managers;

import me.c10coding.coreapi.chat.ChatFactory;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import me.c10coding.generatorpvp.files.StatsConfigManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager implements Listener {

    private GeneratorPvP plugin;
    private ChatFactory chatFactory;

    public ScoreboardManager(GeneratorPvP plugin){
        this.plugin = plugin;
        this.chatFactory = plugin.getAPI().getChatFactory();
    }

    public void setSB(Player player){

        StatsConfigManager scm = new StatsConfigManager(plugin);
        AmplifiersConfigManager acm = new AmplifiersConfigManager(plugin);

        org.bukkit.scoreboard.ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("Stats", "Dummy");
        objective.setDisplayName(chatFactory.colorString("&b&lHEIGHTS"));

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Team playerName = scoreboard.registerNewTeam("PlayerName");
        playerName.addEntry(chatFactory.colorString("&fName: &7"));
        playerName.setSuffix("");
        playerName.setPrefix("");
        objective.getScore(chatFactory.colorString("&fName: &7")).setScore(11);
        Team rank = scoreboard.registerNewTeam("Rank");
        rank.addEntry(chatFactory.colorString("&fRank: "));
        rank.setSuffix("");
        rank.setPrefix("");
        objective.getScore(chatFactory.colorString("&fRank: ")).setScore(10);

        Team empty9 = scoreboard.registerNewTeam("Empty9");
        empty9.addEntry(" ");
        empty9.setSuffix("");
        empty9.setPrefix("");
        objective.getScore(" ").setScore(9);

        Team kills = scoreboard.registerNewTeam("Kills");
        kills.addEntry(chatFactory.colorString("&fKills: &a"));
        kills.setSuffix("");
        kills.setPrefix("");
        objective.getScore(chatFactory.colorString("&fKills: &a")).setScore(8);
        Team deaths = scoreboard.registerNewTeam("Deaths");
        deaths.addEntry(chatFactory.colorString("&fDeaths: &c"));
        deaths.setSuffix("");
        deaths.setPrefix("");
        objective.getScore(chatFactory.colorString("&fDeaths: &c")).setScore(7);

        Team empty6 = scoreboard.registerNewTeam("Empty6");
        empty6.addEntry("  ");
        empty6.setSuffix("");
        empty6.setPrefix("");
        objective.getScore("  ").setScore(6);

        Team coins = scoreboard.registerNewTeam("Coins");
        coins.addEntry(chatFactory.colorString("&fCoins: &6"));
        coins.setSuffix("");
        coins.setPrefix("");
        objective.getScore(chatFactory.colorString("&fCoins: &6")).setScore(5);

        Team empty4 = scoreboard.registerNewTeam("Empty4");
        empty4.addEntry("    ");
        empty4.setSuffix("");
        empty4.setPrefix("");
        objective.getScore("    ").setScore(4);

        Team amplifier = scoreboard.registerNewTeam("Amplifier");
        amplifier.addEntry(chatFactory.colorString("&fAmplifier: "));
        amplifier.setSuffix("");
        amplifier.setPrefix("");
        objective.getScore(chatFactory.colorString("&fAmplifier: ")).setScore(3);

        Team warnings = scoreboard.registerNewTeam("Warnings");
        warnings.addEntry(chatFactory.colorString("&fWarnings: "));
        warnings.setSuffix("");
        warnings.setPrefix("");
        objective.getScore(chatFactory.colorString("&fWarnings: ")).setScore(2);

        Team empty1 = scoreboard.registerNewTeam("Empty1");
        empty1.addEntry("   ");
        empty1.setSuffix("");
        empty1.setPrefix("");
        objective.getScore("   ").setScore(1);

        Team footer = scoreboard.registerNewTeam("Footer");
        footer.addEntry(chatFactory.colorString("&E&LStore.HeightsMC.com"));
        footer.setSuffix("");
        footer.setPrefix("");
        objective.getScore(chatFactory.colorString("&E&LStore.HeightsMC.com")).setScore(0);

        int playerBalance = (int) GeneratorPvP.getEconomy().getBalance(player);
        coins.setSuffix(playerBalance + "");

        String group = chatFactory.colorString(PlaceholderAPI.setPlaceholders(player, "%uperms_rank%"));
        playerName.setSuffix(player.getName());
        rank.setSuffix(group);

        int numKills = scm.getKills(player.getUniqueId());
        int numDeaths = scm.getDeaths(player.getUniqueId());
        kills.setSuffix(numKills+"");
        deaths.setSuffix(numDeaths+"");

        String isActive = chatFactory.colorString("&cInactive");
        if(acm.isAmplifierActivated("Boosters") || acm.isAmplifierActivated("Multipliers") || acm.isAmplifierActivated("Coin Multiplier")){
            isActive = "&aActive";
        }
        amplifier.setSuffix(chatFactory.colorString(isActive));
        player.setScoreboard(scoreboard);

    }

}



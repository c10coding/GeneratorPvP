package me.c10coding.generatorpvp;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import me.c10coding.generatorpvp.files.StatsConfigManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

public class ScoreboardManager implements Listener {

    private GeneratorPvP plugin;
    private Chat chatFactory;

    public ScoreboardManager(GeneratorPvP plugin){
        this.plugin = plugin;
        this.chatFactory = plugin.getApi().getChatFactory();
    }

    @EventHandler
    public void onJoinServer(PlayerJoinEvent e){
        Player player = e.getPlayer();
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
        name.setScore(0);

        //Score rank = obj.getScore("&fRank: " + );

        Score kills = obj.getScore(chatFactory.chat("&fKills: &a" + scm.getKills(player.getUniqueId())));
        kills.setScore(4);

        Score deaths = obj.getScore(chatFactory.chat("&fDeaths: &c" + scm.getDeaths(player.getUniqueId())));
        deaths.setScore(5);

        Score coins = obj.getScore(chatFactory.chat("&fCoins: &6" + GeneratorPvP.getEconomy().getBalance(player)));
        coins.setScore(7);

        String isActive = chatFactory.chat("&cInactive");


        if(acm.isAmplifierActivated("Boosters") || acm.isAmplifierActivated("Multipliers") || acm.isAmplifierActivated("Coin Multiplier")){
            isActive = "&aActive";
        }

        Score amplifier = obj.getScore("&fAmplifier: &6" + isActive);
        amplifier.setScore(8);

        Score warnings = obj.getScore("&fWarnings: " + 0);
        warnings.setScore(9);

        Score footer = obj.getScore(StringUtils.center("&E&LStore.HeightsMC.com", 2));


        /*
        new BukkitRunnable(){
            int index = 0;
            @Override
            public void run() {
                String[] names = {"&3&lHEIGHTS", "&b&lHEIGHTS"};

                Objective obj = scoreboard.getObjective("Stats");

                if(index == 2){
                   index = 0;
                }

                obj.setDisplayName(names[index]);
                index++;

            }
        }.runTaskTimer(plugin, 0L, 20L);*/

        player.setScoreboard(scoreboard);

    }

}

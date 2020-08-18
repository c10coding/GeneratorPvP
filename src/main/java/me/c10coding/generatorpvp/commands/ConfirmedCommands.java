package me.c10coding.generatorpvp.commands;

import me.c10coding.coreapi.chat.ChatFactory;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
import me.c10coding.generatorpvp.files.StatsConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class ConfirmedCommands implements CommandExecutor {

    private GeneratorPvP plugin;
    private ChatFactory chatFactory;
    private String prefix;

    public ConfirmedCommands(GeneratorPvP plugin){
        this.plugin = plugin;
        this.chatFactory = plugin.getAPI().getChatFactory();
        this.prefix = plugin.getPrefix();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        StatsConfigManager scm = new StatsConfigManager(plugin);
        EquippedConfigManager ecm = new EquippedConfigManager(plugin);
        if(sender.isOp()){
            if(args[0].equalsIgnoreCase("stats") && args[1].equalsIgnoreCase("reset") && args.length == 2){
                scm.resetAllStats();
                scm.saveConfig();
                chatFactory.sendPlayerMessage(" ", false, sender, null);
                chatFactory.sendPlayerMessage("You have reset all player stats!", false, sender, prefix);
                chatFactory.sendPlayerMessage(" ", false, sender, null);
            }else if(args[0].equalsIgnoreCase("stats") && args[1].equalsIgnoreCase("reset") && args.length == 3) {
                UUID uuid = UUID.fromString(args[2]);
                scm.resetStat(uuid, StatsConfigManager.Stats.KILLS);
                scm.resetStat(uuid, StatsConfigManager.Stats.DEATHS);
                scm.saveConfig();
                chatFactory.sendPlayerMessage(" ", false, sender, null);
                chatFactory.sendPlayerMessage("You have reset all of &e" + Bukkit.getOfflinePlayer(uuid).getName() + "'s&f stats!" , false, sender, null);
                chatFactory.sendPlayerMessage(" ", false, sender, null);
            }else if(args[0].equalsIgnoreCase("stats") && args[1].equalsIgnoreCase("reset") && args.length == 4){
                UUID uuid = UUID.fromString(args[2]);
                String stat = args[3];
                if(stat.equalsIgnoreCase("kills")){
                    scm.resetStat(uuid, StatsConfigManager.Stats.KILLS);
                    chatFactory.sendPlayerMessage(" ", false, sender, null);
                    chatFactory.sendPlayerMessage("You have reset &e" + Bukkit.getOfflinePlayer(uuid).getName() + "'s&f kills!" , false, sender, null);
                }else{
                    scm.resetStat(uuid, StatsConfigManager.Stats.DEATHS);
                    chatFactory.sendPlayerMessage(" ", false, sender, null);
                    chatFactory.sendPlayerMessage("You have reset &e" + Bukkit.getOfflinePlayer(uuid).getName() + "'s&f deaths!" , false, sender, null);
                }
                chatFactory.sendPlayerMessage(" ", false, sender, null);
                scm.saveConfig();
            }else if(args[0].equalsIgnoreCase("reset") && args.length == 1){
                ecm.resetAllData();
                for(OfflinePlayer p : Bukkit.getOfflinePlayers()){
                    GeneratorPvP.getEconomy().withdrawPlayer(p, GeneratorPvP.getEconomy().getBalance(p));
                }
                chatFactory.sendPlayerMessage(" ", false, sender, null);
                chatFactory.sendPlayerMessage("You have reset all player data (Boots, Warps, Chat colors, etc)", true, sender, prefix);
                chatFactory.sendPlayerMessage(" ", false, sender, null);
            }else if(args[0].equalsIgnoreCase("reset") && args.length == 2){
                UUID uuid = UUID.fromString(args[1]);
                ecm.resetAPlayersData(uuid);
                chatFactory.sendPlayerMessage(" ", false, sender, null);
                chatFactory.sendPlayerMessage("You have reset &e" + Bukkit.getOfflinePlayer(uuid).getName() + "'s&f data (Boots, Warps, Chat colors, etc)!" , false, sender, null);
                chatFactory.sendPlayerMessage(" ", false, sender, null);
            }

        }
        return false;
    }
}

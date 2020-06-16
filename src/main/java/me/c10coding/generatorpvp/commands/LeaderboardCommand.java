package me.c10coding.generatorpvp.commands;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.generatorpvp.GeneratorPvP;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardCommand implements CommandExecutor {

    private Chat chatFactory;

    public LeaderboardCommand(GeneratorPvP plugin){
        this.chatFactory = plugin.getApi().getChatFactory();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        Collection<Player> onlinePlayers = (Collection<Player>) Bukkit.getOnlinePlayers();
        List<OfflinePlayer> uuids = new ArrayList<>();

        for(int x = 0; x < offlinePlayers.length; x++){
            uuids.add(offlinePlayers[x]);
        }

        for(Player p : onlinePlayers){
            uuids.add(p);
        }

        Map<OfflinePlayer, Integer> balancesPerPlayer = new HashMap<>();
        for(OfflinePlayer op : uuids){
            balancesPerPlayer.put(op, (int) Math.round(GeneratorPvP.getEconomy().getBalance(op)));
        }

        Map<OfflinePlayer,Integer> topTenPlayers =
                balancesPerPlayer.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(10)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        chatFactory.sendPlayerMessage("&8====================", false, sender, null);
        int numPlayer = 1;
        for(Map.Entry player : topTenPlayers.entrySet()){
            String playerName = ((OfflinePlayer)player.getKey()).getName();
            int balance = (int) player.getValue();
            chatFactory.sendPlayerMessage("&e#" + numPlayer + " &6 " + playerName + "&7 » &6" + balance + " Coins", false, sender, null);
            numPlayer++;
        }
        chatFactory.sendPlayerMessage("&8====================", false, sender, null);
        return false;
    }
}

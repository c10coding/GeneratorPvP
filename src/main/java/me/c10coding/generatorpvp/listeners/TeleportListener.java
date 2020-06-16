package me.c10coding.generatorpvp.listeners;

import me.c10coding.coreapi.chat.ChatFactory;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.DefaultConfigManager;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportListener implements Listener {

    private GeneratorPvP plugin;
    private DefaultConfigManager dcm;
    private ChatFactory chatFactory = new ChatFactory();

    public TeleportListener(GeneratorPvP plugin){
        this.plugin = plugin;
        this.dcm = new DefaultConfigManager(plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        EquippedConfigManager ecm = new EquippedConfigManager(plugin, p.getUniqueId());

        if(p.hasMetadata("TP")){
            if(e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()){

                int taskID = p.getMetadata("TP").get(0).asInt();

                if(p.hasMetadata("isSpecial")){
                    if(ecm.isPurchased("Special", "Warps")){
                        chatFactory.sendPlayerMessage(" ", false, p, null);
                        plugin.getApi().getChatFactory().sendPlayerMessage("&cTeleportation canceled!", false, p, plugin.getPrefix());
                    }else{
                        double costOfTP = dcm.getTPCost("SpecialTP");
                        GeneratorPvP.getEconomy().depositPlayer(p, costOfTP);
                        chatFactory.sendPlayerMessage(" ", false, p, null);
                        plugin.getApi().getChatFactory().sendPlayerMessage("&cTeleportation canceled! You have been given your &6coins&c back..", false, p, plugin.getPrefix());
                        chatFactory.sendPlayerMessage(" ", false, p, null);
                        chatFactory.sendPlayerMessage(" ", false, p, null);
                        plugin.getApi().getChatFactory().sendPlayerMessage("&a+" + (int) dcm.getTPCost("SpecialTP") + " &6Coins", false, p, plugin.getPrefix());
                    }
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                }else if(p.hasMetadata("TPCOST")){
                    int tpCost = p.getMetadata("TPCOST").get(0).asInt();
                    GeneratorPvP.getEconomy().depositPlayer(p, tpCost);
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    plugin.getApi().getChatFactory().sendPlayerMessage("&cTeleportation canceled! You have been given your &6coins&c back..", false, p, plugin.getPrefix());
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    plugin.getApi().getChatFactory().sendPlayerMessage("&a+" + tpCost + " &6Coins", false, p, plugin.getPrefix());
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                }else{
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    plugin.getApi().getChatFactory().sendPlayerMessage("&cTeleportation canceled!", false, p, plugin.getPrefix());
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                }

                Bukkit.getScheduler().cancelTask(taskID);
                removeMetadata(p);
            }
        }
    }

    public void removeMetadata(Player p){

        p.removeMetadata("TP", plugin);
        if(p.hasMetadata("isSpecial")){
            p.removeMetadata("isSpecial", plugin);
        }

        if(p.hasMetadata("TPCOST")){
            p.removeMetadata("TPCOST", plugin);
        }

    }
}

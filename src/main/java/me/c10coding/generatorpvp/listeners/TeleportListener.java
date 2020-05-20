package me.c10coding.generatorpvp.listeners;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.DefaultConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportListener implements Listener {

    private GeneratorPvP plugin;
    private DefaultConfigManager dcm;

    public TeleportListener(GeneratorPvP plugin){
        this.plugin = plugin;
        this.dcm = new DefaultConfigManager(plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(p.hasMetadata("TP")){
            if(e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()){

                int taskID = p.getMetadata("TP").get(0).asInt();
                double costOfTP = dcm.getTPCost("SpecialTP");

                if(p.hasMetadata("isSpecial") && p.getMetadata("isSpecial").get(0).asBoolean()){
                    GeneratorPvP.getEconomy().depositPlayer(p, costOfTP);
                    plugin.getApi().getChatFactory().sendPlayerMessage("&c&lTeleportation canceled! You have been given your money back..", true, p, plugin.getPrefix());
                    plugin.getApi().getChatFactory().sendPlayerMessage("&a&l+ " + dcm.getTPCost("SpecialTP"), true, p, plugin.getPrefix());
                }else{
                    plugin.getApi().getChatFactory().sendPlayerMessage("&c&lTeleportation canceled!", true, p, plugin.getPrefix());
                }

                Bukkit.getScheduler().cancelTask(taskID);
                removeMetadata(p);
            }
        }
    }

    public void removeMetadata(Player p){
        p.removeMetadata("TP", plugin);
        p.removeMetadata("isSpecial", plugin);
    }
}

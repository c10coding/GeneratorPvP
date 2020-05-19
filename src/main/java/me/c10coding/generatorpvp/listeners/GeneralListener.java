package me.c10coding.generatorpvp.listeners;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GeneralListener implements Listener {

    private GeneratorPvP plugin;

    public GeneralListener(GeneratorPvP plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        EquippedConfigManager ecm = new EquippedConfigManager(plugin, p.getUniqueId());
        if(ecm.isInFile()){
            ecm.addPlayerToFile(p.getUniqueId());
            ecm.saveConfig();
        }
    }

}

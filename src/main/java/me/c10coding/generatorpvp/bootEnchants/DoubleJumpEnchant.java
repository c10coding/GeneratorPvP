package me.c10coding.generatorpvp.bootEnchants;


import com.google.common.collect.Sets;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerToggleFlightEvent;

import org.bukkit.util.Vector;



public class DoubleJumpEnchant extends SuperBootEnchant implements Listener {

    public DoubleJumpEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.DOUBLE_JUMP, Particle.SLIME, plugin, SuperBootsMenu.SuperBoots.DOUBLE_JUMP);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDoubleJump(PlayerToggleFlightEvent e){
        Player p = e.getPlayer();

        if(p.getGameMode() != GameMode.CREATIVE && hasEnchant(p)){
            e.setCancelled(true);
            Block b = p.getWorld().getBlockAt(p.getLocation().subtract(0,2,0));
            if(!b.getType().equals(Material.AIR) && !timer.isActive()){
                timer.setActive(true);
                timer.decreaseXPBar(p);
                Vector v = p.getLocation().getDirection().multiply(1).setY(1);
                p.setVelocity(v);
            }
        }
    }

}

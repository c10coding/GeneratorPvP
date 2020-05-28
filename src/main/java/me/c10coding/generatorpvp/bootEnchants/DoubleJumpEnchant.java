package me.c10coding.generatorpvp.bootEnchants;


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

import org.bukkit.event.entity.EntityDamageEvent;
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
                p.setExp(0);
                p.setLevel((int) cooldown);
                timer.setActive(true);
                timer.putBarInCooldownMode(p);
                Vector v = p.getLocation().getDirection().multiply(1).setY(1);
                p.setVelocity(v);
                p.setAllowFlight(false);
            }
        }
    }

    @EventHandler
    public void onPlayerTakeFallDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(e.getCause().equals(EntityDamageEvent.DamageCause.FALL) && hasEnchant(p)){
                p.getWorld().spawnParticle(enchantParticle, p.getLocation(), 50);
                e.setCancelled(true);
            }
        }
    }

}

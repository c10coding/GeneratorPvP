package me.c10coding.generatorpvp.listeners;

import me.c10coding.generatorpvp.GeneratorPvP;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class WeaponsListener implements Listener {

    private GeneratorPvP plugin;

    public WeaponsListener(GeneratorPvP plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onSnowBallHit(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Snowball){
            Snowball s = (Snowball) e.getDamager();
            if(s.getShooter() instanceof Player){
                Player damager = (Player) s.getShooter();
                Entity enhit = e.getEntity();
                Vector vectorPlayerDamager = damager.getLocation().getDirection();
                enhit.getLocation().setDirection(vectorPlayerDamager.multiply(1.5));
            }
        }
    }

}

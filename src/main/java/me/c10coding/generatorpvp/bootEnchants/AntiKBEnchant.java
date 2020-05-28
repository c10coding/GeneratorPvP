package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class AntiKBEnchant extends SuperBootEnchant implements Listener {

    public AntiKBEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.ANTIKB, Particle.VILLAGER_ANGRY, plugin, SuperBootsMenu.SuperBoots.ANTI_KB);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerTakeDamage(EntityDamageByEntityEvent e){

        Entity entityHit = e.getEntity();

        if(entityHit instanceof Player){
            Player playerHit = (Player) e.getEntity();
            if(hasEnchant(playerHit)){
                if(!timer.isOnCooldown() && timer.isActive()){
                    e.setCancelled(true);
                    double dmg = e.getDamage();
                    double playerHealth = playerHit.getHealth();
                    if(0 < playerHealth - dmg){
                        playerHit.setHealth(playerHealth - dmg);
                        playerHit.getWorld().spawnParticle(enchantParticle, playerHit.getLocation(), 50);
                        playerHit.playEffect(EntityEffect.HURT);
                    }else{
                        playerHit.setHealth(0);
                    }
                }
            }
        }
    }

}

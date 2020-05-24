package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class AntiFallEnchant extends SuperBootEnchant implements Listener {

    public AntiFallEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.ANTI_FALL, Particle.CRIT, plugin, SuperBootsMenu.SuperBoots.ANTI_FALL);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
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

package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class AntiFallEnchant extends SuperBootEnchant{

    public AntiFallEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.ANTI_FALL, Particle.CRIT, plugin, SuperBootsMenu.SuperBoots.ANTI_FALL);
    }

    @EventHandler
    public void onPlayerTakeFallDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(e.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
                p.getWorld().spawnParticle(enchantParticle, p.getLocation(), 50);
                e.setCancelled(true);
            }
        }
    }

}

package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.event.Listener;

public class SpeedEnchant extends SuperBootEnchant implements Listener {

    public SpeedEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.SPEED, Particle.TOTEM, plugin, SuperBootsMenu.SuperBoots.SPEED);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

}

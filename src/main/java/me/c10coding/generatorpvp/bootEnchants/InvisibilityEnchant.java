package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.event.Listener;

public class InvisibilityEnchant extends SuperBootEnchant implements Listener {
    public InvisibilityEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.INVISIBILITY, Particle.SQUID_INK, plugin, SuperBootsMenu.SuperBoots.INVISIBILITY);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }
}

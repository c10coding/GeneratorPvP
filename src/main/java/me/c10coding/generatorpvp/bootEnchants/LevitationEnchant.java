package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.event.Listener;

public class LevitationEnchant extends SuperBootEnchant implements Listener {
    public LevitationEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.LEVITATION, Particle.END_ROD, plugin, SuperBootsMenu.SuperBoots.LEVITATION);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }
}

package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.event.Listener;

public class BlindnessEnchant extends SuperBootEnchant implements Listener {
    public BlindnessEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.BLINDNESS, Particle.SPELL_INSTANT, plugin, SuperBootsMenu.SuperBoots.BLINDNESS);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }
}

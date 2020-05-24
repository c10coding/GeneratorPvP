package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.event.Listener;

public class StonksEnchant extends SuperBootEnchant {
    public StonksEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.STONKS, null, plugin, SuperBootsMenu.SuperBoots.STONKS);
    }
}

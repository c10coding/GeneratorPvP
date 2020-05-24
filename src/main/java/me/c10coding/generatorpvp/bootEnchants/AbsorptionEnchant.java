package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.event.Listener;

public class AbsorptionEnchant extends SuperBootEnchant implements Listener {
    public AbsorptionEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.ABSORPTION, null, plugin, SuperBootsMenu.SuperBoots.ABSORPTION);

    }

}

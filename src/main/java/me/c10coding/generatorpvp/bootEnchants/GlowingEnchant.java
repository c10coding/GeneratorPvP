package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class GlowingEnchant extends SuperBootEnchant implements Listener{

    public GlowingEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.GLOWING, null, plugin, SuperBootsMenu.SuperBoots.GLOWING);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

}

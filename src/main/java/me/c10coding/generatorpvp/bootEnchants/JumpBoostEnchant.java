package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class JumpBoostEnchant extends SuperBootEnchant implements Listener {
    public JumpBoostEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.JUMP_BOOST, null, plugin, SuperBootsMenu.SuperBoots.JUMP_BOOST);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
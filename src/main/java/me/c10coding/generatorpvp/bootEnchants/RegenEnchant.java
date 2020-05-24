package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;

public class RegenEnchant extends SuperBootEnchant{
    public RegenEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.REGEN, null, plugin, SuperBootsMenu.SuperBoots.REGEN);
    }
}

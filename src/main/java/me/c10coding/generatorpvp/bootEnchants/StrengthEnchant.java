package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;

public class StrengthEnchant extends SuperBootEnchant{
    public StrengthEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.STRENGTH, null, plugin, SuperBootsMenu.SuperBoots.STRENGTH);
    }
}

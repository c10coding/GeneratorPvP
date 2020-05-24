package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;

public class CoinsEnchant extends SuperBootEnchant{
    public CoinsEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.COIN, null, plugin, SuperBootsMenu.SuperBoots.COIN);
    }
}

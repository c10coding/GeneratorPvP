package me.c10coding.generatorpvp.runnables;

import me.c10coding.generatorpvp.bootEnchants.EnchantmentKeys;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PotionEffectApplier extends BukkitRunnable {

    List<EnchantmentKeys> enchantsThatArePotionEffects = new ArrayList<>();

    public PotionEffectApplier(){

    }

    @Override
    public void run() {

    }

    public void compileList(){
        enchantsThatArePotionEffects.add(EnchantmentKeys.STRENGTH);
        enchantsThatArePotionEffects.add(EnchantmentKeys.REGEN);
    }
}

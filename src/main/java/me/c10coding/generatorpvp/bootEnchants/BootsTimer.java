package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.DefaultConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class BootsTimer {

    private GeneratorPvP plugin;
    private double duration, cooldown;
    private DefaultConfigManager dm;
    private boolean isActive;

    public BootsTimer(GeneratorPvP plugin, double duration, double cooldown){
        this.plugin = plugin;
        this.duration = duration;
        this.cooldown = cooldown;
        this.dm = new DefaultConfigManager(plugin);
    }

    public void incrementXPBar(Player player){
        float currentXP = player.getExp();
        float maxExp = 1.0F;
        float expToAdd = (float) (1.0F / dm.getBootsActivationTime());
        if(expToAdd + currentXP <= maxExp){
            player.setExp(currentXP + expToAdd);
        }
    }

    public void resetXPBar(Player p){
        p.setExp(0);
    }

    public void decreaseXPBar(Player p){

        float minExp = 0.0F;
        float expToSubtract = (float) (1.0F / duration);

        new BukkitRunnable() {
            @Override
            public void run() {
                float currentXP = p.getExp();
                if(minExp <= currentXP - expToSubtract){
                    p.setExp(currentXP - expToSubtract);
                }else{
                    resetXPBar(p);
                    putBarInCooldownMode(p);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void putBarInCooldownMode(Player p){

        float maxExp = 1.0F;
        float expToAdd = (float) (1.0F / cooldown);

        new BukkitRunnable() {
            @Override
            public void run() {
                float currentXP = p.getExp();
                if(maxExp >= currentXP + expToAdd){
                    p.setExp(currentXP + expToAdd);
                }else{
                    p.setExp(0);
                    setActive(false);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);

    }

    public boolean isActive(){
        return isActive;
    }

    public void setActive(boolean isActive){
        this.isActive = isActive;
    }










}

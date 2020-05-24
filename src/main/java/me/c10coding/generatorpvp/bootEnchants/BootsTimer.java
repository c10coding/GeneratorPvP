package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.DefaultConfigManager;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


public class BootsTimer {

    private GeneratorPvP plugin;
    private double duration, cooldown;
    private DefaultConfigManager dm;
    private boolean isActive;
    private boolean isOnCooldown = false;
    private SuperBootsMenu.SuperBoots boot;

    public BootsTimer(GeneratorPvP plugin, double duration, double cooldown, SuperBootsMenu.SuperBoots boot){
        this.plugin = plugin;
        this.duration = duration;
        this.cooldown = cooldown;
        this.dm = new DefaultConfigManager(plugin);
        this.boot = boot;
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
        float expToSubtract = (float) (1.0 / duration);

        new BukkitRunnable() {
            @Override
            public void run() {
                float currentXP = p.getExp();
                ItemStack boots = p.getInventory().getBoots();
                if(boots != null && boots.getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey(plugin, boot.getEnchantmentKey().toString())))){
                    if(minExp <= currentXP - expToSubtract){
                        p.setExp(currentXP - expToSubtract);
                    }else{
                        resetXPBar(p);
                        putBarInCooldownMode(p);
                        isOnCooldown = true;
                        this.cancel();
                    }
                }else{
                    resetXPBar(p);
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

                ItemStack boots = p.getInventory().getBoots();
                if(boots != null && boots.getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey(plugin, boot.getEnchantmentKey().toString())))){
                    if (maxExp >= currentXP + expToAdd) {
                        p.setExp(currentXP + expToAdd);
                    } else {
                        p.setExp(0);
                        setActive(false);
                        isOnCooldown = false;
                        this.cancel();
                    }
                }else{
                    p.setExp(0);
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

    public boolean isOnCooldown(){
        return isOnCooldown;
    }










}

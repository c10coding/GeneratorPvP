package me.c10coding.generatorpvp.bootEnchants;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.UltraPermissionsAPI;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.objects.Permission;
import me.TechsCode.UltraPermissions.storage.objects.User;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.DefaultConfigManager;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class BootsTimer {

    private GeneratorPvP plugin;
    private double duration, cooldown;
    private DefaultConfigManager dm;
    private boolean isActive;
    private boolean isOnCooldown = false;
    private SuperBootsMenu.SuperBoots boot;
    private List<Player> glowingPlayers = new ArrayList<>();

    public BootsTimer(GeneratorPvP plugin, double duration, double cooldown, SuperBootsMenu.SuperBoots boot){
        this.plugin = plugin;
        this.duration = duration;
        this.cooldown = cooldown;
        this.dm = new DefaultConfigManager(plugin);
        this.boot = boot;
    }

    public BootsTimer(GeneratorPvP plugin, double duration, double cooldown, SuperBootsMenu.SuperBoots boot, List<Player> glowingPlayers){
        this.plugin = plugin;
        this.duration = duration;
        this.cooldown = cooldown;
        this.dm = new DefaultConfigManager(plugin);
        this.boot = boot;
        this.glowingPlayers = glowingPlayers;
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
        p.setLevel(0);
    }

    public void decreaseXPBar(Player p){

        float minExp = 0.0F;
        float expToSubtract = (float) (1.0 / duration);

        new BukkitRunnable() {

            @Override
            public void run() {
                float currentXP = p.getExp();
                ItemStack boots = p.getInventory().getBoots();
                int currentLevel = p.getLevel();

                if(!p.isOnline()){
                    setActive(false);
                    if(!glowingPlayers.isEmpty()){
                        removeGlowEffect();
                    }
                    this.cancel();
                }

                if(boots != null && boots.getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey(plugin, boot.getEnchantmentKey().toString())))){
                    if(minExp <= currentXP - expToSubtract){
                        p.setExp(currentXP - expToSubtract);
                        p.setLevel(currentLevel - 1);
                    }else{
                        if(boots.getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey(plugin, SuperBootsMenu.SuperBoots.GLOWING.toString())))){
                            if(!glowingPlayers.isEmpty()){
                                removeGlowEffect();
                            }
                        }
                        p.setLevel((int) Math.round(cooldown));
                        p.setExp(0);
                        isOnCooldown = true;
                        putBarInCooldownMode(p);
                        this.cancel();
                    }
                }else{
                    if(!glowingPlayers.isEmpty()){
                        removeGlowEffect();
                    }
                    setActive(false);
                    resetXPBar(p);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void putBarInCooldownMode(Player p){

        float maxExp = 1.0F;
        float expToAdd = (float) (1.0F / cooldown);
        UltraPermissionsAPI upAPI = UltraPermissions.getAPI();
        Optional<User> optUser = upAPI.getUsers().uuid(p.getUniqueId());

        User user;
        if(optUser.isPresent()){

            user = optUser.get();

            new BukkitRunnable() {

                @Override
                public void run() {

                    float currentXP = p.getExp();
                    int playerLevel = p.getLevel();

                    if(!p.isOnline()){
                        setActive(false);
                        this.cancel();
                        if(!glowingPlayers.isEmpty()){
                            removeGlowEffect();
                        }
                    }

                    ItemStack boots = p.getInventory().getBoots();
                    Enchantment enchant = Enchantment.getByKey(new NamespacedKey(plugin, boot.getEnchantmentKey().toString()));
                    if(boots != null && boots.getItemMeta().hasEnchant(enchant)){
                        if (maxExp >= currentXP + expToAdd) {
                            p.setLevel((playerLevel - 1));
                            p.setExp(currentXP + expToAdd);
                            if(boots.getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey(plugin, SuperBootsMenu.SuperBoots.DOUBLE_JUMP.toString()))) && playerLevel == cooldown - 1){
                                PermissionList collection = user.getPermissions();
                                for(Permission perm : collection){
                                    if(perm.getName().equalsIgnoreCase("nocheatplus.checks.*")){
                                        perm.remove();
                                        break;
                                    }
                                }
                            }
                        } else {

                            p.setLevel(0);
                            p.setExp(0);
                            setActive(false);
                            isOnCooldown = false;

                            if(boots.getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey(plugin, SuperBootsMenu.SuperBoots.DOUBLE_JUMP.toString())))){
                                p.setAllowFlight(true);
                            }else if(boots.getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey(plugin, SuperBootsMenu.SuperBoots.GLOWING.toString())))){
                                p.removeMetadata("GlowingPlayers", plugin);
                            }
                            this.cancel();
                        }
                    }else{

                        if(p.hasMetadata("GlowingPlayers")){
                            p.removeMetadata("GlowingPlayers", plugin);
                        }

                        if(boots.getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey(plugin, SuperBootsMenu.SuperBoots.DOUBLE_JUMP.toString())))){
                            PermissionList collection = user.getPermissions();
                            for(Permission perm : collection){
                                if(perm.getName().equalsIgnoreCase("nocheatplus.checks.*")){
                                    perm.remove();
                                    break;
                                }
                            }
                        }

                        setActive(false);
                        p.setExp(0);
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 20L, 20L);

        }



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

    private void removeGlowEffect(){
        for(Player player : glowingPlayers){
            if(player.isGlowing()){
                player.setGlowing(false);
            }
        }
        glowingPlayers.clear();
    }










}
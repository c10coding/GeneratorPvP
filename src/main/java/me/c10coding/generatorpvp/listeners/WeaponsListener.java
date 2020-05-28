package me.c10coding.generatorpvp.listeners;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.DefaultConfigManager;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.type.Snow;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class WeaponsListener implements Listener {

    private GeneratorPvP plugin;
    private DefaultConfigManager dcm;
    private Chat chatFactory;
    private List<Player> threwASnowball = new ArrayList<>();

    public WeaponsListener(GeneratorPvP plugin){
        this.plugin = plugin;
        this.dcm = new DefaultConfigManager(plugin);
        this.chatFactory = plugin.getApi().getChatFactory();
    }

    @EventHandler
    public void onSnowBallHit(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Snowball){
            Snowball s = (Snowball) e.getDamager();
            if(s.getShooter() instanceof Player){
                Player damager = (Player) s.getShooter();
                if(s.hasMetadata("CustomSnowBall")){
                    s.removeMetadata("CustomSnowBall", plugin);
                    double multiplier = dcm.getSnowballVelocityMultiplier();
                    Entity enhit = e.getEntity();
                    if(!enhit.getName().equals(damager.getName())){
                        Vector vectorPlayerDamager = damager.getLocation().getDirection();
                        enhit.setVelocity(vectorPlayerDamager.multiply(multiplier));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onWeaponProjectileUse(PlayerInteractEvent e){
        ItemStack item = e.getItem();
        Action action = e.getAction();
        Player p = e.getPlayer();
        if(!GPUtils.isPlayerInSpawn(e.getPlayer())){
            if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)){
                if(item != null){
                    if(item.getType().equals(Material.SNOWBALL) || item.getType().equals(Material.SLIME_BALL) || item.getType().equals(Material.TNT) || item.getType().equals(Material.FIRE_CHARGE) || item.getType().equals(Material.EGG)){
                        String displayName = chatFactory.removeChatColor(item.getItemMeta().getDisplayName());
                        if(displayName.equalsIgnoreCase("Position Swap")){
                            CraftLivingEntity player = (CraftLivingEntity) e.getPlayer();
                            Snowball projectile = player.launchProjectile(Snowball.class);
                            projectile.setMetadata("CustomSlimeBall", new FixedMetadataValue(plugin, true));
                            ((CraftSnowball) projectile).getHandle().setItem(CraftItemStack.asNMSCopy(new ItemStack(Material.SLIME_BALL)));
                        }else if(displayName.equalsIgnoreCase("Knockback")){
                            e.setCancelled(true);
                            Snowball sb = p.launchProjectile(Snowball.class);
                            sb.setMetadata("CustomSnowBall", new FixedMetadataValue(plugin, true));
                        }else if(displayName.equalsIgnoreCase("TNT")){
                            e.setCancelled(true);
                            Player player = e.getPlayer();
                            TNTPrimed tnt = (TNTPrimed) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
                            double tntExplosionTime = dcm.getTNTExplosionTime() * 20;
                            tnt.setFuseTicks((int) tntExplosionTime);
                        }else if(displayName.equalsIgnoreCase("Fireball")){
                            e.setCancelled(true);
                            Fireball fb = p.launchProjectile(Fireball.class);
                            Vector v = p.getLocation().getDirection();
                            double multiplier = dcm.getFireChargeVelocityMultiplier();
                            fb.setVelocity(v.multiply(multiplier));
                            fb.setMetadata("CustomFireBall", new FixedMetadataValue(plugin, true));
                        }else if(displayName.equalsIgnoreCase("Instant Kill")){
                            e.setCancelled(true);
                            Egg egg = p.launchProjectile(Egg.class);
                            Vector v = p.getLocation().getDirection();
                            double multiplier = dcm.getEggVelocityMultiplier();
                            egg.setVelocity(v.multiply(multiplier));
                            egg.setMetadata("CustomEgg", new FixedMetadataValue(plugin, true));
                        }
                        item.setAmount(item.getAmount() - 1);
                    }
                }
            }
        }else{
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onTNTPlace(BlockPlaceEvent e){
        if(!GPUtils.isPlayerInSpawn(e.getPlayer())){
            if(e.getBlock().getType().equals(Material.TNT)){
                e.setCancelled(true);
            }
        }else{
            chatFactory.sendPlayerMessage(" ", false, e.getPlayer(), null);
            chatFactory.sendPlayerMessage("You can't do that here!", false, e.getPlayer(), null);
            chatFactory.sendPlayerMessage(" ", false, e.getPlayer(), null);
        }
    }

    @EventHandler
    public void onEggHit(PlayerEggThrowEvent e){
        e.setHatching(false);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e){
        if(e.getEntity() instanceof Fireball){
            Fireball fb = (Fireball) e.getEntity();
            if(fb.hasMetadata("CustomFireBall")){
                e.setCancelled(true);
                fb.removeMetadata("CustomFireBall", plugin);
            }
        }
    }

    @EventHandler
    public void onSlimeballHit(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Snowball){
            Snowball s = (Snowball) e.getDamager();
            if(s.getShooter() instanceof Player){
                if(e.getEntity() instanceof Player){
                    if(s.hasMetadata("CustomSlimeBall")){
                        s.removeMetadata("CustomSlimeBall", plugin);

                        Player playerHit = (Player) e.getEntity();
                        Player damager = (Player) s.getShooter();

                        if(damager.equals(playerHit)){
                            return;
                        }

                        Location playerHitLoc = playerHit.getLocation();
                        Location damagerLoc = damager.getLocation();

                        playerHit.teleport(damagerLoc);
                        damager.teleport(playerHitLoc);

                    }
                }
            }
        }
    }

    @EventHandler
    public void onEggHit(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Egg){
            Egg egg = (Egg) e.getDamager();
            if(egg.getShooter() instanceof Player){
                if(e.getEntity() instanceof Player){

                    Player damaged = (Player) e.getEntity();
                    double damageAmount = dcm.getEggDamageAmount();
                    double damagedPlayerHealth = damaged.getHealth();

                    if(damagedPlayerHealth - damageAmount >= 0){
                        damaged.setHealth(damagedPlayerHealth - damageAmount);
                    }else{
                        damaged.setHealth(0);
                    }

                    damaged.getWorld().spawnParticle(Particle.CRIT_MAGIC, damaged.getLocation(), 50);

                }
            }
        }
    }

    @EventHandler
    public void onTakeDamageFromFireball(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Fireball){
            Fireball fb = (Fireball) e.getDamager();
            if(fb.getShooter().equals(e.getEntity())){
                return;
            }
        }
    }

}

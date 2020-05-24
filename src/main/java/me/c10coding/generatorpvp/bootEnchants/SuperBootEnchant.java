package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.DefaultConfigBootsSectionManager;
import me.c10coding.generatorpvp.files.DefaultConfigManager;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


public abstract class SuperBootEnchant extends Enchantment{

	protected String name = "";
	protected EnchantmentKeys ek;
	protected ChatColor loreColor;
	protected GeneratorPvP plugin;
	protected Particle enchantParticle;
	protected DefaultConfigBootsSectionManager dsm;
	protected DefaultConfigManager dm;
	protected SuperBootsMenu.SuperBoots superBoot;
	protected double cooldown, duration, bootsActivationTime;
	protected String configKey;
	protected List<UUID> playersThatAreSneaking = new ArrayList<>();
	protected boolean isActive = false;
	protected BootsTimer timer;

	public SuperBootEnchant(EnchantmentKeys ek, Particle p, GeneratorPvP plugin, SuperBootsMenu.SuperBoots superBoot) {
		super(new NamespacedKey(plugin, ek.toString()));
		this.plugin = plugin;
		this.superBoot = superBoot;
		this.configKey = superBoot.getConfigKey();
		this.ek = ek;
		this.dsm = new DefaultConfigBootsSectionManager(plugin);
		this.dm = new DefaultConfigManager(plugin);
		this.cooldown = dsm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.COOLDOWN);
		this.duration = dsm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.DURATION);
		this.bootsActivationTime = dm.getBootsActivationTime();
		this.timer = new BootsTimer(plugin, duration, cooldown, superBoot);

		this.loreColor = GPUtils.matchArmorColorWithChatColor(superBoot.getColorOfArmor());
		this.enchantParticle = p;
		setName();
	}
	
	public void setName() {
		String name = "";
		if(ek.toString().contains("_")){
			String[] arr = ek.toString().split("_");
			for(int x = 0; x < arr.length; x++){
				if(x != (arr.length - 1) ){
					name += GPUtils.firstLowerRestUpper(arr[x])  + " ";
				}else{
					name += GPUtils.firstLowerRestUpper(arr[x]);
				}
			}
		}else{
			name = ek.toString();
		}
		this.name = name;
	}

	protected boolean hasCooldown(){
		return cooldown > 0;
	}

	protected boolean hasDuration(){
		return duration > 0;
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return true;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean isTreasure() {
		return false;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean conflictsWith(Enchantment other) {
		return false;
	}
	
	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public int getMaxLevel(){
		return 1;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ARMOR_FEET;
	}

	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent e){

		Player playerSneaking = e.getPlayer();

		if(!timer.isActive() && hasDuration() && hasEnchant(playerSneaking)){
			if(!playersThatAreSneaking.contains(playerSneaking.getUniqueId())){
				playersThatAreSneaking.add(playerSneaking.getUniqueId());
				new BukkitRunnable() {
					int seconds = 0;
					@Override
					public void run() {

						if(playerSneaking.isSneaking()){

							if(seconds < bootsActivationTime){
								timer.incrementXPBar(playerSneaking);
								seconds++;
							}

						}else{
							if(seconds == bootsActivationTime){

								timer.setActive(true);
								playerSneaking.setExp(1.0F);
								timer.decreaseXPBar(playerSneaking);

								if(superBoot.equals(SuperBootsMenu.SuperBoots.SPEED)){
									playerSneaking.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) (duration * 20), dsm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.LEVEL) - 1));
									playerSneaking.getWorld().spawnParticle(enchantParticle, playerSneaking.getLocation(), 50);
								}else if(superBoot.equals(SuperBootsMenu.SuperBoots.JUMP_BOOST)){
									playerSneaking.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, (int) (duration * 20), dsm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.LEVEL) - 1));
								}else if(superBoot.equals(SuperBootsMenu.SuperBoots.BLINDNESS)){
									double blockRadius = dsm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.BLINDNESS_BLOCK_RANGE);
									Collection<Entity> entitiesNearby = playerSneaking.getNearbyEntities(blockRadius, blockRadius, blockRadius);
									for(Entity e : entitiesNearby){
										if(e instanceof Player){
											Player nearby = (Player) e;
											nearby.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) (duration * 20), dsm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.LEVEL)));
										}
									}
								}else if(superBoot.equals(SuperBootsMenu.SuperBoots.LEVITATION)){
									playerSneaking.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, (int) (duration * 20), dsm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.LEVEL)));
									playerSneaking.getWorld().spawnParticle(enchantParticle, playerSneaking.getLocation(), 50);
								}else if(superBoot.equals(SuperBootsMenu.SuperBoots.INVISIBILITY)){
									playerSneaking.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (int) (duration * 20), dsm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.LEVEL), false,false));
									playerSneaking.getWorld().spawnParticle(enchantParticle, playerSneaking.getLocation(), 50);
								}

								playersThatAreSneaking.remove(playerSneaking.getUniqueId());

							}else{
								playersThatAreSneaking.remove(playerSneaking.getUniqueId());
								timer.resetXPBar(playerSneaking);
							}
							this.cancel();
						}
					}
				}.runTaskTimer(plugin, 0L, 20L);
			}
		}


	}

	protected boolean hasEnchant(Player p){
		if(p.getInventory().getBoots() != null){
			return p.getInventory().getBoots().getItemMeta().hasEnchant(this);
		}else{
			return false;
		}
	}

	public BootsTimer getTimer(){
		return timer;
	}

}

package me.c10coding.generatorpvp.bootEnchants;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.DefaultConfigBootsSectionManager;
import me.c10coding.generatorpvp.files.DefaultConfigManager;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public abstract class SuperBootEnchant extends Enchantment implements Listener {

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
		this.timer = new BootsTimer(plugin, duration, cooldown);

		this.loreColor = GPUtils.matchArmorColorWithChatColor(superBoot.getColorOfArmor());
		this.enchantParticle = p;
		setName();
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
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

		if(!timer.isActive()){
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

}

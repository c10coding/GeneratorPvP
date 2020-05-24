package me.c10coding.generatorpvp.listeners;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.bootEnchants.EnchantmentKeys;
import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import me.c10coding.generatorpvp.files.DefaultConfigBootsSectionManager;
import me.c10coding.generatorpvp.files.DefaultConfigManager;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
import me.c10coding.generatorpvp.menus.ChatMenu;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class GeneralListener implements Listener {

    private GeneratorPvP plugin;
    private DefaultConfigManager dcm;
    private DefaultConfigBootsSectionManager bm;
    private Chat chatFactory;

    public GeneralListener(GeneratorPvP plugin){
        this.plugin = plugin;
        this.dcm = new DefaultConfigManager(plugin);
        this.bm = new DefaultConfigBootsSectionManager(plugin);
        this.chatFactory = plugin.getApi().getChatFactory();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        EquippedConfigManager ecm = new EquippedConfigManager(plugin, p.getUniqueId());
        if(ecm.isInFile()){
            ecm.addPlayerToFile();
            ecm.saveConfig();
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){
        Player messenger = e.getPlayer();
        EquippedConfigManager ecm = new EquippedConfigManager(plugin, messenger.getUniqueId());
        Chat chatFactory = plugin.getApi().getChatFactory();

        if(ecm.hasSomethingEquipped("Chat")){
            String configKey = ecm.getThingEquipped("Chat");
            ChatMenu.ChatColors chatColor = getEquippedChatColor(configKey);
            String colorCode = chatColor.getColorCode();
            String msg = e.getMessage();
            e.setMessage(chatFactory.chat(colorCode + msg));
        }

    }

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent e){

        if(e.getEntity() instanceof Player){
            Player deadPlayer = (Player) e.getEntity();
            Economy econ = GeneratorPvP.getEconomy();
            int coinsLostPerDeath = dcm.getCoinsLostPerDeath();
            int coinsGainedPerKill = dcm.getCoinsGainedPerKill();
            if(deadPlayer.getInventory().getBoots() != null){
                ItemStack deadPlayerBoots = deadPlayer.getInventory().getBoots();

                if(!deadPlayerBoots.getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey(plugin, EnchantmentKeys.STONKS.toString())))){
                    takeCoins(deadPlayer, econ, coinsLostPerDeath);
                }

                if(deadPlayer.getKiller() instanceof Player){
                    Player killer = deadPlayer.getKiller();
                    int totalCoinsReward = coinsGainedPerKill;
                    if(killer.getInventory().getBoots() != null){
                        ItemStack killersBoots = killer.getInventory().getBoots();
                        if(killersBoots.getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey(plugin, EnchantmentKeys.COIN.toString())))){
                            final int MIN_COINS = 1;
                            final int MAX_COINS = 20;
                            int randomCoinAmount = (int) (Math.random() * ((MAX_COINS - MIN_COINS) + 1)) + MIN_COINS;
                            totalCoinsReward = randomCoinAmount;

                            AmplifiersConfigManager acm = new AmplifiersConfigManager(plugin);
                            if(acm.isAmplifierActivated("Coin Multiplier")){
                                double multiplier = acm.getCoinsMultiplier();
                                totalCoinsReward = (int) (multiplier * totalCoinsReward);
                            }

                        }
                    }
                    econ.depositPlayer(killer, totalCoinsReward);
                    chatFactory.sendPlayerMessage("&a+ " + coinsGainedPerKill + " &ecoins", true, deadPlayer, plugin.getPrefix());
                }

            }else{
                takeCoins(deadPlayer, econ, coinsLostPerDeath);
            }
        }

    }

    private ChatMenu.ChatColors getEquippedChatColor(String configKey){
        ChatMenu.ChatColors chatColor = ChatMenu.ChatColors.GRAY;
        for(ChatMenu.ChatColors color : ChatMenu.ChatColors.values()){
            if(configKey.equalsIgnoreCase(color.getConfigKey())){
                return color;
            }
        }
        return chatColor;
    }

    private void takeCoins(Player brokeBoi, Economy econ, int coinsLostPerDeath){
        econ.withdrawPlayer(brokeBoi, coinsLostPerDeath);
        chatFactory.sendPlayerMessage("&c- " + coinsLostPerDeath + " &ecoins", true, brokeBoi, plugin.getPrefix());
    }

}

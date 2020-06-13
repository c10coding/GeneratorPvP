package me.c10coding.generatorpvp.listeners;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.bootEnchants.EnchantmentKeys;
import me.c10coding.generatorpvp.files.*;
import me.c10coding.generatorpvp.managers.ScoreboardManager;
import me.c10coding.generatorpvp.menus.ChatMenu;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class GeneralListener implements Listener {

    private GeneratorPvP plugin;
    private DefaultConfigManager dcm;
    private DefaultConfigBootsSectionManager bm;
    private Chat chatFactory;
    private ScoreboardManager sm;

    public GeneralListener(GeneratorPvP plugin){
        this.plugin = plugin;
        this.dcm = new DefaultConfigManager(plugin);
        this.bm = new DefaultConfigBootsSectionManager(plugin);
        this.chatFactory = plugin.getApi().getChatFactory();
        this.sm = new ScoreboardManager(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        EquippedConfigManager ecm = new EquippedConfigManager(plugin, p.getUniqueId());
        ScoreboardManager sm = new ScoreboardManager(plugin);
        StatsConfigManager scm = new StatsConfigManager(plugin);
        ItemSaverConfigManager iscm = new ItemSaverConfigManager(plugin);

        if(iscm.hasItemsSaved(p.getUniqueId())){
            iscm.giveItems(p);
        }

        if(!ecm.isInFile()){
            ecm.addPlayerToFile();
            ecm.saveConfig();
        }

        if(!scm.isInFile(p.getUniqueId())){
            scm.addPlayerToFile(p.getUniqueId());
            scm.saveConfig();
        }

        if(p.getInventory().getBoots() != null){
            p.getInventory().setBoots(null);
        }

        sm.setSB(p);
        p.setExp(0);
        p.setLevel(0);
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
    public void onPlayerLeave(PlayerQuitEvent e){
        if(e.getPlayer().isGlowing()){
            e.getPlayer().setGlowing(false);
        }
    }

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent e){

        if(e.getEntity() instanceof Player){
            Player deadPlayer = (Player) e.getEntity();
            Economy econ = GeneratorPvP.getEconomy();
            StatsConfigManager scm = new StatsConfigManager(plugin);
            int coinsLostPerDeath = dcm.getCoinsLostPerDeath();
            int coinsGainedPerKill = dcm.getCoinsGainedPerKill();

            ItemStack deadPlayerBoots = deadPlayer.getInventory().getBoots();
            Player killer = deadPlayer.getKiller();
            scm.increaseDeaths(deadPlayer.getUniqueId());
            scm.saveConfig();

            if(killer != null){
                scm.increaseKills(killer.getUniqueId());
                scm.saveConfig();
                if(deadPlayer.getInventory().getBoots() != null){
                    if(deadPlayerBoots.getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey(plugin, EnchantmentKeys.STONKS.toString())))){
                        chatFactory.sendPlayerMessage(" ", false, deadPlayer, null);
                        chatFactory.sendPlayerMessage("&e" + killer.getName() + "&c Killed &7you. No &6Coins &7lost", false, deadPlayer, plugin.getPrefix());
                        chatFactory.sendPlayerMessage(" ", false, deadPlayer, null);
                    }else{
                        takeCoins(deadPlayer, econ, coinsLostPerDeath, killer);
                    }
                }else{
                    takeCoins(deadPlayer, econ, coinsLostPerDeath, killer);
                }
            }else{
                if(deadPlayer.getInventory().getBoots() != null){
                    if(deadPlayerBoots.getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey(plugin, EnchantmentKeys.STONKS.toString())))){
                        chatFactory.sendPlayerMessage(" ", false, deadPlayer, null);
                        chatFactory.sendPlayerMessage("No &6Coins &7lost", false, deadPlayer, plugin.getPrefix());
                        chatFactory.sendPlayerMessage(" ", false, deadPlayer, null);
                    }else{
                        takeCoins(deadPlayer, econ, coinsLostPerDeath);
                    }
                }else{
                    takeCoins(deadPlayer, econ, coinsLostPerDeath);
                }
            }

            int totalCoinsReward = coinsGainedPerKill;
            if(killer != null){
                if(killer.getInventory().getBoots() != null){
                    ItemStack killersBoots = killer.getInventory().getBoots();
                    if(killersBoots.getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey(plugin, EnchantmentKeys.COIN.toString())))) {
                        final int MIN_COINS = 1;
                        final int MAX_COINS = 20;
                        int randomCoinAmount = (int) (Math.random() * ((MAX_COINS - MIN_COINS) + 1)) + MIN_COINS;
                        totalCoinsReward = totalCoinsReward + randomCoinAmount;

                        chatFactory.sendPlayerMessage(" ", false, killer, null);
                        chatFactory.sendPlayerMessage("&7You received &a" + randomCoinAmount + " &6Coins " + "&7from &eCoin Boots&7", false, killer, null);
                        chatFactory.sendPlayerMessage(" ", false, killer, null);
                    }
                    AmplifiersConfigManager acm = new AmplifiersConfigManager(plugin);
                    if(acm.isAmplifierActivated("Coin Multiplier")){
                        int coinsAdditive = (int) (coinsGainedPerKill * acm.getCoinsMultiplier()) - 1;
                        totalCoinsReward = totalCoinsReward + coinsAdditive;
                        chatFactory.sendPlayerMessage(" ", false, killer, null);
                        chatFactory.sendPlayerMessage("&a+" + coinsAdditive + " &6Coins &7from the &eAmplifier&7", false, killer, null);
                        chatFactory.sendPlayerMessage(" ", false, killer, null);
                    }
                }

                chatFactory.sendPlayerMessage(" ", false, killer, null);
                chatFactory.sendPlayerMessage("&7You &cKilled &e" + deadPlayer.getName() + "&7. &a+" + coinsGainedPerKill + " &6Coin", false, killer, null);
                chatFactory.sendPlayerMessage(" ", false, killer, null);
                econ.depositPlayer(killer, totalCoinsReward);
                killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 10);
            }
        }

        e.setDroppedExp(0);
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

    private void takeCoins(Player brokeBoi, Economy econ, int coinsLostPerDeath, Player killer){

        if(econ.getBalance(brokeBoi) > 0){
            econ.withdrawPlayer(brokeBoi, coinsLostPerDeath);
            chatFactory.sendPlayerMessage(" ", false, brokeBoi, null);
            chatFactory.sendPlayerMessage("&e" + killer.getName() + "&c Killed &7you. &4-" + coinsLostPerDeath + " &6Coin", false, brokeBoi, plugin.getPrefix());
        }else{
            chatFactory.sendPlayerMessage(" ", false, brokeBoi, null);
            chatFactory.sendPlayerMessage("&e" + killer.getName() + "&c Killed &7you", false, brokeBoi, plugin.getPrefix());
            chatFactory.sendPlayerMessage(" ", false, brokeBoi, null);
            chatFactory.sendPlayerMessage(" ", false, brokeBoi, null);
            chatFactory.sendPlayerMessage("&fGood news! You did not lose any &6Coins &fsince you have none", false, brokeBoi, null);
        }
        chatFactory.sendPlayerMessage(" ", false, brokeBoi, null);

    }

    private void takeCoins(Player brokeBoi, Economy econ, int coinsLostPerDeath){
        if(econ.getBalance(brokeBoi) > 0){
            econ.withdrawPlayer(brokeBoi, coinsLostPerDeath);
            chatFactory.sendPlayerMessage(" ", false, brokeBoi, null);
            chatFactory.sendPlayerMessage("&fYou Died! &4-" + coinsLostPerDeath + "&6 Coin&f.", false, brokeBoi, plugin.getPrefix());
        }else{
            chatFactory.sendPlayerMessage(" ", false, brokeBoi, null);
            chatFactory.sendPlayerMessage("&fGood news! You did not lose any &6Coins &fsince you have none", false, brokeBoi, null);
        }
        chatFactory.sendPlayerMessage(" ", false, brokeBoi, null);
    }

}

package me.c10coding.generatorpvp.menus;

import javafx.concurrent.Task;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.listeners.TeleportListener;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WarpsMenu extends MenuCreator implements Listener {

    public WarpsMenu(JavaPlugin plugin, Player p) {
        super(plugin, "Warps", 27, p);
        createMenu("WarpMenu");
        fillMenu();
        setHasGivables(false);
    }

    @Override
    public void initializeItems(Player player) { }

    @Override
    public void fillMenu(){
        Material blackPane = Material.BLACK_STAINED_GLASS_PANE;
        Material redPane = Material.RED_STAINED_GLASS_PANE;
        Material orangePane = Material.ORANGE_STAINED_GLASS_PANE;
        Material purplePane = Material.PURPLE_STAINED_GLASS_PANE;
        Material pinkPane = Material.PINK_STAINED_GLASS_PANE;
        Material cyanPane = Material.CYAN_STAINED_GLASS_PANE;
        Material[] pattern = {blackPane, redPane, cyanPane, redPane , orangePane,redPane, purplePane, redPane, pinkPane};
        int patternCounter = 0;
        for(int x = 0; x < 27; x++){

            if(patternCounter == 9){
                patternCounter = 0;
            }

            if(inv.getItem(x) == null){
                setFillerMaterial(pattern[patternCounter]);
                inv.setItem(x, createGuiItem());
            }
            patternCounter++;
        }
    }

    @EventHandler
    @Override
    protected void onInventoryClick(InventoryClickEvent e) {

        if(e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if(clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        Player playerClicked = (Player) e.getWhoClicked();
        int slotClicked = e.getSlot();
        String prefix = ((GeneratorPvP)plugin).getPrefix();

        switch(slotClicked){
            case 9:
                enterTPProcess(new Location(playerClicked.getWorld(), -563, 60, -645), false);
                p.closeInventory();
                break;
            case 11:
                if(playerClicked.hasPermission("gp.teleport.vip")){
                    enterTPProcess(new Location(playerClicked.getWorld(), -563, 85, -645), false);
                }else{
                    chatFactory.sendPlayerMessage("You don't have permission to do that!", true, playerClicked, prefix);
                }
                p.closeInventory();
                break;
            case 13:
                if(playerClicked.hasPermission("gp.teleport.mvp")){
                    enterTPProcess(new Location(playerClicked.getWorld(), -563, 107, -645), false);
                }else{
                    chatFactory.sendPlayerMessage("You don't have permission to do that!", true, playerClicked, prefix);
                }
                p.closeInventory();
                break;
            case 15:
                if(playerClicked.hasPermission("gp.teleport.ultra")){
                    enterTPProcess(new Location(playerClicked.getWorld(), -563, 140, -645), false);
                }else{
                    chatFactory.sendPlayerMessage("You don't have permission to do that!", true, playerClicked, prefix);
                }
                p.closeInventory();
                break;
            case 17:
                double coins = GeneratorPvP.getEconomy().getBalance(playerClicked);
                double cost = cm.getTPCost("SpecialTP");
                if(playerClicked.hasPermission("gp.teleport.special")){
                    if(coins >= cost){
                        chatFactory.sendPlayerMessage("&4&l- " + cost, true, p, prefix);
                        GeneratorPvP.getEconomy().withdrawPlayer(playerClicked, cm.getTPCost("SpecialTP"));
                        enterTPProcess(new Location(playerClicked.getWorld(), -563, 160, -645), true);
                    }else{
                        chatFactory.sendPlayerMessage("You don't have enough coins!", true, playerClicked, prefix);
                    }
                    p.closeInventory();
                    break;
                }else{
                   chatFactory.sendPlayerMessage("You don't have permission to do that!", true, playerClicked, prefix);
                    p.closeInventory();
                }
                break;
        }
    }

    private void enterTPProcess(Location loc, boolean isSpecialTP){

        BukkitRunnable br = new BukkitRunnable() {
            double counter = cm.getTPDelay();
            @Override
            public void run() {
                if(counter == 0){
                    p.teleport(loc);
                    chatFactory.sendPlayerMessage("Teleporting...", true, p, prefix);
                    removeMetadata();
                    this.cancel();
                }else{
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(chatFactory.chat("&d&l" + (int)counter) + "...").create());
                }
                counter--;
            }
        };

        BukkitTask t = br.runTaskTimer(plugin, 0L, 20L);
        final int id = t.getTaskId();

        setMetadata(id, isSpecialTP);
    }

    private void setMetadata(int taskID, boolean isSpecialTP){
        p.setMetadata("TP", new FixedMetadataValue(plugin, taskID));
        p.setMetadata("isSpecial", new FixedMetadataValue(plugin, isSpecialTP));
    }

    private void removeMetadata(){
        p.removeMetadata("TP", plugin);
        p.removeMetadata("isSpecial", plugin);
    }

}

package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.GeneratorPvP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpsMenu extends MenuCreator implements Listener {

    public WarpsMenu(JavaPlugin plugin, String menuTitle, int numSlots) {
        super(plugin, menuTitle, numSlots);
        createMenu("WarpMenu");
        fillMenu();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
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
                playerClicked.teleport(new Location(playerClicked.getWorld(), -563, 60, -645));
                break;
            case 11:
                if(playerClicked.hasPermission("gp.teleport.vip")){
                    playerClicked.teleport(new Location(playerClicked.getWorld(), -563, 85, -645));
                }else{
                    chatFactory.sendPlayerMessage("You don't have permission to do that!", true, playerClicked, prefix);
                }
                break;
            case 13:
                if(playerClicked.hasPermission("gp.teleport.mvp")){
                    playerClicked.teleport(new Location(playerClicked.getWorld(), -563, 107, -645));
                }else{
                    chatFactory.sendPlayerMessage("You don't have permission to do that!", true, playerClicked, prefix);
                }
                break;
            case 15:
                if(playerClicked.hasPermission("gp.teleport.ultra")){
                    playerClicked.teleport(new Location(playerClicked.getWorld(), -563, 140, -645));
                }else{
                    chatFactory.sendPlayerMessage("You don't have permission to do that!", true, playerClicked, prefix);
                }
                break;
            case 17:
                OfflinePlayer op = playerClicked;
                double coins = GeneratorPvP.getEconomy().getBalance(op);
                if(playerClicked.hasPermission("gp.teleport.special")){
                    if(coins >= cm.getTPCost("SpecialTP")){
                        GeneratorPvP.getEconomy().withdrawPlayer(op, cm.getTPCost("SpecialTP"));
                        playerClicked.teleport(new Location(playerClicked.getWorld(), -563, 160, -645));
                    }else{
                        chatFactory.sendPlayerMessage("You don't have enough coins!", true, playerClicked, prefix);
                    }
                    break;
                }else{
                   chatFactory.sendPlayerMessage("You don't have permission to do that!", true, playerClicked, prefix);
                }
                break;
        }
    }
}

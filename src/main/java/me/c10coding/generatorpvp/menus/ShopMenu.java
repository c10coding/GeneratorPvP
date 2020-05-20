package me.c10coding.generatorpvp.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopMenu extends MenuCreator {
    public ShopMenu(JavaPlugin plugin, Player p) {
        super(plugin, "Shop", 27, p);
        createMenu("ShopMenu");
        fillMenu();
        setHasGivables(false);
    }

    @Override
    public void initializeItems(Player player) {
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
        MenuCreator newMenu;

        switch(slotClicked){

            case 12:
                newMenu = new TeleportationsMenu(plugin, p);
                break;
            case 13:
                newMenu = new OresMenu(plugin, p);
                break;
            case 14:
                newMenu = new WeaponsMenu(plugin, p);
                break;
            default:
                return;
        }

        playerClicked.closeInventory();
        newMenu.openInventory(playerClicked);

    }
}

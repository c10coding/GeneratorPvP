package me.c10coding.generatorpvp.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class OresMenu extends MenuCreator implements Listener {

    public OresMenu(JavaPlugin plugin, Player p) {
        super(plugin, "Ores", 27, p);
        createMenu();
        setHasGivables(true);
    }

    public void createMenu(){
        inv.setItem(9, createGuiItem(Material.EMERALD_BLOCK, chatFactory.colorString("&aEmerald Block"), new ArrayList<>()));
        inv.setItem(10, createGuiItem(Material.EMERALD, chatFactory.colorString("&aEmerald"), new ArrayList<>()));
        inv.setItem(11, createGuiItem(Material.DIAMOND_BLOCK, chatFactory.colorString("&bDiamond Block"), new ArrayList<>()));
        inv.setItem(12, createGuiItem(Material.DIAMOND, chatFactory.colorString("&bDiamond"), new ArrayList<>()));
        inv.setItem(13, createGuiItem(Material.GOLD_BLOCK, chatFactory.colorString("&eGold Block"), new ArrayList<>()));
        inv.setItem(14, createGuiItem(Material.GOLD_INGOT, chatFactory.colorString("&eGold"), new ArrayList<>()));
        inv.setItem(15, createGuiItem(Material.IRON_BLOCK, chatFactory.colorString("&fIron Block"), new ArrayList<>()));
        inv.setItem(16, createGuiItem(Material.IRON_INGOT, chatFactory.colorString("&fIron"), new ArrayList<>()));
        inv.setItem(17, createGuiItem(Material.COAL_BLOCK, chatFactory.colorString("&8Coal Block"), new ArrayList<>()));
        fillMenu();
    }

    @EventHandler
    @Override
    protected void onInventoryClick(InventoryClickEvent e) {

        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().equals(Material.AIR) || clickedItem.getType().equals(fillerMat)) return;

        Player p = (Player) e.getWhoClicked();
        OrePurchaseMenu opm = new OrePurchaseMenu(plugin, clickedItem.getType(), p);
        p.closeInventory();
        opm.openInventory(p);

    }



}

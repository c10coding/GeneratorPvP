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
        inv.setItem(9, createGuiItem(Material.EMERALD_BLOCK, chatFactory.chat("&aEmerald Block"), new ArrayList<>()));
        inv.setItem(10, createGuiItem(Material.EMERALD, chatFactory.chat("&aEmerald"), new ArrayList<>()));
        inv.setItem(11, createGuiItem(Material.DIAMOND_BLOCK, chatFactory.chat("&bDiamond Block"), new ArrayList<>()));
        inv.setItem(12, createGuiItem(Material.DIAMOND, chatFactory.chat("&bDiamond"), new ArrayList<>()));
        inv.setItem(13, createGuiItem(Material.GOLD_INGOT, chatFactory.chat("&eGold"), new ArrayList<>()));
        inv.setItem(14, createGuiItem(Material.GOLD_BLOCK, chatFactory.chat("&eGold Block"), new ArrayList<>()));
        inv.setItem(15, createGuiItem(Material.IRON_BLOCK, chatFactory.chat("&fIron Block"), new ArrayList<>()));
        inv.setItem(16, createGuiItem(Material.IRON_INGOT, chatFactory.chat("&fIron"), new ArrayList<>()));
        inv.setItem(17, createGuiItem(Material.COAL_BLOCK, chatFactory.chat("&8Coal Block"), new ArrayList<>()));
    }

    @EventHandler
    @Override
    protected void onInventoryClick(InventoryClickEvent e) {

        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        Player p = (Player) e.getWhoClicked();
        OrePurchaseMenu opm = new OrePurchaseMenu(plugin, clickedItem.getType(), p);
        p.closeInventory();
        opm.openInventory(p);

    }



}

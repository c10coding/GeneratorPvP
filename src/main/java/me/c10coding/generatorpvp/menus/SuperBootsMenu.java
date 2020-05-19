package me.c10coding.generatorpvp.menus;

import me.c10coding.coreapi.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SuperBootsMenu extends Menu {

    public SuperBootsMenu(JavaPlugin plugin, String menuTitle, int numSlots) {
        super(plugin, menuTitle, numSlots);
    }

    @Override
    public void initializeItems(Player player) {

    }

    @Override
    protected void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

}

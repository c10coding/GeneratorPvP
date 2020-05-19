package me.c10coding.generatorpvp.menus;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.coreapi.menus.Menu;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.DefaultConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class MenuCreator extends Menu implements Listener {

    protected me.c10coding.generatorpvp.files.DefaultConfigManager cm;
    protected Chat chatFactory;

    public MenuCreator(JavaPlugin plugin) {
        super(plugin, "Menu", 27);
        fillerMat = Material.RED_STAINED_GLASS_PANE;
        this.cm = new DefaultConfigManager(plugin);
        this.chatFactory = ((GeneratorPvP) plugin).getApi().getChatFactory();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public MenuCreator(JavaPlugin plugin, String menuTitle, int numSlots) {
        super(plugin, menuTitle, numSlots);
        fillerMat = Material.RED_STAINED_GLASS_PANE;
        this.cm = new DefaultConfigManager(plugin);
        this.chatFactory = ((GeneratorPvP) plugin).getApi().getChatFactory();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void initializeItems(Player player) { }

    public void createMenu(String menuType) {

        List<Integer> menuSlots = cm.getSlots(menuType);

        for(Integer i : menuSlots){

            Map<String, Object> slotInfo = cm.getSlotInfo(menuType, i);
            String displayName = (String) slotInfo.get("DisplayName");
            Material mat = (Material) slotInfo.get("Material");
            List<String> lore = (List<String>) slotInfo.get("Lore");

            inv.setItem(i, createGuiItem(mat, displayName, 1, lore));
        }

    }

    public void fillMenu(){
        for(int x = 0; x < inv.getSize(); x++){
            if(inv.getItem(x) == null){
                inv.setItem(x, createGuiItem());
            }
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
        Menu newMenu;

        switch(slotClicked){
            case 10:
                newMenu = new ShopMenu(plugin, "Shop", 27);
                break;
            case 11:
                newMenu = new WarpsMenu(plugin, "Warps", 27);
                break;
            case 12:
                newMenu = new ChatMenu(plugin, "Statistics", 27);
                break;
            case 13:
                newMenu = new ChatMenu(plugin, "Amplifiers", 27);
                break;
            case 14:
                newMenu = new ChatMenu(plugin, "SuperBoots", 27);
                break;
            case 15:
                newMenu = new ChatMenu(plugin, "EnderChest", 27);
                break;
            case 16:
                newMenu = new ChatMenu(plugin, "Chat", 27);
                break;
            default:
                return;
        }
        playerClicked.closeInventory();
        newMenu.openInventory(playerClicked);
    }
}

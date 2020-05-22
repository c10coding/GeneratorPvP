package me.c10coding.generatorpvp.menus;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.coreapi.menus.Menu;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.DefaultConfigManager;
import net.milkbowl.vault.economy.Economy;
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
    protected Economy econ = GeneratorPvP.getEconomy();
    protected String prefix;
    protected Player p;
    private boolean hasGivables;

    public MenuCreator(JavaPlugin plugin) {
        super(plugin, "Menu", 27);
        fillerMat = Material.RED_STAINED_GLASS_PANE;
        this.cm = new DefaultConfigManager(plugin);
        this.chatFactory = ((GeneratorPvP) plugin).getApi().getChatFactory();
        this.prefix = ((GeneratorPvP) plugin).getPrefix();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setHasGivables(false);
    }

    public MenuCreator(JavaPlugin plugin, String menuTitle, int numSlots, Player p) {
        super(plugin, menuTitle, numSlots);
        fillerMat = Material.RED_STAINED_GLASS_PANE;
        this.cm = new DefaultConfigManager(plugin);
        this.chatFactory = ((GeneratorPvP) plugin).getApi().getChatFactory();
        this.prefix = ((GeneratorPvP) plugin).getPrefix();
        this.p = p;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setHasGivables(false);
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
            lore = applyPlaceholders(lore);

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

        int slotClicked = e.getSlot();
        Menu newMenu = null;

        switch(slotClicked){
            case 10:
                newMenu = new ShopMenu(plugin, p);
                break;
            case 11:
                newMenu = new WarpsMenu(plugin, p);
                break;
            case 12:
                newMenu = new ChatMenu(plugin, p);
                break;
            case 14:
                newMenu = new AmplifiersMenu(plugin, p);
                break;
            case 15:
                //newMenu = new ChatMenu(plugin, "EnderChest", 27);
                break;
            case 16:
                //newMenu = new ChatMenu(plugin, "Chat", 27);
                break;
            default:
                return;
        }
        p.closeInventory();
        newMenu.openInventory(p);
    }

    public List<String> applyPlaceholders(List<String> lore){
        for(String s : lore){
            if(s.contains("%coins%")){
                int index = lore.indexOf(s);
                lore.set(index, s.replace("%coins%", String.valueOf((int)econ.getBalance(p))));
            }
        }
        return lore;
    }

    public void setHasGivables(boolean b) {
        this.hasGivables = b;
    }

    public boolean hasGivables() {
        return hasGivables;
    }
}

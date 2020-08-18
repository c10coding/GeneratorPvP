package me.c10coding.generatorpvp.menus;

import me.c10coding.coreapi.APIHook;
import me.c10coding.coreapi.BetterJavaPlugin;
import me.c10coding.coreapi.chat.ChatFactory;
import me.c10coding.coreapi.menus.Menu;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.DefaultConfigManager;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
import me.c10coding.generatorpvp.managers.ScoreboardManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;

public class MenuCreator extends Menu implements Listener {

    protected me.c10coding.generatorpvp.files.DefaultConfigManager cm;
    protected EquippedConfigManager ecm;
    protected ChatFactory chatFactory;
    protected Economy econ = GeneratorPvP.getEconomy();
    protected String prefix;
    protected Player p;
    protected boolean hasGivables;

    public MenuCreator(APIHook plugin) {
        super(plugin, "Menu", 27);
        fillerMat = Material.RED_STAINED_GLASS_PANE;
        this.cm = new DefaultConfigManager(plugin);
        this.chatFactory = plugin.getAPI().getChatFactory();
        this.prefix = ((GeneratorPvP) plugin).getPrefix();
        this.ecm = new EquippedConfigManager(plugin, p.getUniqueId());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        if(!ecm.isInFile()){
            ecm.addPlayerToFile();
            ecm.saveConfig();
        }
    }

    public MenuCreator(APIHook plugin, String menuTitle, int numSlots, Player p) {
        super(plugin, menuTitle, numSlots);
        fillerMat = Material.RED_STAINED_GLASS_PANE;
        this.cm = new DefaultConfigManager(plugin);
        this.chatFactory = plugin.getAPI().getChatFactory();
        this.prefix = ((GeneratorPvP) plugin).getPrefix();
        this.p = p;
        this.ecm = new EquippedConfigManager(plugin, p.getUniqueId());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        if(!ecm.isInFile()){
            ecm.addPlayerToFile();
            ecm.saveConfig();
        }
    }

    @Override
    public void initializeItems(Player player) { }

    public void createMenu() {

        List<Integer> menuSlots = cm.getSlots("MainMenu");

        for(Integer i : menuSlots){

            Map<String, Object> slotInfo = cm.getSlotInfo("MainMenu", i);
            String displayName = (String) slotInfo.get("DisplayName");
            Material mat = (Material) slotInfo.get("Material");
            List<String> lore = (List<String>) slotInfo.get("Lore");
            lore = applyPlaceholders(lore);

            if(i == 16){
                if(!ecm.isPurchased("EnderChest", "EnderChest")){
                    int cost = cm.getEnderChestCost();
                    lore.add(chatFactory.colorString("&aCost: &6" + cost + " Coins"));
                }
            }

            if(i == 13){
                ItemStack playerHead = getHead(p);
                ItemMeta meta = playerHead.getItemMeta();
                meta.setLore(lore);
                playerHead.setItemMeta(meta);
                inv.setItem(13, playerHead);
            }else{
                inv.setItem(i, createGuiItem(mat, displayName, 1, lore));
            }
        }

    }

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

        Menu newMenu;
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
                newMenu = new AmplifiersMenu(plugin, p, "Amplifiers");
                break;
            case 15:
                newMenu = new SuperBootsMenu(plugin, p);
                break;
            case 16:
                if(ecm.isPurchased("EnderChest", "EnderChest")){
                    Inventory enderChest = p.getEnderChest();
                    p.closeInventory();
                    p.openInventory(enderChest);
                    return;
                }else{
                    newMenu = new ConfirmPurchaseMenu(plugin, p, Material.ENDER_CHEST, cm.getEnderChestCost(), "EnderChest", this, 1, "Ender Chest");
                }
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

    private ItemStack getHead(Player player) {

        ItemStack item = new ItemStack(Material.LEGACY_SKULL_ITEM, 1 , (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName(chatFactory.colorString("&7Statistics"));
        item.setItemMeta(meta);

        return item;

    }
}

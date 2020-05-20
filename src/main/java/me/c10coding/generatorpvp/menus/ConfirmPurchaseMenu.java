package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.files.DefaultConfigManager;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ConfirmPurchaseMenu extends MenuCreator {

    private Material matPurchasing;
    private String configKey;
    private int cost;
    private MenuCreator prevMenu;
    private int amount;

    public ConfirmPurchaseMenu(JavaPlugin plugin, Player p, Material mat, double cost, String configKey, MenuCreator prevMenu, int amount) {
        super(plugin, "Purchasing: &b&l" + GPUtils.matToName(mat), 27, p);
        this.matPurchasing = mat;
        this.configKey = configKey;
        this.cost = (int) cost;
        this.prevMenu = prevMenu;
        this.amount = amount;
        createMenu();
    }

    public void createMenu(){
        int slotItem = 13;
        int slotCost = 22;
        DefaultConfigManager dcm = new DefaultConfigManager(plugin);
        List<String> lore = new ArrayList<>();
        lore.add(chatFactory.chat("Cost: &a&l" + cost));
        inv.setItem(13, createGuiItem(matPurchasing, chatFactory.chat(GPUtils.matToName(matPurchasing)), amount, lore));
        inv.setItem(22, createGuiItem(Material.SUNFLOWER, chatFactory.chat("Balance: &a&l" + econ.getBalance(p)), new ArrayList<>()));
        fillMenu();
    }

    @Override
    public void fillMenu(){

        Material greenPane = Material.GREEN_STAINED_GLASS_PANE;
        Material whitePane = Material.WHITE_STAINED_GLASS_PANE;
        Material redPane = Material.RED_STAINED_GLASS_PANE;

        Material[] pattern = {greenPane, greenPane, greenPane, whitePane, whitePane, whitePane, redPane, redPane, redPane};
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

        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        if(prevMenu instanceof OrePurchaseMenu){
            prevMenu = new OresMenu(plugin, p);
        }else if (prevMenu instanceof TeleportationsMenu){
            prevMenu = new TeleportationsMenu(plugin, p);
        }else if(prevMenu instanceof WeaponsMenu){
            prevMenu = new WeaponsMenu(plugin, p);
        }else if(prevMenu instanceof WarpsMenu){
            prevMenu = new WarpsMenu(plugin, p);
        }else if(prevMenu instanceof ChatMenu){
            prevMenu = new ChatMenu(plugin, p);
        }

        if(clickedItem.getType().equals(Material.GREEN_STAINED_GLASS_PANE)){
            if(prevMenu.hasGivables()){
                giveItems();
            }
            econ.withdrawPlayer(p, cost);
            p.closeInventory();
            chatFactory.sendPlayerMessage("&c&l- " + cost + " coins", true, p, prefix);

            if(prevMenu instanceof ChatMenu){
                EquippedConfigManager ecm = new EquippedConfigManager(plugin, p.getUniqueId());
                ecm.setPurchased(configKey, "Chat", true);
                ecm.saveConfig();
            }else{
                prevMenu.openInventory(p);
            }
        }else if(clickedItem.getType().equals(Material.RED_STAINED_GLASS_PANE)){
            p.closeInventory();
            prevMenu.openInventory(p);
        }

    }

    public void giveItems(){
        p.getInventory().addItem(new ItemStack(matPurchasing, amount));
    }

}

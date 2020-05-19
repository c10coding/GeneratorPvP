package me.c10coding.generatorpvp.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class OrePurchaseMenu extends MenuCreator implements Listener {

    private Material oreClicked;

    public OrePurchaseMenu(JavaPlugin plugin, String menuTitle, int numSlots, Material oreClicked) {
        super(plugin, menuTitle, numSlots);
        this.oreClicked = oreClicked;
        createMenu();
    }

    enum OreTypes{

        COAL_BLOCK(Material.COAL_BLOCK, "CoalBlock", "Coal Block"),
        IRON(Material.IRON_INGOT, "Iron", "Iron"),
        IRON_BLOCK(Material.IRON_BLOCK, "IronBlock", "Iron Block"),
        GOLD(Material.GOLD_INGOT, "Gold", "Gold"),
        GOLD_BLOCK(Material.GOLD_BLOCK, "GoldBlock", "Gold Block"),
        DIAMOND(Material.DIAMOND, "Diamond", "Diamond"),
        DIAMOND_BLOCK(Material.DIAMOND_BLOCK, "DiamondBlock", "Diamond Block"),
        EMERALD(Material.EMERALD, "Emerald", "Emerald Block"),
        EMERALD_BLOCK(Material.EMERALD_BLOCK, "EmeraldBlock", "Emerald Block");

        private Material mat;
        private String configKey, name;
        OreTypes(Material mat, String configKey, String name){
            this.mat = mat;
            this.configKey = configKey;
            this.name = name;
        }
    }

    public void createMenu(){
        Material emBlock = Material.EMERALD_BLOCK;
        inv.setItem(11, createGuiItem(emBlock, "Purchase 64", 64, new ArrayList<>()));
        inv.setItem(13, createGuiItem(emBlock, "Purchase 32", 32, new ArrayList<>()));
        inv.setItem(15, createGuiItem(emBlock, "Purchase 1", 1, new ArrayList<>()));
    }

    @EventHandler
    @Override
    protected void onInventoryClick(InventoryClickEvent e) {

        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        Player p = (Player) e.getWhoClicked();
        int slotNum = e.getSlot();
        int amount;
        double cost;
        double playerBalance = econ.getBalance(p);

        if(clickedItem.getType().equals(Material.EMERALD_BLOCK)){
            if(slotNum == 11){
                amount = 64;
            }else if(slotNum == 13){
                amount = 32;
            }else{
                amount = 1;
            }

            String configKey = OreTypes.COAL_BLOCK.configKey;
            OreTypes oreType = OrePurchaseMenu.OreTypes.COAL_BLOCK;

            for(OreTypes ot : OreTypes.values()){
                if(oreClicked.equals(ot.mat)){
                    configKey = ot.configKey;
                    oreType = ot;
                    break;
                }
            }

            cost = cm.getOreCost(configKey, amount);
            if(playerBalance >= cost){
                econ.withdrawPlayer(p, cost);
                giveItems(p, amount);
                chatFactory.sendPlayerMessage("You have been given &b&l" + amount + "&a " + oreType.name, true, p, prefix);
            }else{
                chatFactory.sendPlayerMessage("You can't afford this! It costs &c&l" + cost + "&r coins!", true, p, prefix);
            }
        }
    }

    public void giveItems(Player p , int amount){
        p.getInventory().addItem(new ItemStack(oreClicked, amount));
    }

}

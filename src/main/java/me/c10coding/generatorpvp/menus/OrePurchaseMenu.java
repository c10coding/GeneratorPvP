package me.c10coding.generatorpvp.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrePurchaseMenu extends MenuCreator implements Listener {

    private Material oreClicked;
    private OreTypes oreType;

    public OrePurchaseMenu(JavaPlugin plugin, Material oreClicked, Player p) {
        super(plugin, "Purchase ores", 27, p);
        this.oreClicked = oreClicked;
        this.oreType = getOreType();
        createMenu();
        setHasGivables(true);
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

        public Material getMat(){
            return mat;
        }

        public String getConfigKey(){
            return configKey;
        }

    }

    public void createMenu(){
        Material emBlock = Material.EMERALD_BLOCK;
        int cost1 = (int) cm.getOreCost(oreType.configKey , 1);
        int cost32 = (int) cm.getOreCost(oreType.configKey, 32);
        int cost64 = (int) cm.getOreCost(oreType.configKey, 64);
        List<String> cost1Lore = new ArrayList((Arrays.asList(chatFactory.chat("&b&lCost: &a&l" + cost1))));
        List<String> cost32Lore = new ArrayList((Arrays.asList(chatFactory.chat("&b&lCost: &a&l" + cost32))));
        List<String> cost64Lore = new ArrayList((Arrays.asList(chatFactory.chat("&b&lCost: &a&l" + cost64))));

        inv.setItem(11, createGuiItem(emBlock, "Purchase 64", 64, cost64Lore));
        inv.setItem(13, createGuiItem(emBlock, "Purchase 32", 32, cost32Lore));
        inv.setItem(15, createGuiItem(emBlock, "Purchase 1", 1, cost1Lore));
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

            String configKey = oreType.configKey;
            cost = cm.getOreCost(configKey, amount);

            if(playerBalance >= cost){
                ConfirmPurchaseMenu cpm = new ConfirmPurchaseMenu(plugin, p, oreType.mat , cost, configKey, this, amount);
                p.closeInventory();
                cpm.openInventory(p);
                //econ.withdrawPlayer(p, cost);
                //giveItems(p, amount);
                //chatFactory.sendPlayerMessage("You have been given &b&l" + amount + "&a " + oreType.name, true, p, prefix);
            }else{
                chatFactory.sendPlayerMessage("You can't afford this! It costs &c&l" + cost + "&r coins!", true, p, prefix);
                p.closeInventory();
            }
        }
    }

    public void giveItems(Player p , int amount){
        p.getInventory().addItem(new ItemStack(oreClicked, amount));
    }

    public OreTypes getOreType(){
        OreTypes oreType = null;
        for(OreTypes ot : OreTypes.values()){
            if(oreClicked.equals(ot.mat)){
                oreType = ot;
                break;
            }
        }
        return oreType;
    }

}

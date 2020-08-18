package me.c10coding.generatorpvp.menus;

import me.c10coding.coreapi.APIHook;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class OrePurchaseMenu extends MenuCreator implements Listener {

    private Material oreClicked;
    private OreTypes oreType;

    public OrePurchaseMenu(JavaPlugin plugin, Material oreClicked, Player p) {
        super((APIHook) plugin, "Purchase ores", 27, p);
        this.oreClicked = oreClicked;
        this.oreType = getOreType();
        createMenu();
        setHasGivables(true);
    }

    enum OreTypes{

        COAL_BLOCK(Material.COAL_BLOCK, "CoalBlock", "Coal Block", "&7"),
        IRON(Material.IRON_INGOT, "Iron", "Iron", "&f"),
        IRON_BLOCK(Material.IRON_BLOCK, "IronBlock", "Iron Block", "&f"),
        GOLD(Material.GOLD_INGOT, "Gold", "Gold", "&e"),
        GOLD_BLOCK(Material.GOLD_BLOCK, "GoldBlock", "Gold Block", "&e"),
        DIAMOND(Material.DIAMOND, "Diamond", "Diamond", "&b"),
        DIAMOND_BLOCK(Material.DIAMOND_BLOCK, "DiamondBlock", "Diamond Block", "&b"),
        EMERALD(Material.EMERALD, "Emerald", "Emerald", "&a"),
        EMERALD_BLOCK(Material.EMERALD_BLOCK, "EmeraldBlock", "Emerald Block", "&a");

        private Material mat;
        private String configKey, name;
        private String colorCode;
        OreTypes(Material mat, String configKey, String name, String colorCode){
            this.mat = mat;
            this.configKey = configKey;
            this.name = name;
            this.colorCode = colorCode;
        }

        public Material getMat(){
            return mat;
        }

        public String getConfigKey(){
            return configKey;
        }

        public String getName(){
            return name;
        }

        public String getColorCode(){
            return colorCode;
        }

    }

    public void createMenu(){

        List<Integer> menuSlots = cm.getSlots("OrePurchaseMenu");

        for(Integer i : menuSlots){

            Map<String, Object> slotInfo = cm.getSlotInfo("OrePurchaseMenu", i);
            String displayName = chatFactory.colorString("&f" + slotInfo.get("DisplayName"));
            Material mat = oreType.mat;
            List<String> lore = (List<String>) slotInfo.get("Lore");

            int amount;

            if(i == 11){
                amount = 64;
            }else if(i == 13){
                amount = 32;
            }else{
                amount = 1;
            }

            for(int x = 0; x < lore.size(); x++){
                lore.set(x, applyPlaceholders(lore.get(x), amount));
            }

            inv.setItem(i, createGuiItem(mat, displayName, amount, lore));
        }

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
        int slotNum = e.getSlot();
        int amount;
        double cost;
        double playerBalance = econ.getBalance(p);

        if(clickedItem.getType().equals(oreType.mat)){

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
                ConfirmPurchaseMenu cpm = new ConfirmPurchaseMenu(plugin, p, oreType.mat , cost, configKey, this, amount, oreType.name);
                p.closeInventory();
                cpm.openInventory(p);
            }else{
                int amountMissing = (int) (cost - playerBalance);
                chatFactory.sendPlayerMessage(" ", false, p, null);
                chatFactory.sendPlayerMessage("&fYou are missing &6" + amountMissing + " Coins&f to purchase " + oreType.getName() + ".You can purchase more coins from &eStore.HeightsMC.com", false, p, prefix);
                chatFactory.sendPlayerMessage(" ", false, p, null);
                p.closeInventory();
            }
        }
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

    private String applyPlaceholders(String s, int amount){
        if(s.contains("%cost%")){
            s = s.replace("%cost%", String.valueOf((int)cm.getOreCost(oreType.configKey, amount)));
        }
        return s;
    }

}

package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.files.EquippedConfigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ChatMenu extends MenuCreator{

    private EquippedConfigManager ecm;

    public ChatMenu(JavaPlugin plugin, Player p) {
        super(plugin, "Chat", 27, p);
        this.ecm = new EquippedConfigManager(plugin, p.getUniqueId());
        createMenu();
        fillMenu();
        setHasGivables(false);
    }

    public enum ChatColors{

        GRAY("Gray", Material.GRAY_CONCRETE, "gp.purchase.gray","gp.unlock.gray", false, "&7"),
        GREEN("Green", Material.GREEN_CONCRETE, "gp.purchase.green","gp.unlock.green", true, "&a"),
        YELLOW("Yellow", Material.YELLOW_CONCRETE, "gp.purchase.yellow","gp.unlock.yellow", true, "&e"),
        BLUE("Blue", Material.LIGHT_BLUE_CONCRETE, "gp.purchase.blue","gp.unlock.blue", false, "&1"),
        GOLD("Gold", Material.ORANGE_CONCRETE, "gp.purchase.orange","gp.unlock.orange", false, "&6"),
        PURPLE("Purple", Material.MAGENTA_CONCRETE, "gp.purchase.purple","gp.unlock.magenta", false, "&5");

        private Material mat;
        private String unlockPermission, purchasePermission, configKey, colorCode;
        private boolean isPurchasable;
        ChatColors(String configKey, Material mat, String purchasePermission, String unlockPermission, boolean isPurchasable, String colorCode){
            this.configKey = configKey;
            this.mat = mat;
            this.unlockPermission = unlockPermission;
            this.purchasePermission = purchasePermission;
            this.isPurchasable = isPurchasable;
            this.colorCode = colorCode;
        }

        public String getColorCode(){
            return colorCode;
        }

        public String getConfigKey(){
            return configKey;
        }

    }

    public void createMenu(){

        List<Integer> menuSlots = cm.getSlots("ChatMenu");

        for(int x = 0; x < 6; x++){

            int numSlot = menuSlots.get(x);
            Map<String, Object> slotInfo = cm.getSlotInfo("ChatMenu", numSlot);
            String displayName = (String) slotInfo.get("DisplayName");
            Material mat = (Material) slotInfo.get("Material");
            List<String> lore = (List<String>) slotInfo.get("Lore");
            ChatColors[] colors = ChatColors.values();

            boolean isPurchased = ecm.isPurchased(colors[x].configKey, "Chat");
            verifyChatColors(colors[x], isPurchased);

            if(ecm.isEquipped(colors[x].configKey, "Chat")){
                inv.setItem(numSlot, createGuiItem(Material.REDSTONE_ORE, chatFactory.chat(displayName + " &b&l[Equipped]"),1, lore));
            }else{
                if(ecm.isPurchased(colors[x].configKey, "Chat")){
                    if(colors[x].isPurchasable){
                        inv.setItem(numSlot, createGuiItem(mat, chatFactory.chat(displayName + " &a&l[Purchased]"), 1, lore));
                    }else{
                        inv.setItem(numSlot, createGuiItem(mat, chatFactory.chat(displayName + " &d&l[Unlocked]"), 1, lore));
                    }
                }else{
                    if(colors[x].isPurchasable){
                        inv.setItem(numSlot, createGuiItem(mat, chatFactory.chat(displayName + " &c&l[Not Purchased]"), 1, lore));
                    }else{
                        inv.setItem(numSlot, createGuiItem(mat, chatFactory.chat(displayName + " &4&l[Locked]"), 1, lore));
                    }
                }
            }
        }
    }

    @EventHandler
    @Override
    protected void onInventoryClick(InventoryClickEvent e) {

        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        Player p = (Player) e.getWhoClicked();
        int slotClicked = e.getSlot();
        String wantedKey;
        Material mat;
        ChatColors chatColor;

        switch(slotClicked){
            case 10:
                chatColor = ChatColors.GRAY;
                wantedKey = ChatColors.GRAY.configKey;
                mat = getSlotMaterial(10);
                break;
            case 11:
                chatColor = ChatColors.GREEN;
                wantedKey = ChatColors.GREEN.configKey;
                mat = getSlotMaterial(11);
                break;
            case 12:
                chatColor = ChatColors.YELLOW;
                wantedKey = ChatColors.YELLOW.configKey;
                mat = getSlotMaterial(12);
                break;
            case 14:
                chatColor = ChatColors.BLUE;
                wantedKey = ChatColors.BLUE.configKey;
                mat = getSlotMaterial(14);
                break;
            case 15:
                chatColor = ChatColors.GOLD;
                wantedKey = ChatColors.GOLD.configKey;
                mat = getSlotMaterial(15);
                break;
            case 16:
                chatColor = ChatColors.PURPLE;
                wantedKey = ChatColors.PURPLE.configKey;
                mat = getSlotMaterial(16);
                break;
            default:
                return;
        }

        double cost = cm.getChatCost(wantedKey);
        double playerBalance = econ.getBalance(p);


        if(!ecm.isPurchased(wantedKey,"Chat") && chatColor.isPurchasable){

            if(playerBalance >= cost) {

                if(p.hasPermission(chatColor.purchasePermission)){
                    ConfirmPurchaseMenu cpm = new ConfirmPurchaseMenu(plugin, p, mat, cost, wantedKey,this, 1);
                    p.closeInventory();
                    cpm.openInventory(p);
                }else{
                    chatFactory.sendPlayerMessage("You don't have permissions to do that!", true, p, prefix);
                    p.closeInventory();
                }

            }else{
                chatFactory.sendPlayerMessage("You're too broke for that. Go kill some people to get more coins!", true, p, prefix);
                p.closeInventory();
            }

        }else{

            if(!chatColor.isPurchasable && !p.hasPermission(chatColor.unlockPermission)){
                chatFactory.sendPlayerMessage("This is not purchase-able! You must buy a rank to unlock this", true, p, prefix);
                p.closeInventory();
            }

            if(ecm.isPurchased(wantedKey, "Chat")){
            /*
                Sets the thing that was equipped to it's regular self
            */
                if(ecm.hasSomethingEquipped("Chat")){
                    int slotEquipped = getSlotEquipped(Material.REDSTONE_ORE);
                    if(slotEquipped != slotClicked){
                        setSlotToNormal(slotEquipped);
                    }else{
                        return;
                    }
                }

                setSlotToEquipped(slotClicked);
                ecm.setEquipped(wantedKey, "Chat");
                chatFactory.sendPlayerMessage("You have equipped &b&l" + wantedKey + "!", true, p, prefix);
                p.closeInventory();
            }
            ecm.saveConfig();
        }

    }

    public Material getSlotMaterial(int numSlot){
        return (Material) cm.getSlotInfo("ChatMenu", numSlot).get("Material");
    }

    public int getSlotEquipped(Material matEquipped){
        for(int x = 0; x < 27; x++){
            if(matEquipped.equals(inv.getItem(x).getType())){
                return x;
            }
        }
        return 0;
    }

    /*
    Made these two methods practically the same because i'm lazy
     */
    public void setSlotToNormal(int slotEquipped){
        Map<String, Object> slotInfo = cm.getSlotInfo("ChatMenu", slotEquipped);
        String equippedDisplayname = (String) slotInfo.get("DisplayName");
        Material equippedMat = (Material) slotInfo.get("Material");
        List<String> equippedLore = (List<String>) slotInfo.get("Lore");
        inv.setItem(slotEquipped, createGuiItem(equippedMat, chatFactory.chat(equippedDisplayname + "&a&l [Bought]"), equippedLore));
    }

    public void setSlotToEquipped(int slotClicked){
        Map<String, Object> slotInfo = cm.getSlotInfo("ChatMenu", slotClicked);
        String equippedDisplayname = (String) slotInfo.get("DisplayName");
        List<String> equippedLore = (List<String>) slotInfo.get("Lore");
        inv.setItem(slotClicked, createGuiItem(Material.REDSTONE_ORE, chatFactory.chat(equippedDisplayname + "&b&l [Equipped]"), equippedLore));
    }

    /*
    public void verifyChatColors(){
        for(ChatColors c : ChatColors.values()){
            if(!ecm.isInFile()){
                ecm.addPlayerToFile(p.getUniqueId());
            }
            if(!ecm.isPurchased(c.configKey, "Chat")){
                if(p.hasPermission(c.unlockPermission)){
                    ecm.setPurchased(c.configKey, "Chat", true);
                }
            }
        }
        ecm.saveConfig();
    }*/

    public void verifyChatColors(ChatColors cc, boolean isPurchased){

        if(!p.hasPermission(cc.unlockPermission) && isPurchased && !cc.isPurchasable && !cc.equals(ChatColors.GRAY)){
            ecm.setPurchased(cc.configKey, "Chat", false);
            if(ecm.isEquipped(cc.configKey, "Chat")){
                ecm.setEquipped(cc.configKey, "Chat", false);
            }
        }else{

            if(p.hasPermission(cc.unlockPermission) && !cc.isPurchasable){
                ecm.setPurchased(cc.configKey, "Chat", true);
            }

        }
        ecm.saveConfig();
    }

}

package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.files.EquippedConfigManager;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

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

        GRAY("Gray", Material.GRAY_CONCRETE, "gp.purchase.gray","gp.unlock.gray", false, "&7", "Gray Chat Color"),
        GREEN("LimeGreen", Material.LIME_CONCRETE, "gp.purchase.limegreen","gp.unlock.limegreen", true, "&a", "Lime Green Chat Color"),
        YELLOW("Yellow", Material.YELLOW_CONCRETE, "gp.purchase.yellow","gp.unlock.yellow", true, "&e", "Yellow Chat Color"),
        BLUE("Blue", Material.LIGHT_BLUE_CONCRETE, "gp.purchase.blue","gp.unlock.blue", false, "&b", "Blue Chat Color"),
        GOLD("Gold", Material.ORANGE_CONCRETE, "gp.purchase.gold","gp.unlock.gold", false, "&6", "Gold Chat Color"),
        PURPLE("Purple", Material.MAGENTA_CONCRETE, "gp.purchase.lightpurple","gp.unlock.lightpurple", false, "&d", "Light Purple Chat Color");

        private Material mat;
        private String unlockPermission, purchasePermission, configKey, colorCode, purchasedMenuName;
        private boolean isPurchasable;
        ChatColors(String configKey, Material mat, String purchasePermission, String unlockPermission, boolean isPurchasable, String colorCode, /*The name that gets displayed on ConfirmPUrchaseMenu*/String purchasedMenuName){
            this.configKey = configKey;
            this.mat = mat;
            this.unlockPermission = unlockPermission;
            this.purchasePermission = purchasePermission;
            this.isPurchasable = isPurchasable;
            this.colorCode = colorCode;
            this.purchasedMenuName = colorCode + purchasedMenuName;
        }

        public String getColorCode(){
            return colorCode;
        }

        public String getConfigKey(){
            return configKey;
        }

        public String getPurchasedMenuName(){
            return purchasedMenuName;
        }

    }

    public void createMenu(){

        List<Integer> menuSlots = cm.getSlots("ChatMenu");
        ecm.reloadConfig();
        for(int x = 0; x < 6; x++){

            int numSlot = menuSlots.get(x);
            Map<String, Object> slotInfo = cm.getSlotInfo("ChatMenu", numSlot);
            String displayName = (String) slotInfo.get("DisplayName");
            Material mat = (Material) slotInfo.get("Material");
            List<String> lore = GPUtils.colorLore((List<String>) slotInfo.get("Lore"));
            ChatColors[] colors = ChatColors.values();

            boolean isPurchased = ecm.isPurchased(colors[x].configKey, "Chat");
            verifyChatColors(colors[x], isPurchased);

            if(ecm.isEquipped(colors[x].configKey, "Chat")){
                setSlotToEquipped(numSlot);
            }else{
                if(ecm.isPurchased(colors[x].configKey, "Chat") || p.hasPermission(colors[x].unlockPermission)){
                    if(colors[x].isPurchasable || colors[x].equals(ChatColors.GRAY)){
                        lore.add(chatFactory.colorString("&aPurchased"));
                    }else{
                        lore.add(chatFactory.colorString("&aUnlocked"));
                    }
                }else{
                    if(colors[x].isPurchasable){
                        lore.add(chatFactory.colorString("&cNot Purchased"));
                        lore.add(chatFactory.colorString("&aCost: &6" + (int)cm.getChatCost(colors[x].configKey) + " Coins"));
                    }else{
                        lore.add(chatFactory.colorString("&cLocked"));
                    }
                }
                inv.setItem(numSlot, createGuiItem(mat, chatFactory.colorString(displayName), 1, lore));
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
        String wantedKey, msg;
        Material mat;
        ChatColors chatColor;

        switch(slotClicked){
            case 10:
                chatColor = ChatColors.GRAY;
                wantedKey = ChatColors.GRAY.configKey;
                mat = getSlotMaterial(10);
                msg = chatFactory.colorString("&fYour chat colour is now &7Grey");
                break;
            case 11:
                chatColor = ChatColors.GREEN;
                wantedKey = ChatColors.GREEN.configKey;
                mat = getSlotMaterial(11);
                msg = chatFactory.colorString("&fYour chat colour is now &aLime Green");
                break;
            case 12:
                chatColor = ChatColors.YELLOW;
                wantedKey = ChatColors.YELLOW.configKey;
                mat = getSlotMaterial(12);
                msg = chatFactory.colorString("&fYour chat colour is now &eYellow");
                break;
            case 14:
                chatColor = ChatColors.BLUE;
                wantedKey = ChatColors.BLUE.configKey;
                mat = getSlotMaterial(14);
                msg = chatFactory.colorString("&fYour chat colour is now &bAqua");
                break;
            case 15:
                chatColor = ChatColors.GOLD;
                wantedKey = ChatColors.GOLD.configKey;
                mat = getSlotMaterial(15);
                msg = chatFactory.colorString("&fYour chat colour is now &6Gold");
                break;
            case 16:
                chatColor = ChatColors.PURPLE;
                wantedKey = ChatColors.PURPLE.configKey;
                mat = getSlotMaterial(16);
                msg = chatFactory.colorString("&fYour chat colour is now &dLight Purple");
                break;
            default:
                return;
        }

        double cost = cm.getChatCost(wantedKey);
        double playerBalance = econ.getBalance(p);

        if(!ecm.isPurchased(wantedKey,"Chat") && chatColor.isPurchasable){

            if(playerBalance >= cost) {

                if(p.hasPermission(chatColor.purchasePermission)){
                    ConfirmPurchaseMenu cpm = new ConfirmPurchaseMenu(plugin, p, mat, cost, wantedKey,this, 1, chatFactory.colorString(chatColor.purchasedMenuName));
                    p.closeInventory();
                    cpm.openInventory(p);
                }else{
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    chatFactory.sendPlayerMessage("You don't have permissions to do that!", false, p, prefix);
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    p.closeInventory();
                }

            }else{
                int amountMissing = (int) (cost - playerBalance);
                chatFactory.sendPlayerMessage(" ", false, p, null);
                chatFactory.sendPlayerMessage("&fYou are missing &6" + amountMissing + " Coins&f to purchase " + chatColor.purchasedMenuName + ".&f You can purchase more coins from &eStore.HeightsMC.com", false, p, prefix);
                chatFactory.sendPlayerMessage(" ", false, p, null);
                p.closeInventory();
            }

        }else{

            if(!chatColor.isPurchasable && !p.hasPermission(chatColor.unlockPermission) && !chatColor.equals(ChatColors.GRAY)){
                switch(chatColor){
                    case BLUE:
                        chatFactory.sendPlayerMessage(" ", false, p, null);
                        chatFactory.sendPlayerMessage("&7You must atleast purchase &9VIP &7rank", false, p, prefix);
                        chatFactory.sendPlayerMessage(" ", false, p, null);
                        break;
                    case GOLD:
                        chatFactory.sendPlayerMessage(" ", false, p, null);
                        chatFactory.sendPlayerMessage("&7You must atleast purchase &6MVP &7rank", false, p, prefix);
                        chatFactory.sendPlayerMessage(" ", false, p, null);
                        break;
                    case PURPLE:
                        chatFactory.sendPlayerMessage(" ", false, p, null);
                        chatFactory.sendPlayerMessage("&7You must atleast purchase &d&lULTRA &7rank", false, p, prefix);
                        chatFactory.sendPlayerMessage(" ", false, p, null);
                        break;
                }
                p.closeInventory();
                return;
            }

            if(ecm.isPurchased(wantedKey, "Chat")){
            /*
                Sets the thing that was equipped to it's regular self
            */
                if(ecm.hasSomethingEquipped("Chat")){
                    int slotEquipped = getSlotEquipped();
                    if(slotEquipped != slotClicked){
                        setSlotToNormal(slotEquipped);
                    }else{
                        return;
                    }
                }

                setSlotToEquipped(slotClicked);
                ecm.setEquipped(wantedKey, "Chat");
                chatFactory.sendPlayerMessage(" ", false, p, prefix);
                chatFactory.sendPlayerMessage(msg, false, p, prefix);
                chatFactory.sendPlayerMessage(" ", false, p, prefix);
            }
            ecm.saveConfig();
        }

    }

    public Material getSlotMaterial(int numSlot){
        return (Material) cm.getSlotInfo("ChatMenu", numSlot).get("Material");
    }

    public int getSlotEquipped(){

        for(int x = 0; x < 27; x++){
            ItemStack currentStack = inv.getItem(x);
            if(currentStack.hasItemMeta()){
                ItemMeta meta = currentStack.getItemMeta();
                if(meta.hasEnchant(Enchantment.WATER_WORKER)){
                    return x;
                }
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
        equippedLore.add(chatFactory.colorString("&aPurchased"));
        inv.setItem(slotEquipped, createGuiItem(equippedMat, chatFactory.colorString(equippedDisplayname), equippedLore));
    }

    public void setSlotToEquipped(int slotClicked){
        Map<String, Object> slotInfo = cm.getSlotInfo("ChatMenu", slotClicked);
        String equippedDisplayname = (String) slotInfo.get("DisplayName");
        List<String> equippedLore = GPUtils.colorLore((List<String>) slotInfo.get("Lore"));
        Material material = (Material) slotInfo.get("Material");

        ItemStack colorEquipped = new ItemStack(material, 1);
        colorEquipped = GPUtils.addGlow(colorEquipped);
        ItemMeta meta = colorEquipped.getItemMeta();
        equippedLore.add(chatFactory.colorString("&aEquipped"));
        meta.setDisplayName(chatFactory.colorString(equippedDisplayname));
        meta.setLore(equippedLore);
        colorEquipped.setItemMeta(meta);

        inv.setItem(slotClicked, colorEquipped);
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

package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.DefaultConfigManager;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
import me.c10coding.generatorpvp.managers.ScoreboardManager;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ConfirmPurchaseMenu extends MenuCreator {

    private Material matPurchasing;
    private String configKey, itemName;
    private int cost;
    private MenuCreator prevMenu;
    private int amount;
    final String ORE_PURCHASE_LORE = "&c[&4!&c] &rUse this item to trade &c[&4!&c]";
    private EquippedConfigManager ecm;
    private ScoreboardManager sm;

    public ConfirmPurchaseMenu(JavaPlugin plugin, Player p, Material mat, double cost, String configKey, MenuCreator prevMenu, int amount, String itemName) {
        super(plugin, "Purchasing: " + itemName, 27, p);
        this.matPurchasing = mat;
        this.configKey = configKey;
        this.cost = (int) cost;
        this.prevMenu = prevMenu;
        this.amount = amount;
        this.itemName = itemName;
        this.ecm = new EquippedConfigManager(plugin,p.getUniqueId());
        this.sm = new ScoreboardManager((GeneratorPvP) plugin);
        sm.setSB(p);
        createMenu();
    }

    public void createMenu(){
        int playerBalance = (int) econ.getBalance(p);
        List<String> lore = new ArrayList<>();
        lore.add(chatFactory.colorString("&aCost: &6" + cost + " Coins"));
        if(prevMenu instanceof SuperBootsMenu){
            ItemStack boots = createEnchantedBoot();
            inv.setItem(13, boots);
        }else{
            inv.setItem(13, createGuiItem(matPurchasing, chatFactory.colorString(itemName), amount, lore));
        }
        inv.setItem(22, createGuiItem(Material.SUNFLOWER, chatFactory.colorString("&7Coins: &c" + playerBalance), new ArrayList<>()));
        fillMenu();
    }

    private ItemStack createEnchantedBoot(){
        SuperBootsMenu.SuperBoots bootType = getSuperBootType(configKey);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta lam = (LeatherArmorMeta) boots.getItemMeta();
        lam.setColor(bootType.getColorOfArmor());
        lam.setDisplayName(chatFactory.colorString("&e" + bootType.getConfigKey() + "&7 Boots"));
        boots.setItemMeta(lam);
        return boots;
    }

    private SuperBootsMenu.SuperBoots getSuperBootType(String configKey){
        SuperBootsMenu.SuperBoots bootType = SuperBootsMenu.SuperBoots.ANTI_FALL;
        for(SuperBootsMenu.SuperBoots sb : SuperBootsMenu.SuperBoots.values()){
            if(sb.getConfigKey().equalsIgnoreCase(configKey)){
                bootType = sb;
            }
        }
        return bootType;
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
                ItemStack fillerItem = createPane();
                inv.setItem(x, fillerItem);
            }
            patternCounter++;
        }
    }

    private ItemStack createPane(){
        ItemStack glassPane = new ItemStack(fillerMat, 1);
        ItemMeta itemMeta = glassPane.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if(fillerMat.equals(Material.GREEN_STAINED_GLASS_PANE)){
            itemMeta.setDisplayName(chatFactory.colorString("&aPurchase"));
        }else if(fillerMat.equals(Material.RED_STAINED_GLASS_PANE)){
            itemMeta.setDisplayName(chatFactory.colorString("&cCancel"));
        }else{
            itemMeta.setDisplayName(" ");
        }
        glassPane.setItemMeta(itemMeta);
        return glassPane;
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
        }else if(prevMenu instanceof SuperBootsMenu){
            prevMenu = new SuperBootsMenu(plugin, p);
        }

        if(clickedItem.getType().equals(Material.GREEN_STAINED_GLASS_PANE)){

            if(prevMenu.hasGivables()){
                giveItems();
            }

            econ.withdrawPlayer(p, cost);
            p.closeInventory();
            chatFactory.sendPlayerMessage(" ", false, p, null);
            chatFactory.sendPlayerMessage(chatFactory.colorString("&fYou just &apurchased " + itemName + " &ffor &6" + cost + " coins"), false, p, prefix);
            chatFactory.sendPlayerMessage(" ", false, p, null);

            if(prevMenu instanceof ChatMenu) {
                ecm.setPurchased(configKey, "Chat", true);
                ecm.saveConfig();
                prevMenu = new ChatMenu(plugin, p);
            }else if(prevMenu instanceof SuperBootsMenu){
                ecm.setPurchased(configKey, "SuperBoots", true);
                ecm.saveConfig();
                prevMenu = new SuperBootsMenu(plugin, p);
            }else if(matPurchasing.equals(Material.ENDER_CHEST)){
                ecm.setPurchased(configKey, "EnderChest", true);
                ecm.saveConfig();
                p.openInventory(p.getEnderChest());
                return;
            }

            prevMenu.openInventory(p);
        }else if(clickedItem.getType().equals(Material.RED_STAINED_GLASS_PANE)){
            p.closeInventory();
            prevMenu.openInventory(p);
        }
    }

    public void giveItems(){
        ItemStack item = new ItemStack(matPurchasing, amount);

        if(prevMenu instanceof OresMenu){
            for(OrePurchaseMenu.OreTypes oreType : OrePurchaseMenu.OreTypes.values()){
                if(oreType.getMat().equals(matPurchasing)){
                    ItemMeta itemMeta = item.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    lore.add(chatFactory.colorString(ORE_PURCHASE_LORE));
                    itemMeta.setDisplayName(chatFactory.colorString(oreType.getColorCode() + oreType.getName()));
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                }
            }
        }else if(prevMenu instanceof WeaponsMenu){
            ItemMeta meta = item.getItemMeta();
            String displayName;
            List<String> lore = new ArrayList<>();
            if(matPurchasing.equals(Material.SNOWBALL)){
                displayName = chatFactory.colorString("&fKnockback");
                lore.add("&eDeals a little knockback upon hit");
            }else if(matPurchasing.equals(Material.SLIME_BALL)){
                displayName = chatFactory.colorString("&aPosition Swap");
                lore.add("&eSwaps position with whoever gets hit with it");
            }else if(matPurchasing.equals(Material.TNT)){
                displayName = chatFactory.colorString("&cTNT");
                lore.add("&eYou better move out of the way after you place this stuff...");
            }else if(matPurchasing.equals(Material.FIRE_CHARGE)){
                displayName = chatFactory.colorString("&4Fireball");
                lore.add("&eOnce again, you're a human Ghast!");
            }else if(matPurchasing.equals(Material.EGG)){
                displayName = chatFactory.colorString("&eInstant Kill");
                lore.add("&ePretty much instantly kills whoever you throw this at.");
            }else{
                displayName = "";
            }

            lore = GPUtils.colorLore(lore);
            meta.setLore(lore);
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }

        p.getInventory().addItem(item);
    }

}

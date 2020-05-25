package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.files.DefaultConfigManager;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
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
    private String configKey;
    private int cost;
    private MenuCreator prevMenu;
    private int amount;
    final String ORE_PURCHASE_LORE = "&c[!] &rUse this item to trade &c[!]";
    private EquippedConfigManager ecm;

    public ConfirmPurchaseMenu(JavaPlugin plugin, Player p, Material mat, double cost, String configKey, MenuCreator prevMenu, int amount) {
        super(plugin, "Purchasing: &b&l" + GPUtils.matToName(mat), 27, p);
        this.matPurchasing = mat;
        this.configKey = configKey;
        this.cost = (int) cost;
        this.prevMenu = prevMenu;
        this.amount = amount;
        this.ecm = new EquippedConfigManager(plugin,p.getUniqueId());
        createMenu();
    }

    public void createMenu(){
        int playerBalance = (int) econ.getBalance(p);
        List<String> lore = new ArrayList<>();
        lore.add(chatFactory.chat("&7Cost: &c" + cost + " coins"));
        if(prevMenu instanceof SuperBootsMenu){
            ItemStack boots = createEnchantedBoot();
            inv.setItem(13, boots);
        }else{
            inv.setItem(13, createGuiItem(matPurchasing, chatFactory.chat(GPUtils.matToName(matPurchasing)), amount, lore));
        }
        inv.setItem(22, createGuiItem(Material.SUNFLOWER, chatFactory.chat("&7Coins: &c" + playerBalance), new ArrayList<>()));
        fillMenu();
    }

    private ItemStack createEnchantedBoot(){
        SuperBootsMenu.SuperBoots bootType = getSuperBootType(configKey);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta lam = (LeatherArmorMeta) boots.getItemMeta();
        lam.setColor(bootType.getColorOfArmor());
        lam.setDisplayName(chatFactory.chat("&e" + bootType.getConfigKey() + "&7 Boots"));
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
            itemMeta.setDisplayName(chatFactory.chat("&aPurchase"));
        }else{
            itemMeta.setDisplayName(chatFactory.chat("&cCancel"));
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
            chatFactory.sendPlayerMessage("&c&l- &c" + cost + "&e coins", true, p, prefix);

            if(prevMenu instanceof ChatMenu) {
                ecm.setPurchased(configKey, "Chat", true);
                ecm.saveConfig();
            }else if(prevMenu instanceof SuperBootsMenu){
                ecm.setPurchased(configKey, "SuperBoots", true);
                ecm.saveConfig();
            }else if(prevMenu instanceof MenuCreator){
                ecm.setPurchased(configKey, "EnderChest", true);
                ecm.saveConfig();
            }

            p.closeInventory();
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
                    lore.add(chatFactory.chat(ORE_PURCHASE_LORE));
                    itemMeta.setDisplayName(chatFactory.chat(oreType.getColorCode() + oreType.getName()));
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                }
            }
        }else if(prevMenu instanceof WeaponsMenu){
            ItemMeta meta = item.getItemMeta();
            String displayName;
            List<String> lore = new ArrayList<>();
            if(matPurchasing.equals(Material.SNOWBALL)){
                displayName = chatFactory.chat("&fKnockback");
                lore.add("&eDeals a little knockback upon hit");
            }else if(matPurchasing.equals(Material.SLIME_BALL)){
                displayName = chatFactory.chat("&aPosition Swap");
                lore.add("&eSwaps position with whoever gets hit with it");
            }else if(matPurchasing.equals(Material.TNT)){
                displayName = chatFactory.chat("&cTNT");
                lore.add("&eYou better move out of the way after you place this stuff...");
            }else if(matPurchasing.equals(Material.FIRE_CHARGE)){
                displayName = chatFactory.chat("&4Fireball");
                lore.add("&eOnce again, you're a human Ghast!");
            }else if(matPurchasing.equals(Material.EGG)){
                displayName = chatFactory.chat("&eInstant Kill");
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

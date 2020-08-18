package me.c10coding.generatorpvp.menus;

import me.c10coding.coreapi.APIHook;
import me.c10coding.generatorpvp.GeneratorPvP;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Map;

public class WarpsMenu extends MenuCreator implements Listener {

    public WarpsMenu(APIHook plugin, Player p) {
        super(plugin, "Warps", 27, p);
        createMenu("WarpMenu");
        adjustSpecial();
        fillMenu();
        setHasGivables(false);
    }

    public void adjustSpecial(){
        if(ecm.isPurchased("Special", "Warps")){
            Map<String, Object> slotInfo = cm.getSlotInfo("WarpMenu", 17);
            String displayName = (String) slotInfo.get("DisplayName");
            Material mat = (Material) slotInfo.get("Material");

            inv.setItem(17, createGuiItem(mat, displayName, 1, new ArrayList<>()));

        }
    }

    @Override
    public void initializeItems(Player player) { }

    @Override
    public void fillMenu(){
        Material blackPane = Material.BLACK_STAINED_GLASS_PANE;
        Material redPane = Material.RED_STAINED_GLASS_PANE;
        Material orangePane = Material.ORANGE_STAINED_GLASS_PANE;
        Material purplePane = Material.PURPLE_STAINED_GLASS_PANE;
        Material pinkPane = Material.PINK_STAINED_GLASS_PANE;
        Material cyanPane = Material.CYAN_STAINED_GLASS_PANE;
        Material[] pattern = {blackPane, redPane, cyanPane, redPane , orangePane,redPane, purplePane, redPane, pinkPane};
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

        if(e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if(clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        Player playerClicked = (Player) e.getWhoClicked();
        int slotClicked = e.getSlot();
        int tpDelay = (int) cm.getTPDelay();

        switch(slotClicked){
            case 9:
                chatFactory.sendPlayerMessage(" ", false, playerClicked, prefix);
                chatFactory.sendPlayerMessage("&cTeleporting &fyou in &c" + tpDelay + "&f seconds.&4 Do Not Move!", false, playerClicked, prefix);
                chatFactory.sendPlayerMessage(" ", false, playerClicked, prefix);
                enterTPProcess(new Location(playerClicked.getWorld(), -563, 60, -645), false);
                p.closeInventory();
                break;
            case 11:
                if(playerClicked.hasPermission("gp.teleport.vip")){
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, prefix);
                    chatFactory.sendPlayerMessage("&cTeleporting &fyou in &c" + tpDelay + "&f seconds.&4 Do Not Move!", false, playerClicked, prefix);
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, prefix);
                    enterTPProcess(new Location(playerClicked.getWorld(), -563, 85, -645), false);
                }else{
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, null);
                    chatFactory.sendPlayerMessage("&7You must atleast purchase &9VIP &7rank", false, playerClicked, prefix);
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, null);
                }
                p.closeInventory();
                break;
            case 13:
                if(playerClicked.hasPermission("gp.teleport.mvp")){
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, prefix);
                    chatFactory.sendPlayerMessage("&cTeleporting &fyou in &c" + tpDelay + "&f seconds.&4 Do Not Move!", false, playerClicked, prefix);
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, prefix);
                    enterTPProcess(new Location(playerClicked.getWorld(), -563, 107, -645), false);
                }else{
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, null);
                    chatFactory.sendPlayerMessage("&7You must atleast purchase &6MVP &7rank", false, playerClicked, prefix);
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, null);
                }
                p.closeInventory();
                break;
            case 15:
                if(playerClicked.hasPermission("gp.teleport.ultra")){
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, prefix);
                    chatFactory.sendPlayerMessage("&cTeleporting &fyou in &c" + tpDelay + "&f seconds.&4 Do Not Move!", false, playerClicked, prefix);
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, prefix);
                    enterTPProcess(new Location(playerClicked.getWorld(), -563, 140, -645), false);
                }else{
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, null);
                    chatFactory.sendPlayerMessage("&7You must atleast purchase &d&lULTRA &7rank", false, playerClicked, prefix);
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, null);
                }
                p.closeInventory();
                break;
            case 17:
                int coins = (int) GeneratorPvP.getEconomy().getBalance(playerClicked);
                int cost = (int) cm.getTPCost("SpecialTP");
                if(!ecm.isPurchased("Special", "Warps")){
                    if(coins >= cost){
                        chatFactory.sendPlayerMessage(" ", false, playerClicked, null);
                        chatFactory.sendPlayerMessage("&7You just purchased the &eSpecial Warp &7for " + cost + " &6Coins", false, p, prefix);
                        chatFactory.sendPlayerMessage(" ", false, playerClicked, prefix);
                        chatFactory.sendPlayerMessage("&cTeleporting &fyou in &c" + tpDelay + "&f seconds.&4 Do Not Move!", false, playerClicked, prefix);
                        chatFactory.sendPlayerMessage(" ", false, playerClicked, prefix);
                        GeneratorPvP.getEconomy().withdrawPlayer(playerClicked, cm.getTPCost("SpecialTP"));
                        enterTPProcess(new Location(playerClicked.getWorld(), -563, 160, -645), true);
                    }else{
                        //&fYou are missing &6[AMOUNT] Coins&f to purchase [ITEM NAME AND COLOUR]&f. You can purchase more coins from &eStore.HeightsMC.com
                        int amountMissing = cost - coins;
                        chatFactory.sendPlayerMessage(" ", false, playerClicked, null);
                        chatFactory.sendPlayerMessage("&7You are missing &6" + amountMissing + " Coins&f " + " to purchase the Special Warp. You can purchase more coins from &eStore.HeightsMC.com", false, playerClicked, prefix);
                        chatFactory.sendPlayerMessage(" ", false, playerClicked, null);
                    }
                    p.closeInventory();
                    break;
                }else{
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, prefix);
                    chatFactory.sendPlayerMessage("&cTeleporting &fyou in &c" + tpDelay + "&f seconds.&4 Do Not Move!", false, playerClicked, prefix);
                    chatFactory.sendPlayerMessage(" ", false, playerClicked, prefix);
                    enterTPProcess(new Location(playerClicked.getWorld(), -563, 160, -645), true);
                    p.closeInventory();
                }
                break;
        }
    }

    public void enterTPProcess(Location loc, boolean isSpecialTP){

        BukkitRunnable br = new BukkitRunnable() {
            double counter = cm.getTPDelay();
            @Override
            public void run() {
                if(counter == 0){
                    p.teleport(loc);
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    chatFactory.sendPlayerMessage("&7Teleporting...", false, p, prefix);
                    chatFactory.sendPlayerMessage(" ", false, p, null);

                    ecm.setPurchased("Special", "Warps", true);
                    ecm.saveConfig();

                    removeMetadata();
                    this.cancel();
                }else{
                    p.sendTitle("Teleporting you...", chatFactory.colorString("&d&l") + (int)counter + "...", 5, 20, 5);
                }
                counter--;
            }
        };

        BukkitTask t = br.runTaskTimer(plugin, 0L, 20L);
        final int id = t.getTaskId();

        setMetadata(id, isSpecialTP);
    }

    private void setMetadata(int taskID, boolean isSpecialTP){
        p.setMetadata("TP", new FixedMetadataValue(plugin, taskID));
        if(isSpecialTP){
            p.setMetadata("isSpecial", new FixedMetadataValue(plugin, true));
        }
    }

    private void removeMetadata(){
        p.removeMetadata("TP", plugin);
        if(p.hasMetadata("isSpecial")){
            p.removeMetadata("isSpecial", plugin);
        }
    }

}

package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.IncompleteLocation;
import me.c10coding.generatorpvp.utils.GPUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeleportationsMenu extends MenuCreator{

    public TeleportationsMenu(JavaPlugin plugin, Player p) {
        super(plugin, "Teleportations", 27, p);
        createMenu("TeleportationsMenu");
        addCostToLore();
        fillMenu();
        setHasGivables(false);
    }

    public void addCostToLore(){
        for(Teleportations t : Teleportations.values()){
            int numSlot = t.numSlot;
            ItemStack item = inv.getItem(numSlot);
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            int cost = (int) Math.round(cm.getOreTPCost(t.configKey));
            if (meta.getLore() == null) {
                lore = new ArrayList<>();
            }
            lore.add(chatFactory.chat("&aCost: &6" + cost + " Coins"));
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(numSlot, item);
        }
    }

    public enum Teleportations{

        EMERALD(10, "Emerald", new IncompleteLocation(-561, 90, -473), null),
        DIAMOND_BLOCK(11, "DiamondBlock", new IncompleteLocation(-560, 89, -492), null),
        DIAMOND(12, "Diamond", new IncompleteLocation(-557, 85, -511), null),
        GOLD_BLOCK(13, "GoldBlock", new IncompleteLocation(-557, 78, -521), null),
        GOLD(14, "Gold" , new IncompleteLocation(-570, 68, -534), null),
        IRON_BLOCK(15, "IronBlock", new IncompleteLocation(-556, 65, -560), null),
        IRON(16, "Iron", new IncompleteLocation(-523, 64, -599), new IncompleteLocation(-574, 76, -610));

        private int numSlot;
        private String configKey;
        private IncompleteLocation firstLoc, secondLoc;
        Teleportations(int numSlot, String configKey, IncompleteLocation firstLoc, IncompleteLocation secondLoc){
            this.numSlot = numSlot;
            this.configKey = configKey;
            this.firstLoc = firstLoc;
            this.secondLoc = secondLoc;
        }

        public int getNumSlot(){
            return numSlot;
        }

        public String getConfigKey(){
            return configKey;
        }

        public IncompleteLocation getIncLocation(int num){
            return num == 1 ? firstLoc : secondLoc;
        }

        public IncompleteLocation getIncLocation(){
            return firstLoc;
        }
    }

    @EventHandler
    @Override
    protected void onInventoryClick(InventoryClickEvent e) {

        if(e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if(clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        Player p = (Player) e.getWhoClicked();
        int slotClicked = e.getSlot();
        String prefix = ((GeneratorPvP)plugin).getPrefix();
        double coins = econ.getBalance(p);
        double cost;
        Location teleLoc;
        int tpDelay = (int) cm.getTPDelay();

        for(Teleportations t : Teleportations.values()){
            if(t.numSlot == slotClicked){
                cost = cm.getOreTPCost(t.configKey);
                if(coins >= cost){
                    if(t.equals(Teleportations.IRON)){
                        Random rnd = new Random();
                        int i = rnd.nextInt(100);
                        if(i < 50){
                            teleLoc = t.getIncLocation(1).completeLocation(p.getWorld());
                        }else{
                            teleLoc = t.getIncLocation(2).completeLocation(p.getWorld());
                        }
                    }else{
                        teleLoc = t.getIncLocation().completeLocation(p.getWorld());
                    }

                    econ.withdrawPlayer(p, cost);
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    chatFactory.sendPlayerMessage(chatFactory.chat("&fYou just &apurchased the " + GPUtils.enumToName(t) + " Warp &ffor &6" + (int)cost + " coins"), false, p, prefix);
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    chatFactory.sendPlayerMessage("&cTeleporting &fyou in &c" + tpDelay + " &fseconds.&4 Do Not Move!", false, p, prefix);
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    enterTPProcess(teleLoc, (int) cost);
                    p.closeInventory();
                    return;
                }else{
                    int amountMissing = (int) (cost - coins);
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    chatFactory.sendPlayerMessage("&fYou are missing &6" + amountMissing + " Coins&f to purchase the " + GPUtils.enumToName(t) + " Warp" + ".&f You can purchase more coins from &eStore.HeightsMC.com", false, p, prefix);
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    p.closeInventory();
                }
            }
        }

        fillMenu();

    }

    public void enterTPProcess(Location loc, int cost){

        BukkitRunnable br = new BukkitRunnable() {
            double counter = cm.getTPDelay();
            @Override
            public void run() {
                if(p.isOnline()){
                    if(counter == 0){
                        p.teleport(loc);
                        chatFactory.sendPlayerMessage(" ", false, p, null);
                        chatFactory.sendPlayerMessage("Teleporting...", false, p, prefix);
                        chatFactory.sendPlayerMessage(" ", false, p, null);
                        removeMetadata();
                        this.cancel();
                    }else{
                        p.sendTitle("Teleporting you...", chatFactory.chat("&d&l") + (int)counter + "...", 5, 20, 5);
                    }
                    counter--;
                }else{
                    this.cancel();
                }
            }
        };

        BukkitTask t = br.runTaskTimer(plugin, 0L, 20L);
        final int id = t.getTaskId();

        setMetadata(id, cost);
    }

    private void setMetadata(int taskID, int cost){
        p.setMetadata("TP", new FixedMetadataValue(plugin, taskID));
        p.setMetadata("TPCOST", new FixedMetadataValue(plugin, cost));
    }

    private void removeMetadata(){
        p.removeMetadata("TP", plugin);
        p.removeMetadata("TPCOST", plugin);
    }

}

package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.IncompleteLocation;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class TeleportationsMenu extends MenuCreator implements Listener {

    public TeleportationsMenu(JavaPlugin plugin, String menuTitle, int numSlots) {
        super(plugin, menuTitle, numSlots);
        createMenu("TeleportationsMenu");
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public enum Teleportations{

        EMERALD(10, "Emerald", new IncompleteLocation(-561, 90, -473), null),
        DIAMOND_BLOCK(11, "DiamondBlock", new IncompleteLocation(-560, 89, -492), null),
        DIAMOND(12, "Diamond", new IncompleteLocation(-557, 85, -511), null),
        GOLD(13, "Gold" , new IncompleteLocation(-570, 68, -534), null),
        GOLD_BLOCK(14, "GoldBlock", new IncompleteLocation(-557, 78, -521), null),
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

    @Override
    public void initializeItems(Player player) {

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
        Economy econ = GeneratorPvP.getEconomy();
        double coins = econ.getBalance(p);
        double cost;
        Location teleLoc;

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
                    p.teleport(teleLoc);
                    chatFactory.sendPlayerMessage("Teleporting you...", true, p, prefix);
                }else{
                    chatFactory.sendPlayerMessage("You do not have enough coins! You need at least &a&l" + cost + "&r coins!", true, p, prefix);
                }
            }
        }

    }

}

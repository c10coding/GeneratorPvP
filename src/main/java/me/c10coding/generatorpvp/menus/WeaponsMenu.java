package me.c10coding.generatorpvp.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class WeaponsMenu extends MenuCreator implements Listener {

    public WeaponsMenu(JavaPlugin plugin, String menuTitle, int numSlots) {
        super(plugin, menuTitle, numSlots);
        createMenu("WeaponsMenu");
        fillMenu();
    }

    enum Weapons{
        KNOCKBACK(Material.SNOWBALL, "Knockback"),
        POSITION_SWAP(Material.SLIME_BALL, "PositionSwap"),
        TNT(Material.TNT, "TNT"),
        FIREBALL(Material.FIRE_CHARGE, "Fireball"),
        INSTA_KILL(Material.EGG, "InstaKill");

        private Material mat;
        private String configKey;
        Weapons(Material mat, String configKey){
            this.mat = mat;
            this.configKey = configKey;
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
        double playerBalance = econ.getBalance(p);
        double cost;
        Material mat;

        switch(slotClicked){
            case 9:
                cost = cm.getWeaponsCost(Weapons.KNOCKBACK.configKey);
                mat = Weapons.KNOCKBACK.mat;
                break;
            case 11:
                cost = cm.getWeaponsCost(Weapons.POSITION_SWAP.configKey);
                mat = Weapons.POSITION_SWAP.mat;
                break;
            case 13:
                cost = cm.getWeaponsCost(Weapons.TNT.configKey);
                mat = Weapons.TNT.mat;
                break;
            case 15:
                cost = cm.getWeaponsCost(Weapons.FIREBALL.configKey);
                mat = Weapons.FIREBALL.mat;
                break;
            case 17:
                cost = cm.getWeaponsCost(Weapons.INSTA_KILL.configKey);
                mat = Weapons.INSTA_KILL.mat;
                break;
            default:
                return;
        }

        if(playerBalance >= cost){
            econ.withdrawPlayer(p, cost);
            giveWeapon(p, mat);
            chatFactory.sendPlayerMessage("Here you go boss! Have fun with your new weapon", true, p, prefix);
        }else{
            chatFactory.sendPlayerMessage("Looks like you don't have enough coins for this weapon. Bummer (&c&l" + cost + "&r coins)", true, p, prefix);
        }

    }

    public void giveWeapon(Player p, Material mat){
        p.getInventory().addItem(new ItemStack(mat, 1));
    }


}

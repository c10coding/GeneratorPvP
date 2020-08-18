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

public class WeaponsMenu extends MenuCreator implements Listener {

    public WeaponsMenu(JavaPlugin plugin, Player p) {
        super((APIHook) plugin, "Weapons", 27, p);
        createMenu();
        fillMenu();
        setHasGivables(true);
    }

    public void createMenu(){

        List<Integer> menuSlots = cm.getSlots("WeaponsMenu");

        for(Integer i : menuSlots){

            String configKey = null;

            if(i == 9){
                configKey = "Knockback";
            }else if(i == 11){
                configKey = "PositionSwap";
            }else if(i == 13){
                configKey = "TNT";
            }else if(i == 15){
                configKey = "Fireball";
            }else if(i == 17){
                configKey = "InstaKill";
            }

            Map<String, Object> slotInfo = cm.getSlotInfo("WeaponsMenu", i);
            String displayName = (String) slotInfo.get("DisplayName");
            Material mat = (Material) slotInfo.get("Material");
            List<String> lore = (List<String>) slotInfo.get("Lore");
            int cost = (int) cm.getWeaponsCost(configKey);
            lore.add(chatFactory.colorString("&aCost: &6" + cost + " Coins"));
            lore = applyPlaceholders(lore);

            inv.setItem(i, createGuiItem(mat, displayName, 1, lore));
        }
    }

    enum Weapons{
        KNOCKBACK(Material.SNOWBALL, "Knockback", "Knockback Weapon"),
        POSITION_SWAP(Material.SLIME_BALL, "PositionSwap", "Position Swap Weapon"),
        TNT(Material.TNT, "TNT", "TNT"),
        FIREBALL(Material.FIRE_CHARGE, "Fireball", "Fireball Weapon"),
        INSTA_KILL(Material.EGG, "InstaKill", "Insta-Kill");

        private Material mat;
        private String configKey, displayName;
        Weapons(Material mat, String configKey, String displayName){
            this.mat = mat;
            this.configKey = configKey;
            this.displayName = displayName;
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
        String configKey;
        Weapons weapon;

        switch(slotClicked){
            case 9:
                weapon = Weapons.KNOCKBACK;
                cost = cm.getWeaponsCost(Weapons.KNOCKBACK.configKey);
                mat = Weapons.KNOCKBACK.mat;
                configKey = Weapons.KNOCKBACK.configKey;
                break;
            case 11:
                weapon = Weapons.POSITION_SWAP;
                cost = cm.getWeaponsCost(Weapons.POSITION_SWAP.configKey);
                mat = Weapons.POSITION_SWAP.mat;
                configKey = Weapons.POSITION_SWAP.configKey;
                break;
            case 13:
                weapon = Weapons.TNT;
                cost = cm.getWeaponsCost(Weapons.TNT.configKey);
                mat = Weapons.TNT.mat;
                configKey = Weapons.TNT.configKey;
                break;
            case 15:
                weapon = Weapons.FIREBALL;
                cost = cm.getWeaponsCost(Weapons.FIREBALL.configKey);
                mat = Weapons.FIREBALL.mat;
                configKey = Weapons.FIREBALL.configKey;
                break;
            case 17:
                weapon = Weapons.INSTA_KILL;
                cost = cm.getWeaponsCost(Weapons.INSTA_KILL.configKey);
                mat = Weapons.INSTA_KILL.mat;
                configKey = Weapons.KNOCKBACK.configKey;
                break;
            default:
                return;
        }

        if(playerBalance >= cost){
            ConfirmPurchaseMenu cpm = new ConfirmPurchaseMenu(plugin, p, mat, cost, configKey,this, 1, weapon.displayName);
            p.closeInventory();
            cpm.openInventory(p);
        }else{
            int amountMissing = (int) (cost - playerBalance);
            chatFactory.sendPlayerMessage(" ", false, p, null);
            chatFactory.sendPlayerMessage("&fYou are missing &6" + amountMissing + " Coins&f to purchase " + weapon.displayName + ".&f You can purchase more coins from &eStore.HeightsMC.com", false, p, prefix);
            chatFactory.sendPlayerMessage(" ", false, p, null);

        }

    }

    public void giveWeapon(Player p, Material mat){
        p.getInventory().addItem(new ItemStack(mat, 1));
    }


}

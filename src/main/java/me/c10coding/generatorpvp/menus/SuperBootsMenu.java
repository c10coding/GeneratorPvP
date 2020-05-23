package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SuperBootsMenu extends MenuCreator {


    public SuperBootsMenu(JavaPlugin plugin, String menuTitle, int numSlots, Player p) {
        super(plugin, menuTitle, numSlots, p);
        createMenu();
        fillerMat = Material.RED_STAINED_GLASS_PANE;
    }

    public enum SuperBoots{

        ANTI_FALL("Anti Fall", "gp.purchase.antifall", "gp.unlock.antifall", true, Color.GRAY),
        STRENGTH("Strength", "gp.purchase.strength", "gp.unlock.strength", false, Color.RED),
        REGEN("Regen", "gp.purchase.regen", "gp.unlock.regen", true, Color.fromBGR(255,192,203)),
        GLOWING("Glowing", "gp.purchase.glowing", "gp.unlock.glowing", true, Color.YELLOW),

        SPEED("Speed", "gp.purchase.speed", "gp.unlock.speed", true, Color.BLUE),
        JUMP_BOOST("Jump Boost", "gp.purchase.jumpboost", "gp.unlock.jumpboost", false, Color.LIME),
        DOUBLE_JUMP("Double Jump", "gp.purchase.doublejump", "gp.unlock.doublejump", true, Color.fromBGR(0,100,0)),
        ABSORPTION("Absorption", "gp.purchase.absorption", "gp.unlock.absorption", true, Color.fromBGR(139,0,0)),
        BLINDNESS("Blindness", "gp.purchase.blindness", "gp.unlock.blindness", true, Color.BLACK),

        COIN("Coin", "gp.purchase.coin", "gp.unlock.coin", false, Color.fromBGR(212,175,55)),
        ANTI_KB("AntiKB", "gp.purchase.antikb", "gp.unlock.antikb", true, Color.BLACK),
        STONKS("Stonks", "gp.purchase.stonks", "gp.unlock.stonks", true, Color.fromBGR(212,175,55)),
        LEVITATION("Levitation", "gp.purchase.levitation", "gp.unlock.levitation", true, Color.WHITE),
        INVISIBILITY("Invisibility", "gp.purchase.invisibility", "gp.unlock.invisibility", false, Color.WHITE);

        private String unlockPermission;
        private String purchasePermission;
        private boolean isPurchasable;
        private String configKey;
        private Color colorOfArmor;
        SuperBoots(String configKey, String purchasePermission,  String unlockPermission, boolean isPurchasable, Color colorOfArmor){
            this.configKey = configKey;
            this.unlockPermission = unlockPermission;
            this.purchasePermission = purchasePermission;
            this.isPurchasable = isPurchasable;
            this.colorOfArmor = colorOfArmor;
        }

    }

    public void createMenu(){
        for(int column = 0; column < 9; column++){
            if(column % 2 == 0){
                fillerMat = Material.RED_STAINED_GLASS_PANE;
            }else{
                fillerMat = Material.DIAMOND_BOOTS;
            }
        }
    }

    public void fillColumn(int columnNum){
        List<Integer> indexes = GPUtils.getIndexesInColumn(inv, columnNum, 3);
        for(Integer index : indexes){
            inv.setItem(index, createGuiItem());
        }
    }


    @Override
    protected void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

}

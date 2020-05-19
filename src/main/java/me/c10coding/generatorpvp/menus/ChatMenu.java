package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.EmptyEnchant;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatMenu extends MenuCreator implements Listener {

    private EquippedConfigManager ecm;
    private Player p;

    public ChatMenu(JavaPlugin plugin, String menuTitle, int numSlots, Player p) {
        super(plugin, menuTitle, numSlots);
        this.ecm = new EquippedConfigManager(plugin, p.getUniqueId());
        this.p = p;
        createMenu();
        fillMenu();
    }

    enum ChatColors{

        GRAY("Gray"),
        GREEN("Green"),
        YELLOW("Yellow"),
        BLUE("Blue"),
        ORANGE("Orange"),
        PINK("Pink");

        private String configKey;
        ChatColors(String configKey){
            this.configKey = configKey;
        }
    }

    public void createMenu(){

        List<Integer> menuSlots = cm.getSlots("ChatMenu");

        for(Integer i : menuSlots){

            Map<String, Object> slotInfo = cm.getSlotInfo("ChatMenu", i);
            String displayName = (String) slotInfo.get("DisplayName");
            Material mat = (Material) slotInfo.get("Material");
            List<String> lore = (List<String>) slotInfo.get("Lore");
            Map<Enchantment, Integer> enchants = new HashMap<>();
            enchants.put(((GeneratorPvP)plugin).empty, 1);

            for(ChatColors c : ChatColors.values()){
                if(ecm.isEquipped(c.configKey, "Chat")){
                    inv.setItem(i, createGuiItem(mat, chatFactory.chat(displayName), enchants, 1, lore));
                }else{
                    if(ecm.isPurchased(c.configKey, "Chat")){
                        inv.setItem(i, createGuiItem(mat, chatFactory.chat(displayName + " &a&l[Bought]"), 1, lore));
                    }else{
                        inv.setItem(i, createGuiItem(mat, chatFactory.chat(displayName), 1, lore));
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
        double playerBalance = econ.getBalance(p);
        double cost;
        String wantedKey = null;

        switch(slotClicked){
            case 10:
                wantedKey = ChatColors.GRAY.configKey;
        }

        if(!ecm.isPurchased(wantedKey,"Chat")){
            Bukkit.broadcastMessage("Here!");
        }else{
            ecm.setEquipped(wantedKey, "Chat", true);
            Bukkit.broadcastMessage("Here2!");
        }

        ecm.saveConfig();

    }

}

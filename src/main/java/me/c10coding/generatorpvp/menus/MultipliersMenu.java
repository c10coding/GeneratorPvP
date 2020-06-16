package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class MultipliersMenu extends AmplifiersMenu{

    public MultipliersMenu(JavaPlugin plugin, Player p) {
        super(plugin, p, "Multipliers");
    }

    @Override
    public void createMenu(){

        List<Integer> menuSlots = cm.getSlots("MultipliersMenu");

        for(Integer numSlot : menuSlots){

            Map<String, Object> slotInfo = cm.getSlotInfo("MultipliersMenu", numSlot);
            String displayName = (String) slotInfo.get("DisplayName");
            Material mat = (Material) slotInfo.get("Material");
            List<String> lore = (List<String>) slotInfo.get("Lore");
            ItemStack potion = new ItemStack(mat, 1);

            //Don't need to check if this is is an instance of potionMeta because it's guaranteed to be one in config.
            PotionMeta pm = (PotionMeta) potion.getItemMeta();
            applyFlags(pm);
            potion.setItemMeta(pm);

            String amplifierName = "Multipliers";
            int levelAmplifier = 0;

            if(numSlot == 10){
                levelAmplifier = 1;
            }else if(numSlot == 13){
                levelAmplifier = 2;
            }else if(numSlot == 16){
                levelAmplifier = 3;
            }

            lore.add(chatFactory.colorString("&cAmount: &7" + em.getAmplifierAmount(amplifierName, levelAmplifier)));
            displayName = replacePlaceholders(displayName);

            for(int x = 0; x < lore.size(); x++){
                lore.set(x, replacePlaceholders(lore.get(x)));
            }

            boolean hasOneOfAmplifier = em.ownsAtleastOne(amplifierName, levelAmplifier);

            inv.setItem(numSlot, createGuiItem(potion, displayName, 1, lore));
            fillMenu(hasOneOfAmplifier, numSlot);

        }
    }

    @Override
    public void fillMenu(boolean hasAtleastOneAmp, int numSlot){
        List<Integer> indexesAroundItem = GPUtils.getIndexesAroundItem(inv, 3, numSlot);
        if(hasAtleastOneAmp){
            fillerMat = Material.GREEN_STAINED_GLASS_PANE;
        }else{
            fillerMat = Material.RED_STAINED_GLASS_PANE;
        }
        for(int index : indexesAroundItem){
            inv.setItem(index, createGuiItem());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() != inv) return;
        e.setCancelled(true);
        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        int slotClicked = e.getSlot();
        int levelAmplifier = 0;
        Player playerClicked = (Player) e.getWhoClicked();

        switch(slotClicked){
            case 10:
                levelAmplifier = 1;
                break;
            case 13:
                levelAmplifier = 2;
                break;
            case 16:
                levelAmplifier = 3;
                break;
        }

        boolean hasAtleastOneAmplifier = em.ownsAtleastOne("Multipliers", levelAmplifier);
        boolean amplifierIsAlreadyActive = am.isAmplifierActivated("Multipliers");

        if(hasAtleastOneAmplifier){
            if(amplifierIsAlreadyActive){
                chatFactory.sendPlayerMessage(" ", false, p, null);
                chatFactory.sendPlayerMessage("&7There is already a &eMultiplier &7activated!", false, playerClicked, prefix);
                chatFactory.sendPlayerMessage(" ", false, p, null);
            }else{
                em.decreaseAmplifierAmount("Multipliers", levelAmplifier);
                em.saveConfig();
                activateAmplifier("Multipliers", levelAmplifier, playerClicked.getName());
            }
        }
    }
}

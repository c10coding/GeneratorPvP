package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class BoostersMenu extends AmplifiersMenu{
    public BoostersMenu(JavaPlugin plugin, Player p) {
        super(plugin, p, "Boosters");
    }

    @Override
    public void createMenu(){

        List<Integer> menuSlots = cm.getSlots("BoostersMenu");

        for(Integer numSlot : menuSlots){

            Map<String, Object> slotInfo = cm.getSlotInfo("BoostersMenu", numSlot);
            String displayName = (String) slotInfo.get("DisplayName");
            Material mat = (Material) slotInfo.get("Material");
            List<String> lore = (List<String>) slotInfo.get("Lore");
            ItemStack potion = new ItemStack(mat, 1);

            //Don't need to check if this is is an instance of potionMeta because it's guaranteed to be one in config.
            PotionMeta pm = (PotionMeta) potion.getItemMeta();
            pm.setColor(Color.AQUA);
            applyFlags(pm);
            potion.setItemMeta(pm);

            String amplifierName = "Boosters";
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

        boolean hasAtleastOneAmplifier = em.ownsAtleastOne("Boosters", levelAmplifier);
        boolean amplifierIsAlreadyActive = am.isAmplifierActivated("Boosters");

        if(hasAtleastOneAmplifier){
            if(amplifierIsAlreadyActive){
                chatFactory.sendPlayerMessage(" ", false, p, null);
                chatFactory.sendPlayerMessage("&7There is already a &eBooster &7activated!", false, playerClicked, prefix);
                chatFactory.sendPlayerMessage(" ", false, p, null);
            }else{
                em.decreaseAmplifierAmount("Boosters", levelAmplifier);
                em.saveConfig();
                activateAmplifier("Boosters", levelAmplifier, playerClicked.getName());
            }
        }
    }
}

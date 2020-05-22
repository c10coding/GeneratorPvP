package me.c10coding.generatorpvp.menus;


import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmplifiersMenu extends MenuCreator {

    private AmplifiersConfigManager am;

    public AmplifiersMenu(JavaPlugin plugin, Player p) {
        super(plugin, "Amplifiers", 27, p);
        this.am = new AmplifiersConfigManager(plugin);
        createMenu();
        fillerMat = Material.RED_STAINED_GLASS_PANE;
    }

    enum Placeholders{
        BOOSTER_MULT("%boosterMult%", "Amplifier Info.Boosters.Multiplier");

        private String placeholder;
        private String configPath;
        Placeholders(String placeholder, String configPath){
            this.placeholder = placeholder;
            this.configPath = configPath;
        }

        public String getPlaceholder(){
            return placeholder;
        }

        public String getConfigValue(){
            return configPath;
        }

    }

    public void createMenu(){

        List<Integer> menuSlots = cm.getSlots("AmplifiersMenu");
        Map<Integer, String> amplifierInfo = new HashMap<>();

        for(Integer numSlot : menuSlots){

            Map<String, Object> slotInfo = cm.getSlotInfo("AmplifiersMenu", numSlot);
            String displayName = (String) slotInfo.get("DisplayName");
            Material mat = (Material) slotInfo.get("Material");
            List<String> lore = (List<String>) slotInfo.get("Lore");
            ItemStack potion = new ItemStack(mat, 1);
            //Don't need to check if this is is an instance of potionMeta because it's guaranteed to be one in config.
            PotionMeta pm = (PotionMeta) potion.getItemMeta();
            String amplifierName;

            if(numSlot == 13){
                pm.setColor(Color.AQUA);
                potion.setItemMeta(pm);
                amplifierName = "Boosters";
            }else if(numSlot == 16){
                pm.setColor(Color.fromBGR(229, 180, 255));
                potion.setItemMeta(pm);
                amplifierName = "Coin Multiplier";
            }else{
                potion = new ItemStack(mat, 1);
                amplifierName = "Multipliers";
            }

            boolean isActive = am.isAmplifierActivated(amplifierName);

            if(isActive){
                amplifierInfo.put(numSlot, amplifierName);
                displayName = chatFactory.chat(displayName + " &a&l[Active]");
            }else{
                displayName = chatFactory.chat(displayName + chatFactory.chat(" &c&l[Inactive]"));
            }

            for(int x = 0; x < lore.size(); x++){
                lore.set(x, replacePlaceholders(lore.get(x)));
            }

            inv.setItem(numSlot, createGuiItem(potion, displayName, 1, lore));
            fillMenu(isActive, numSlot);

        }

        new BukkitRunnable(){
            @Override
            public void run() {

                for(Map.Entry info : amplifierInfo.entrySet()){

                    am.reloadConfig();
                    int numSlot = (int) info.getKey();
                    Map<String, Object> slotInfo = cm.getSlotInfo("AmplifiersMenu", numSlot);
                    String amplifierName = (String) info.getValue();
                    String playerThatActivatedAmp = am.getWhoActivatedAmplifier(amplifierName);
                    ItemStack itemInSlot = inv.getItem(numSlot);
                    ItemMeta itemMeta = itemInSlot.getItemMeta();
                    List<String> lore = (List<String>) slotInfo.get("Lore");
                    int secondsLeft = am.getAmplifierSecondsLeft(amplifierName);

                    lore.add(chatFactory.chat("Activated by &b&l" + playerThatActivatedAmp));
                    lore.add(chatFactory.chat(GPUtils.secondsToSerializedTime(secondsLeft)));

                    for(int x = 0; x < lore.size(); x++){
                        lore.set(x, replacePlaceholders(lore.get(x)));
                    }

                    itemMeta.setLore(lore);
                    itemInSlot.setItemMeta(itemMeta);

                    inv.setItem(numSlot, itemInSlot);

                }

            }
        }.runTaskTimer(plugin, 0L, 20L);

    }

    private void fillMenu(boolean isActive, int indexOfAmplifier){
        if(isActive){
            fillerMat = Material.GREEN_STAINED_GLASS_PANE;
        }else{
            fillerMat = Material.RED_STAINED_GLASS_PANE;
        }
        List<Integer> indexesAroundItem = GPUtils.getIndexesAroundItem(inv, 3, indexOfAmplifier);
        for(int index : indexesAroundItem){
            inv.setItem(index, createGuiItem());
        }
    }

    private ItemStack createGuiItem(ItemStack potion, String displayName, int amount, List<String> lore) {

        ItemMeta meta = potion.getItemMeta();
        meta.setDisplayName(displayName);
        potion.setAmount(amount);
        ArrayList<String> metaLore = new ArrayList<>();

        if(lore.size() != 0) {
            for(String lorecomments : lore) {
                metaLore.add(lorecomments);
            }
            meta.setLore(metaLore);
        }else {
            meta.setLore(null);
        }

        potion.setItemMeta(meta);
        return potion;
    }

    public String replacePlaceholders(String s){
        String newString = s;
        for(Placeholders p : Placeholders.values()){
            if(s.contains(p.placeholder)){
                newString = newString.replace(p.placeholder, am.getConfig().getString(p.configPath));
            }
        }
        return newString;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if(clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        Player playerClicked = (Player) e.getWhoClicked();
        int slotClicked = e.getSlot();
    }



}

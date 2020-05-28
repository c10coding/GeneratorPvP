package me.c10coding.generatorpvp.menus;


import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
import me.c10coding.generatorpvp.files.GeneratorConfigManager;
import me.c10coding.generatorpvp.managers.AnnouncementsManager;
import me.c10coding.generatorpvp.managers.Generator;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmplifiersMenu extends MenuCreator {

    protected AmplifiersConfigManager am;
    protected EquippedConfigManager em;

    public AmplifiersMenu(JavaPlugin plugin, Player p, String menuTitle) {
        super(plugin, menuTitle, 27, p);
        this.am = new AmplifiersConfigManager(plugin);
        this.em = new EquippedConfigManager(plugin, p.getUniqueId());
        createMenu();
    }

    enum Placeholders{

        BOOSTER_MULT("%boosterMult%", "Amplifier Info.Boosters.Multiplier"),
        COINS_MULT("%coinsMult%", "Amplifier Info.Coin Multiplier.Multiplier"),
        MULTIPLIERS_MULT("%multiplierMult%", "Amplifier Info.Multipliers.Multiplier"),

        MULT_LENGTH_1("%multLength1%", "Amplifier Info.Multipliers.1.Duration"),
        MULT_LENGTH_2("%multLength2%", "Amplifier Info.Multipliers.2.Duration"),
        MULT_LENGTH_3("%multLength3%", "Amplifier Info.Multipliers.3.Duration"),

        BOOST_LENGTH_1("%boostLength1%", "Amplifier Info.Boosters.1.Duration"),
        BOOST_LENGTH_2("%boostLength2%", "Amplifier Info.Boosters.2.Duration"),
        BOOST_LENGTH_3("%boostLength3%", "Amplifier Info.Boosters.3.Duration"),

        COINS_MULT_1("%coinsMultLength1%", "Amplifier Info.Coin Multiplier.1.Duration"),
        COINS_MULT_2("%coinsMultLength2%", "Amplifier Info.Coin Multiplier.2.Duration"),
        COINS_MULT_3("%coinsMultLength3%", "Amplifier Info.Coin Multiplier.3.Duration");

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
            applyFlags(pm);
            String amplifierName;

            if(numSlot == 13){
                pm.setColor(Color.AQUA);
                amplifierName = "Boosters";
            }else if(numSlot == 16){
                pm.setColor(Color.fromBGR(229, 180, 255));
                amplifierName = "Coin Multiplier";
            }else{
                potion = new ItemStack(mat, 1);
                amplifierName = "Multipliers";
            }
            potion.setItemMeta(pm);

            boolean isActive = am.isAmplifierActivated(amplifierName);

            if(isActive){
                amplifierInfo.put(numSlot, amplifierName);
                lore.add(chatFactory.chat("&aActive"));
            }else{
                lore.add(chatFactory.chat("&cInactive"));
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
                    applyFlags(itemMeta);
                    List<String> lore = (List<String>) slotInfo.get("Lore");
                    int secondsLeft = am.getAmplifierSecondsLeft(amplifierName);

                    lore.add(chatFactory.chat("&7Activated by &c" + playerThatActivatedAmp));
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

    void fillMenu(boolean isActive, int indexOfAmplifier){
        List<Integer> indexesAroundItem = GPUtils.getIndexesAroundItem(inv, 3, indexOfAmplifier);
        if(isActive){
            fillerMat = Material.GREEN_STAINED_GLASS_PANE;
        }else{
            fillerMat = Material.RED_STAINED_GLASS_PANE;
        }
        for(int index : indexesAroundItem){
            inv.setItem(index, createGuiItem());
        }
    }

    public String replacePlaceholders(String s){
        String newString = s;
        for(Placeholders p : Placeholders.values()){
            if(newString.contains(p.placeholder)){
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

        int slotClicked = e.getSlot();

        MenuCreator newMenu;

        switch(slotClicked){
            case 10:
                newMenu = new MultipliersMenu(plugin, p);
                break;
            case 13:
                newMenu = new BoostersMenu(plugin, p);
                break;
            case 16:
                newMenu = new CoinsMultMenu(plugin, p);
                break;
            default:
                return;
        }

        p.closeInventory();
        newMenu.openInventory(p);

    }

    public void applyFlags(ItemMeta im){
        im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    public ItemStack createGuiItem(ItemStack potion, String displayName, int amount, List<String> lore) {

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

    public void activateAmplifier(String amplifierName, int levelAmplifier, String nameOfActivator){

        am.setWhoActivatedAmplifier(amplifierName, nameOfActivator);
        am.setAmplifierToActive(amplifierName);
        am.setAmplifierLevel(amplifierName, levelAmplifier);
        am.setAmplifierTimer(amplifierName, levelAmplifier);
        am.saveConfig();

        AnnouncementsManager announcementsManager = new AnnouncementsManager((GeneratorPvP) plugin);
        announcementsManager.announceAmplifierActivated(p.getName(), amplifierName, am.getAmplifierDuration(amplifierName, levelAmplifier));

        p.closeInventory();
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 10);
        p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 50);

        ((GeneratorPvP) plugin).restartGenerators();

    }



}

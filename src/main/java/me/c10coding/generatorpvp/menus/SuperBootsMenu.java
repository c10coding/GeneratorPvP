package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.bootEnchants.AntiFallEnchant;
import me.c10coding.generatorpvp.bootEnchants.EnchantmentKeys;
import me.c10coding.generatorpvp.bootEnchants.SuperBootEnchant;
import me.c10coding.generatorpvp.files.DefaultConfigBootsSectionManager;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SuperBootsMenu extends MenuCreator implements Listener {

    private DefaultConfigBootsSectionManager bm;

    public SuperBootsMenu(JavaPlugin plugin, Player p) {
        super(plugin, "Super Boots", 27, p);
        this.bm = new DefaultConfigBootsSectionManager(plugin);
        fillerMat = Material.RED_STAINED_GLASS_PANE;
        createMenu();
    }

    public enum SuperBoots{

        ANTI_FALL("Anti Fall", "gp.purchase.antifall", "gp.unlock.antifall", true, Color.GRAY, EnchantmentKeys.ANTI_FALL),
        STRENGTH("Strength", "gp.purchase.strength", "gp.unlock.strength", false, Color.RED, EnchantmentKeys.STRENGTH),
        REGEN("Regen", "gp.purchase.regen", "gp.unlock.regen", true, Color.fromBGR(255, 192, 250), EnchantmentKeys.REGEN),
        GLOWING("Glowing", "gp.purchase.glowing", "gp.unlock.glowing", true, Color.YELLOW, EnchantmentKeys.GLOWING),

        SPEED("Speed", "gp.purchase.speed", "gp.unlock.speed", true, Color.BLUE, EnchantmentKeys.SPEED),
        JUMP_BOOST("Jump Boost", "gp.purchase.jumpboost", "gp.unlock.jumpboost", false, Color.LIME, EnchantmentKeys.JUMP_BOOST),
        DOUBLE_JUMP("Double Jump", "gp.purchase.doublejump", "gp.unlock.doublejump", true, Color.fromBGR(0,100,0), EnchantmentKeys.DOUBLE_JUMP),
        ABSORPTION("Absorption", "gp.purchase.absorption", "gp.unlock.absorption", true, Color.fromBGR(125, 0, 0), EnchantmentKeys.ABSORPTION),
        BLINDNESS("Blindness", "gp.purchase.blindness", "gp.unlock.blindness", true, Color.BLACK, EnchantmentKeys.BLINDNESS),

        COIN("Coin", "gp.purchase.coin", "gp.unlock.coin", false, Color.fromBGR(197,179,88), EnchantmentKeys.COIN),
        ANTI_KB("AntiKB", "gp.purchase.antikb", "gp.unlock.antikb", true, Color.BLACK, EnchantmentKeys.ANTIKB),
        STONKS("Stonks", "gp.purchase.stonks", "gp.unlock.stonks", true, Color.fromBGR(197, 179, 88), EnchantmentKeys.STONKS),
        LEVITATION("Levitation", "gp.purchase.levitation", "gp.unlock.levitation", true, Color.WHITE, EnchantmentKeys.LEVITATION),
        INVISIBILITY("Invisibility", "gp.purchase.invisibility", "gp.unlock.invisibility", false, Color.WHITE, EnchantmentKeys.INVISIBILITY);

        private String unlockPermission;
        private String purchasePermission;
        private boolean isPurchasable;
        private String configKey;
        private Color colorOfArmor;
        private EnchantmentKeys enchantmentKey;
        SuperBoots(String configKey, String purchasePermission, String unlockPermission, boolean isPurchasable, Color colorOfArmor, EnchantmentKeys enchantmentKey){
            this.configKey = configKey;
            this.unlockPermission = unlockPermission;
            this.purchasePermission = purchasePermission;
            this.isPurchasable = isPurchasable;
            this.colorOfArmor = colorOfArmor;
            this.enchantmentKey = enchantmentKey;
        }

        public String getConfigKey(){
            return configKey;
        }

        public Color getColorOfArmor(){
            return colorOfArmor;
        }

        public EnchantmentKeys getEnchantmentKey(){
            return enchantmentKey;
        }

    }

    public void createMenu(){
        int bootCounter = 0;
        fillMenu();

        for(int x = 0; x < 27; x++){
            if(inv.getItem(x) == null){
                SuperBoots superboot = SuperBoots.values()[bootCounter];
                ItemStack boots = createBoots(superboot);
                inv.setItem(x, boots);
                bootCounter++;
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
    public void fillMenu(){
        inv.setItem(0, new ItemStack(Material.BARRIER, 1));
        fillerMat = Material.RED_STAINED_GLASS_PANE;
        for(int column = 0; column < 9; column++){
            if(column % 2 != 0 && column != 0){
                fillColumn(column);
            }
        }
    }

    private ItemStack addEmptyEnchantment(ItemMeta bootsMeta, ItemStack boots){
        bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
        boots.setItemMeta(bootsMeta);
        return boots;
    }

    private ItemStack createBoots(SuperBoots superBoot){

        String displayName = superBoot.configKey;
        int cost = (int) cm.getBootCost(superBoot.configKey);
        List<String> lore = cm.getBootsLore(superBoot.configKey);
        boolean isPurchaseAble = superBoot.isPurchasable;

        checkPlayerPermissions(superBoot, ecm.isPurchased(superBoot.configKey, "SuperBoots"));
        boolean isPurchased = ecm.isPurchased(superBoot.configKey, "SuperBoots");
        boolean isEquipped = ecm.isEquipped(superBoot.configKey, "SuperBoots");
        ItemStack boots = getBootType(isPurchased);

        if(p.hasPermission(superBoot.unlockPermission)){
            boots = getBootType(true);
        }

        ItemMeta bootMeta = boots.getItemMeta();
        bootMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        bootMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        if(isPurchaseAble && !isPurchased){
            lore.add("&7Cost: %cost%");
            lore = replaceCostPlaceholder(lore, cost);
        }

        lore = replaceRestOfPlaceholders(lore, superBoot.configKey);

        if(isPurchased || p.hasPermission(superBoot.unlockPermission)){
            if(isEquipped){
                boots = addEmptyEnchantment(bootMeta, boots);
                bootMeta.setDisplayName(chatFactory.chat(displayName + " &e[Equipped]"));
            }else{
                bootMeta.setDisplayName(chatFactory.chat(displayName + " &a[Purchased]"));
            }
        }else{
            if(isPurchaseAble){
                bootMeta.setDisplayName(chatFactory.chat(displayName + " &c[Not Purchased]"));
            }else{
                bootMeta.setDisplayName(chatFactory.chat(displayName + " &f[&bBuy&eCraft &7Only&f]"));
            }
        }

        lore = GPUtils.colorLore(lore);
        bootMeta.setLore(lore);
        boots.setItemMeta(bootMeta);

        return boots;

    }

    private List<String> replaceCostPlaceholder(List<String> lore, int cost){
        for(int x = 0; x < lore.size(); x++){
            if(lore.get(x).contains("%cost%")){
                lore.set(x, chatFactory.chat(lore.get(x).replace("%cost%", "&c" + cost)));
            }
        }
        return lore;
    }

    private List<String> replaceRestOfPlaceholders(List<String> lore, String superBoot){
        List<String> placeholders = Arrays.asList("%duration%", "%cooldown%", "%blindnessBlockRange%", "%glowBlockRange%", "%absorptionAmount%", "%level%");
        for(String line : lore){
            for(String placeholder : placeholders){
                if(line.contains(placeholder)){
                    int replacer;
                    switch(placeholder){
                        case "%duration%":
                            replacer = bm.getBootsProperty(superBoot, DefaultConfigBootsSectionManager.SuperBootsProperty.DURATION);
                            break;
                        case "%cooldown%":
                            replacer = bm.getBootsProperty(superBoot, DefaultConfigBootsSectionManager.SuperBootsProperty.COOLDOWN);
                            break;
                        case "%blindnessBlockRange%":
                            replacer = bm.getBootsProperty(superBoot, DefaultConfigBootsSectionManager.SuperBootsProperty.BLINDNESS_BLOCK_RANGE);
                            break;
                        case "%glowBlockRange%":
                            replacer = bm.getBootsProperty(superBoot, DefaultConfigBootsSectionManager.SuperBootsProperty.GLOW_BLOCK_RANGE);
                            break;
                        case "%absorptionAmount%":
                            replacer = bm.getBootsProperty(superBoot, DefaultConfigBootsSectionManager.SuperBootsProperty.EXTRA_HEART_AMOUNT);
                            break;
                        case "%level%":
                            replacer = bm.getBootsProperty(superBoot, DefaultConfigBootsSectionManager.SuperBootsProperty.LEVEL);
                            break;
                        default:
                            replacer = 0;
                            break;
                    }

                    int index = lore.indexOf(line);
                    String newValue = String.valueOf(replacer);

                    lore.set(index, line.replace(placeholder, newValue));
                }
            }
        }
        return lore;
    }

    private ItemStack getBootType(boolean isPurchased){
        if(isPurchased){
            return new ItemStack(Material.DIAMOND_BOOTS, 1);
        }else{
            return new ItemStack(Material.LEATHER_BOOTS);
        }
    }

    @EventHandler
    @Override
    protected void onInventoryClick(InventoryClickEvent e) {
        if(e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if(clickedItem == null || clickedItem.getType().equals(Material.AIR) || clickedItem.getType().equals(Material.RED_STAINED_GLASS_PANE)) return;

        int slotClicked = e.getSlot();

        SuperBoots superBoot;
        switch(slotClicked){
            case 0:
                if(ecm.hasSomethingEquipped("SuperBoots")){
                    String bootsEquipped = ecm.getThingEquipped("SuperBoots");
                    int slotEquipped = getSlotEquipped();
                    setSlotToNormal(slotEquipped);
                    ecm.setEquipped(bootsEquipped, "SuperBoots", false);
                    ecm.saveConfig();
                    p.getInventory().setBoots(null);
                    p.closeInventory();
                    chatFactory.sendPlayerMessage("You have unequipped your boots!", true, p, prefix);
                    removePotions();
                    removeFlight();
                    removeExtraHealth();
                }
                return;
            case 2:
                superBoot = SuperBoots.ANTI_FALL;
                break;
            case 4:
                superBoot = SuperBoots.STRENGTH;
                break;
            case 6:
                superBoot = SuperBoots.REGEN;
                break;
            case 8:
                superBoot = SuperBoots.GLOWING;
                break;
            case 9:
                superBoot = SuperBoots.SPEED;
                break;
            case 11:
                superBoot = SuperBoots.JUMP_BOOST;
                break;
            case 13:
                superBoot = SuperBoots.DOUBLE_JUMP;
                break;
            case 15:
                superBoot = SuperBoots.ABSORPTION;
                break;
            case 17:
                superBoot = SuperBoots.BLINDNESS;
                break;
            case 18:
                superBoot = SuperBoots.COIN;
                break;
            case 20:
                superBoot = SuperBoots.ANTI_KB;
                break;
            case 22:
                superBoot = SuperBoots.STONKS;
                break;
            case 24:
                superBoot = SuperBoots.LEVITATION;
                break;
            case 26:
                superBoot = SuperBoots.INVISIBILITY;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + slotClicked);
        }

        String configKey = superBoot.configKey;
        String purchasePermission = superBoot.purchasePermission;
        String unlockPermission = superBoot.unlockPermission;

        int cost = (int) cm.getBootCost(configKey);
        int playerBalance = (int) econ.getBalance(p);

        if(!ecm.isPurchased(configKey,"SuperBoots") && superBoot.isPurchasable){

            if(playerBalance >= cost) {

                if(p.hasPermission(purchasePermission)){
                    ConfirmPurchaseMenu cpm = new ConfirmPurchaseMenu(plugin, p, Material.LEATHER_BOOTS, cost, configKey,this, 1);
                    p.closeInventory();
                    cpm.openInventory(p);
                }else{
                    chatFactory.sendPlayerMessage("You don't have permissions to do that!", true, p, prefix);
                    p.closeInventory();
                }

            }else{
                chatFactory.sendPlayerMessage("You're too broke for that. Go kill some people to get more coins!", true, p, prefix);
                p.closeInventory();
            }

        }else{

            if(!superBoot.isPurchasable && !p.hasPermission(unlockPermission)){
                chatFactory.sendPlayerMessage("This is not purchase-able! You must buy a rank to unlock this", true, p, prefix);
                p.closeInventory();
            }

            if(ecm.isPurchased(configKey, "SuperBoots")){
            /*
                Sets the thing that was equipped to it's regular self
            */
                if(ecm.hasSomethingEquipped("SuperBoots")){
                    int slotEquipped = getSlotEquipped();
                    if(slotEquipped != slotClicked){
                        setSlotToNormal(slotEquipped);
                        removePotions();
                        removeFlight();
                        removeExtraHealth();
                    }else{
                        return;
                    }
                }

                setSlotToEquipped(superBoot, slotClicked);
                ecm.setEquipped(configKey, "SuperBoots");
                chatFactory.sendPlayerMessage("You have equipped &e" + configKey + " boots!", true, p, prefix);

                ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
                Color colorOfArmor = superBoot.colorOfArmor;
                ChatColor chatColor = GPUtils.matchArmorColorWithChatColor(colorOfArmor);

                bootsMeta.setDisplayName(chatFactory.chat(chatColor + superBoot.configKey));
                bootsMeta.setColor(colorOfArmor);
                bootsMeta.addEnchant(Enchantment.getByKey(new NamespacedKey(plugin, superBoot.enchantmentKey.toString())), 1, false);
                bootsMeta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
                bootsMeta.setUnbreakable(true);
                bootsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                boots.setItemMeta(bootsMeta);
                p.getInventory().setBoots(boots);

                if(superBoot.equals(SuperBoots.REGEN)){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,Integer.MAX_VALUE, bm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.LEVEL) - 1));
                }else if(superBoot.equals(SuperBoots.STRENGTH)){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,Integer.MAX_VALUE, bm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.LEVEL) - 1));
                }else if(superBoot.equals(SuperBoots.DOUBLE_JUMP)){
                    p.setAllowFlight(true);
                }else if(superBoot.equals(SuperBoots.ABSORPTION)){
                    double maxHealthAdditive = bm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.EXTRA_HEART_AMOUNT);
                    p.setMaxHealth(p.getMaxHealth() + maxHealthAdditive);
                    p.setHealth(p.getMaxHealth());
                }

                p.closeInventory();
            }
            ecm.saveConfig();
        }

    }

    private int getSlotEquipped(){
        for(int x = 0; x < 27; x++){
            ItemStack slotItem = inv.getItem(x);
            if(slotItem.getItemMeta().hasEnchants()){
                return x;
            }
        }
        return 0;
    }

    private void setSlotToNormal(int slotEquipped){
        String bootsEquipped = ecm.getThingEquipped("SuperBoots");
        SuperBoots boot = null;
        for(SuperBoots b : SuperBoots.values()){
            if(b.configKey.equalsIgnoreCase(bootsEquipped)){
                boot = b;
                break;
            }
        }
        inv.setItem(slotEquipped, createBoots(boot));
    }

    private void setSlotToEquipped(SuperBoots b, int slotClicked){
        ItemStack boots = createBoots(b);
        boots = addEmptyEnchantment(boots.getItemMeta(),boots);
        inv.setItem(slotClicked, boots);
    }

    public void checkPlayerPermissions(SuperBoots sb, boolean isPurchased){

        if(!p.hasPermission(sb.unlockPermission) && isPurchased && !sb.isPurchasable){
            ecm.setPurchased(sb.configKey, "SuperBoots", false);
            if(ecm.isEquipped(sb.configKey, "SuperBoots")){
                ecm.setEquipped(sb.configKey, "SuperBoots", false);
            }
        }else{

            if(p.hasPermission(sb.unlockPermission)){
                ecm.setPurchased(sb.configKey, "SuperBoots", true);
            }

        }
        ecm.saveConfig();
    }

    private void removePotions(){
        Collection<PotionEffect> potionEffects = p.getActivePotionEffects();
        for(PotionEffect pe : potionEffects){
            p.removePotionEffect(pe.getType());
        }
    }

    private void removeFlight(){
        p.setAllowFlight(false);
    }

    private void removeExtraHealth(){
        p.setMaxHealth(10);
        p.setHealth(p.getMaxHealth());
    }

}

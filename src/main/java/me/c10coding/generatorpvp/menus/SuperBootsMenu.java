package me.c10coding.generatorpvp.menus;

import me.c10coding.generatorpvp.bootEnchants.AntiFallEnchant;
import me.c10coding.generatorpvp.bootEnchants.EnchantmentKeys;
import me.c10coding.generatorpvp.bootEnchants.SuperBootEnchant;
import me.c10coding.generatorpvp.files.DefaultConfigBootsSectionManager;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
        ANTI_KB("AntiKB", "gp.purchase.antikb", "gp.unlock.antikb", true, Color.GRAY, EnchantmentKeys.ANTIKB),
        STONKS("Stonks", "gp.purchase.stonks", "gp.unlock.stonks", true, Color.fromBGR(197, 179, 88), EnchantmentKeys.STONKS),
        LEVITATION("Levitation", "gp.purchase.levitation", "gp.unlock.levitation", true, Color.PURPLE, EnchantmentKeys.LEVITATION),
        INVISIBILITY("Invisibility", "gp.purchase.invisibility", "gp.unlock.invisibility", true, Color.WHITE, EnchantmentKeys.INVISIBILITY);

        private String unlockPermission;
        private String purchasePermission;
        private boolean isPurchasable;
        private String configKey;
        private Color colorOfArmor;
        private EnchantmentKeys enchantmentKey;
        private String displayName;
        SuperBoots(String configKey, String purchasePermission, String unlockPermission, boolean isPurchasable, Color colorOfArmor, EnchantmentKeys enchantmentKey){
            this.configKey = configKey;
            this.unlockPermission = unlockPermission;
            this.purchasePermission = purchasePermission;
            this.isPurchasable = isPurchasable;
            this.colorOfArmor = colorOfArmor;
            this.enchantmentKey = enchantmentKey;
            this.displayName = GPUtils.matchArmorColorWithChatColor(colorOfArmor) + GPUtils.enumToName(this) + " Boots";
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
        ChatColor colorCode = GPUtils.matchArmorColorWithChatColor(superBoot.colorOfArmor);

        if(p.hasPermission(superBoot.unlockPermission)){
            boots = getBootType(true);
        }

        ItemMeta bootMeta = boots.getItemMeta();
        bootMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        bootMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        if(isPurchaseAble && !isPurchased){
            lore.add("&aCost: &6" + cost + " Coins");
            lore = replaceCostPlaceholder(lore, cost);
        }

        lore = replaceRestOfPlaceholders(lore, superBoot.configKey);
        bootMeta.setDisplayName(chatFactory.colorString(colorCode + displayName));

        if(isPurchased || p.hasPermission(superBoot.unlockPermission)){
            if(isEquipped){
                boots = addEmptyEnchantment(bootMeta, boots);
                lore.add(chatFactory.colorString("&aEquipped"));
            }else{
                lore.add(chatFactory.colorString("&aPurchased"));
            }
        }else{
            if(isPurchaseAble){
                lore.add(chatFactory.colorString("&cNot Purchased"));
            }else{
                lore.add(chatFactory.colorString("&bYou can purchase these boots from &eStore.HeightsMC.com"));
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
                lore.set(x, chatFactory.colorString(lore.get(x).replace("%cost%", "&c" + cost)));
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

        if(!GPUtils.isPlayerInSpawn((Player)e.getWhoClicked())){
            chatFactory.sendPlayerMessage("You can only do this in &espawn!", false, p, prefix);
            return;
        }

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

                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    chatFactory.sendPlayerMessage("You have unequipped your boots!", false, p, prefix);
                    chatFactory.sendPlayerMessage(" ", false, p, null);

                    removePotions();
                    removeFlight();
                    removeExtraHealth();

                    p.setLevel(0);
                    p.setExp(0);

                    if(p.hasMetadata("GlowingPlayers")){
                        p.removeMetadata("GlowingPlayers", plugin);
                    }

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
                    ConfirmPurchaseMenu cpm = new ConfirmPurchaseMenu(plugin, p, Material.LEATHER_BOOTS, cost, configKey,this, 1,  superBoot.displayName);
                    p.closeInventory();
                    cpm.openInventory(p);
                }else{
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    chatFactory.sendPlayerMessage("You don't have permissions to do that!", false, p, prefix);
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    p.closeInventory();
                }

            }else{
                int amountMissing = cost - playerBalance;
                chatFactory.sendPlayerMessage(" ", false, p, null);
                chatFactory.sendPlayerMessage("&fYou are missing &6" + amountMissing + " Coins&f to purchase " + superBoot.displayName + " Boots" + ".&f You can purchase more coins from &eStore.HeightsMC.com", false, p, prefix);
                chatFactory.sendPlayerMessage(" ", false, p, null);
                p.closeInventory();
            }

        }else{

            if(!superBoot.isPurchasable && !p.hasPermission(unlockPermission)){
                chatFactory.sendPlayerMessage(" ", false, p, null);
                chatFactory.sendPlayerMessage("&bYou can only purchase these boots from &eStore&f.&eHeightsMC&f.&ecom", false, p, prefix);
                chatFactory.sendPlayerMessage(" ", false, p, null);
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

                        if(p.hasMetadata("GlowingPlayers")){
                            p.removeMetadata("GlowingPlayers", plugin);
                        }

                    }else{
                        return;
                    }
                }

                p.setLevel(0);
                p.setExp(0);
                setSlotToEquipped(superBoot, slotClicked);
                ecm.setEquipped(configKey, "SuperBoots");
                chatFactory.sendPlayerMessage(" ", false, p, null);
                chatFactory.sendPlayerMessage("&fYou have equipped &e" +  superBoot.displayName, false, p, prefix);
                chatFactory.sendPlayerMessage(" ", false, p, null);

                if(bm.getBootsProperty(superBoot.getConfigKey(), DefaultConfigBootsSectionManager.SuperBootsProperty.DURATION) != 0){
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                    chatFactory.sendPlayerMessage("&cCrouch &fto active its powers", false, p, prefix);
                    chatFactory.sendPlayerMessage(" ", false, p, null);
                }

                ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
                Color colorOfArmor = superBoot.colorOfArmor;
                ChatColor chatColor = GPUtils.matchArmorColorWithChatColor(colorOfArmor);

                bootsMeta.setDisplayName(chatFactory.colorString(chatColor + superBoot.configKey));
                bootsMeta.setColor(colorOfArmor);
                bootsMeta.addEnchant(Enchantment.getByKey(new NamespacedKey(plugin, superBoot.enchantmentKey.toString())), 1, false);
                bootsMeta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
                bootsMeta.setUnbreakable(true);
                bootsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                boots.setItemMeta(bootsMeta);
                p.getInventory().setBoots(boots);

                if(superBoot.equals(SuperBoots.REGEN)){
                    p.setLevel(0);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,Integer.MAX_VALUE, bm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.LEVEL) - 1));
                }else if(superBoot.equals(SuperBoots.STRENGTH)){
                    p.setLevel(0);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,Integer.MAX_VALUE, bm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.LEVEL) - 1));
                }else if(superBoot.equals(SuperBoots.DOUBLE_JUMP)){
                    p.setAllowFlight(true);
                    p.setLevel(0);
                }else if(superBoot.equals(SuperBoots.ABSORPTION)) {
                    double maxHealthAdditive = bm.getBootsProperty(configKey, DefaultConfigBootsSectionManager.SuperBootsProperty.EXTRA_HEART_AMOUNT);
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20 + (maxHealthAdditive * 2));
                    p.setHealth(p.getMaxHealth());
                }else if(superBoot.equals(SuperBoots.STONKS) || superBoot.equals(SuperBoots.ANTI_FALL) || superBoot.equals(SuperBoots.COIN)){
                    p.setLevel(0);
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
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        p.setHealth(20);
    }

}

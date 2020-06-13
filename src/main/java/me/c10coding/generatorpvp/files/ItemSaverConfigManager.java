package me.c10coding.generatorpvp.files;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.coreapi.files.ConfigManager;
import me.c10coding.generatorpvp.utils.GPUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ItemSaverConfigManager extends ConfigManager {

    public ItemSaverConfigManager(JavaPlugin plugin) {
        super(plugin, "itemsaver.yml");
    }

    public void saveItem(ItemStack item, UUID u){

        UUID itemID = UUID.randomUUID();
        ItemMeta meta = item.getItemMeta();
        String displayName;
        List<String> lore;
        Material mat = item.getType();
        int amount = item.getAmount();
        displayName = meta.getDisplayName();

        if(meta.getLore() != null){
            lore = meta.getLore();
        }else{
            lore = new ArrayList<>();
        }

        config.set("Items." + u.toString() + "." + itemID.toString() + ".DisplayName", displayName);
        config.set("Items." + u.toString() + "." + itemID.toString() + ".Amount", amount);
        config.set("Items." + u.toString() + "." + itemID.toString() + ".Material", mat.toString());
        config.set("Items." + u.toString() + "." + itemID.toString() + ".Lore", lore);

        saveConfig();
    }

    public void giveItems(Player player){

        UUID u = player.getUniqueId();
        Chat chatFactory = new Chat();
        ConfigurationSection playerItemsIDs = config.getConfigurationSection("Items." + u.toString());

        for(String uuid : playerItemsIDs.getKeys(false)){

            String displayName = chatFactory.chat(config.getString("Items." + u.toString() + "." + uuid + ".DisplayName"));
            List<String> lore = config.getStringList("Items." + u.toString() + "." + uuid + ".Lore");
            int amount = config.getInt("Items." + u.toString() + "." + uuid + ".Amount");
            String materialString = config.getString("Items." + u.toString() + "." + uuid + ".Material");
            Material material = Material.valueOf(materialString);

            if(material.equals(Material.SNOWBALL)){
                displayName = chatFactory.chat("&fKnockback");
            }

            ItemStack itemToAdd = new ItemStack(material, amount);
            ItemMeta meta = itemToAdd.getItemMeta();
            lore = GPUtils.colorLore(lore);
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            itemToAdd.setItemMeta(meta);

            final Map<Integer, ItemStack> map = player.getInventory().addItem(itemToAdd);
            if(map.isEmpty()){
                chatFactory.sendPlayerMessage("&fItem given: &e" + displayName + " &fx &e" + amount, false, player, null);
                config.set("Items." + u.toString() + "." + uuid, null);
            }else{
                chatFactory.sendPlayerMessage("Could not give you the item &e" + displayName + ".&f Your inventory is full!", false, player, null);
            }

        }

        if(config.getConfigurationSection("Items." + u.toString()).getKeys(false).isEmpty()){
            config.set("Items." + u.toString(), null);
        }

        chatFactory.sendPlayerMessage("You were offline when given items. You have been given them", false, player, null);
        saveConfig();

    }

    public boolean hasItemsSaved(UUID u){
        return config.getString("Items." + u.toString()) != null;
    }

}

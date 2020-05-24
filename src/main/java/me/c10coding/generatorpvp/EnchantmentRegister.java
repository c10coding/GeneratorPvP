package me.c10coding.generatorpvp;

import me.c10coding.generatorpvp.bootEnchants.AntiFallEnchant;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnchantmentRegister {

    private List<Enchantment> enchantList = new ArrayList<>();
    private GeneratorPvP plugin;

    public EnchantmentRegister(GeneratorPvP plugin){
        this.plugin = plugin;
        compileEnchantmentList();
    }

    public void registerEnchantment(Enchantment ench) {
        //Using Reflection
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(ench);
        }catch(Exception e) {
            e.printStackTrace();
        }

        /*
        if(registered) {

        }*/
    }

    public void registerEnchantments(){
        for(Enchantment e : enchantList){
            registerEnchantment(e);
        }
    }

    public void unRegisterEnchantments(){
        try {
            Field keyField = Enchantment.class.getDeclaredField("byKey");

            keyField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);

            for(Enchantment e : enchantList) {
                byKey.remove(e.getKey());
            }

            Field nameField = Enchantment.class.getDeclaredField("byName");

            nameField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);

            for(Enchantment e : enchantList) {
                byName.remove(e.getName());
            }

        } catch (Exception ignored) { }
    }

    private void compileEnchantmentList(){
        enchantList.add(new AntiFallEnchant(plugin));
    }

}

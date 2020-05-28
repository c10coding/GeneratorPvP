package me.c10coding.generatorpvp.bootEnchants;

import com.google.common.collect.Maps;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import net.minecraft.server.v1_15_R1.DataWatcher;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Map;

public class GlowingEnchant extends SuperBootEnchant implements Listener{

    public GlowingEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.GLOWING, null, plugin, SuperBootsMenu.SuperBoots.GLOWING);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

}

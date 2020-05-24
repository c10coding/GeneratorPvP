package me.c10coding.generatorpvp.bootEnchants;

import com.google.common.collect.Maps;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.SuperBootsMenu;
import net.minecraft.server.v1_15_R1.DataWatcher;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;

public class GlowingEnchant extends SuperBootEnchant{

    public GlowingEnchant(GeneratorPvP plugin) {
        super(EnchantmentKeys.GLOWING, null, plugin, SuperBootsMenu.SuperBoots.GLOWING);
    }

    @SuppressWarnings("unchecked")
    public void setGlowing(Player glowingPlayer, Player sendPacketPlayer, boolean glow) {
        try {
            EntityPlayer entityPlayer = ((CraftPlayer) glowingPlayer).getHandle();

            DataWatcher toCloneDataWatcher = entityPlayer.getDataWatcher();
            DataWatcher newDataWatcher = new DataWatcher(entityPlayer);

            // The map that stores the DataWatcherItems is private within the DataWatcher Object.
            // We need to use Reflection to access it from Apache Commons and change it.
            Map<Integer, DataWatcher.Item<?>> currentMap = (Map<Integer, DataWatcher.Item<?>>) FieldUtils.readDeclaredField(toCloneDataWatcher, "d", true);
            Map<Integer, DataWatcher.Item<?>> newMap = Maps.newHashMap();

            // We need to clone the DataWatcher.Items because we don't want to point to those values anymore.
            for (Integer integer : currentMap.keySet()) {
                newMap.put(integer, currentMap.get(integer).d()); // Puts a copy of the DataWatcher.Item in newMap
            }

            // Get the 0th index for the BitMask value. http://wiki.vg/Entities#Entity
            DataWatcher.Item item = newMap.get(0);
            byte initialBitMask = (Byte) item.b(); // Gets the initial bitmask/byte value so we don't overwrite anything.
            byte bitMaskIndex = (byte) 6; // The index as specified in wiki.vg/Entities
            if (glow) {
                item.a((byte) (initialBitMask | 1 << bitMaskIndex));
            } else {
                item.a((byte) (initialBitMask & ~(1 << bitMaskIndex))); // Inverts the specified bit from the index.
            }

            // Set the newDataWatcher's (unlinked) map data
            FieldUtils.writeDeclaredField(newDataWatcher, "d", newMap, true);

            PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(glowingPlayer.getEntityId(), newDataWatcher, true);

            ((CraftPlayer) sendPacketPlayer).getHandle().playerConnection.sendPacket(metadataPacket);
        } catch (IllegalAccessException e) { // Catch statement necessary for FieldUtils.readDeclaredField()
            e.printStackTrace();
        }
    }

}

package me.c10coding.generatorpvp.listeners;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
import me.c10coding.generatorpvp.menus.ChatMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class GeneralListener implements Listener {

    private GeneratorPvP plugin;

    public GeneralListener(GeneratorPvP plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        EquippedConfigManager ecm = new EquippedConfigManager(plugin, p.getUniqueId());
        if(ecm.isInFile()){
            ecm.addPlayerToFile();
            ecm.saveConfig();
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){
        Player messenger = e.getPlayer();
        EquippedConfigManager ecm = new EquippedConfigManager(plugin, messenger.getUniqueId());
        Chat chatFactory = plugin.getApi().getChatFactory();

        if(ecm.hasSomethingEquipped("Chat")){
            String configKey = ecm.getThingEquipped("Chat");
            ChatMenu.ChatColors chatColor = getEquippedChatColor(configKey);
            String colorCode = chatColor.getColorCode();
            String msg = e.getMessage();
            e.setMessage(chatFactory.chat(colorCode + msg));
        }

    }

    private ChatMenu.ChatColors getEquippedChatColor(String configKey){
        ChatMenu.ChatColors chatColor = ChatMenu.ChatColors.GRAY;
        for(ChatMenu.ChatColors color : ChatMenu.ChatColors.values()){
            if(configKey.equalsIgnoreCase(color.getConfigKey())){
                return color;
            }
        }
        return chatColor;
    }

}

package me.c10coding.generatorpvp.commands;

import me.c10coding.coreapi.chat.ChatFactory;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.MenuCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand implements CommandExecutor {

    private GeneratorPvP plugin;
    private ChatFactory chatFactory;

    public MenuCommand(GeneratorPvP plugin){
        this.plugin = plugin;
        this.chatFactory = plugin.getAPI().getChatFactory();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(sender instanceof Player){
            Player p = (Player) sender;
            MenuCreator mm = new MenuCreator(plugin, "Menu", 27, p);
            mm.createMenu();
            mm.fillMenu();
            mm.openInventory(p);
        }else{
            chatFactory.sendPlayerMessage(" ", false, sender, null);
            chatFactory.sendPlayerMessage("Only players can run this command!", false, sender, plugin.getPrefix());
            chatFactory.sendPlayerMessage(" ", false, sender, null);
        }

        return false;
    }


}

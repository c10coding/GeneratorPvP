package me.c10coding.generatorpvp.commands;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.menus.MenuCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    private GeneratorPvP plugin;
    private Chat chatFactory;

    public Commands(GeneratorPvP plugin){
        this.plugin = plugin;
        this.chatFactory = plugin.getApi().getChatFactory();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(sender instanceof Player){
            Player p = (Player) sender;
            MenuCreator mm = new MenuCreator(plugin, "Menu", 27, p);
            mm.createMenu("MainMenu");
            mm.fillMenu();
            mm.openInventory(p);
        }else{
            chatFactory.sendPlayerMessage("Only players can run this command!", true, sender, plugin.getPrefix());
        }

        return false;
    }
}

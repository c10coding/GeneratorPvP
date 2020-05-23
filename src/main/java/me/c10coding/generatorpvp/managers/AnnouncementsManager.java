package me.c10coding.generatorpvp.managers;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.generatorpvp.GeneratorPvP;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class AnnouncementsManager {

    private GeneratorPvP plugin;
    private String announcerPrefix;
    private Chat chatFactory;

    public AnnouncementsManager(GeneratorPvP plugin){
        this.plugin = plugin;
        this.announcerPrefix = plugin.getConfig().getString("AnnouncerPrefix");
        this.chatFactory = plugin.getApi().getChatFactory();
    }

    public void announceAmplifierActivated(Player playerWhoActivated, String amplifierName, double durationAmplifier){
        Bukkit.broadcastMessage(chatFactory.chat(announcerPrefix + "&e" + playerWhoActivated.getName() + " &7has activated a &e" + removeSFromEndOfString(amplifierName) + " &7for &e" + durationAmplifier + "&7 hours!"));

        TextComponent clickablePart = new TextComponent(chatFactory.chat(announcerPrefix + "&eClick Here"));
        TextComponent message = new TextComponent(chatFactory.chat("&7 to thank them and get &6" + getThankRewardAmount() + " coins!"));

        clickablePart.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gp givecoins %playerName% " + getThankRewardAmount() + " " + playerWhoActivated.getName() + " " + amplifierName));
        clickablePart.addExtra(message);
        Bukkit.spigot().broadcast(clickablePart);

    }

    private int getThankRewardAmount(){
        return plugin.getConfig().getInt("ThankingRewardAmount");
    }

    private String removeSFromEndOfString(String s){
        return s.substring(0, s.length()-1);
    }

}

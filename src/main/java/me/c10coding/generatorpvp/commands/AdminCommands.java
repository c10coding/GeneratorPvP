package me.c10coding.generatorpvp.commands;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdminCommands implements CommandExecutor {

    private GeneratorPvP plugin;
    private Chat chatFactory;
    private String prefix;

    public AdminCommands(GeneratorPvP plugin){
        this.plugin = plugin;
        this.chatFactory = plugin.getApi().getChatFactory();
        this.prefix = plugin.getPrefix();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length > 0){
            //gp give amp <player name> <type> <level> <amount>
            if(args[0].equalsIgnoreCase("give") && args[1].equals("amp") && args.length == 6){
                int amount;

                String playerName = args[2];
                String amplifierType = args[3];
                List<String> possibleAmplifierTypes = getPossibleAmplifiers();
                int levelAmplifier;

                /*
                    I know this is bad practice, but I'm lazy
                 */
                try{
                    amount = Integer.parseInt(args[5]);
                }catch(IllegalArgumentException e){
                    chatFactory.sendPlayerMessage("This is not a valid amount. It must be a number!", true, sender, prefix);
                    return false;
                }

                try{
                    levelAmplifier = Integer.parseInt(args[4]);
                    if(levelAmplifier > 3){
                        chatFactory.sendPlayerMessage("The amplifier level can only be 3 or lower!", true, sender, prefix);
                        return false;
                    }
                }catch(IllegalArgumentException e){
                    chatFactory.sendPlayerMessage("This is not a valid level amplifier. It must be a number!", true, sender, prefix);
                    return false;
                }

                if(!possibleAmplifierTypes.contains(amplifierType)){
                    chatFactory.sendPlayerMessage("This is not a valid amplifier type! It has to be boost, mult, or coinmult", true, sender, prefix);
                    return false;
                }

                if(Bukkit.getPlayer(playerName) == null){
                    chatFactory.sendPlayerMessage("A player by the name " + playerName + " has bought an amplifier, but is offline right now. Can't give them the amplifier...", true, sender, prefix);
                }else{
                    chatFactory.sendPlayerMessage("Giving " + playerName + " " + amount + " " + amplifierType, true, sender, prefix);
                    EquippedConfigManager ecm = new EquippedConfigManager(plugin, Bukkit.getPlayer(playerName).getUniqueId());
                    ecm.increaseAmplifierAmount(amplifierType, levelAmplifier, amount);
                    ecm.saveConfig();
                }

            }else if(args[0].equalsIgnoreCase("givecoins") && args.length == 5){
                //Guaranteed to be a player because this command is only run when a player clicks on the "Click Here" text
                Player playerWhoClicked = (Player) sender;
                if(args[1].trim().equalsIgnoreCase("%playerName%")){
                    String playerName = sender.getName();
                    String playerWhoActivatedName = args[3];
                    String amplifierName = args[4];
                    Player activator;
                    AmplifiersConfigManager acm = new AmplifiersConfigManager(plugin);

                    if(playerName.equalsIgnoreCase(playerWhoActivatedName)){
                        chatFactory.sendPlayerMessage("You can't thank yourself silly!", true, sender, prefix);
                        return false;
                    }else{
                        if(acm.isOnThankfulPeopleList(playerName, amplifierName)){
                            chatFactory.sendPlayerMessage("&7You've already received your coins for thanking &e" + playerWhoActivatedName, true, playerWhoClicked, prefix);
                            return false;
                        }else{
                            rewardCoinsToClicker(playerWhoClicked);
                            acm.addToThankfulPeopleList(playerName, amplifierName);
                            acm.saveConfig();
                        }
                    }

                    if(Bukkit.getPlayer(playerWhoActivatedName) != null){
                        activator = Bukkit.getPlayer(playerWhoActivatedName);
                        rewardCoinsToActivator(activator);
                    }
                }
            }
        }
        return false;
    }

    private List<String> getPossibleAmplifiers(){
        List<String> possibleAmplifiers = new ArrayList<>();
        possibleAmplifiers.add("booster");
        possibleAmplifiers.add("coinmult");
        possibleAmplifiers.add("mult");
        return possibleAmplifiers;
    }

    public void rewardCoinsToClicker(Player playerWhoClicked){
        int rewardAmount = plugin.getConfig().getInt("ThankingRewardAmount");
        plugin.getEconomy().depositPlayer(playerWhoClicked, rewardAmount);
        chatFactory.sendPlayerMessage("&7You have received &e" + rewardAmount  + " &6Coins!", true, playerWhoClicked, prefix);
    }

    public void rewardCoinsToActivator(Player activator){
        int rewardAmount = plugin.getConfig().getInt("ActivatorRewardAmount");
        plugin.getEconomy().depositPlayer(activator, rewardAmount);
        chatFactory.sendPlayerMessage("&7You have received &e" + rewardAmount  + " &6Coins!", true, activator, prefix);
    }

}

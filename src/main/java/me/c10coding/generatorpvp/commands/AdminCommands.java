package me.c10coding.generatorpvp.commands;

import me.c10coding.coreapi.chat.Chat;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
import me.c10coding.generatorpvp.utils.GPUtils;
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

        if(args[0].equalsIgnoreCase("help") && args.length == 1){
            if(sender instanceof Player){
                GPUtils.sendCenteredMessage((Player)sender, "&e==============HELP==============");
                chatFactory.sendPlayerMessage("&6/menu &f- Brings up the main menu for GeneratorPvP", false, sender, null);
                chatFactory.sendPlayerMessage("&6/gp give coins <playername> <amount> &f- Gives the desired player a certain amount of coins.", false, sender, null);
                chatFactory.sendPlayerMessage("&6/gp give amp <player name> <booster | coinmult | mult> <level> <amount> &f- Gives the desired player a certain amount of an amplifier", false, sender, null);
                chatFactory.sendPlayerMessage("&6/lb &f- Brings up the leaderboard.", false, sender, null);
                chatFactory.sendPlayerMessage("&6/gp set coins &f- <playername> <amount>.", false, sender, null);
                GPUtils.sendCenteredMessage((Player)sender, "&e================================");
            }else{
                chatFactory.sendPlayerMessage("Only players can use this command!", false, sender, null);
            }
        }

        if(sender.isOp()){
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
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("This is not a valid amount. It must be a number!", true, sender, prefix);
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        return false;
                    }

                    try{
                        levelAmplifier = Integer.parseInt(args[4]);
                        if(levelAmplifier > 3){
                            chatFactory.sendPlayerMessage(" ", false, sender, null);
                            chatFactory.sendPlayerMessage("The amplifier level can only be 3 or lower!", true, sender, prefix);
                            chatFactory.sendPlayerMessage(" ", false, sender, null);
                            return false;
                        }
                    }catch(IllegalArgumentException e){
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("This is not a valid level amplifier. It must be a number!", true, sender, prefix);
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        return false;
                    }

                    if(!possibleAmplifierTypes.contains(amplifierType)){
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("This is not a valid amplifier type! It has to be boost, mult, or coinmult", true, sender, prefix);
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        return false;
                    }

                    if(Bukkit.getPlayer(playerName) == null){
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("A player by the name " + playerName + " has bought an amplifier, but is offline right now. Can't give them the amplifier...", true, sender, prefix);
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                    }else{
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("Giving " + playerName + " " + amount + " " + amplifierType, false, sender, prefix);
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
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

                        if(amplifierName.equalsIgnoreCase("Coin_Multiplier")){
                            amplifierName = amplifierName.replace("_", "");
                        }

                        Player activator = null;
                        AmplifiersConfigManager acm = new AmplifiersConfigManager(plugin);

                        if(playerName.equalsIgnoreCase(playerWhoActivatedName)){
                            chatFactory.sendPlayerMessage(" ", false, playerWhoClicked, null);
                            chatFactory.sendPlayerMessage("You can't thank yourself silly!", false, sender, prefix);
                            chatFactory.sendPlayerMessage(" ", false, sender, null);
                            return false;
                        }else{
                            if(acm.isOnThankfulPeopleList(playerName, amplifierName)){
                                chatFactory.sendPlayerMessage(" ", false, playerWhoClicked, null);
                                chatFactory.sendPlayerMessage("&7You've already received your &6Coins &7for thanking &e" + playerWhoActivatedName, false, playerWhoClicked, prefix);
                                chatFactory.sendPlayerMessage(" ", false, sender, null);
                                return false;
                            }else{
                                if(Bukkit.getPlayer(playerWhoActivatedName) != null){
                                    activator = Bukkit.getPlayer(playerWhoActivatedName);
                                    rewardCoinsToActivator(activator, playerWhoClicked);
                                }
                                rewardCoinsToClicker(playerWhoClicked, activator);
                                acm.addToThankfulPeopleList(playerName, amplifierName);
                                acm.saveConfig();
                            }
                        }
                    }
                    //give coins C10_MC 50
                }else if(args[0].equalsIgnoreCase("give") && args[1].equalsIgnoreCase("coins") && args.length == 4){

                    int amount;

                    try{
                        amount = Integer.parseInt(args[3]);
                    }catch(IllegalArgumentException e){
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("&cThis is not a valid number!", false, sender, null);
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        return false;
                    }

                    if(Bukkit.getPlayer(args[2]) != null){
                        GeneratorPvP.getEconomy().depositPlayer(Bukkit.getPlayer(args[2]), amount);

                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("&aYou have received &6" + amount  + " &6Coins&a.", false, Bukkit.getPlayer(args[2]), null);
                        chatFactory.sendPlayerMessage(" ", false, Bukkit.getPlayer(args[2]), null);

                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("&aYou have given &e" + args[2] + "&6 " + amount + " Coins&a.", false, sender, null);
                    }else{
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("&cThis is either not a valid player or the player isn't online!", false, sender, null);
                    }
                    chatFactory.sendPlayerMessage(" ", false, sender, null);


                }else if(args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("coins") && args.length == 4){

                    int amount;

                    try{
                        amount = Integer.parseInt(args[3]);
                    }catch(IllegalArgumentException e){
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("&cThis is not a valid number!", false, sender, null);
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        return false;
                    }

                    if(Bukkit.getPlayer(args[2]) != null){
                        double playerCurrentAmount = GeneratorPvP.getEconomy().getBalance(Bukkit.getPlayer(args[2]));
                        GeneratorPvP.getEconomy().withdrawPlayer(Bukkit.getPlayer(args[2]), playerCurrentAmount);
                        GeneratorPvP.getEconomy().depositPlayer(Bukkit.getPlayer(args[2]), amount);

                        chatFactory.sendPlayerMessage(" ", false, Bukkit.getPlayer(args[2]), null);
                        chatFactory.sendPlayerMessage("&aYour balance was set to &6" + amount  + " &6Coins&a.", false, Bukkit.getPlayer(args[2]), null);
                        chatFactory.sendPlayerMessage(" ", false, Bukkit.getPlayer(args[2]), null);

                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("&aYou set &e" + args[2] + "'s balance to &6" + amount + " Coins&a.", false, sender, null);
                    }else{
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("&cThis is either not a valid player or the player isn't online!", false, sender, null);
                    }
                    chatFactory.sendPlayerMessage(" ", false, sender, null);
                }else if(args[0].equalsIgnoreCase("reload") && args.length == 1){
                    plugin.reloadConfig();
                    chatFactory.sendPlayerMessage(" ", false, sender, null);
                    chatFactory.sendPlayerMessage("The config has been reloaded!", false, sender, null);
                    chatFactory.sendPlayerMessage(" ", false, sender, null);
                }else if(args[0].equalsIgnoreCase("clearholo") && args.length == 1){
                    chatFactory.sendPlayerMessage(" ", false, sender, null);
                    chatFactory.sendPlayerMessage("Disabling holograms...", false, sender, null);
                    chatFactory.sendPlayerMessage(" ", false, sender, null);
                    plugin.disableHolograms();
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

    public void rewardCoinsToClicker(Player playerWhoClicked, Player activator){
        int rewardAmount = plugin.getConfig().getInt("ThankingRewardAmount");

        if(activator == null){
            chatFactory.sendPlayerMessage(" ", false, playerWhoClicked, null);
            chatFactory.sendPlayerMessage("&7You Received &e" + rewardAmount  + " &6Coins", false, playerWhoClicked, prefix);
            chatFactory.sendPlayerMessage(" ", false, playerWhoClicked, null);
        }else{
            chatFactory.sendPlayerMessage(" ", false, playerWhoClicked, null);
            chatFactory.sendPlayerMessage("&7You Received &e" + rewardAmount  + " &6Coins &7for thanking &e" + activator.getName(), false, playerWhoClicked, prefix);
            chatFactory.sendPlayerMessage(" ", false, playerWhoClicked, null);
        }
        plugin.getEconomy().depositPlayer(playerWhoClicked, rewardAmount);
    }

    public void rewardCoinsToActivator(Player activator, Player clicker){
        int rewardAmount = plugin.getConfig().getInt("ActivatorRewardAmount");
        chatFactory.sendPlayerMessage(" ", false, activator, null);
        chatFactory.sendPlayerMessage("&e" + clicker.getName() + " &7thanked you. You have received &e" + rewardAmount + " &6Coins", false, activator, prefix);
        chatFactory.sendPlayerMessage(" ", false, activator, null);
        plugin.getEconomy().depositPlayer(activator, rewardAmount);
    }

}

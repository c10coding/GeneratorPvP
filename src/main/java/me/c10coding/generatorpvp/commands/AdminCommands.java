package me.c10coding.generatorpvp.commands;

import me.c10coding.coreapi.chat.ChatFactory;
import me.c10coding.generatorpvp.GeneratorPvP;
import me.c10coding.generatorpvp.files.AmplifiersConfigManager;
import me.c10coding.generatorpvp.files.EquippedConfigManager;
import me.c10coding.generatorpvp.files.ItemSaverConfigManager;
import me.c10coding.generatorpvp.files.StatsConfigManager;
import me.c10coding.generatorpvp.utils.GPUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AdminCommands implements CommandExecutor {

    private GeneratorPvP plugin;
    private ChatFactory chatFactory;
    private String prefix;

    public AdminCommands(GeneratorPvP plugin){
        this.plugin = plugin;
        this.chatFactory = plugin.getAPI().getChatFactory();
        this.prefix = plugin.getPrefix();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(args.length > 0){

            if(sender.hasPermission("gp.help") && args[0].equalsIgnoreCase("help") && args.length == 1){
                if(sender instanceof Player){
                    GPUtils.sendCenteredMessage((Player)sender, "&e==============HELP==============");
                    chatFactory.sendPlayerMessage("&6/menu &f- Brings up the main menu for GeneratorPvP", false, sender, null);
                    chatFactory.sendPlayerMessage("&6/gp give coins <playername> <amount> &f- Gives the desired player a certain amount of coins.", false, sender, null);
                    chatFactory.sendPlayerMessage("&6/gp give amp <playername> <booster | coinmult | mult> <level> <amount> &f- Gives the desired player a certain amount of an amplifier", false, sender, null);
                    chatFactory.sendPlayerMessage("&6/gp give weapon <playername> <knockback | positionswap | tnt | fireball | instakill> <amount>", false, sender, null);
                    chatFactory.sendPlayerMessage("&6/lb &f- Brings up the leaderboard.", false, sender, null);
                    chatFactory.sendPlayerMessage("&6/gp set coins <playername> <amount>.", false, sender, null);
                    chatFactory.sendPlayerMessage("&6/gp stats reset <playername> <kills | deaths>", false, sender, null);
                    chatFactory.sendPlayerMessage("&6/gp stats reset &f- Resets all player stats", false, sender, null);
                    chatFactory.sendPlayerMessage("&6/gp reset &f- Resets things like Boots, Warps, Chat colors, etc", false, sender, null);
                    chatFactory.sendPlayerMessage("&6/gp reset <playername> &f- Same thing as above except it does it to a specific player", false, sender, null);
                    GPUtils.sendCenteredMessage((Player)sender, "&e================================");
                }else{
                    chatFactory.sendPlayerMessage("Only players can use this command!", false, sender, null);
                }
            }

            if(sender.isOp()){
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
                            amplifierName = amplifierName.replace("_", " ");
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
                }else if(args[0].equalsIgnoreCase("reset")){

                    if(args.length == 1){
                        sendResetConfirmation(sender, null);
                    }else if(args.length == 2){
                        String playerName = args[1];
                        if(Bukkit.getOfflinePlayer(playerName) != null){
                            if(Bukkit.getOfflinePlayer(playerName).getUniqueId() != null){
                                sendResetConfirmation(sender, Bukkit.getOfflinePlayer(playerName));
                            }else{
                                chatFactory.sendPlayerMessage(" ", false, sender, null);
                                chatFactory.sendPlayerMessage("There was trouble getting this player's UUID", true, sender, prefix);
                            }
                        }else{
                            chatFactory.sendPlayerMessage(" ", false, sender, null);
                            chatFactory.sendPlayerMessage("This player does not exist!", true, sender, prefix);
                        }
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                    }
                }else if(args[0].equalsIgnoreCase("stats") && args[1].equalsIgnoreCase("reset")){

                    StatsConfigManager scm = new StatsConfigManager(plugin);
                    if(args.length == 3 || args.length == 4){
                        String playerName = args[2];
                        if(Bukkit.getOfflinePlayer(playerName) != null){
                            OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
                            if(args.length == 3){
                                sendStatConfirmation(sender, op, null);
                            }else{
                                String stat = args[3];
                                if(stat.equalsIgnoreCase("kills") || stat.equalsIgnoreCase("deaths")){
                                    sendStatConfirmation(sender, op, stat);
                                }else{
                                    chatFactory.sendPlayerMessage(" ", false, sender, null);
                                    chatFactory.sendPlayerMessage("That is not a valid statistic to reset. It must be either kills or deaths!", false, sender, null);
                                    chatFactory.sendPlayerMessage(" ", false, sender, null);
                                }
                            }
                        }else{
                            chatFactory.sendPlayerMessage(" ", false, sender, null);
                            chatFactory.sendPlayerMessage("This player does not exist!", true, sender, prefix);
                            chatFactory.sendPlayerMessage(" ", false, sender, null);
                        }
                    }else if(args.length == 2){
                        sendStatConfirmation(sender, null, null);
                    }
                }else if(args[0].equalsIgnoreCase("give") && args[1].equalsIgnoreCase("weapon") && args.length == 5){
                    String weaponName = args[3];
                    String playerName = args[2];
                    int amount;

                    try{
                        amount = Integer.parseInt(args[4]);
                    }catch(IllegalArgumentException e){
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("The amount given must be a number!", false, sender, null);
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        return false;
                    }

                    List<OfflinePlayer> serverOfflinePlayers = Arrays.asList(Bukkit.getOfflinePlayers());
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

                    if(!serverOfflinePlayers.contains(offlinePlayer)){
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                        chatFactory.sendPlayerMessage("This is not a valid player! Could not give the player the weapon...", false, sender, null);
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                    }else{
                        if(weaponName.equalsIgnoreCase("Knockback") || weaponName.equalsIgnoreCase("PositionSwap") || weaponName.equalsIgnoreCase("TNT") || weaponName.equalsIgnoreCase("Fireball") || weaponName.equalsIgnoreCase("InstaKill")){
                            ItemStack item = getWeapon(weaponName, amount);
                            if(offlinePlayer.isOnline()){
                                Player onlinePlayer = offlinePlayer.getPlayer();
                                Map<Integer, ItemStack> map = onlinePlayer.getInventory().addItem(item);

                                if(!map.isEmpty()){
                                    chatFactory.sendPlayerMessage(" ", false, sender, null);
                                    chatFactory.sendPlayerMessage("Could not give you that weapon! Your inventory is full", false, onlinePlayer, null);
                                    chatFactory.sendPlayerMessage(" ", false, sender, null);
                                    return false;
                                }

                                if(weaponName.equalsIgnoreCase("PositionSwap")){
                                    weaponName = "Position Swap";
                                }else if(weaponName.equalsIgnoreCase("tnt")){
                                    weaponName = "TNT";
                                }

                                chatFactory.sendPlayerMessage(" ", false, sender, null);
                                chatFactory.sendPlayerMessage("You have given " + amount + " " + weaponName + " to&e " + onlinePlayer.getName(), false, sender, null);

                                chatFactory.sendPlayerMessage(" ", false, onlinePlayer, null);
                                chatFactory.sendPlayerMessage("You have been given " + amount + " " + weaponName + "!", false, onlinePlayer, null);
                                chatFactory.sendPlayerMessage(" ", false, onlinePlayer, null);

                            }else{
                                ItemSaverConfigManager iscm = new ItemSaverConfigManager(plugin);
                                iscm.saveItem(item, offlinePlayer.getUniqueId());
                                chatFactory.sendPlayerMessage(" ", false, sender, null);
                                chatFactory.sendPlayerMessage("The player is offline. Their item has been saved in a configuration file for later...", false, sender, null);
                            }
                        }else{
                            chatFactory.sendPlayerMessage(" ", false, sender, null);
                            chatFactory.sendPlayerMessage("This is not a valid weapon to give! Valid weapons are &eKnockback, PositionSwap, TNT, InstaKill, and Fireball", false, sender, null);
                        }
                        chatFactory.sendPlayerMessage(" ", false, sender, null);
                    }
                }
            }
        }else{
            chatFactory.sendPlayerMessage(" ", false, sender, null);
            chatFactory.sendPlayerMessage("Do /gp help to see all of this plugin's commands!", false, sender, null);
            chatFactory.sendPlayerMessage(" ", false, sender, null);
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

    private void sendStatConfirmation(CommandSender sender, OfflinePlayer p, String statToReset){

        TextComponent msg = new TextComponent("\nClick confirm if you wish to proceed with this command... \n");
        TextComponent yes = new TextComponent("Confirm \n");

        yes.setColor(ChatColor.GREEN);

        if(p == null){
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gpconfirm stats reset"));
        }else{
            if(statToReset == null){
                yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gpconfirm stats reset " + p.getUniqueId()));
            }else{
                yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gpconfirm stats reset " + p.getUniqueId() + " " + statToReset));
            }
        }

        msg.addExtra(yes);
        sender.spigot().sendMessage(msg);
    }

    private void sendResetConfirmation(CommandSender sender, OfflinePlayer p){
        TextComponent msg = new TextComponent("\nClick confirm if you wish to proceed with this command... \n");
        TextComponent yes = new TextComponent("Confirm \n");

        yes.setColor(ChatColor.GREEN);

        if(p == null){
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gpconfirm reset"));
        }else{
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gpconfirm reset " + p.getUniqueId()));
        }

        msg.addExtra(yes);
        sender.spigot().sendMessage(msg);

    }

    private ItemStack getWeapon(String weaponName, int amount){
        Material mat;
        String displayName;
        List<String> lore = new ArrayList<>();
        if(weaponName.equalsIgnoreCase("Knockback")){
            mat = Material.SNOWBALL;
            displayName = chatFactory.colorString("&fKnockback");
            lore.add("&eDeals a little knockback upon hit");
        }else if(weaponName.equalsIgnoreCase("PositionSwap")){
            mat = Material.SLIME_BALL;
            displayName = chatFactory.colorString("&aPosition Swap");
            lore.add("&eSwaps position with whoever gets hit with it");
        }else if(weaponName.equalsIgnoreCase("TNT")){
            mat = Material.TNT;
            displayName = chatFactory.colorString("&cTNT");
            lore.add("&eYou better move out of the way after you place this stuff...");
        }else if(weaponName.equalsIgnoreCase("Fireball")){
            mat = Material.FIRE_CHARGE;
            displayName = chatFactory.colorString("&4Fireball");
            lore.add("&eOnce again, you're a human Ghast!");
        }else if(weaponName.equalsIgnoreCase("InstaKill")){
            mat = Material.EGG;
            displayName = chatFactory.colorString("&eInstant Kill");
            lore.add("&ePretty much instantly kills whoever you throw this at.");
        }else{
            mat = null;
            displayName = null;
        }

        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        lore = GPUtils.colorLore(lore);
        meta.setLore(lore);
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);

        return item;
    }

}

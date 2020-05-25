package me.c10coding.generatorpvp.utils;

import me.c10coding.coreapi.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import java.util.ArrayList;
import java.util.List;

public class GPUtils {

    static Chat chat = new Chat();

    public static String matToName(Material mat){
        String enumName = mat.name();
        if(enumName.contains("_")){
            enumName = enumName.replace("_", " ");
        }
        enumName = firstLowerRestUpper(enumName);
        return enumName;
    }

    public static String enumToName(Enum e){
        String stringValue = e.toString();
        String finalStringValue = "";

        if(stringValue.contains("_")){
            String[] enumSplit = stringValue.split("_");
            for(int x = 0; x < enumSplit.length; x++){
                if(x != enumSplit.length - 1){
                    finalStringValue += firstLowerRestUpper(enumSplit[x]) + " ";
                }else{
                    finalStringValue += firstLowerRestUpper(enumSplit[x]);
                }
            }
        }else{
            finalStringValue = firstLowerRestUpper(stringValue);
        }

        return finalStringValue;
    }

    public static String enumToConfigKey(Enum e){
        String stringValue = e.toString();
        String finalStringValue = "";

        if(stringValue.contains("_")){
            String[] enumSplit = stringValue.split("_");
            for(String s : enumSplit){
                finalStringValue += firstLowerRestUpper(s);
            }
        }else{
            finalStringValue = firstLowerRestUpper(stringValue);
        }

        return finalStringValue;
    }

    public static String firstLowerRestUpper(String s){
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static int getColumnNum(int inventoryRows, int targetIndex, Inventory inv){
        int currentIndex = 0;
        for(int x = 0; x < inventoryRows; x++){
            for(int c = 0; c < 9; c++){
                if(currentIndex == targetIndex){
                    return c;
                }
                currentIndex++;
            }
        }
        return 0;
    }

    public static List<Integer> getIndexesAroundItem(Inventory inv, int inventoryRows, int itemIndex){
        List<Integer> indexes = new ArrayList<>();
        //The column directly to the left of the item.
        int leftColumnIndex = getColumnNum(inventoryRows, itemIndex-1, inv);
        int invIndex = 0;
        /*
        Loops through all the indexes and gets the indexes that are in the same column as leftColumnIndex variable
         */
        for(int rows = 0; rows < inventoryRows; rows++){
            for(int x = 0; x < 9; x++){
                if(leftColumnIndex == getColumnNum(inventoryRows, invIndex, inv)){
                    indexes.add(invIndex);
                }
                invIndex++;
            }
        }

        invIndex = 0;
        int itemColumnIndex = getColumnNum(inventoryRows, itemIndex, inv);

        for(int rows = 0; rows < inventoryRows; rows++){
            for(int x = 0; x < 9; x++){
                if(invIndex != itemIndex){
                    if(itemColumnIndex == getColumnNum(inventoryRows, invIndex, inv)){
                        indexes.add(invIndex);
                    }
                }
                invIndex++;
            }
        }

        invIndex = 0;
        int rightColumnIndex = getColumnNum(inventoryRows, itemIndex+1, inv);

        for(int rows = 0; rows < inventoryRows; rows++){
            for(int x = 0; x < 9; x++){
                if(rightColumnIndex == getColumnNum(inventoryRows, invIndex, inv)){
                    indexes.add(invIndex);
                }
                invIndex++;
            }
        }

        return indexes;

    }

    public static List<Integer> getIndexesInColumn(Inventory inv, int inventoryIndex, int inventoryNumRows){
        List<Integer> indexes = new ArrayList<>();
        int columnNum = getColumnNum(inventoryNumRows, inventoryIndex, inv);
        int invIndex = 0;
        for(int row = 0; row < inventoryNumRows; row++){
            for(int column = 0; column < 9; column++){
                if(column == columnNum){
                    indexes.add(invIndex);
                }
                invIndex++;
            }
        }
        return indexes;
    }

    public static String secondsToSerializedTime(int seconds){
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int correctSeconds = seconds % 60;
        return chat.chat("&c" + hours + " &7Hours &c" + minutes + " &7Minutes &c" + correctSeconds + " &7Seconds");
    }

    public static List<String> colorLore(List<String> lore){
        List<String> newLore = new ArrayList<>();
        for(String s : lore){
            newLore.add(chat.chat(s));
        }
        return newLore;
    }

    public static ChatColor matchArmorColorWithChatColor(Color armorColor){
        if(armorColor.equals(Color.GRAY)){
            return ChatColor.GRAY;
        }else if(armorColor.equals(Color.RED)){
            return ChatColor.RED;
        }else if(armorColor.equals(Color.fromBGR(255, 192, 250))){
            return ChatColor.LIGHT_PURPLE;
        }else if(armorColor.equals(Color.YELLOW)){
            return ChatColor.YELLOW;
        }else if(armorColor.equals(Color.BLUE)){
            return ChatColor.BLUE;
        }else if(armorColor.equals(Color.LIME)){
            return ChatColor.GREEN;
        }else if(armorColor.equals(Color.fromBGR(0,100,0))){
            return ChatColor.DARK_GREEN;
        }else if(armorColor.equals(Color.fromBGR(125, 0, 0))){
            return ChatColor.DARK_RED;
        }else if(armorColor.equals(Color.BLACK)){
            return ChatColor.BLACK;
        }else if(armorColor.equals(Color.fromBGR(197,179,88))){
            return ChatColor.GOLD;
        }else if(armorColor.equals(Color.WHITE)){
            return ChatColor.WHITE;
        }else{
            return ChatColor.DARK_AQUA;
        }
    }


}

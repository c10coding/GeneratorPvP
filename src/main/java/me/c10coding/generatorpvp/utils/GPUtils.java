package me.c10coding.generatorpvp.utils;

import me.c10coding.coreapi.chat.ChatFactory;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GPUtils {

    static ChatFactory chat = new ChatFactory();

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
        return chat.colorString("&c" + hours + " &7Hours &c" + minutes + " &7Minutes &c" + correctSeconds + " &7Seconds");
    }

    public static List<String> colorLore(List<String> lore){
        List<String> newLore = new ArrayList<>();
        for(String s : lore){
            newLore.add(chat.colorString(s));
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
        }else if(armorColor.equals(Color.WHITE)) {
            return ChatColor.WHITE;
        }else if(armorColor.equals(Color.PURPLE)){
            return ChatColor.LIGHT_PURPLE;
        }else{
            return ChatColor.DARK_AQUA;
        }
    }

    public static ItemStack addGlow(ItemStack stack){
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.WATER_WORKER, 70, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }

    public static boolean isPlayerInSpawn(Player p){
        Location playerLoc = p.getLocation();
        int x = playerLoc.getBlockX();
        int z = playerLoc.getBlockZ();

        if(x >= -577 && x <= -547){
            if(z >= -659 && z <= -629){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }

    }

    public static void sendCenteredMessage(Player player, String message){
        final  int CENTER_PX = 154;
        if(message == null || message.equals("")) player.sendMessage("");
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
                continue;
            }else if(previousCode == true){
                previousCode = false;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                }else isBold = false;
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb.toString() + message);
    }

    public enum DefaultFontInfo {

        A('A', 5),
        a('a', 5),
        B('B', 5),
        b('b', 5),
        C('C', 5),
        c('c', 5),
        D('D', 5),
        d('d', 5),
        E('E', 5),
        e('e', 5),
        F('F', 5),
        f('f', 4),
        G('G', 5),
        g('g', 5),
        H('H', 5),
        h('h', 5),
        I('I', 3),
        i('i', 1),
        J('J', 5),
        j('j', 5),
        K('K', 5),
        k('k', 4),
        L('L', 5),
        l('l', 1),
        M('M', 5),
        m('m', 5),
        N('N', 5),
        n('n', 5),
        O('O', 5),
        o('o', 5),
        P('P', 5),
        p('p', 5),
        Q('Q', 5),
        q('q', 5),
        R('R', 5),
        r('r', 5),
        S('S', 5),
        s('s', 5),
        T('T', 5),
        t('t', 4),
        U('U', 5),
        u('u', 5),
        V('V', 5),
        v('v', 5),
        W('W', 5),
        w('w', 5),
        X('X', 5),
        x('x', 5),
        Y('Y', 5),
        y('y', 5),
        Z('Z', 5),
        z('z', 5),
        NUM_1('1', 5),
        NUM_2('2', 5),
        NUM_3('3', 5),
        NUM_4('4', 5),
        NUM_5('5', 5),
        NUM_6('6', 5),
        NUM_7('7', 5),
        NUM_8('8', 5),
        NUM_9('9', 5),
        NUM_0('0', 5),
        EXCLAMATION_POINT('!', 1),
        AT_SYMBOL('@', 6),
        NUM_SIGN('#', 5),
        DOLLAR_SIGN('$', 5),
        PERCENT('%', 5),
        UP_ARROW('^', 5),
        AMPERSAND('&', 5),
        ASTERISK('*', 5),
        LEFT_PARENTHESIS('(', 4),
        RIGHT_PERENTHESIS(')', 4),
        MINUS('-', 5),
        UNDERSCORE('_', 5),
        PLUS_SIGN('+', 5),
        EQUALS_SIGN('=', 5),
        LEFT_CURL_BRACE('{', 4),
        RIGHT_CURL_BRACE('}', 4),
        LEFT_BRACKET('[', 3),
        RIGHT_BRACKET(']', 3),
        COLON(':', 1),
        SEMI_COLON(';', 1),
        DOUBLE_QUOTE('"', 3),
        SINGLE_QUOTE('\'', 1),
        LEFT_ARROW('<', 4),
        RIGHT_ARROW('>', 4),
        QUESTION_MARK('?', 5),
        SLASH('/', 5),
        BACK_SLASH('\\', 5),
        LINE('|', 1),
        TILDE('~', 5),
        TICK('`', 2),
        PERIOD('.', 1),
        COMMA(',', 1),
        SPACE(' ', 3),
        DEFAULT('a', 4);

        private char character;
        private int length;

        DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public char getCharacter() {
            return this.character;
        }

        public int getLength() {
            return this.length;
        }

        public int getBoldLength() {
            if (this == DefaultFontInfo.SPACE) return this.getLength();
            return this.length + 1;
        }

        public static DefaultFontInfo getDefaultFontInfo(char c) {
            for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
                if (dFI.getCharacter() == c) return dFI;
            }
            return DefaultFontInfo.DEFAULT;
        }
    }


}



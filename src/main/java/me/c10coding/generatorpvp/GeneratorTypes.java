package me.c10coding.generatorpvp;

public enum GeneratorTypes {
    COAL_ORE("&7", "Coal"),
    COAL_BLOCK("&7", "Coal Block"),
    IRON_ORE("&f", "Iron"),
    IRON_BLOCK("&f", "Iron Block"),
    GOLD_ORE("&e", "Gold"),
    GOLD_BLOCK("&e", "Gold Block"),
    DIAMOND_ORE("&b", "Diamond"),
    DIAMOND_BLOCK("&b", "Diamond Block"),
    EMERALD_ORE("&a", "Emerald");

    private String colorCode, displayName;
    GeneratorTypes(String colorCode, String displayName){
        this.colorCode = colorCode;
        this.displayName = displayName;
    }

    public String getColorCode(){
        return colorCode;
    }

    public String getDisplayName(){
        return displayName;
    }

}

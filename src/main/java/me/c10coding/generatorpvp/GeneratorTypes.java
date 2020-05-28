package me.c10coding.generatorpvp;

import org.bukkit.Material;

public enum GeneratorTypes {
    COAL_ORE("&7", "Coal", Material.COAL),
    COAL_BLOCK("&7", "Coal Block", Material.COAL_BLOCK),
    IRON_ORE("&f", "Iron", Material.IRON_INGOT),
    IRON_BLOCK("&f", "Iron Block", Material.IRON_BLOCK),
    GOLD_ORE("&e", "Gold", Material.GOLD_INGOT),
    GOLD_BLOCK("&e", "Gold Block", Material.GOLD_BLOCK),
    DIAMOND_ORE("&b", "Diamond", Material.DIAMOND),
    DIAMOND_BLOCK("&b", "Diamond Block", Material.DIAMOND_BLOCK),
    EMERALD_ORE("&a", "Emerald", Material.EMERALD);

    private String colorCode, displayName;
    private Material mat;
    GeneratorTypes(String colorCode, String displayName, Material mat){
        this.colorCode = colorCode;
        this.displayName = displayName;
        this.mat = mat;
    }

    public String getColorCode(){
        return colorCode;
    }

    public String getDisplayName(){
        return displayName;
    }

    public Material getMaterial(){
        return mat;
    }

}

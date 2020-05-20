package me.c10coding.generatorpvp.utils;

import com.google.gson.internal.$Gson$Preconditions;
import org.bukkit.Material;

public class GPUtils {

    public static String matToName(Material mat){
        String enumName = mat.name();
        if(enumName.contains("_")){
            enumName = enumName.replace("_", " ");
        }
        enumName = firstLowerRestUpper(enumName);
        return enumName;
    }

    public static String firstLowerRestUpper(String s){
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }

}

package me.c10coding.generatorpvp;

import org.bukkit.Location;
import org.bukkit.World;

public class IncompleteLocation {

    private int x,y,z;

    public IncompleteLocation(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location completeLocation(World w){
        return new Location(w, x, y , z);
    }

}

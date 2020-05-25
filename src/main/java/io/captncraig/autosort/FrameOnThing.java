package io.captncraig.autosort;

import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.InventoryHolder;

public class FrameOnThing {
    public ItemFrame iFrame;
    public InventoryHolder holder;
    public Location holderLoc;
    public FrameOnThing(ItemFrame i, InventoryHolder b, Location l){
        iFrame = i;
        holder = b;
        holderLoc = l;
    }
}

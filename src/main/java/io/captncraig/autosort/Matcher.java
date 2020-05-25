package io.captncraig.autosort;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class Matcher {

    private Collection<FrameOnThing> _frames;
    private JavaPlugin _plugin;

    public Matcher(Collection<FrameOnThing> allPossible, JavaPlugin plug) {
        _frames = allPossible;
        _plugin = plug;
    }

    private  ArrayList<InventoryHolder> getPrimaryMatches(ItemStack stack){
        // TODO: caching
        ArrayList<InventoryHolder> list = new ArrayList<>();
        for (FrameOnThing fot : _frames) {
            if (fot.iFrame.getItem().getType().equals(stack.getType())) {
                list.add(fot.holder);
            }
            // TODO: exclude item overflow and global overflow
        }
        return list;
    }

    public int disposeOf(ItemStack stack){
        ArrayList<InventoryHolder> holders = getPrimaryMatches(stack);
        return distribute(stack, holders);
    }

    private int distribute(ItemStack stack, ArrayList<InventoryHolder> holders){
        int remaining = stack.getAmount();
        // distribute items evenly into all possible inventories.

        // need to loop a few times in case things are full and we gotta distribute unevenly
        while(remaining > 0 && holders.size() > 0) {
            // if you successfully take everything given, you make it to the next loop
            ArrayList<InventoryHolder> next = new ArrayList<>();
            // randomize on each loop
            Collections.shuffle(holders);
            // divide evenly between all
            int increment = (int)Math.ceil((double) remaining / holders.size());

            for (InventoryHolder h : holders){
                if (increment > remaining){
                    increment = remaining;
                }
                ItemStack stackToAdd = stack.clone();
                stackToAdd.setAmount(increment);
                HashMap<Integer, ItemStack> result = h.getInventory().addItem(stackToAdd);
                if (result.size() != 0){
                    // some did not get added
                    int actual = increment - result.get(0).getAmount();
                    remaining -= actual;
                }else{
                    // all got added
                    remaining -= increment;
                    next.add(h);
                }
                if (remaining <= 0){
                    return 0;
                }
            }
            holders = next;
        }
        return remaining;
    }
}

package io.captncraig.autosort;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Events implements Listener {

    private JavaPlugin _plugin;
    public Events(JavaPlugin plugin){
        _plugin = plugin;
    }


    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e){
        // only interested in item frames
        if (e.getRightClicked().getType() != EntityType.ITEM_FRAME){
            return;
        }
        // TODO: prevent players from placing or removing Autosort chest from frames without permission
        // TODO: prevent player from destroying frame or chest without permission

        Player p = e.getPlayer();
        ItemFrame iframe = (ItemFrame) e.getRightClicked();
    }

    @EventHandler
    public void onChestClosed(InventoryCloseEvent e){
        Inventory inv = e.getInventory();
        InventoryHolder holder = inv.getHolder();
        Boolean b = isAutoSorter(holder);
        if (b) {
            Drain((Chest)holder);
        }
    }

    private  void Drain(Chest c){
        Collection<FrameOnThing> nearby = getFramesAround(c.getBlock(),14,500);
        Collection<FrameOnThing> allPossible = new ArrayList<>(nearby.size());
        for(FrameOnThing fot : nearby){
            if (fot.holderLoc.equals(c.getLocation())){
                continue;
            }
            allPossible.add(fot);
        }
        Matcher m = new Matcher(allPossible, _plugin);
        drainInventoryContainer(m,c);
    }

    private void drainInventoryContainer(Matcher m, InventoryHolder c){
        Inventory snapshot = c.getInventory();
        for (int i = 0; i< snapshot.getSize(); i++){
            // find what we got
            ItemStack stack = snapshot.getItem(i);
            if (stack == null) continue;
            // empty shulkerbox
            ItemMeta im = stack.getItemMeta();
            if (im instanceof BlockStateMeta){
                BlockStateMeta bsm = (BlockStateMeta) im;
                BlockState bs = bsm.getBlockState();
                if (bs instanceof ShulkerBox){
                    ShulkerBox sb = (ShulkerBox)bs;
                    drainInventoryContainer(m, sb);
                    bsm.setBlockState(sb);
                    stack.setItemMeta(bsm);
                    return;
                }
            }
            // get rid of it
            int remaining = m.disposeOf(stack);
            // delete it (or put back unroutable garbage)
            stack.setAmount(remaining);
            if (remaining == 0){
                stack = null;
            }
            c.getInventory().setItem(i, stack);
        }
    }

    private Boolean isAutoSorter(InventoryHolder holder){
        if (holder instanceof Chest) {
            Chest c = (Chest) holder;
            Block b = c.getBlock();
            Collection<FrameOnThing> frames = getFramesAround(b,2,2);
            for (FrameOnThing fit : frames){
                if (fit.holderLoc.equals(c.getLocation()) && fit.iFrame.getItem().getType() == Material.CHEST && fit.iFrame.getItem().getItemMeta().getDisplayName().equals("AutoSorter")){
                    return true;
                }
            }
        }
        return false;
    }

    private Collection<FrameOnThing> getFramesAround(Block b, int xz, int y){
        Collection<Entity> entities = b.getWorld().getNearbyEntities(b.getLocation(),xz,y,xz);
        Collection<FrameOnThing> newSet = new ArrayList<>();
        for (Entity ent : entities) {
            if (ent.getType() != EntityType.ITEM_FRAME){
                continue;
            }
            ItemFrame iFrame = (ItemFrame) ent;
            if (iFrame.getItem().getType() == Material.AIR){
                continue;
            }
            Block b2 = iFrame.getLocation().getBlock().getRelative(iFrame.getAttachedFace());
            BlockState bs = b2.getState();
            if (!(bs instanceof InventoryHolder)){
                continue;
            }
            newSet.add(new FrameOnThing(iFrame, (InventoryHolder) bs, b2.getLocation()));
        }
        return newSet;
    }
}


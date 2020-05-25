package io.captncraig.autosort;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;

public class AutoSortCommand implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack tool = new ItemStack(Material.WOODEN_SWORD);
            ItemMeta meta = tool.getItemMeta();
            meta.setDisplayName("AutoSort Tool");
            tool.setItemMeta(meta);
            player.getInventory().addItem(tool);
            return true;
        }
        return false;
    }
}
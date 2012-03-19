package me.flyinglawnmower.simplesort;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;

public class KeyListener implements BindingExecutionDelegate {
	SimpleSort plugin;
	
	public KeyListener (SimpleSort plugin) {
		this.plugin = plugin;
	}
	
	public void keyPressed(KeyBindingEvent event) {
		Player player = event.getPlayer();
		if (player.getOpenInventory().getTopInventory().getType() == InventoryType.CHEST) {
			ItemStack[] chestItems = player.getOpenInventory().getTopInventory().getContents();
			chestItems = plugin.sortItems(chestItems, 0, chestItems.length);
			player.getOpenInventory().getTopInventory().setContents(chestItems);
			event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Chest sorted!");
		} else {
			plugin.getServer().dispatchCommand(player, "sort");
		}
	}
	
	public void keyReleased(KeyBindingEvent event) {
	}
}
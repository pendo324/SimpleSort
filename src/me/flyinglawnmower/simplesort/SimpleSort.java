package me.flyinglawnmower.simplesort;

/*
 * SimpleSort plugin by:
 * 	- Shadow1013GL
 *  - Pyr0Byt3
 *  - pendo324
 */

import java.util.Arrays;
import java.util.Comparator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.blocks.ItemType;

class ItemComparator implements Comparator<ItemStack> {
	public int compare(ItemStack item1, ItemStack item2) {
		if (item1 == null && item2 != null) {
			return -1;
		} else if (item1 == null && item2 == null) {
			return 0;
		} else if (item1 != null && item2 == null) {
			return 1;
		} else if (item1.getTypeId() > item2.getTypeId()) {
			return 1;
		} else if (item1.getTypeId() < item2.getTypeId()) {
			return -1;
		} else if (item1.getTypeId() == item2.getTypeId()) {
			if (ItemType.usesDamageValue(item1.getTypeId())) {
				if (item1.getDurability() < item2.getDurability()) {
					return 1;
				} else if (item1.getDurability() > item2.getDurability()) {
					return -1;
				}
			}
			if (item1.getAmount() < item2.getAmount()) {
				return -1;
			} else if (item1.getAmount() > item2.getAmount()) {
				return 1;
			}
		}
		return 0;
	}
}

public class SimpleSort extends JavaPlugin implements Listener {
	private ItemStack[] stackItems(ItemStack[] items, int first, int last) {
		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (item == null || item.getAmount() <= 0 || item.getMaxStackSize() == 1) {
				continue;
			}
			if (item.getAmount() < item.getMaxStackSize()) {
				int needed = item.getMaxStackSize() - item.getAmount();
				for (int j = i + 1; j < items.length; j++) {
					ItemStack item2 = items[j];
					if (item2 == null || item2.getAmount() <= 0 || item.getMaxStackSize() == 1) {
						continue;
					}
					if (item2.getTypeId() == item.getTypeId() && (!ItemType.usesDamageValue(item.getTypeId()) || item.getDurability() == item2.getDurability()) && item.getEnchantments().equals(item2.getEnchantments())) {
						if (item2.getAmount() > needed) {
							item.setAmount(64);
							item2.setAmount(item2.getAmount() - needed);
							break;
						} else {
							items[j] = null;
							item.setAmount(item.getAmount() + item2.getAmount());
							needed = 64 - item.getAmount();
						}
					}
				}
			}
		}
		return items;
	}
	
	private ItemStack[] sortItems(ItemStack[] list, int first, int last) {
		list = stackItems(list, first, last);
		Arrays.sort(list, first, last, new ItemComparator());
		return list;
	}
	
	public void onEnable() {
		this.getConfig().options().copyDefaults(true);
		saveConfig();
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player)sender;
			ItemStack[] items = player.getInventory().getContents();
			
			if (cmd.getName().equalsIgnoreCase("sort")) {
				if (args.length == 0 || args[0].equalsIgnoreCase("top")) {
					items = sortItems(items, 10, 36);
					player.sendMessage(ChatColor.GREEN + "Inventory top sorted!");
				} else if (args[0].equalsIgnoreCase("all")) {
					items = sortItems(items, 0, 36);
					player.sendMessage(ChatColor.GREEN + "Entire inventory sorted!");
				} else if (args[0].equalsIgnoreCase("hot")) {
					items = sortItems(items, 0, 9);
					player.sendMessage(ChatColor.GREEN + "Hotbar sorted!");
				} else {
					return false;
				}
				player.getInventory().setContents(items);
				return true;
			}
		} else {
			sender.sendMessage("You need to be a player to sort your inventory!");
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getPlayer().hasPermission("simplesort.chest")) {
			Block block = event.getClickedBlock();
			
			if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getMaterial().getId() == Integer.parseInt(getConfig().getString("wand")) && block.getType() == Material.CHEST) {
				Chest chest = (Chest)block.getState();
				ItemStack[] chestItems = chest.getInventory().getContents();
				chestItems = sortItems(chestItems, 0, chestItems.length);
				chest.getInventory().setContents(chestItems);
				event.getPlayer().sendMessage(ChatColor.GREEN + "Chest sorted!");
			}
		}
	}
}
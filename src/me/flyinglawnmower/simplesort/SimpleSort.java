package me.flyinglawnmower.simplesort;

/*
 * SimpleSort plugin by:
 *  - Shadow1013GL
 *  - Pyr0Byt3
 *  - pendo324
 */

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleSort extends JavaPlugin implements Listener {
	private ItemStack[] stackItems(ItemStack[] items, int first, int last) {
		for (int i = first; i < last; i++) {
			ItemStack item1 = items[i];
			if (item1 == null || item1.getAmount() <= 0 || item1.getMaxStackSize() == 1) {
				continue;
			}
			if (item1.getAmount() < item1.getMaxStackSize()) {
				int needed = item1.getMaxStackSize() - item1.getAmount();
				for (int j = i + 1; j < last; j++) {
					ItemStack item2 = items[j];
					if (item2 == null || item2.getAmount() <= 0 || item1.getMaxStackSize() == 1) {
						continue;
					}
					if (item2.getTypeId() == item1.getTypeId() && item1.getDurability() == item2.getDurability() && item1.getEnchantments().equals(item2.getEnchantments())) {
						if (item2.getAmount() > needed) {
							item1.setAmount(item1.getMaxStackSize());
							item2.setAmount(item2.getAmount() - needed);
							break;
						} else {
							items[j] = null;
							item1.setAmount(item1.getAmount() + item2.getAmount());
							needed = item1.getMaxStackSize() - item1.getAmount();
						}
					}
				}
			}
		}
		return items;
	}
	
	public ItemStack[] sortItems(ItemStack[] items, int first, int last) {
		items = stackItems(items, first, last);
		Arrays.sort(items, first, last, new ItemComparator());
		return items;
	}
	
	public void onEnable() {
		this.getConfig().options().header("The item ID of the chest-sorting wand.");
		this.getConfig().options().copyDefaults(true);
		saveConfig();
		if (getServer().getPluginManager().getPlugin("Spout") != null) {
			SpoutStuff.registerKeys(this);
        }
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player)sender;
			ItemStack[] items = player.getInventory().getContents();
			if (cmd.getName().equalsIgnoreCase("sort")) {
				if (args.length == 0 || args[0].equalsIgnoreCase("top")) {
					items = sortItems(items, 9, 36);
					player.sendMessage(ChatColor.DARK_GREEN + "Inventory top sorted!");
				} else if (args[0].equalsIgnoreCase("all")) {
					items = sortItems(items, 0, 36);
					player.sendMessage(ChatColor.DARK_GREEN + "Entire inventory sorted!");
				} else if (args[0].equalsIgnoreCase("hot")) {
					items = sortItems(items, 0, 9);
					player.sendMessage(ChatColor.DARK_GREEN + "Hotbar sorted!");
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
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getPlayer().hasPermission("simplesort.chest")) {
			Block block = event.getClickedBlock();
			if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getMaterial().getId() == getConfig().getInt("wand") && block.getType() == Material.CHEST) {
				ItemStack[] chestItems = ((Chest)block.getState()).getInventory().getContents();
				chestItems = sortItems(chestItems, 0, chestItems.length);
				((Chest)block.getState()).getInventory().setContents(chestItems);
				event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Chest sorted!");
			}
		}
	}
}
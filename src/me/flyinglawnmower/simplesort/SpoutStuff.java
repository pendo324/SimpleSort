package me.flyinglawnmower.simplesort;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.keyboard.Keyboard;

public class SpoutStuff {
 public static void registerKeys(SimpleSort plugin) {
		SpoutManager.getKeyBindingManager().registerBinding("Sort", Keyboard.KEY_K, "Sorts the currently open inventory screen.", new KeyListener(plugin), plugin);
	}
}
package io.github.sharafian.craftwork;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

public class DiscordCommandExecutor implements CommandExecutor {
	private final CraftWork plugin;
	public DiscordCommandExecutor (CraftWork plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand (CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("discord")) {
			this.plugin.getLogger().info("Got /discord command from " + sender.getName());
			return true;
		}
		
		return false;
	}
}

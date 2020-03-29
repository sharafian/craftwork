package io.github.sharafian.craftwork;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class DiscordCommandExecutor implements CommandExecutor {
	private final CraftWork plugin;
	private static final String serverUrl = "https://localhost:8080";
	private final Map<String, Location> targetedBlocks = new HashMap<String, Location>();

	public DiscordCommandExecutor (CraftWork plugin) {
		this.plugin = plugin;
	}
	
	private void handleGetUserCode (CommandSender sender) {
		return;
	}

	private void handleMakeRoom (CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Must be a player to use mkroom");
			return;
		}

		if (args.length < 2) {
			sender.sendMessage("Usage: craftwork mkroom [voice channel]");
			return;
		}
		
		final Player player = (Player) sender;
		
		if (this.targetedBlocks.containsKey(player.getName())) {
			this.targetedBlocks.remove(player.getName());
			
			final List<Room> rooms = (List<Room>) this.plugin.getConfig().getList("rooms");
			final Map<String, Object> room = new HashMap<String, Object>();

			room.put("p1", this.targetedBlocks.get(player.getName()));
			room.put("p2", player.getTargetBlock(null, 5));
			room.put("room", "");

			rooms.add(new Room(room));

			this.plugin.saveConfig();
			return;
		} else {
			this.targetedBlocks.put(player.getName(), player.getTargetBlock(null, 5).getLocation());
			return;
		}
	}

	private void handleRemoveRoom (CommandSender sender, String[] args) {
		return;
	}

	private void handleSetRoom (CommandSender sender, String[] args) {
		return;
	}
	
	private void handleServerLink (CommandSender sender, String[] args) {
		if (args.length < 2) {
			sender.sendMessage("Usage: craftwork server-link <key>");
			return;
		}

		// TODO: do an HTTP call in order to check this works
		// TODO: how to configure the default HTTP server?
		this.plugin.getConfig().set("server.key", args[1]);
		this.plugin.saveConfig();
		sender.sendMessage("Saved key to configuration");
		return;
	}
	
	public boolean onCommand (CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("craftwork")) {
			return false;
		}

		if (args.length == 0) {
			this.handleGetUserCode(sender);
			return true;
		}
		
		final String subcommand = args[0];

		if (subcommand.equals("server-link")) {
			this.handleServerLink(sender, args);
		} else if (subcommand.equals("mkroom")) {
			this.handleMakeRoom(sender, args);
		} else if (subcommand.equals("rmroom")) {
			this.handleRemoveRoom(sender, args);
		} else if (subcommand.equals("setroom")) {
			this.handleSetRoom(sender, args);
		}
		
		return true;
	}
}

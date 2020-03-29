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
	private final RoomManager rooms;
	private static final String serverUrl = "https://localhost:8080";
	private final Map<String, Location> targetedBlocks = new HashMap<String, Location>();

	public DiscordCommandExecutor (CraftWork plugin, RoomManager rooms) {
		this.plugin = plugin;
		this.rooms = rooms;
	}
	
	private void handleGetUserCode (CommandSender sender) {
		return;
	}

	private void handleMakeRoom (CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Must be a player to use mkroom");
			return;
		}
		
		final Player player = (Player) sender;

		final Location target = player.getTargetBlock(null, 10).getLocation();
		if (target == null) {
			player.sendMessage("You must be pointing at a block to use mkroom");
			return;
		}
		
		if (this.targetedBlocks.containsKey(player.getName())) {
			final Location p1 = this.targetedBlocks.get(player.getName());
			this.targetedBlocks.remove(player.getName());
			
			final List<Room> rooms = this.rooms.getRooms();
			final Map<String, Object> room = new HashMap<String, Object>();

			room.put("p1", p1);
			room.put("p2", target);
			room.put("room", "");

			rooms.add(new Room(room));
			this.rooms.saveRooms(rooms);

			player.sendMessage("Saved room. Use `/craftwork setroom` to bind to a voice channel");

			return;
		} else {
			this.targetedBlocks.put(player.getName(), target);
			player.sendMessage("Set corner 1 for this room. Run mkroom again to complete.");
			return;
		}
	}

	private void handleRemoveRoom (CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Must be a player to use rmroom");
			return;
		}
		
		final Player player = (Player) sender;

		final Location target = player.getTargetBlock(null, 10).getLocation();
		if (target == null) {
			player.sendMessage("You must be pointing at a block to use rmroom");
			return;
		}
		
		final List<Room> rooms = this.rooms.getRooms();
		final int index = this.rooms.getTargetedRoomIndex(target);

		if (index < 0) {
			player.sendMessage("Could not find any room at target location");
			return;
		}

		rooms.remove(index);
		this.rooms.saveRooms(rooms);
		player.sendMessage("Removed the targeted room");
		return;
	}

	private void handleSetRoom (CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Must be a player to use setroom");
			return;
		}
		
		if (args.length < 2) {
			sender.sendMessage("Usage: /craftwork setroom <voice channel>");
		}
		
		final Player player = (Player) sender;

		final Location target = player.getTargetBlock(null, 10).getLocation();
		if (target == null) {
			player.sendMessage("You must be pointing at a block to use setroom");
			return;
		}
		
		final List<Room> rooms = this.rooms.getRooms();
		final int index = this.rooms.getTargetedRoomIndex(target);

		if (index < 0) {
			player.sendMessage("Could not find any room at target location");
			return;
		}

		rooms.get(index).setRoom(args[1]);
		this.rooms.saveRooms(rooms);
		player.sendMessage("Set the voce channel for this room to `" + args[1] + "`");
		return;
	}
	
	private void handleGetRoom (CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Must be a player to use getroom");
			return;
		}
		
		if (args.length < 2) {
			sender.sendMessage("Usage: /craftwork getroom <voice channel>");
		}
		
		final Player player = (Player) sender;

		final Location target = player.getTargetBlock(null, 10).getLocation();
		if (target == null) {
			player.sendMessage("You must be pointing at a block to use setroom");
			return;
		}
		
		final Room room = this.rooms.getTargetedRoom(target);
		if (room == null) {
			player.sendMessage("Could not find room at target location");
			return;
		}
		
		player.sendMessage("Room for channel `" + room.getRoom() + "`" +
			"; Spans " + room.getP1().toString() + " to " + room.getP2().toString());
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
		} else if (subcommand.equals("getroom")) {
			this.handleGetRoom(sender, args);
		}
		
		return true;
	}
}

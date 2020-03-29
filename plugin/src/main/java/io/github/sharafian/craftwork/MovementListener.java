package io.github.sharafian.craftwork;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;

public class MovementListener implements Listener {
	private CraftWork plugin;
	private RoomManager rooms;
	private HashMap<String, String> occupiedRoom = new HashMap<String, String>();
	
	public MovementListener (CraftWork plugin, RoomManager rooms) {
		this.plugin = plugin;
		this.rooms = rooms;
		
	}
	
	private void placeInRoom (Player player) {
		final String occupied = this.occupiedRoom.get(player.getName());
		final Room room = this.rooms.getTargetedRoom(player.getLocation());
		
		if (room == null) {
			if (occupied != null) {
				this.occupiedRoom.remove(player.getName());
				player.sendMessage("Exited room `" + occupied + "`");
			}
			return;
		}

		if (!room.getRoom().equals(occupied)) {
			this.occupiedRoom.put(player.getName(), room.getRoom());

			if (occupied == null) {
				player.sendMessage("Entered room `" + room.getRoom() + "`");
			} else {
				player.sendMessage("Moved from room `" + occupied + "` to `" + room.getRoom() + "`");
			}

			return;
		}
	}
	
	public void initPlayers () {
		final Iterator<Player> players = ((Collection<Player>) this.plugin.getServer().getOnlinePlayers()).iterator();
		while (players.hasNext()) {
			this.placeInRoom(players.next());
		}
	}
	
	@EventHandler
	public void onLogin (PlayerLoginEvent ev) {
		this.placeInRoom(ev.getPlayer());
	}
	
	@EventHandler
	public void onQuit (PlayerQuitEvent ev) {
		this.occupiedRoom.remove(ev.getPlayer().getName());
	}

	@EventHandler
	public void onMove (PlayerMoveEvent ev) {
		this.placeInRoom(ev.getPlayer());
	}
}

package io.github.sharafian.craftwork;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
	
	private void httpMoveToRoom (Player player, String room) {
		final String serverKey = this.plugin.getConfig().getString("server.key");
		if (serverKey == null || serverKey.equals("")) {
			this.plugin.getLogger().info("Cannot move player; server is not linked to discord");
			return;
		}

		try {
			URL getUserCode = new URL(
					Util.serverUrl + 
					"/player/" + player.getName() + "/room/name");

			HttpURLConnection conn = (HttpURLConnection) getUserCode.openConnection();
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Authorization", "Bearer " + serverKey);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.connect();

			OutputStream os = conn.getOutputStream();
			try {
			    byte[] input = ("{\"name\":\"" + room + "\"}").getBytes("utf-8");
			    os.write(input, 0, input.length);           
			} finally {
				os.close();
			}

			player.sendMessage("Status:" + conn.getResponseCode());
		} catch (Exception e) {
			player.sendMessage("Something went wrong: " + e.getMessage());
		}
	}
	
	private void placeInRoom (Player player) {
		final String occupied = this.occupiedRoom.get(player.getName());
		final Room room = this.rooms.getTargetedRoom(player.getLocation());
		
		if (room == null) {
			if (occupied != null) {
				this.occupiedRoom.remove(player.getName());
				this.httpMoveToRoom(player, "general");
				player.sendMessage("Exited room `" + occupied + "`");
			}
			return;
		}

		if (!room.getRoom().equals(occupied)) {
			this.occupiedRoom.put(player.getName(), room.getRoom());
			this.httpMoveToRoom(player, room.getRoom());

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

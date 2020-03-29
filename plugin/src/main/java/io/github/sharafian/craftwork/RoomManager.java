package io.github.sharafian.craftwork;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class RoomManager {
	private CraftWork plugin;
	public RoomManager (CraftWork plugin) {
		this.plugin = plugin;
	}

	public List<Room> getRooms () {
		List<Room> rooms = (List<Room>) this.plugin.getConfig().getList("rooms");
		if (rooms == null) {
			rooms = new ArrayList<Room>();
		}
		
		return rooms;
	}
	
	public void saveRooms (List<Room> rooms) {
		this.plugin.getConfig().set("rooms", rooms);
		this.plugin.saveConfig();
	}
	
	public int getTargetedRoomIndex (Location target) {
		final List<Room> rooms = this.getRooms();
		for (int i = 0; i < rooms.size(); ++i) {
			final Room room = rooms.get(i);
			if (room == null) continue;
			if (room.contains(target)) {
				return i;
			}
		}
		return -1;
	}
	
	public Room getTargetedRoom (Location target) {
		final int index = this.getTargetedRoomIndex(target);

		if (index < 0) return null;
		return this.getRooms().get(index);
	}
}

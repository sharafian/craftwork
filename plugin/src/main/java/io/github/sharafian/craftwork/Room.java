package io.github.sharafian.craftwork;

import java.util.Map;
import java.util.HashMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.Location;

public class Room implements ConfigurationSerializable {
	public Location p1;
	public Location p2;
	public String room;

	Room (Map<String, Object> map) {
		this.p1 = (Location) map.get("p1");
		this.p2 = (Location) map.get("p2");
		this.room = (String) map.get("room");
	}
	
	public Map<String, Object> serialize () {
		final Map<String, Object> serialized = new HashMap<String, Object>();
		
		serialized.put("p1", this.p1);
		serialized.put("p2", this.p2);
		serialized.put("room", this.room);

		return serialized;
	}
}

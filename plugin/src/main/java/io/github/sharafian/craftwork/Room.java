package io.github.sharafian.craftwork;

import java.util.Map;
import java.lang.Math;
import java.util.HashMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.Location;

public class Room implements ConfigurationSerializable {
	public Location p1;
	public Location p2;
	public String room;

	public Room (Map<String, Object> map) {
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
	
	public void setRoom (String room) {
		this.room = room;
	}
	
	public String getRoom () { return this.room; }
	public Location getP1 () { return this.p1; }
	public Location getP2 () { return this.p2; }
	
	private double getMinX () { return Math.min(this.p1.getX(), this.p2.getX()); }
	private double getMaxX () { return Math.max(this.p1.getX(), this.p2.getX()); }
	private double getMinY () { return Math.min(this.p1.getY(), this.p2.getY()); }
	private double getMaxY () { return Math.max(this.p1.getY(), this.p2.getY()); }
	private double getMinZ () { return Math.min(this.p1.getZ(), this.p2.getZ()); }
	private double getMaxZ () { return Math.max(this.p1.getZ(), this.p2.getZ()); }
	
	public boolean contains (Location l) {
		if (this.p1 == null || this.p2 == null) {
			return false;
		}

		final double lx = Math.floor(l.getX());
		final double ly = Math.floor(l.getY());
		final double lz = Math.floor(l.getZ());

		final boolean xBound = lx >= this.getMinX() && lx <= this.getMaxX();
		final boolean yBound = ly >= this.getMinY() && ly <= this.getMaxY();
		final boolean zBound = lz >= this.getMinZ() && lz <= this.getMaxZ();

		return xBound && yBound && zBound;
	}
}

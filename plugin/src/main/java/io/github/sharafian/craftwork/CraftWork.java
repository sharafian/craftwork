package io.github.sharafian.craftwork;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftWork extends JavaPlugin {
	private RoomManager rooms = new RoomManager(this);

	@Override
	public void onEnable () {
		ConfigurationSerialization.registerClass(Room.class);
		this.getCommand("craftwork").setExecutor(new DiscordCommandExecutor(this, rooms));
		getServer().getPluginManager().registerEvents(new MovementListener(this, rooms), this);
		getLogger().info("CraftWork enabled");
	}

	@Override
	public void onDisable () {
		getLogger().info("CraftWork disabled");
	}
}

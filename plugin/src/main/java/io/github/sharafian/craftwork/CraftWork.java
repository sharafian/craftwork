package io.github.sharafian.craftwork;

import org.bukkit.plugin.java.JavaPlugin;

public class CraftWork extends JavaPlugin {
	@Override
	public void onEnable () {
		this.getCommand("discord").setExecutor(new DiscordCommandExecutor(this));
		getLogger().info("CraftWork enabled");
	}

	@Override
	public void onDisable () {
		getLogger().info("CraftWork disabled");
	}
}

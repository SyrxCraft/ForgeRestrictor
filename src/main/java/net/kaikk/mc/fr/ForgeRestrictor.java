package net.kaikk.mc.fr;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ForgeRestrictor extends JavaPlugin {

	private static ForgeRestrictor instance;
	Config config;
	EventListener eventListener;
	CommandExec commandExec;

	@Override
	public void onEnable() {
		instance=this;

		this.config=new Config();
		
		this.eventListener=new EventListener(this);
		this.getServer().getPluginManager().registerEvents(this.eventListener, this);
		
		this.commandExec=new CommandExec(this);
		this.getCommand("forgerestrictor").setExecutor(this.commandExec);
		
		for (ProtectionPlugins pp : ProtectionPlugins.values()) {
			Plugin plugin = this.getServer().getPluginManager().getPlugin(pp.toString());
			if (plugin!=null && plugin.isEnabled()) {
				this.eventListener.pluginEnable(pp.toString());
			}
		}

	}

	public static ForgeRestrictor getInstance() {
		return instance;
	}

	public static void setInstance(ForgeRestrictor instance) {
		ForgeRestrictor.instance = instance;
	}
}

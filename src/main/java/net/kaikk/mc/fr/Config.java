package net.kaikk.mc.fr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

class Config {

	final static String configFilePath = "plugins" + File.separator + "ForgeRestrictor" + File.separator + "config.yml";

	private final File configFile;
	FileConfiguration config;
	
	List<ListedItem> whitelist;
	List<ListedRangedItem> ranged;
	List<ListedRangedItem> aoe;

	int confiscateTicks;
	
	Config() {
		this.configFile = new File(configFilePath);
		this.config = YamlConfiguration.loadConfiguration(this.configFile);
		this.load();
	}

	void load() {

		ProtectionPlugins.GriefPrevention.setEnabled(this.config.getBoolean("Protection.GriefPrevention", true));
		ProtectionPlugins.PlotSquared.setEnabled(this.config.getBoolean("Protection.PlotSquared", true));
		ProtectionPlugins.WorldGuard.setEnabled(this.config.getBoolean("Protection.WorldGuard", true));
		
		this.whitelist= new ArrayList<>();
		for (String serialized : this.config.getStringList("Whitelist")) {
			try {
				this.whitelist.add(new ListedItem(serialized));
			} catch (Exception e) {
				ForgeRestrictor.getInstance().getLogger().warning("Invalid Whitelist element in config: "+serialized);
			}
		}
		
		this.ranged= new ArrayList<>();
		for (String serialized : this.config.getStringList("Ranged")) {
			try {
				this.ranged.add(new ListedRangedItem(serialized));
			} catch (Exception e) {
				ForgeRestrictor.getInstance().getLogger().warning("Invalid Ranged element in config: "+serialized);
			}
		}
		
		this.aoe=new ArrayList<ListedRangedItem>();
		for (String serialized : this.config.getStringList("AoE")) {
			try {
				this.aoe.add(new ListedRangedItem(serialized));
			} catch (Exception e) {
				ForgeRestrictor.getInstance().getLogger().warning("Invalid AoE element in config: "+serialized);
			}
		}
		
		this.confiscateTicks = this.config.getInt("ConfiscateTicks", 3);
		
		this.save();
	}
	
	void save() {
		try {
			this.config.set("Protection.GriefPreventionPlus", ProtectionPlugins.GriefPrevention.isEnabled());
			this.config.set("Protection.PlotSquared", ProtectionPlugins.PlotSquared.isEnabled());
			this.config.set("Protection.WorldGuard", ProtectionPlugins.WorldGuard.isEnabled());
			
			this.config.set("Whitelist", serializeListedItemList(this.whitelist));
			this.config.set("Ranged", serializeListedItemList(this.ranged));
			this.config.set("AoE", serializeListedItemList(this.aoe));
			
			this.config.save(this.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void addWhitelistItem(ListedItem item) {
		this.whitelist.add(item);
		this.save();
	}
	
	void addRangedItem(ListedRangedItem item) {
		this.ranged.add(item);
		this.save();
	}
	
	void addAoEItem(ListedRangedItem item) {
		this.aoe.add(item);
		this.save();
	}
	
	ListedItem getWhitelistItem(Material material, Byte data, String world) {
		ForgeRestrictor.getInstance().getLogger().info("getWhitelistItem");
		return getListedItem(this.whitelist, material, data, world);
	}
	
	ListedRangedItem getRangedItem(Material material, Byte data, String world) {
		return (ListedRangedItem) getListedItem(this.ranged, material, data, world);
	}
	
	ListedRangedItem getAoEItem(Material material, Byte data, String world) {
		return (ListedRangedItem) getListedItem(this.aoe, material, data, world);
	}
	
	private static ListedItem getListedItem(List<? extends ListedItem> list, Material material, Byte data, String world) {
		for(ListedItem item : list) {
			if (item.equals(material, data, world)) {
				return item;
			}
		}
		return null;
	}
	
	
	ListedItem matchWhitelistItem(Material material, Byte data, String world) {
		return matchListedItem(this.whitelist, material, data, world);
	}
	
	ListedRangedItem matchRangedItem(Material material, Byte data, String world) {
		return (ListedRangedItem) matchListedItem(this.ranged, material, data, world);
	}
	
	ListedRangedItem matchAoEItem(Material material, Byte data, String world) {
		return (ListedRangedItem) matchListedItem(this.aoe, material, data, world);
	}
	
	private static ListedItem matchListedItem(List<? extends ListedItem> list, Material material, Byte data, String world) {
		for(ListedItem item : list) {
			if (item.match(material, data, world)) {
				return item;
			}
		}
		return null;
	}
	
	
	boolean removeWhitelistItem(Material material, Byte data, String world) {
		return removeListedItem(this.whitelist, material, data, world);
	}
	boolean removeRangedItem(Material material, Byte data, String world) {
		return removeListedItem(this.ranged, material, data, world);
	}
	boolean removeAoEItem(Material material, Byte data, String world) {
		return removeListedItem(this.aoe, material, data, world);
	}
	
	private static boolean removeListedItem(List<? extends ListedItem> list, Material material, Byte data, String world) {
		Iterator<? extends ListedItem> iterator=list.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().equals(material, data, world)) {
				iterator.remove();
				return true;
			}
		}

		return false;
	}
	
	private static String[] serializeListedItemList(List<? extends ListedItem> list) {
		String[] serializedList = new String[list.size()];
		int i=0;
		for (ListedItem item : list) {
			serializedList[i]=item.serialize();
			i++;
		}
		return serializedList;
	}
}
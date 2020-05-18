package net.kaikk.mc.fr;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;

class ListedRangedItem extends ListedItem {
	int range;
	
	ListedRangedItem(Material material, Byte data, String world, int range) {
		super(material, data, world);	
		this.range=range;
	}
	
	ListedRangedItem(Map<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			switch(entry.getKey()) {
			case "material":
				this.material= Material.valueOf((String) entry.getValue());
				break;
			case "data":
				this.data= (byte) entry.getValue();
				break;
			case "world":
				this.world=(String) entry.getValue();
				break;
			case "range":
				this.range=(int) entry.getValue();
				break;
			}
		}
	}
	
	ListedRangedItem(String serialized) {
		String[] arr=serialized.split(":");
		if (arr.length<2) {
			throw new IllegalArgumentException();
		}
		
		this.material = Material.valueOf(arr[0]);
		this.range = Integer.parseInt(arr[1]);
		
		if (arr.length>2) {
			this.data = (arr[2].equals("*")?null:Byte.valueOf(arr[2]));
			if (arr.length>3) {
				this.world = (arr[3].equals("*")?null:arr[3]);
			}
		}
	}
	
	String serialize() {
		return material+":"+range+(data==null&&world==null ? "" : (data==null ? ":*" : ":"+data)+(world==null ? "" : ":"+world));
	}
	
	@Override
	public String toString() {
		return material+":"+(data==null?"*":data)+" ("+range+")"+(world==null ? "" : " ["+world+"]");
	}
}

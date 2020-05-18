package net.kaikk.mc.fr;

import org.bukkit.Material;

class ListedItem {
	Material material;
	Byte data;
	String world;
	
	ListedItem(){}
	
	ListedItem(Material material, Byte data, String world) {
		this.material = material;
		this.data = data;
		this.world = world;
	}
	
	ListedItem(String serialized){
		String[] arr=serialized.split(":");
		if (arr.length<1) {
			throw new IllegalArgumentException();
		}
		
		this.material = Material.valueOf(arr[0]);
		if (arr.length>1) {
			this.data = (arr[1].equals("null")?null:Byte.valueOf(arr[1]));
			if (arr.length>2) {
				this.world = (arr[2].equals("null")?null:arr[2]);
			}
		}
	}
	
	String serialize() {
		return material+":"+(data==null&&world==null ? "" : (data==null ? ":*" : ":"+data)+(world==null ? "" : ":"+world));
	}
	
	boolean match(Material material, Byte data, String world) {
		return material== this.material && (this.data==null || this.data.equals(data)) && (this.world==null || this.world.equals(world));
	}
	
	boolean equals(Material material, Byte data, String world) {
		return material== this.material && (this.data==data || (this.data!=null && this.data.equals(data))) && (this.world==world || (this.world!=null && this.world.equals(world)));
	}

	@Override
	public String toString() {
		return material+":"+(data==null?"*":data)+(world==null ? "" : " ["+world+"]");
	}
}

package net.kaikk.mc.fr;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class ConfiscatedInventory {
	private final Player player;
	private final ItemStack[] is;
	private boolean isReleased;
	
	ConfiscatedInventory(Player player) {
		this.player = player;
		this.is = player.getInventory().getContents().clone();
		player.getInventory().clear();
		player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
	}
	
	void release() {
		if (!this.isReleased) {
			if (this.player.isOnline()) {
				this.player.getInventory().setContents(this.is);
				this.isReleased=true;
			} else {
				ForgeRestrictor.getInstance().getLogger().warning("Couldn't restore "+this.player.getName()+"'s confiscated inventory, content: "+this.inventoryContent());
			}
		}
	}
	
	Player getPlayer() {
		return player;
	}
	
	boolean isPlayerInventoryEmpty() {
		for(ItemStack is : this.player.getInventory()) {
			if (is!=null && is.getType()!=Material.AIR) {
				return false;
			}
		}
		return true;
	}
	
	private String inventoryContent() {
		StringBuilder sb = new StringBuilder();
		for (ItemStack is : this.is) {
			if (is!=null) {
				sb.append(is.getType().toString()).append("(").append(is.getAmount()).append(") ");
			}
		}
		return sb.toString();
	}
}

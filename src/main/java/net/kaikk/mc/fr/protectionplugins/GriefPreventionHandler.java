package net.kaikk.mc.fr.protectionplugins;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.CreateClaimResult;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.kaikk.mc.fr.ProtectionHandler;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class GriefPreventionHandler implements ProtectionHandler {
	DataStore dataStore;

	public GriefPreventionHandler() {
		this.dataStore = GriefPrevention.instance.dataStore;
	}
	
	private Claim getClaim(Location location){
		return dataStore.getClaimAt(location,false,null);
	}

	@Override
	public boolean canBuild(Player player, Location location) {
		Claim claim = getClaim(location);

		if (claim==null) {
			return true;
		}
		
		String reason=claim.allowBuild(player, Material.STONE);
		
		if (reason==null) {
			return true;
		}
		
		player.sendMessage(reason);
		
		return false;
	}

	@Override
	public boolean canAccess(Player player, Location location) {
		Claim claim = getClaim(location);

		if (claim==null) {
			return true;
		}
		
		String reason=claim.allowAccess(player);
		
		if (reason==null) {
			return true;
		}
		
		player.sendMessage(reason);
		
		return false;
	}

	@Override
	public boolean canUse(Player player, Location location) {
		return canBuild(player, location);
	}
	
	@Override
	public boolean canOpenContainer(Player player, Block block) {

		Claim claim = getClaim(block.getLocation());

		if (claim==null) {
			return true;
		}
		
		String reason=claim.allowContainers(player);
		
		if (reason==null) {
			return true;
		}
		
		player.sendMessage(reason);
		
		return false;
	}

	@Override
	public boolean canInteract(Player player, Location location) {
		return canBuild(player, location);
	}


	@Override
	public boolean canAttack(Player damager, Entity damaged) {
		return canBuild(damager, damaged.getLocation());
	}

	@Override
	public boolean canProjectileHit(Player player, Location location) {
		return canBuild(player, location);
	}
	
	@Override
	public boolean canUseAoE(Player player, Location location, int range) {

		Claim claim = getClaim(location);

		if (claim!=null) {

			if (claim.allowBuild(player, Material.STONE) != null) {
				// you have no perms on this claim, disallow.
				return false;
			}
			
			if (claimContains(claim, location, range)) {
				// the item's range is in this claim's boundaries. You're allowed to use this item.
				return true;
			}
			
			if (claim.parent !=null) {
				// you're on a subdivision
				if (claim.parent.allowBuild(player, Material.STONE) != null) {
					// you have no build permission on the top claim... disallow.
					return false;
				}
			
				if (claimContains(claim, location, range)) {
				    // the restricted item's range is in the top claim's boundaries. you're allowed to use this item.
					return true;
				}
			}
		}
		
		// the range is not entirely on a claim you're trusted in... we need to search for nearby claims too.
		for (Claim nClaim : posClaimsGet(location, range).values()) {
			if (nClaim.allowBuild(player, Material.STONE)!=null) {
				// if not allowed on claims in range, disallow.
				return false;
			}
		}
		return true;
	}
	
	static boolean claimContains(Claim claim, Location location, int range) {
		return (claim.contains(new Location(location.getWorld(), location.getBlockX()+range, 0, location.getBlockZ()+range), true, false) &&
				claim.contains(new Location(location.getWorld(), location.getBlockX()-range, 0, location.getBlockZ()-range), true, false));
	}

	@Override
	public String getName() {
		return "GriefPreventionPlus";
	}
	
	
	public Map<Long, Claim> posClaimsGet(Location loc, int blocksRange) {

		final Map<Long, Claim> claims = new HashMap<>();

		int lx = loc.getBlockX() - blocksRange;
		int lz = loc.getBlockZ() - blocksRange;

		int gx = loc.getBlockX() + blocksRange;
		int gz = loc.getBlockZ() + blocksRange;

		final Claim validArea;

		CreateClaimResult createClaimResult = GriefPrevention.instance.dataStore.createClaim(loc.getWorld(), lx, lz,100,100,gx, gz, null, null, null, null, true);

		if(createClaimResult.succeeded) return claims;


		if((validArea = createClaimResult.claim) != null){

			lx = lx >> 8;
			lz = lz >> 8;
			gx = gx >> 8;
			gz = gz >> 8;

			for (int i = lx; i <= gx; i++) {
				for (int j = lz; j <= gz; j++) {

					Collection<Claim> claimMap = dataStore.getClaims(i,j);

					if (claimMap != null) {
						for (final Claim claim : claimMap) {

							if (overlaps(claim, validArea)) {
								claims.put(claim.getID(), claim);
							}
						}
					}
				}
			}

		}

		return claims;
	}

	private boolean overlaps(Claim claim, Claim claim2){

		try {

			Method method = Claim.class.getDeclaredMethod("overlaps", Claim.class);
			method.setAccessible(true);

			return (boolean) method.invoke(claim, claim2);

		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return false;
	}


}

package net.kaikk.mc.fr.protectionplugins;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import net.kaikk.mc.fr.ProtectionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlotSquaredHandler implements ProtectionHandler {

    PlotSquared plotSquared = PlotSquared.get();

    @Override
    public String getName() {
        return "PlotSquared";
    }

    @Override
    public boolean canBuild(Player player, Location location) {

        PlotArea plotArea = plotSquared.getApplicablePlotArea(fromBukkit(location));
        Plot plot = plotArea.getPlot(fromBukkit(location));

        if(plot != null){
            return plot.isOwner(player.getUniqueId()) || isTrusted(plot, player) || isAddAvailable(plot, player);
        }

        return false;
    }

    @Override
    public boolean canAccess(Player player, Location location) {
        return canBuild(player, location);
    }

    @Override
    public boolean canUse(Player player, Location location) {
        return canBuild(player, location);
    }

    @Override
    public boolean canOpenContainer(Player player, Block block) {
        return canBuild(player, block.getLocation());
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
        return false;
    }

    public com.plotsquared.core.location.Location fromBukkit(Location location){
        return new com.plotsquared.core.location.Location(location.getWorld().getName(),location.getBlockX(),location.getBlockY(),location.getBlockZ(),location.getYaw(),location.getPitch());
    }

    public boolean isTrusted(Plot plot, Player player){
        return plot.getTrusted().contains(player.getUniqueId());
    }

    public boolean isAddAvailable(Plot plot, Player player){
        
        boolean isAnyOwnerOnline = false;
        
        for(UUID uuid : plot.getOwners()){
            Player p;
            if((p = Bukkit.getPlayer(uuid)) != null &&  p.isOnline()){
                isAnyOwnerOnline = true; 
                break;
            }
        }
        
        return plot.isAdded(player.getUniqueId()) && isAnyOwnerOnline;
    }

}

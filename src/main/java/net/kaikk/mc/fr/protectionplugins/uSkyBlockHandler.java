package net.kaikk.mc.fr.protectionplugins;

import net.kaikk.mc.fr.ProtectionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;
import us.talabrek.ultimateskyblock.uSkyBlock;


public class uSkyBlockHandler implements ProtectionHandler {

    uSkyBlockAPI uSkyBlockAPI = (us.talabrek.ultimateskyblock.api.uSkyBlockAPI) Bukkit.getPluginManager().getPlugin("uSkyBlock");

    public boolean isOnIsland(Player player){
        return (((uSkyBlock)uSkyBlockAPI).playerIsOnIsland(player));
    }

    @Override
    public String getName() {
        return "uSkyBlock";
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        return isOnIsland(player);
    }

    @Override
    public boolean canAccess(Player player, Location location) {
        return isOnIsland(player);
    }

    @Override
    public boolean canUse(Player player, Location location) {
        return isOnIsland(player);
    }

    @Override
    public boolean canOpenContainer(Player player, Block block) {
        return isOnIsland(player);
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return isOnIsland(player);
    }

    @Override
    public boolean canAttack(Player damager, Entity damaged) {
        return isOnIsland(damager);
    }

    @Override
    public boolean canProjectileHit(Player player, Location location) {
        return isOnIsland(player);
    }

    @Override
    public boolean canUseAoE(Player player, Location location, int range) {

        int x1 = location.getBlockX() + range;
        int z1 = location.getBlockZ() + range;

        int x2 = location.getBlockX() - range;
        int z2 = location.getBlockZ() - range;

        for(int i = x2; i < x1; i++) {
            for (int k = z2; k < z1; k++) {
                if((((uSkyBlock)uSkyBlockAPI).islandAtLocation(new Location(location.getWorld(), i,100,k)))){
                    return isOnIsland(player);
                }
            }
        }

        return false;
    }

}

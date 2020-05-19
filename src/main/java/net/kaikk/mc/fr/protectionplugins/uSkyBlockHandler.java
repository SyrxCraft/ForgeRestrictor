package net.kaikk.mc.fr.protectionplugins;

import net.kaikk.mc.fr.ProtectionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import us.talabrek.ultimateskyblock.api.IslandInfo;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;
import us.talabrek.ultimateskyblock.uSkyBlock;


public class uSkyBlockHandler implements ProtectionHandler {

    uSkyBlockAPI uSkyBlockAPI = (us.talabrek.ultimateskyblock.api.uSkyBlockAPI) Bukkit.getPluginManager().getPlugin("uSkyBlock");

    private int islandSize = -1;

    public uSkyBlockHandler(){

        islandSize = uSkyBlockAPI.getConfig().getInt("options.island.protectionRange", -1);

        if(islandSize == -1) islandSize = 64; // Maybe it's a good number

    }

    @Override
    public String getName() {
        return "uSkyBlock";
    }

    public IslandInfo getIslandInfo(Location location){

        if(uSkyBlockAPI != null){
            System.out.println("!");
            return (((uSkyBlock)uSkyBlockAPI).getIslandInfo(location));
        }
        System.out.println("@");
        return null;
    }

    public boolean isMember(Player player, IslandInfo islandInfo){
        System.out.println(islandInfo.getMembers());
        return islandInfo.getMembers().contains(player.getName());
    }

    public boolean haveAccess(Player player, Location location){
        IslandInfo islandInfo;

        System.out.println("A");

        if((islandInfo = getIslandInfo(location)) != null){

            System.out.println("B");

            return isMember(player, islandInfo);
        }

        System.out.println("C");
        return false;
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        return haveAccess(player, location);
    }

    @Override
    public boolean canAccess(Player player, Location location) {
        return haveAccess(player, location);
    }

    @Override
    public boolean canUse(Player player, Location location) {
        return haveAccess(player, location);
    }

    @Override
    public boolean canOpenContainer(Player player, Block block) {
        return haveAccess(player, block.getLocation());
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return haveAccess(player, location);
    }

    @Override
    public boolean canAttack(Player damager, Entity damaged) {
        return haveAccess(damager, damaged.getLocation());
    }

    @Override
    public boolean canProjectileHit(Player player, Location location) {
        return haveAccess(player, location);
    }

    @Override
    public boolean canUseAoE(Player player, Location location, int range) {

        IslandInfo islandInfo;
        Location isCenter = null;

        if((islandInfo = getIslandInfo(location)) != null){
            isCenter = islandInfo.getIslandLocation();
        }

        if(isCenter != null){

            int lx = isCenter.getBlockX() - islandSize;
            int lz = isCenter.getBlockZ() - islandSize;

            int ux = isCenter.getBlockX() + islandSize;
            int uz = isCenter.getBlockZ() + islandSize;

            if(isOnBounds(isCenter, lx, lz, ux, uz)){
                return canAccess(player, location);
            }

        }

        int x1 = location.getBlockX() + range;
        int z1 = location.getBlockZ() + range;

        int x2 = location.getBlockX() - range;
        int z2 = location.getBlockZ() - range;

        IslandInfo predominantIsland = null;

        for(int i = x2; i < x1; i++) {
            for (int k = z2; k < z1; k++) {
                if(predominantIsland != null){

                    int lx = predominantIsland.getIslandLocation().getBlockX() - islandSize;
                    int lz = predominantIsland.getIslandLocation().getBlockZ() - islandSize;
                    int ux = predominantIsland.getIslandLocation().getBlockX() + islandSize;
                    int uz = predominantIsland.getIslandLocation().getBlockZ() + islandSize;

                    if(isOnBounds(predominantIsland.getIslandLocation(), lx, lz, ux, uz)){
                        return canAccess(player, location);
                    }

                }else {
                    predominantIsland = getIslandInfo(new Location(location.getWorld(), i, location.getBlockY(), k));
                }
            }
        }

        return false;
    }

    public boolean isOnBounds(Location location, int lx, int lz, int ux, int uz){
       return (location.getBlockX() > lx && location.getBlockX() < ux && location.getBlockZ() > lz && location.getBlockZ() < uz);
    }
}

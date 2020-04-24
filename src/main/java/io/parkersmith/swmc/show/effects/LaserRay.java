package io.parkersmith.swmc.show.effects;

import java.util.ArrayList;
import java.util.List;

import io.parkersmith.swmc.show.Main;
import org.bukkit.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class LaserRay implements Runnable {
    private List<Location> locs;

    private String hex;

    private BukkitTask task;

    private int ticks;

    public void showLaser(String hex, World world, double x, double y, double z, float pitch, float yaw, double length, int ticks) {
        Location startLoc = new Location(world, x, y, z);
        startLoc.setYaw(yaw);
        startLoc.setPitch(pitch);
        this.locs = getLine(startLoc, length);
        this.hex = hex;
        this.ticks = ticks;
        this.task = Main.getInstance().bs.runTaskTimerAsynchronously((Plugin)Main.getInstance(), this, 0L, 1L);
    }

    public List<Location> getLine(Location loc, double range) {
        List<Location> locs = new ArrayList<>();
        for (double i = 1.0D; i <= range; i += 0.25D)
            locs.add(loc.clone().add(loc.getDirection().multiply(i)));
        return locs;
    }

    public static void particleEffect(List<Location> locs, String hex) {
        for (Location loc : locs)
            displayColoredParticle(loc, hex);
    }

    public static void displayColoredParticle(Location loc, String hexVal) {
        int R = 0;
        int G = 0;
        int B = 0;
        if (hexVal.contains("flame"))
            loc.getWorld().spawnParticle(Particle.FLAME, loc, 0);
        if (hexVal.contains("happyVillager"))
            loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 0);
        if (hexVal.contains("fireworksSpark"))
            loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 0);
        if (hexVal.startsWith("#")) {
            if (hexVal.length() <= 6) {
                R = Integer.valueOf(hexVal.substring(0, 2), 16).intValue();
                G = Integer.valueOf(hexVal.substring(2, 4), 16).intValue();
                B = Integer.valueOf(hexVal.substring(4, 6), 16).intValue();
                if (R <= 0)
                    R = 1;
            } else if (hexVal.length() <= 7 && hexVal.substring(0, 1).equals("#")) {
                R = Integer.valueOf(hexVal.substring(1, 3), 16).intValue();
                G = Integer.valueOf(hexVal.substring(3, 5), 16).intValue();
                B = Integer.valueOf(hexVal.substring(5, 7), 16).intValue();
                if (R <= 0)
                    R = 1;
            }
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(R, G, B), 1);
            loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, dustOptions);
        }
    }

    public void run() {
        particleEffect(this.locs, this.hex);
        this.ticks--;
        if (this.ticks <= 0)
            this.task.cancel();
    }
}
package io.parkersmith.swmc.show.fountains;

import io.parkersmith.swmc.show.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Fountain {
    private String name;
    private String size;
    private Location location;
    private float motionx = 0.0F;
    private float motiony = 0.0F;
    private float motionz = 0.0F;
    private double delay = 0.0;
    private boolean isOn = false;
    private String material;

    public Fountain(String name, String size, Location location, String material, Double delay) {
        this.name = name;
        this.size = size;
        this.location = location;
        this.material = material;
        this.delay = delay;
    }

    public void setMotion(float motionx, float motiony, float motionz) {
        this.motionx = motionx;
        this.motiony = motiony;
        this.motionz = motionz;
    }

    public void setBlock(String material) {
        this.material = material;
    }

    public String getName() {
        return this.name;
    }

    public float getMotionX() {
        return this.motionx;
    }

    public float getMotionY() {
        return this.motiony;
    }

    public float getMotionZ() {
        return this.motionz;
    }

    public void reset() {
        this.motionx = 0.0F;
        this.motiony = 0.0F;
        this.motionz = 0.0F;
    }

    public void start() {
        if (!this.isOn) {
            this.isOn = true;
            (new BukkitRunnable() {
                public void run() {
                    if (Fountain.this.isOn) {
                        Location loc = Fountain.this.location;
                        Fountain fon = Fountain.this;
                        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                        String command;
                        switch(Fountain.this.size) {
                            case "small":
                                // /summon falling_block ~ ~10 ~ {CustomName:"iron",Motion:[1.0,0.5,1.0],Invisible:1,BlockState:{Name:"minecraft:barrier"},Data:0,Time:-999999,DropItem:0,Passengers:[{id:"armor_stand",CustomName:"amourSand",Tags:["fofe"],Invisible:1,Invulnerable:1,Small:1,ArmorItems:[{},{},{},{id:"minecraft:stone",Count:1b}]}]}
                                command = "summon minecraft:falling_block " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " {TileID:8,BlockState:{Name:\"minecraft:barrier\"},Time:1,DropItem:0,Motion:[" + fon.getMotionX() + "," + fon.getMotionY()+ "," + fon.getMotionZ() + "],Passengers:[{id:\"armor_stand\",Tags:[\"fofe\"],Invisible:1,Invulnerable:1,ArmorItems:[{},{},{},{id:\"minecraft:" + Fountain.this.material.toLowerCase() + "\",Count:1b}]}]}";
                                Bukkit.dispatchCommand(console, command);
                                break;
                            case "xs":
                                command = "summon minecraft:falling_block " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " {TileID:8,BlockState:{Name:\"minecraft:barrier\"},Time:1,DropItem:0,Motion:[" + fon.getMotionX() + "," + fon.getMotionY()+ "," + fon.getMotionZ() + "],Passengers:[{id:\"armor_stand\",Tags:[\"fofe\"],Invisible:1,Invulnerable:1,Small:1,ArmorItems:[{},{},{},{id:\"minecraft:" + Fountain.this.material.toLowerCase() + "\",Count:1b}]}]}";
                                Bukkit.dispatchCommand(console, command);
                                break;
                            default:
                                command = "summon minecraft:falling_block " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " {TileID:8,BlockState:{Name:\"minecraft:" + Fountain.this.material.toLowerCase() + "\"},Time:1,DropItem:0,Motion:[" + fon.getMotionX() + "," + fon.getMotionY()+ "," + fon.getMotionZ() + "]}";
                                Bukkit.dispatchCommand(console, command);
                        }
                    } else {
                        this.cancel();
                    }

                }
            }).runTaskTimer(Main.getInstance(), 0L, (long) this.delay);
        } else {
            this.isOn = false;
        }

    }

    // /summon minecraft:falling_block -902 64 -221 {TileID:8,BlockState:{Name:"minecraft:BLUE_GLAZED_TERRACOTTA"},Time:1,DropItem:0,Motion:[1.0,1.0,1.0]}"



    public void stop() {
        this.isOn = false;
    }


}

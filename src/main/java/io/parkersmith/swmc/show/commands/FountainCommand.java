package io.parkersmith.swmc.show.commands;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import io.parkersmith.swmc.show.Main;
import io.parkersmith.swmc.show.fountains.Fountain;
import io.parkersmith.swmc.show.fountains.FountainManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class FountainCommand implements TabExecutor {
    private FountainManager manager;
    private static final String[] COMMANDS = new String[0];

    public FountainCommand(FountainManager manager) {
        this.manager = manager;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            this.sendHelpMessage(sender);
        } else {
            String name;
            Fountain fountain;
            switch(args[0]) {
                case "remove":
                        if (sender.hasPermission("swmc.show.use")) {
                            if (args.length == 2) {
                                if (this.manager.getFountainByName(args[1]) != null) {
                                    name = args[1];
                                    fountain = this.manager.getFountainByName(name);
                                    fountain.stop();
                                    this.manager.removeFountain(fountain);
                                    sender.sendMessage(ChatColor.GREEN + "Successfully removed the fountain named " + name + "!");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "There isn't a fountain named " + args[1] + "!");
                                }

                                return true;
                            } else {
                                sender.sendMessage(ChatColor.DARK_AQUA + "/fountain remove <name>");
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
                            return true;
                        }
                case "info":
                        if (sender.hasPermission("swmc.show.use")) {
                            if (args.length == 2) {
                                if (this.manager.getFountainByName(args[1]) != null) {
                                    name = args[1];
                                    fountain = this.manager.getFountainByName(name);
                                    sender.sendMessage("Name: " + fountain.getName() + "\nMotionX: " + fountain.getMotionX() + "\nMotionY: " + fountain.getMotionY() + "\nMotionZ: ");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "There isn't a fountain named " + args[1] + "!");
                                }

                                return true;
                            } else {
                                sender.sendMessage(ChatColor.DARK_AQUA + "/fountain info <name>");
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
                            return true;
                        }
                case "list":
                        if (sender.hasPermission("swmc.show.use")) {
                            sender.sendMessage(String.join(", ", (Iterable)this.manager.getFountains().stream().map((fountainx) -> {
                                return fountainx.getName();
                            }).collect(Collectors.toList())));
                        } else {
                            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
                        }

                        return true;
                case "spawn":
                        if (sender.hasPermission("swmc.show.use")) {
                            if (args.length == 12) {
                                if (this.manager.getFountainByName(args[1]) == null) {
                                    name = args[1];
                                    String size = args[2];
                                    double x = Double.parseDouble(args[3]);
                                    double y = Double.parseDouble(args[4]);
                                    double z = Double.parseDouble(args[5]);
                                    World world = Bukkit.getWorld(args[6]);
                                    String block = args[7];
                                    float motionx = Float.parseFloat(args[8]);
                                    float motiony = Float.parseFloat(args[9]);
                                    float motionz = Float.parseFloat(args[10]);
                                    Double delay = Double.parseDouble(args[11]);
                                    Location spawn = new Location(world, x, y, z);

                                    Fountain newFountain = spawnFountain(name, size, spawn, motionx, motiony, motionz, block, delay);
                                    this.manager.addFountain(newFountain);
                                    sender.sendMessage(ChatColor.BLUE + "Successfully spawned a fountain named " + ChatColor.DARK_AQUA + name + ChatColor.BLUE + "!");

                                } else {
                                    sender.sendMessage(ChatColor.RED + "There is already a fountain named " + args[1] + "!");
                                }

                                return true;
                            } else {
                                sender.sendMessage(ChatColor.BLUE + "/fountain spawn [name] [size] [x] [y] [z] [world] [block] [motion x] [motion y] [motion z] [delay]");
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
                            return true;
                        }
                default:
                    this.sendHelpMessage(sender);
            }

            }


        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> completions = new ArrayList();
        List<String> commands = new ArrayList(Arrays.asList(COMMANDS));
        Player player = (Player)sender;
        Location playerLocation = player.getLocation();
        String partialCommand = null;
        if (args.length == 1) {
            partialCommand = args[0];
            if (sender.hasPermission("swmc.show.use")) {
                commands.add("spawn");
            }

            if (sender.hasPermission("swmc.show.use")) {
                commands.add("move");
            }

            if (sender.hasPermission("swmc.show.use")) {
                commands.add("remove");
            }

            if (sender.hasPermission("swmc.show.use")) {
                commands.add("info");
            }

            if (sender.hasPermission("swmc.show.use")) {
                commands.add("list");
            }

            StringUtil.copyPartialMatches(partialCommand, commands, completions);
        }

        if (args[0].equalsIgnoreCase("spawn") && sender.hasPermission("swmc.show.use")) {
            if (args.length == 3) {
                partialCommand = args[2];
                commands.add("regular");
                commands.add("small");
                commands.add("xs");
                StringUtil.copyPartialMatches(partialCommand, commands, completions);
            } else if (args.length == 4) {
                partialCommand = args[3];
                commands.add(String.valueOf(Math.round(playerLocation.getX())));
                StringUtil.copyPartialMatches(partialCommand, commands, completions);
            } else if (args.length == 5) {
                partialCommand = args[4];
                commands.add(String.valueOf(Math.round(playerLocation.getY())));
                StringUtil.copyPartialMatches(partialCommand, commands, completions);
            } else if (args.length == 6) {
                partialCommand = args[5];
                commands.add(String.valueOf(Math.round(playerLocation.getZ())));
                StringUtil.copyPartialMatches(partialCommand, commands, completions);
            } else if (args.length == 7) {
                partialCommand = args[6];
                String worldName = player.getWorld().getName();
                if (Bukkit.getWorld(worldName) != null) {
                    commands.add(worldName);
                    StringUtil.copyPartialMatches(partialCommand, commands, completions);
                }
            } else if (args.length == 7) {
                List<String> materials = new ArrayList();
                Material[] var14;
                int var13 = (var14 = Material.values()).length;

                for(int var12 = 0; var12 < var13; ++var12) {
                    Material material = var14[var12];
                    materials.add(material.toString());
                }

                partialCommand = args[6];
                StringUtil.copyPartialMatches(partialCommand, materials, completions);
            }
        } else {
            Fountain fountain;
            Iterator var11;
            if (args[0].equalsIgnoreCase("remove") && sender.hasPermission("swmc.show.use") || args[0].equalsIgnoreCase("info") && sender.hasPermission("swmc.show.use")) {
                if (args.length == 2) {
                    var11 = Main.getFountainManager().getFountains().iterator();

                    while(var11.hasNext()) {
                        fountain = (Fountain)var11.next();
                        commands.add(fountain.getName());
                    }

                    partialCommand = args[1];
                    StringUtil.copyPartialMatches(partialCommand, commands, completions);
                }
            }
        }

        Collections.sort(completions);
        return completions;
    }

    public static Fountain spawnFountain(String name, String size, Location loc, float motionx, float motiony, float motionz, String block, Double delay) {
        Fountain fountain = new Fountain(name, size, loc, block, delay);
        fountain.setMotion(motionx, motiony, motionz);
        fountain.start();
        return fountain;
    }

    public void sendHelpMessage(CommandSender sender) {
            sender.sendMessage(ChatColor.DARK_AQUA + "/fountain spawn [name] [size] [x] [y] [z] [world] [block] [motion x] [motion y] [motion z] [delay]");
            sender.sendMessage(ChatColor.DARK_AQUA + "/fountain remove [name]");
            sender.sendMessage(ChatColor.DARK_AQUA + "/fountain info [name]");
    }
}

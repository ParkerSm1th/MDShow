package io.parkersmith.swmc.show.commands;

import io.parkersmith.swmc.show.Main;
import io.parkersmith.swmc.show.effects.LaserRay;
import io.parkersmith.swmc.show.fountains.Fountain;
import io.parkersmith.swmc.show.fountains.FountainManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class LaserCommand implements TabExecutor {

    private static final String[] COMMANDS = new String[0];

    public LaserCommand() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            this.sendHelpMessage(sender);
        } else {
            String name;
            Fountain fountain;
            if (args.length == 9) {
                if (!(sender instanceof Player)) {
                    String hex = args[0];
                    double x = Double.parseDouble(args[1]);
                    double y = Double.parseDouble(args[2]);
                    double z = Double.parseDouble(args[3]);
                    String worldname = args[4];
                    int yaw = Integer.parseInt(args[5]);
                    int pitch = Integer.parseInt(args[6]);
                    int length = Integer.parseInt(args[7]);
                    int ticks = Integer.parseInt(args[8]);
                    (new LaserRay()).showLaser(hex, Bukkit.getWorld(worldname), x, y, z, pitch, yaw, length, ticks);
                } else if (sender.hasPermission("swmc.show.use")) {
                    sender.sendMessage(ChatColor.RED + "You can only execute this command in command blocks or show files.");
                } else {
                    sender.sendMessage("You do not have permission to execute this command!");
                }
            } else {
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
            commands.add("red");
            commands.add("blue");
            commands.add("#");
            StringUtil.copyPartialMatches(partialCommand, commands, completions);
        } else if (args.length == 2) {
            partialCommand = args[1];
            commands.add(String.valueOf(Math.round(playerLocation.getX())));
            StringUtil.copyPartialMatches(partialCommand, commands, completions);
        } else if (args.length == 3) {
            partialCommand = args[2];
            commands.add(String.valueOf(Math.round(playerLocation.getY())));
            StringUtil.copyPartialMatches(partialCommand, commands, completions);
        } else if (args.length == 4) {
            partialCommand = args[3];
            commands.add(String.valueOf(Math.round(playerLocation.getZ())));
            StringUtil.copyPartialMatches(partialCommand, commands, completions);
        } else if (args.length == 5) {
            partialCommand = args[4];
            String worldName = player.getWorld().getName();
            if (Bukkit.getWorld(worldName) != null) {
                commands.add(worldName);
                StringUtil.copyPartialMatches(partialCommand, commands, completions);
            }
        } else if (args.length == 6) {
            partialCommand = args[5];
            commands.add(String.valueOf(Math.round(playerLocation.getYaw())));
            StringUtil.copyPartialMatches(partialCommand, commands, completions);
        } else if (args.length == 7) {
            partialCommand = args[6];
            commands.add(String.valueOf(Math.round(playerLocation.getPitch())));
            StringUtil.copyPartialMatches(partialCommand, commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }

    public void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Correct usage: " + ChatColor.DARK_AQUA + "/laser [hex] [x] [y] [z] [world] [yaw] [pitch] [length] [ticks]");
    }

}

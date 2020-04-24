package io.parkersmith.swmc.show.commands;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import io.parkersmith.swmc.show.Main;
import io.parkersmith.swmc.show.Show;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;

public class ShowCommand implements CommandExecutor {
    private Main mainInstance;
    private static HashMap<String, Show> shows;
    private String infoString = ChatColor.BLUE.toString() + ChatColor.BOLD.toString() + "Magical Dreams Shows: \n" + ChatColor.GRAY + "-----------------------\n" + ChatColor.DARK_AQUA + "/show create [name] - " + ChatColor.GREEN

            .toString() + "This will create a new show, in the MDShows Folder, it will create a .yml file, and that is where you code the show\n" + ChatColor.DARK_AQUA + "/show start [name] - " + ChatColor.GREEN

            .toString() + "Starts the show\n" + ChatColor.DARK_AQUA + "/show stop [name] - " + ChatColor.GREEN
            .toString() + "Stops the show\n" + ChatColor.DARK_AQUA + "/show reload [name] - " + ChatColor.GREEN
            .toString() + "Reloads the show and updates any changes\n" + ChatColor.DARK_AQUA + "/show list - " + ChatColor.GREEN
            .toString() + "Lists all available shows\n" + ChatColor.DARK_AQUA + "/ticks [seconds] - " + ChatColor.GREEN
            .toString() + "Converts seconds into ticks\n" + ChatColor.DARK_AQUA + "/repeat [times] [interval] [command] - " + ChatColor.GREEN
            .toString() + "Repeats a command [times] with a [interval] delay in between\n" + ChatColor.DARK_AQUA + "/fountain - " + ChatColor.GREEN
            .toString() + "Shows the help menu for fountains\n" + ChatColor.DARK_AQUA + "/laser - " + ChatColor.GREEN
            .toString() + "Shows the help menu for lasers";

    public ShowCommand(Main mainInstance)
    {
        this.mainInstance = mainInstance;
        shows = new HashMap();

        loadExistingShows();
    }

    public boolean onCommand(CommandSender commandSender, Command command, String cmdLabel, String[] args)
    {
        if ((commandSender instanceof Player))
        {
            Player sender = (Player)commandSender;
            if (!sender.hasPermission("swmc.show.use"))
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return false;
            }
        }
        if (args.length != 2)
        {
            if ((args.length == 1) &&
                    (args[0].equalsIgnoreCase("list")))
            {
                StringBuilder stringBuilder = new StringBuilder(ChatColor.BLUE + "Available shows: \n");
                for (String showEntry : shows.keySet())
                {
                    Show show = (Show)shows.get(showEntry);

                    stringBuilder.append(ChatColor.DARK_AQUA + showEntry + ChatColor.BLUE + " - Not running");
                    stringBuilder.append("\n");
                }
                String availableShows = stringBuilder.toString().trim();
                commandSender.sendMessage(availableShows);
                return true;
            }
            commandSender.sendMessage(this.infoString);
            return false;
        }
        String subCommand = args[0];
        String showName = args[1];
        if (subCommand.equalsIgnoreCase("create"))
        {
            if (shows.get(showName) != null)
            {
                commandSender.sendMessage(ChatColor.BLUE + "That show already exists, you can find it at: " + ChatColor.DARK_AQUA + showName + ".yml");
                return false;
            }
            Show show = new Show(showName, this.mainInstance);

            boolean successful = show.init();

            if (successful) {
                shows.put(showName, show);
                commandSender.sendMessage(ChatColor.GREEN + showName +" has been created! Check the SWMCShows folder.");
            } else {
                commandSender.sendMessage(ChatColor.RED + "An error occurred creating the show.");
            }
            return successful;
        }
        if (subCommand.equalsIgnoreCase("start"))
        {
            Show show = (Show)shows.get(showName);
            if (show == null)
            {
                commandSender.sendMessage(ChatColor.BLUE + "That show does not exist, to create it, you can use " + ChatColor.DARK_AQUA + "/show create " + showName);
                return false;
            }
            if (show.isRunning())
            {
                commandSender.sendMessage(ChatColor.BLUE + "Show " + ChatColor.DARK_AQUA + showName + ChatColor.BLUE + " is already running.");
                return false;
            }
            boolean successful = show.start();

            if (!successful) commandSender.sendMessage(ChatColor.RED + "An error occurred starting the show.");


            return successful;
        }
        if (subCommand.equalsIgnoreCase("stop"))
        {
            Show show = (Show)shows.get(showName);
            if (show == null)
            {
                commandSender.sendMessage(ChatColor.BLUE + "That show does not exist, to create it, you can use " + ChatColor.DARK_AQUA + "/show create " + showName);
                return false;
            }
            if (!show.isRunning())
            {
                commandSender.sendMessage(ChatColor.BLUE + "Show " + ChatColor.DARK_AQUA + showName + ChatColor.BLUE + " is not running.");
                return false;
            }
            boolean successful = show.stop(true);

            if (!successful) commandSender.sendMessage(ChatColor.RED + "An error occurred when trying to stop the show.");

            return successful;
        }
        if (subCommand.equalsIgnoreCase("reload"))
        {
            Show show = (Show)shows.get(showName);
            if (show == null)
            {
                commandSender.sendMessage(ChatColor.BLUE + "That show does not exist, to create it, you can use " + ChatColor.DARK_AQUA + "/show create " + showName);
                return false;
            }
            if (show.isRunning())
            {
                commandSender.sendMessage(ChatColor.BLUE + "Show " + ChatColor.DARK_AQUA + showName + ChatColor.BLUE + " is currently running. Please use" + ChatColor.DARK_AQUA + "/show stop " + showName + ChatColor.BLUE + ", and try again.");
                return false;
            }
            boolean successful = show.init();

            if (!successful) commandSender.sendMessage(ChatColor.RED + "An error occurred trying to reload the show");

            if (successful) commandSender.sendMessage(ChatColor.GREEN + showName +" has been reloaded!");

            return successful;
        }
        commandSender.sendMessage(this.infoString);

        return false;
    }

    private void loadExistingShows() {
        File mainDirectory = new File(this.mainInstance.getDataFolder().getAbsolutePath());
        if (!mainDirectory.exists()) {
            mainDirectory.mkdir();
        }
        Iterator iterator = FileUtils.iterateFiles(mainDirectory, null, false);
        while (iterator.hasNext()) {
            File currentFile = (File)iterator.next();

            String name = currentFile.getName();
            if (name.endsWith(".yml")) {
                name = name.substring(0, name.length() - 4);
            }
            Show show = new Show(name, this.mainInstance);
            boolean successful = show.init();
            if (successful) {
                shows.put(name, show);
            }
        }
    }

    public void stopRunningShows()
    {
        for (String showEntry : shows.keySet())
        {
            Show show = (Show)shows.get(showEntry);

            show.stop(false);
        }
    }
}


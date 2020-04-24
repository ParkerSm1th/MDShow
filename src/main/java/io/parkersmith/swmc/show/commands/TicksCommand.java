package io.parkersmith.swmc.show.commands;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TicksCommand implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        if ((commandSender instanceof Player))
        {
            Player sender = (Player)commandSender;
            if (!sender.hasPermission("swmc.show.use"))
            {
                sender.sendMessage(ChatColor.RED + "You do not have the permissions to execute this command.");
                return false;
            }
        }
        if (args.length != 1)
        {
            commandSender.sendMessage(ChatColor.BLUE + "Please use /ticks [seconds]");
            return false;
        }
        if (!isNumeric(args[0]))
        {
            commandSender.sendMessage(ChatColor.BLUE + "Please use /ticks [seconds]");
            return false;
        }
        Double ticks = Double.valueOf(Double.parseDouble(args[0]) * 20.0D);
        commandSender.sendMessage(ChatColor.DARK_AQUA + args[0] + ChatColor.BLUE +" second(s) is " + ChatColor.DARK_AQUA + ticks.intValue() + ChatColor.BLUE +" ticks.");

        return false;
    }

    private boolean isNumeric(String input)
    {
        try
        {
            Double.parseDouble(input);

            return true;
        }
        catch (Exception e) {}
        return false;
    }
}

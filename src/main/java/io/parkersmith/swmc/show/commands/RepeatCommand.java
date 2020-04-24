package io.parkersmith.swmc.show.commands;


import io.parkersmith.swmc.show.CommandRunnable;
import io.parkersmith.swmc.show.Main;
import org.apache.logging.log4j.core.Core;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RepeatCommand
        implements CommandExecutor
{
    private Main mainInstance;

    public RepeatCommand(Main mainInstancePassed)
    {
        this.mainInstance = mainInstancePassed;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        if ((commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You can only execute this command in command blocks or show files.");
            return false;
        }
        if (args.length < 3)
        {
            commandSender.sendMessage(ChatColor.RED + "/repeat [times] [interval] [command]");
            return false;
        }
        if ((!isNumeric(args[0])) || (!isNumeric(args[1])))
        {
            commandSender.sendMessage(ChatColor.RED + "/repeat [times] [interval] [command] \nBoth [times] and [interval] should be a number.");
            return false;
        }
        int times = Integer.parseInt(args[0]);
        int interval = Integer.parseInt(args[1]);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++)
        {
            stringBuilder.append(args[i]);
            stringBuilder.append(" ");
        }
        String passedCommand = stringBuilder.toString().trim();

        CommandRunnable commandRunnable = new CommandRunnable(passedCommand, times, false, "Null");
        commandRunnable.runTaskTimer(this.mainInstance, 0L, interval);

        return false;
    }

    private boolean isNumeric(String input)
    {
        try
        {
            Integer.parseInt(input);

            return true;
        }
        catch (Exception e) {}
        return false;
    }
}

package io.parkersmith.swmc.show;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandRunnable
        extends BukkitRunnable
{
    private String command;
    private int times;
    private int counter = 0;
    private boolean end;
    private String showName;

    public CommandRunnable(String passedCommand, int timesPassed, boolean end, String showName)
    {
        this.command = passedCommand;
        this.times = timesPassed;
        this.end = end;
        this.showName = showName;
    }

    public void run()
    {
        this.counter += 1;
        if (this.counter > this.times)
        {
            if (this.end) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "show stop " + this.showName);
            }
            cancel();

            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.command);
    }
}

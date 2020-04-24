package io.parkersmith.swmc.show;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class SectionRunnable extends BukkitRunnable {
    private ArrayList<String> commands;

    public SectionRunnable(ArrayList<String> commands)
    {
        this.commands = commands;
    }

    public void run()
    {
        for (String command : this.commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
        this.commands.clear();
        cancel();
    }
}
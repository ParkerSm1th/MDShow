package io.parkersmith.swmc.show;


import io.parkersmith.swmc.show.commands.*;
import io.parkersmith.swmc.show.fountains.FountainManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.VersionCommand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;


public class Main extends JavaPlugin implements Listener {
    private ShowCommand showCommand;
    private static Main instance;
    private static FountainManager fountainManager;
    public static List<FallingBlock> fallingBlocks = new ArrayList();

    public BukkitScheduler bs;

    public void onEnable()
    {
        bs = getServer().getScheduler();
        instance = this;
        fountainManager = new FountainManager();
        registerCommands();
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void onDisable()
    {
        stopRunningShows();
        instance = null;
        fountainManager.disable();
        fountainManager = null;
    }

    private void registerCommands()
    {
        getCommand("repeat").setExecutor(new RepeatCommand(this));
        getCommand("ticks").setExecutor(new TicksCommand());

        this.showCommand = new ShowCommand(this);
        getCommand("show").setExecutor(this.showCommand);
        getCommand("fountain").setExecutor(new FountainCommand(fountainManager));
        getCommand("fountain").setTabCompleter(new FountainCommand(fountainManager));
        getCommand("laser").setExecutor(new LaserCommand());
        getCommand("laser").setTabCompleter(new LaserCommand());
    }

    private void stopRunningShows()
    {
        this.showCommand.stopRunningShows();
    }

    @EventHandler
    public void onFountainLand(EntityChangeBlockEvent event)
    {
        if ((event.getEntity() instanceof FallingBlock))
        {
            if (fountainManager.getFountains() != null) {
                FallingBlock fb = (FallingBlock) event.getEntity();
                for (Entity e : fb.getPassengers()) {
                    fb.removePassenger(e);
                    e.remove();
                }
                event.getEntity().remove();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event)
    {
        Command versionCommand = new VersionCommand("version");
        List<String> blockedCmds = new ArrayList();
        String command = event.getBuffer();
        blockedCmds.add("/? ");
        blockedCmds.add("/bukkit:? ");
        blockedCmds.add("/" + versionCommand.getName() + " ");
        blockedCmds.add("/bukkit:" + versionCommand.getName() + " ");
        for (String alias : versionCommand.getAliases())
        {
            blockedCmds.add("/bukkit:" + alias + " ");
            blockedCmds.add("/" + alias + " ");
        }
        if ((blockedCmds.contains(command)) && (!event.getSender().isOp()))
        {
            event.getSender().sendMessage(ChatColor.RED + "You do not have permission to do this.");
            event.setCancelled(true);
        }
    }

    public static Main getInstance()
    {
        return instance;
    }

    public static FountainManager getFountainManager()
    {
        return fountainManager;
    }
}

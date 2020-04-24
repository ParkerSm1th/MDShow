package io.parkersmith.swmc.show;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

public class Show
{
    private boolean isRunning;
    private String name;
    private File file;
    private FileConfiguration fileConfiguration;
    private HashMap<String, SectionRunnable> sections;
    private HashMap<CommandRunnable, Integer> repeatables;
    private HashMap<String, HashMap<CommandRunnable, Integer>> repeatablesWithTicks;
    private Main mainInstance;

    public Show(String name, Main mainInstance)
    {
        this.mainInstance = mainInstance;

        this.isRunning = false;
        this.name = name;
    }

    public boolean isRunning()
    {
        return this.isRunning;
    }

    public boolean start() {
        this.repeatablesWithTicks = new HashMap();
        this.repeatables = new HashMap();
        this.sections = new HashMap();
        init();
        try {
            for (String tick : this.sections.keySet()) {
                int ticks = Integer.parseInt(tick);

                SectionRunnable section = (SectionRunnable)this.sections.get(tick);
                section.runTaskLater(this.mainInstance, ticks);
            }
            SectionRunnable section;
            for (Iterator<String> tick = this.repeatablesWithTicks.keySet().iterator(); tick.hasNext();) {
                String tick2 = (String) tick.next();
                HashMap<CommandRunnable, Integer> repeatablesMap = (HashMap)this.repeatablesWithTicks.get(tick2);
                for (CommandRunnable commandRunnable : repeatablesMap.keySet()) {
                    commandRunnable.runTaskTimer(this.mainInstance, Integer.parseInt(tick2), ((Integer)this.repeatables.get(commandRunnable)).intValue());
                }
            }
            String tick2;
            this.isRunning = true;
            Bukkit.broadcast(ChatColor.BLUE.toString() + "Show " + ChatColor.DARK_AQUA + this.name + ChatColor.BLUE
                    .toString() + " has started!", "swmc.show.use");

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean stop(boolean broadcast)
    {
        try
        {
            for (String tick : this.sections.keySet())
            {
                SectionRunnable section = (SectionRunnable)this.sections.get(tick);

                section.cancel();
            }
            for (CommandRunnable repeatable : this.repeatables.keySet()) {
                repeatable.cancel();
            }
            this.isRunning = false;
            if (broadcast) {
                Bukkit.broadcast(ChatColor.BLUE.toString() + "Show " + ChatColor.RED + this.name + ChatColor.BLUE
                        .toString() + " has ended!", "swmc.show.use");
            }
            return true;
        }
        catch (Exception e) {}
        return false;
    }

    public boolean init()
    {
        File fileDirectory = new File(this.mainInstance.getDataFolder().getAbsolutePath());
        if (!fileDirectory.exists()) {
            fileDirectory.mkdir();
        }
        this.file = new File(this.mainInstance.getDataFolder().getAbsolutePath() + File.separator + this.name + ".yml");
        if (!this.file.exists()) {
            try
            {
                this.file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();

                return false;
            }
        }
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.file);

        this.sections = new HashMap();
        this.repeatables = new HashMap();
        this.repeatablesWithTicks = new HashMap();
        for (String key : this.fileConfiguration.getKeys(false))
        {
            ArrayList<String> commandsToPass = new ArrayList();
            List<String> sectionCommands = this.fileConfiguration.getStringList(key);
            HashMap<CommandRunnable, Integer> newRepeatables = new HashMap();

            boolean containsShowStop = sectionCommands.contains("show stop " + this.name);

            int repeatLastIndex = -1;
            if (containsShowStop) {
                for (int i = 0; i < sectionCommands.size(); i++) {
                    if (((String)sectionCommands.get(i)).startsWith("repeat")) {
                        repeatLastIndex = i;
                    }
                }
            }
            for (int k = 0; k < sectionCommands.size(); k++)
            {
                String command = (String)sectionCommands.get(k);
                String[] commandSplit = command.split(" ");
                if (commandSplit[0].equalsIgnoreCase("repeat"))
                {
                    int times = Integer.parseInt(commandSplit[1]);
                    int interval = Integer.parseInt(commandSplit[2]);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 3; j < commandSplit.length; j++)
                    {
                        stringBuilder.append(commandSplit[j]);
                        stringBuilder.append(" ");
                    }
                    String passedCommand = stringBuilder.toString().trim();
                    if ((k == repeatLastIndex) && (containsShowStop))
                    {
                        CommandRunnable commandRunnable = new CommandRunnable(passedCommand, times, true, this.name);
                        this.repeatables.put(commandRunnable, Integer.valueOf(interval));
                        newRepeatables.put(commandRunnable, Integer.valueOf(interval));
                    }
                    else
                    {
                        CommandRunnable commandRunnable = new CommandRunnable(passedCommand, times, false, this.name);
                        this.repeatables.put(commandRunnable, Integer.valueOf(interval));
                        newRepeatables.put(commandRunnable, Integer.valueOf(interval));
                    }
                }
                else
                {
                    commandsToPass.add(command);
                }
            }
            this.repeatablesWithTicks.put(key, newRepeatables);
            if (repeatLastIndex != -1) {
                commandsToPass.remove("show stop " + this.name);
            }
            SectionRunnable section = new SectionRunnable(commandsToPass);
            this.sections.put(key, section);
        }
        try
        {
            this.fileConfiguration.save(this.file);

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}

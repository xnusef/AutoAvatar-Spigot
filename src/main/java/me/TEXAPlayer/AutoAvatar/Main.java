package me.TEXAPlayer.AutoAvatar;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Main extends JavaPlugin
{
    private File configFile;
    private File avatarFile;
    public FileConfiguration config;
    public FileConfiguration avatar;

    private Methods methods;
    public Timer timer;
    private Join join;
    private Death death;
    private Server server;
    private PluginManager pm;

    @Override
    public void onEnable() 
    {
        server = Bukkit.getServer();
        pm = server.getPluginManager();
        methods = new Methods(this);
        timer = new Timer(this, methods);
        join = new Join(this, methods);
        death = new Death(this, methods);
        pm.registerEvents(join, this);
        pm.registerEvents(death, this);
        ManageFiles();
        if (config.getBoolean("enable-cycle"))
            server.getScheduler().scheduleSyncRepeatingTask(
                this,
                timer.RepetitionCycle(),
                20 * config.getLong("delay"),
                20 * config.getLong("interval")
            );
    }

    private void ManageFiles()
    {
        ManageConfig();
        ManageAvatar();
    }

    private void ManageConfig()
    {
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists())
        {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        config = new YamlConfiguration();
        try { config.load(configFile); }
        catch (IOException | InvalidConfigurationException e) { e.printStackTrace(); }
    }

    private void ManageAvatar()
    {
        avatarFile = new File(getDataFolder(), "avatar.yml");
        if (!avatarFile.exists())
        {
            avatarFile.getParentFile().mkdirs();
            saveResource("avatar.yml", false);
        }

        avatar = new YamlConfiguration();
        try { avatar.load(avatarFile); }
        catch (IOException | InvalidConfigurationException e) { e.printStackTrace(); }
    }

    public void SaveAvatar()
    {
        try { avatar.save(avatarFile); }
        catch (IOException e) { e.printStackTrace(); }
    }
}
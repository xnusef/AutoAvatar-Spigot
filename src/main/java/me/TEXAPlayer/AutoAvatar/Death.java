package me.TEXAPlayer.AutoAvatar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Death implements Listener
{
    Main main;
    Methods m;
    DateTimeFormatter formatter;

    public Death(Main plugin, Methods methods) 
    {
        main = plugin;
        m = methods;
        formatter =  DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); 
    }

    @EventHandler
    public void OnPlayerDeath(PlayerDeathEvent event)
    {
        Player ePlayer = event.getEntity();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.parse(main.avatar.getString("start-date"), formatter);;
        LocalDateTime immune = start.plusDays(main.config.getInt("death-immunity"));

        if (now.isAfter(immune) && main.avatar.getString("avatar").equals(ePlayer.getName()))
        {
            if (!main.avatar.getString("avatar").equals(""))
                m.RemoveAvatar();
        }
    }
}

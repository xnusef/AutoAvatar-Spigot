package me.TEXAPlayer.AutoAvatar;

import com.projectkorra.projectkorra.event.BendingPlayerCreationEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Join implements Listener
{
    Main main;
    Methods m;

    public Join(Main plugin, Methods methods) 
    {
        main = plugin;
        m = methods;
    }

    @EventHandler
    public void onPlayerJoin(BendingPlayerCreationEvent event)
    {
        Player player = event.getBendingPlayer().getPlayer();
        if (main.avatar.getStringList("name-list").contains(player.getName()))
            m.RemoveExistingAvatar(player);
    }
}

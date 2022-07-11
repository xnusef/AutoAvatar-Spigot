package me.TEXAPlayer.AutoAvatar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.projectkorra.projectkorra.BendingPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Timer
{
    Main main;
    Methods m;
    DateTimeFormatter formatter;
    
    public Timer(Main plugin, Methods methods)
    {
        main = plugin;
        m = methods;
        formatter =  DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");  
    }

    public Runnable RepetitionCycle()
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                CheckAvatarExpiration();
            }
        };
    }

    private void CheckAvatarExpiration()
    {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.parse(main.avatar.getString("end-date"), formatter); 
        if (now.isAfter(end))
        {
            Player player = m.RandomPlayer();
            if (!main.avatar.getString("avatar").equals(""))
                m.RemoveAvatar();
            if (Bukkit.getOnlinePlayers().size() >= main.config.getInt("player-amount") && 
                main.avatar.getBoolean("choose-new") && player != null)
                NewAvatar(player);
        }
    }

    public void NewAvatar(Player player)
    {
        m.SetAvatarInfo(player);
        m.GiveAllElements(BendingPlayer.getBendingPlayer(player));
        m.AddToGroup(player, "avatar");
        m.NewAvatarNotification(player);
    }
}

package me.TEXAPlayer.AutoAvatar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class Messaging implements PluginMessageListener
{
    Main main = Main.GetInstance();
    Methods m;
    public DateTimeFormatter formatter;

    public Messaging(Methods methods)
    {
        main.server.getMessenger().registerOutgoingPluginChannel(main, "autoavatar:newavatar");
        main.server.getMessenger().registerIncomingPluginChannel(main, "autoavatar:newavatar", this);
        this.m = methods;
        formatter =  DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");  
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message)
    {
        if (CheckAvatarExpiration())
        {
            if (!main.avatar.getString("avatar").equals(""))
                m.RemoveAvatar();
            if (main.avatar.getBoolean("choose-new") && player != null)
                m.NewAvatar(player);
        }
    }
    
    private boolean CheckAvatarExpiration()
    {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.parse(main.avatar.getString("end-date"), formatter); 
        return now.isAfter(end);
    }
}

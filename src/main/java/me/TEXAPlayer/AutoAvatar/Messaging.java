package me.TEXAPlayer.AutoAvatar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

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
    public void onPluginMessageReceived(String channel, Player executer, byte[] message)
    {
        if (!channel.equals("autoavatar:newavatar"))
            return;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        String playerName = in.readUTF();
        String playerElement = in.readUTF();
        main.server.getLogger().info("recieved: + " + subChannel + " + " + playerName + " + " + playerElement);
        Player player = Bukkit.getPlayer(playerName);
        if (CheckAvatarExpiration() && !main.avatar.getString("avatar").equals(""))
            m.RemoveAvatar();
        if (subChannel.equals("removeExistingAvatarInfo"))
            m.RemoveExistingAvatarInfo(playerName);
        if (subChannel.equals("newAvatar") && main.avatar.getBoolean("choose-new") && player != null)
            m.NewAvatar(player);
        if (subChannel.equals("newInfo") && main.avatar.getBoolean("choose-new") && playerName != null && !playerName.isEmpty() && playerElement != null && !playerElement.isEmpty() && !playerElement.equalsIgnoreCase("noInfo"))
            m.NewAvatarOffline(playerName, playerElement);
    }

    public void SendMessage(String channel, Player player, List<String> data)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        String messageSent = "sent: + "; 
        for (String info : data)
        {
            messageSent += info + " + ";
            out.writeUTF(info);
        }
        main.server.getLogger().info(messageSent);
        player.sendPluginMessage(main, channel, out.toByteArray());
    }
    
    private boolean CheckAvatarExpiration()
    {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.parse(main.avatar.getString("end-date"), formatter); 
        return now.isAfter(end);
    }
}

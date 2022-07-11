package me.TEXAPlayer.AutoAvatar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.Element.SubElement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;

public class Methods
{
    Main main;

    public Methods(Main plugin)
    {
        main = plugin;
    }

    public Player RandomPlayer()
    {
        List<Player> weightedPlayers = WeightedList();
        if (weightedPlayers.isEmpty())
            return null;
        int index = (int) Math.floor(Math.random() * weightedPlayers.size()); 
        return weightedPlayers.get(index);
    }

    private List<Player> WeightedList()
    {
        List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
        List<Player> playersToRandomize = new ArrayList<Player>();
        for (Player player : players)
            if (player.hasPermission("avatar.choosable") && HasPermissionStartingWith(player, "avatar.weight."))
            {
                String number = GetPermissionStartingWith(player, "avatar.weight.").replace("avatar.weight.", "");
                for (int i = 1 ; i <= Integer.parseInt(number); i++)
                    playersToRandomize.add(player);
            }
        return playersToRandomize;
    }

    private boolean HasPermissionStartingWith(Player player, String match)
    {
        for(PermissionAttachmentInfo permission : player.getEffectivePermissions())
            if(permission.getPermission().startsWith(match) && permission.getValue())
                return true;
        return false;
    }

    private String GetPermissionStartingWith(Player player, String match)
    {
        for(PermissionAttachmentInfo permission : player.getEffectivePermissions())
            if(permission.getPermission().startsWith(match) && permission.getValue())
                return permission.getPermission();
        return null;
    }

    public void RemoveAvatar()
    {
        main.avatar.set("choose-new", true);
        main.SaveAvatar();
        List<String> names = main.avatar.getStringList("name-list");
        List<String> elements = main.avatar.getStringList("element-list");
        for (Player player : Bukkit.getOnlinePlayers())
            if (player.getName().equals(main.avatar.getString("avatar")))
            {
                BackToBender(player, main.avatar.getString("previous-element"));
                RemoveAvatarInfo();
                return;
            }
        if (names.contains(main.avatar.getString("avatar")))
            return;
        AddPlayerToList(names, elements);
    }

    public void SetAvatarInfo(Player player)
    {
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        LocalDateTime now = LocalDateTime.now();
        main.avatar.set("choose-new", false);
        main.avatar.set("avatar", player.getName());
        main.avatar.set("start-date", now.format(main.timer.formatter));
        main.avatar.set
        (
            "end-date",
            now.plusDays(main.config.getInt("avatar-duration")).format(main.timer.formatter)
        );
        main.avatar.set("previous-element", ElementToString(bPlayer));
        main.SaveAvatar();
    }

    private String ElementToString(BendingPlayer bPlayer)
    {
        if (bPlayer.hasElement(Element.getElement("Water")))
            return "Water";
        if (bPlayer.hasElement(Element.getElement("Earth")))
            return "Earth";
        if (bPlayer.hasElement(Element.getElement("Fire")))
            return "Fire";
        if (bPlayer.hasElement(Element.getElement("Air")))
            return "Air";
        if (bPlayer.hasElement(Element.getElement("Chi")))
            return "Chi";
        return null;
    }

    public void GiveAllElements(BendingPlayer bPlayer)
    {
        Element[] elements = Element.getAllElements();
        for (Element element : elements)
        {
            if (!element.equals(Element.getElement("Chi")) && !element.equals(Element.getElement("Avatar")))
                GivePlayerElement(bPlayer, element);
        }
        GeneralMethods.saveElements(bPlayer);
        GeneralMethods.saveSubElements(bPlayer);
    }

    public void GivePlayerElement(BendingPlayer bPlayer, Element element)
    {
        if (!bPlayer.hasElement(element))
            bPlayer.addElement(element);
        for (final SubElement sub : Element.getAllSubElements())
            if (bPlayer.hasElement(sub.getParentElement()) && !bPlayer.hasSubElement(sub) && !sub.equals(Element.SubElement.BLUE_FIRE))
                bPlayer.addSubElement(sub);
        GeneralMethods.saveElements(bPlayer);
        GeneralMethods.saveSubElements(bPlayer);
    }

    public void RemovePlayerElements(BendingPlayer bPlayer, String stringElement)
    {
        Element[] elements = Element.getAllElements();
        for (Element element : elements)
        {
            if (!element.equals(Element.getElement(stringElement)))
            {
                for (final SubElement sub : Element.getAllSubElements())
                    if (bPlayer.hasElement(element) && bPlayer.hasSubElement(sub) && !sub.equals(Element.SubElement.BLUE_FIRE))
                        bPlayer.getSubElements().remove(sub);
                if (bPlayer.hasElement(element))
                    bPlayer.getElements().remove(element);
            }
        }
        GeneralMethods.removeUnusableAbilities(bPlayer.getName());
        GeneralMethods.saveElements(bPlayer);
		GeneralMethods.saveSubElements(bPlayer);
    }

    public void AddPlayerToList(List<String> names, List<String> elements)
    {
        names.add(main.avatar.getString("avatar"));
        elements.add(main.avatar.getString("previous-element"));
        main.avatar.set("name-list", names);
        main.avatar.set("element-list", elements);
        RemoveAvatarInfo();
    }

    public void RemovePlayerFromList(List<String> names, List<String> elements, Player player)
    {
        int index = names.indexOf(player.getName());
        String element = elements.get(index);
        elements.remove(index);
        names.remove(index);
        main.avatar.set("name-list", names);
        main.avatar.set("element-list", elements);
        main.SaveAvatar();
        BackToBender(player, element);
    }

    public void RemoveAvatarInfo()
    {
        main.avatar.set("avatar", "");
        main.avatar.set("previous-element", "");
        main.SaveAvatar();
    }

    public void RemoveExistingAvatar(Player player)
    {
        if (!main.avatar.getStringList("name-list").contains(player.getName()))
            return;
        List<String> names = main.avatar.getStringList("name-list");
        List<String> elements = main.avatar.getStringList("element-list");
        RemovePlayerFromList(names, elements, player);
    }

    public void BackToBender(Player player, String element)
    {
        RestoreElement(player, element);
        RemoveFromGroup(player, "avatar");
    }

    private void RestoreElement(Player player, String element)
    {
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        RemovePlayerElements(bPlayer, element);
    }

    public boolean AddToGroup(Player player, String group)
    {
        LuckPerms luckPerms = Bukkit.getServer().getServicesManager().load(LuckPerms.class);

        Group newGroup = luckPerms.getGroupManager().getGroup(group.toLowerCase());
        String playerName = player.getName();
        if (newGroup == null || playerName == null)
            return false;    
        User user = luckPerms.getUserManager().getUser(playerName);
        if (user == null) 
            return false;
        InheritanceNode node = InheritanceNode.builder(group.toLowerCase()).build();
        DataMutateResult result = user.data().add(node);
        if (result == DataMutateResult.FAIL)
            return false;
        luckPerms.getUserManager().saveUser(user);
        return true;
    }

    public boolean RemoveFromGroup(Player player, String group)
    {
        LuckPerms luckPerms = Bukkit.getServer().getServicesManager().load(LuckPerms.class);

        Group newGroup = luckPerms.getGroupManager().getGroup(group.toLowerCase());
        String playerName = player.getName();
        if (newGroup == null || playerName == null)
            return false;    
        User user = luckPerms.getUserManager().getUser(playerName);
        if (user == null) 
            return false;
        InheritanceNode node = InheritanceNode.builder(group.toLowerCase()).build();
        DataMutateResult result = user.data().remove(node);
        if (result == DataMutateResult.FAIL)
            return false;
        luckPerms.getUserManager().saveUser(user);
        return true;
    }   
    
    public void NewAvatarNotification(Player player)
    {
        List<String> configMsgs = main.config.getStringList("new-avatar-messages");
        if (!configMsgs.isEmpty())
            for (String parameterizedMsg : configMsgs) 
            {
                String msg = parameterizedMsg.replace("{player}", player.getName())
                    .replace("{duration}", main.config.getString("avatar-duration"))
                    .replace("{inmunity}", main.config.getString("death-immunity"))
                    .replace("&", "§").replace("1 dias", "1 día").replace("1 días", "1 día");
                Bukkit.getServer().broadcastMessage(msg);
            }
    }
}

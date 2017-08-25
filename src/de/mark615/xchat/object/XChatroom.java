package de.mark615.xchat.object;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.mark615.xchat.XChat;
import de.mark615.xchat.file.SettingManager;
import de.mark615.xchat.object.XMessage.XMessageReturnType;

public class XChatroom
{
	private String name;
	private String format;
	private String permission;
	private List<UUID> players;
	private List<String> worlds;
	private boolean muted;
	private boolean standard;
	private boolean accessAuto;
	private int maxplayer;
	private boolean privateChat;
	private boolean allowMutedPlayer;
	
	
	
	public XChatroom(ConfigurationSection section)
	{
		this(section.getString("name"));
		this.format = section.getString("format", "");
		this.permission = section.getString("permission", null);
		this.players = new ArrayList<>();
		this.worlds = section.getStringList("worlds");
		this.standard = section.getBoolean("standard", false);
		this.maxplayer = section.getInt("maxplayer", -1);
		this.allowMutedPlayer = section.getBoolean("muted-allowed");
		
		if (section.getString("access") != null && section.getString("access").equalsIgnoreCase("auto"))
		{
			this.accessAuto = true;
		}
		
		if (worlds == null)
		{
			this.worlds = new ArrayList<String>();
			this.worlds.add("all");
		}
	}
	
	public XChatroom(String name)
	{
		this.name = name;
		this.format = "";
		this.permission = null;
		this.players = new ArrayList<>();
		this.worlds = new ArrayList<>();
		this.worlds.add("all");
		this.muted = false;
		this.standard = false;
		this.maxplayer = -1;
		this.accessAuto = false;
		this.privateChat = false;
		this.allowMutedPlayer = false;
	}
	
	public XChatroom(String name, boolean auto)
	{
		this(name);
		this.accessAuto = auto;
	}
	
	public XChatroom(String name, String permission)
	{
		this(name);
		this.permission = permission;
	}
	
	public XChatroom(String name, String permission, List<String> worlds)
	{
		this(name, permission);
		this.worlds = worlds;
	}
	
	public XChatroom(String name, String permission, List<String> worlds, int maxplayer)
	{
		this(name, permission, worlds);
		this.maxplayer = maxplayer;
	}
	
	
	
	public XMessageReturnType send(XChat plugin, XMessage message)
	{
		XMessageReturnType type = null;
		switch (message.getType())
		{
			case PRIVATECHAT:
				message.setMessage(format + message.getMessage());
				XChatUtil.sendPrivateChatMessage(message);
				sendToPrivateRoom(message);
				type = XMessageReturnType.SUCCESS;
				break;
			
			case STANDARD:
				XPlayerSubject subject = plugin.getChatManager().getXPlayerSubject(message.getSender().getUniqueId());
				message.setMessage(format + subject.getChatFormat() + message.getMessage());
				sendToRoom(message);
				
				type = XMessageReturnType.SUCCESS;
				break;
		
			default:
				type = XMessageReturnType.ERROR;
				break;
		}
		
		return type;
	}
	
	private void sendToRoom(XMessage msg)
	{
		for (UUID uuid : players)
		{
			if (XChat.getInstance().getChatManager().getXPlayerSubject(uuid).hasPrivateXChatroom())
			{
				Bukkit.getPlayer(uuid).sendMessage(XUtil.replaceColorCodes("&7[G]") + msg.getMessage());
			}
			else
			{
				Bukkit.getPlayer(uuid).sendMessage(msg.getMessage());
			}
		}
		
		//senden an alle andere, die diesen chat bekommen sollten aber in privaten chats sind
		
		if (privateChat)
			return;
		
		if (SettingManager.getInstance().isAlwaysShowStandardChat())
		{
			for(Player p : Bukkit.getServer().getOnlinePlayers())
			{
				if (!players.contains(p.getUniqueId()))
				{
					//TODO color to goast message 
					p.sendMessage(msg.getMessage());
				}
			}
		}
	}
	
	private void sendToPrivateRoom(XMessage msg)
	{
		for (UUID uuid : players)
		{
			Bukkit.getPlayer(uuid).sendMessage(msg.getMessage());
		}
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public String getForamt()
	{
		return format;
	}
	
	public String getPermission()
	{
		return permission;
	}
	
	public boolean hasPermission()
	{
		return permission != null;
	}

	public boolean addPlayer(UUID uuid)
	{
		return players.add(uuid);
	}
	
	public boolean removePlayer(UUID uuid)
	{
		return players.remove(uuid);
	}
	
	public boolean hasPlayer(UUID uuid)
	{
		return players.contains(uuid);
	}

	public boolean containsWorld(String name)
	{
		return worlds.contains(name) ? worlds.contains(name) : worlds.contains("all");
	}
	
	public boolean isAllWorlds()
	{
		return worlds.contains("all");
	}

	public boolean isMuted()
	{
		return muted;
	}
	
	public void setMaxPlayer(int max)
	{
		this.maxplayer = max;
	}

	public int getMaxplayer()
	{
		return maxplayer;
	}
	
	public boolean isFull()
	{
		if (maxplayer == -1)
			return false;
		
		return (players.size() >= maxplayer) ? true : false;
	}
	
	public boolean isFull(Player p)
	{
		if (p.hasPermission("xchat.room.joinfull"))
			return false;
		
		return isFull();
	}
	
	public XChatroom setStandart()
	{
		this.standard = true;
		this.accessAuto = true;
		return this;
	}
	
	public boolean isStandard()
	{
		return standard;
	}
	
	public boolean isAccessAuto()
	{
		return accessAuto;
	}
	
	public boolean isPrivateChatRoom()
	{
		return privateChat;
	}
	
	public boolean isMutedAllowed()
	{
		return allowMutedPlayer;
	}
	
}

package de.mark615.xchat;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.mark615.xchat.object.XChatroom;
import de.mark615.xchat.object.XPlayerSubject;
import de.mark615.xchat.object.XUtil;

public class ChatManager
{
	private XChat plugin;
	private HashMap<UUID, XPlayerSubject> players = null;
	private HashMap<String, XChatroom> chatroooms = null;
	
	
	
	public ChatManager(XChat plugin)
	{
		this.plugin = plugin;
		this.players = new HashMap<>();
		this.chatroooms = new HashMap<>();
	}
	
	
	
	public boolean hasXPlayerSubject(UUID uuid)
	{
		return uuid != null ? players.containsKey(uuid) : null;
	}
	
	public XPlayerSubject getXPlayerSubject(UUID uuid)
	{
		return uuid != null ? players.get(uuid) : null;
	}
	
	public XChatroom getXChatroom(String name)
	{
		return name != null ? chatroooms.get(name) : null;
	}
	
	public XChatroom getStandardXChatroom()
	{
		for (String room : chatroooms.keySet())
		{
			if (chatroooms.get(room).isStandard())
				return chatroooms.get(room);
		}
		return null;
	}
	
	public XChatroom getStandardXChatroom(Player p)
	{
		for (String key : chatroooms.keySet())
		{
			XChatroom room = chatroooms.get(key);
			if (!room.isFull(p) && room.isAccessAuto())
			{
				if (room.containsWorld(p.getWorld().getName()))
				{
					if (!room.hasPermission() || (room.hasPermission() && p.hasPermission(room.getPermission())))
						return room;
				}
			}
		}
		
		return getStandardXChatroom();
	}
	
	public boolean hasXChatRoom(String name)
	{
		return name != null ? chatroooms.containsKey(name) : null;
	}
	
	
	
	public void registerXChatroom(XChatroom room)
	{
		chatroooms.put(room.getName(), room);
	}
	
	public void unregisterAllXChatRooms()
	{
		chatroooms.clear();
	}
	
	private void registerPlayerToXChatroom(XPlayerSubject subject, XChatroom room)
	{
		if (subject == null || room == null)
			return;
		
		if (room.hasPlayer(subject.getUUID()))
		{
			XUtil.sendFileMessage(subject.getPlayer(), "message.room.inroom", true);
			return;
		}
		
		if (room.hasPermission() && subject.getPlayer().hasPermission(room.getPermission()))
		{
			XUtil.sendFileMessage(subject.getPlayer(), "message.room.nopermission", true);
			return;
		}
		
		if (!room.isAllWorlds() && !room.containsWorld(subject.getPlayer().getWorld().getName()))
		{
			XUtil.sendFileMessage(subject.getPlayer(), "message.room.wrongworld", true);
			return;
		}
		
		if (room.isFull(subject.getPlayer()))
		{
			XUtil.sendFileMessage(subject.getPlayer(), "message.room.full", true);
			return;
		}
		subject.setXChatroom(room);
	}
	
	public void playerswitchWorld(XPlayerSubject subject)
	{		
		for (String key : chatroooms.keySet())
		{
			XChatroom room = chatroooms.get(key);
			if (!room.isFull(subject.getPlayer()) && room.isAccessAuto())
			{
				if (room.containsWorld(subject.getPlayer().getWorld().getName()))
				{
					if (!room.hasPermission() || (room.hasPermission() && subject.getPlayer().hasPermission(room.getPermission())))
					{
						subject.setXChatroom(room);
						return;
					}
				}
			}
		}
		
		subject.setXChatroom(getStandardXChatroom());
	}
	
	public void registerPlayer(Player p)
	{
		XPlayerSubject subject = players.get(p.getUniqueId());
		if (subject == null)
		{
			subject = new XPlayerSubject(p);
			players.put(p.getUniqueId(), subject);
		}
		else
		{
			subject.reloadxSubjectPlayer(p);
		}
		playerswitchWorld(subject);
	}
	
	public void unregisterPlayer(Player p)
	{
		XPlayerSubject subject = getXPlayerSubject(p.getUniqueId());
		subject.setXChatroom(null);
	}
}

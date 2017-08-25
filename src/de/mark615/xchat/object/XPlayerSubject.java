package de.mark615.xchat.object;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.mark615.xchat.XChat;
import de.mark615.xchat.file.SettingManager;

public class XPlayerSubject
{
	private UUID uuid;
	private boolean afk;
	private String prefix;
	private String name;
	private String suffix;
	private String chatFormat;
	private UUID lastMsgChatTarget;
	private HashMap<String, XChatroom> rooms;
	
	public XPlayerSubject(Player p)
	{
		this.uuid = p.getUniqueId();
		
		reloadxSubjectPlayer(p);
	}
	
	public void reloadxSubjectPlayer(Player p)
	{
		this.afk = false;
		this.name = p.getName();
		this.rooms = new HashMap<>();
		this.rooms.put("global", XChat.getInstance().getChatManager().getStandardXChatroom());
		this.rooms.get("global").addPlayer(uuid);
		this.chatFormat = "[" + name + "]";
		
		reloadDisplayname();
	}
	
	public void reloadDisplayname()
	{
		try
		{
			Player p = getPlayer();
			
			if (XChat.getInstance().hasVaultChat() && isOnline())
			{
				this.prefix = XChat.getInstance().getVaultChat().getPlayerPrefix(p);
				this.suffix = XChat.getInstance().getVaultChat().getPlayerSuffix(p);
				
				this.chatFormat = getFormatedName(SettingManager.getInstance().getFormatPattern("chat_format"));
				p.setPlayerListName(getFormatedName(SettingManager.getInstance().getFormatPattern("displayname_format")));
				p.setDisplayName(getFormatedName(SettingManager.getInstance().getFormatPattern("listname_format")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private String getFormatedName(String format)
	{
		format = format.replace("%prefix%", prefix);
		format = format.replace("%name%", name);
		format = format.replace("%suffix%", suffix);
		return XUtil.replaceColorCodes(format);
	}
	
	
	
	public UUID getUUID()
	{
		return uuid;
	}
	
	public Player getPlayer()
	{
		return Bukkit.getPlayer(uuid);
	}
	
	public boolean isAfk()
	{
		return afk;
	}
	
	public boolean isOnline()
	{
		return getPlayer() != null;
	}
	
	public void setAfkMode(boolean value)
	{
		this.afk = value;
	}

	public String getPrefix()
	{
		return prefix;
	}
	
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}
	
	public String getName()
	{
		return name;
	}

	public String getSuffix()
	{
		return suffix;
	}
	
	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}
	
	public String getChatFormat()
	{
		return chatFormat;
	}
	
	public void setLastMsgChatTarget(Player p)
	{
		this.lastMsgChatTarget = p.getUniqueId();
	}
	
	public UUID getLastMsgChatTarget()
	{
		return lastMsgChatTarget;
	}
	
	public void setXChatroom(XChatroom room)
	{
		if (room == null)
		{
			if (hasPrivateXChatroom())
			{
				rooms.get("private").removePlayer(uuid);
				rooms.remove("private");
			}
			
			rooms.get("global").removePlayer(uuid);
			rooms.remove("global");
		}
		else
		{
			if (room.isPrivateChatRoom())
			{
				if (hasPrivateXChatroom())
					rooms.get("private").removePlayer(uuid);
				
				rooms.put("private", room);
				rooms.get("private").addPlayer(uuid);
			}
			else
			{
				//TODO privater raum muss nicht verlassen werdend er globale raum gewechselt wird
				/*if (hasPrivateXChatroom())
				{
					rooms.get("private").removePlayer(uuid);
					rooms.remove("private");
				}*/
				
				if (!rooms.get("global").getName().equalsIgnoreCase(room.getName()))
				{
					rooms.get("global").removePlayer(uuid);
					rooms.put("global", room);
					rooms.get("global").addPlayer(uuid);
				}
			}
		}
	}
	
	public XChatroom getXChatroom()
	{
		return rooms.get("global");
	}
	
	public XChatroom getPrivateXChatroom()
	{
		return rooms.get("private");
	}
	
	public boolean hasPrivateXChatroom()
	{
		return rooms.containsKey("private") ? true : false;
	}
	
}

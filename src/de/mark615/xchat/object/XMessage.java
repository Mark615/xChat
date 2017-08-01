package de.mark615.xchat.object;

import org.bukkit.entity.Player;

import de.mark615.xchat.XApiConnector;
import de.mark615.xchat.XChat;

public class XMessage
{
	private XMessageType type;
	private XChatroom room;
	private String message;
	private Player sender;
	private Player target;
	
	

	private XMessage(Player p, String message, XMessageType type)
	{
		this.sender = p;
		this.target = null;
		this.message = message;
		this.type = type;
		this.room = null;
		
		
		if (p.hasPermission("xchat.chat.color"))
			this.message = XUtil.replaceColorCodes(this.message);
		
		if (this.type.equals(XMessageType.STANDARD))
			this.room = XChat.getInstance().getChatManager().getStandardXChatroom();
	}
	
	private XMessage(Player p, Player t, String message, XMessageType type)
	{
		this(p, message, type);
		this.target = t;
	}
	
	private XMessage(Player p, Player t, String message, XChatroom room, XMessageType type)
	{
		this(p, t, message, type);
		this.room = room;
	}
	
	
	
	public XMessageType getType()
	{
		return type;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Player getSender()
	{
		return sender;
	}
	
	public Player getTarget()
	{
		return target;
	}
	
	public XChatroom getXChatroom()
	{
		return room;
	}
	
	
	public XMessageReturnType send(XChat plugin)
	{
		XApiConnector connector = null;
		if (plugin != null && plugin.hasXApiConnector()) 
		{
			connector = plugin.getXApiConnector();
		}
		
		
		if (connector != null && connector.isChatroomMuted(room.getName()))
		{
			XUtil.sendFileMessage(sender, "message.muted.chatroom");
			return XMessageReturnType.MUTEDROOM;
		}
		
		if (type.equals(XMessageType.PRIVATECHAT))
		{
			return room.send(plugin, this);
		}
		
		
		if (connector != null && connector.isPlayerMuted(sender.getUniqueId()))
		{
			XUtil.sendFileMessage(sender, "message.muted.player");
			return XMessageReturnType.MUTEDPLAYER;
		}
		
		if (type.equals(XMessageType.MSGCHAT))
		{
			XChatUtil.sendMsgChatMessage(this);
			return XMessageReturnType.SUCCESS;
		}
		
		
		if (type.equals(XMessageType.STANDARD))
		{
			if (connector != null && connector.isServerMuted(null))
			{
				XUtil.sendFileMessage(sender, "message.muted.server");
				return XMessageReturnType.MUTEDSERVER;
			}
			
			return room.send(plugin,this);
		}
		
		
		return XMessageReturnType.ERROR;
	}



	public enum XMessageType
	{
		STANDARD,
		PRIVATECHAT,
		MSGCHAT,
	}
	
	public enum XMessageReturnType
	{
		SUCCESS,
		MUTEDPLAYER,
		MUTEDROOM,
		MUTEDSERVER,
		ERROR,
	}
	
	
	
	public static class XMessage_Standard extends XMessage
	{
		public XMessage_Standard(XPlayerSubject subject, String message)
		{
			super(subject.getPlayer(), null, message, subject.getXChatroom(), XMessageType.STANDARD);
		}
	}
	
	public static class XMessage_PRIVATECHAT extends XMessage
	{
		public XMessage_PRIVATECHAT(XPlayerSubject subject, String message)
		{
			super(subject.getPlayer(), null, message, subject.getXChatroom(), XMessageType.PRIVATECHAT);
		}
	}
	
	public static class XMessage_MSGCHAT extends XMessage
	{
		public XMessage_MSGCHAT(Player p, Player t, String message)
		{
			super(p, t, message, XMessageType.MSGCHAT);
		}
	}
}

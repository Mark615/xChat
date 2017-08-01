package de.mark615.xchat.object;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.mark615.xchat.file.SettingManager;
import de.mark615.xchat.object.XMessage.XMessageType;

public class XChatUtil
{
	public static String replaceColorCode(String msg, Player p)
	{
		if (p.hasPermission("xchat.chat.color"))
		{
			return XUtil.replaceColorCodes(msg);
		}
		return msg;
	}
	
	public static void sendPrivateChatMessage(XMessage msg)
	{
		//TODO private chat implementation
	}

	public static void sendMsgChatMessage(XMessage msg)
	{
		msg.getSender().sendMessage(XUtil.replaceColorCodes(getMsgChatToSenderFormat(msg.getSender(), msg.getTarget()) + " ") + msg.getMessage());
		msg.getTarget().sendMessage(XUtil.replaceColorCodes(getMsgChatToTargetFormat(msg.getSender(), msg.getTarget()) + " ") + msg.getMessage());
		
		sendChatSpy(msg);
	}
	
	private static void sendChatSpy(XMessage msg)
	{
		if (msg.getSender().hasPermission("xchat.chat.spy.admin") || msg.getTarget().hasPermission("xchat.chat.spy.admin"))
			return;
		
		String spyformat = getSpyFormat(msg.getSender(), msg.getTarget());
		
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if (!p.getUniqueId().equals(msg.getSender().getUniqueId()) && !p.getUniqueId().equals(msg.getTarget().getUniqueId()))
			{
				if (msg.getType().equals(XMessageType.MSGCHAT))
				{
					if (p.hasPermission("xchat.chat.spy.msgchat"))
					{
						p.sendMessage(XUtil.replaceColorCodes(spyformat + " ") + msg.getMessage());
					}
				}
				else
				if (msg.getType().equals(XMessageType.PRIVATECHAT))
				{
					if (p.hasPermission("xchat.chat.spy.privatechat"))
					{
						p.sendMessage(XUtil.replaceColorCodes(spyformat + "&2[P]&6 ") + msg.getMessage());
					}
				}
			}
		}
	}
	
	private static String getSpyFormat(Player sender, Player target)
	{
		String format = SettingManager.getInstance().getFormatPattern("spy_format");
		format = format.replace("%sender%", sender.getName());
		format = format.replace("%target%", target.getName());
		return XUtil.replaceColorCodes(format);
	}
	
	private static String getMsgChatToSenderFormat(Player sender, Player target)
	{
		String format = SettingManager.getInstance().getFormatPattern("msgchat_toSender_format");
		format = format.replace("%sender%", sender.getName());
		format = format.replace("%target%", target.getName());
		return XUtil.replaceColorCodes(format);
	}
	
	private static String getMsgChatToTargetFormat(Player sender, Player target)
	{
		String format = SettingManager.getInstance().getFormatPattern("msgchat_toTarget_format");
		format = format.replace("%sender%", sender.getName());
		format = format.replace("%target%", target.getName());
		return XUtil.replaceColorCodes(format);
	}
	
	
	
}

package de.mark615.xchat.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mark615.xchat.XChat;
import de.mark615.xchat.object.XMessage;
import de.mark615.xchat.object.XMessage.XMessageReturnType;
import de.mark615.xchat.object.XPlayerSubject;
import de.mark615.xchat.object.XUtil;

public class CommandMsg extends XCommand
{
	private XChat plugin;
	
	public CommandMsg(XChat plugin)
	{
		super("msg", "xchat.msg", true);
		this.plugin = plugin;
	}

	@Override
	public void fillSubCommands(List<XSubCommand> subcommands)
	{
		subcommands.add(new XSubCommand("msg", "tell", "wispher", "send"));
	}

	@Override
	protected void showHelp(CommandSender p)
	{
		p.sendMessage(ChatColor.GREEN + XChat.PLUGIN_NAME + ChatColor.GRAY + " - " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		p.sendMessage(ChatColor.GREEN + "/msg <player> <msg...>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.msg.description"));
	}

	@Override
	public XCommandReturnType run(CommandSender sender, Command command, String s, String[] args)
	{
		if (!(sender instanceof Player))
		{
			XUtil.sendFileMessage(sender, "command.no-consol-command");
			return XCommandReturnType.NEEDTOBEPLAYER;
		}
		XPlayerSubject subject = plugin.getChatManager().getXPlayerSubject(((Player)sender).getUniqueId());
		
		if ((args.length < 1 && subject.getLastMsgChatTarget() == null) || (args.length < 2))
		{
			XUtil.sendCommandUsage(sender, "use: /msg <player> <msg...>");
			return XCommandReturnType.NONE;
		}
		

		Player target = Bukkit.getServer().getPlayer(args[1]);
		XPlayerSubject subjectTarget = null;
		
		if (target != null)
		{
			if (subject.getUUID().equals(target.getUniqueId()))
			{
				XUtil.sendFileMessage(sender, "command.msg.yourself", ChatColor.RED);
				return XCommandReturnType.NONE;
			}
		}
		
		if (subject.getLastMsgChatTarget() == null)
		{
			if (target == null)
			{
				XUtil.sendFileMessage(sender, "command.player-not-found", ChatColor.RED);
				return XCommandReturnType.NOPLAYERMATCH;
			}
			subject = plugin.getChatManager().getXPlayerSubject(target.getUniqueId());
		}
		else
		{
			if (target != null)
			{
				subjectTarget = plugin.getChatManager().getXPlayerSubject(target.getUniqueId());
			}
			else
			{
				subjectTarget = plugin.getChatManager().getXPlayerSubject(subject.getLastMsgChatTarget());	
			}
			
			if (subjectTarget == null || subjectTarget.getPlayer() == null)
			{
				subject.setLastMsgChatTarget(null);
				XUtil.sendFileMessage(sender, "command.player-not-found", ChatColor.RED);
				return XCommandReturnType.NOPLAYERMATCH;
			}
		}
		subject.setLastMsgChatTarget(subject.getPlayer());
		subjectTarget.setLastMsgChatTarget(subject.getPlayer());
		
		String message = "";
		int i = 0;
		if (target != null)
			i = 1;
		for (; i < args.length; i++)
		{
			message = message + " " + args[i];
		}
		
		XMessage msg = new XMessage.XMessage_MSGCHAT(subject.getPlayer(), subjectTarget.getPlayer(), message);
		if (msg.send(plugin).equals(XMessageReturnType.ERROR))
		{
			XUtil.sendFileMessage(sender, "command.msg.error", ChatColor.RED);
			return XCommandReturnType.NONE;
		}
		
		return XCommandReturnType.SUCCESS;
	}
}

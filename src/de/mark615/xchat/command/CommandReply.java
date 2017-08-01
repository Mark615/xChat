package de.mark615.xchat.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mark615.xchat.XChat;
import de.mark615.xchat.object.XMessage;
import de.mark615.xchat.object.XMessage.XMessageReturnType;
import de.mark615.xchat.object.XPlayerSubject;
import de.mark615.xchat.object.XUtil;

public class CommandReply extends XCommand
{
	private XChat plugin;
	
	public CommandReply(XChat plugin)
	{
		super("reply", "xchat.reply", true);
		this.plugin = plugin;
	}

	@Override
	public void fillSubCommands(List<XSubCommand> subcommands)
	{
		subcommands.add(new XSubCommand("reply", "r"));
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
		
		if (!this.isSubCommand(args[0]))
		{
			XUtil.sendCommandUsage(sender, "use: /reply <help/?> " + ChatColor.YELLOW + "- for help");
			return XCommandReturnType.NONE;
		}
		
		if (args.length < 1)
		{
			XUtil.sendCommandUsage(sender, "use: /reply <msg>");
			return XCommandReturnType.NONE;
		}

		XPlayerSubject subjectTarget = plugin.getChatManager().getXPlayerSubject(subject.getLastMsgChatTarget());
		if (subjectTarget == null || subjectTarget.getPlayer() == null)
		{
			subject.setLastMsgChatTarget(null);
			XUtil.sendFileMessage(sender, "command.player-not-found", ChatColor.RED);
			return XCommandReturnType.NOPLAYERMATCH;
		}
		
		String message = "";
		for (int i = 0; i < args.length; i++)
		{
			message = message + " " + args[i];
		}
		
		XMessage msg = new XMessage.XMessage_MSGCHAT(subject.getPlayer(), subjectTarget.getPlayer(), message);
		if (msg.send(plugin).equals(XMessageReturnType.ERROR))
		{
			XUtil.sendFileMessage(sender, "command.reply.error", ChatColor.RED);
			return XCommandReturnType.NONE;
		}
		
		return XCommandReturnType.SUCCESS;
	}

	@Override
	protected void showHelp(CommandSender p)
	{
		p.sendMessage(ChatColor.GREEN + XChat.PLUGIN_NAME + ChatColor.GRAY + " - " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		p.sendMessage(ChatColor.GREEN + "/reply <msg...>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.reply.description"));
	}

}

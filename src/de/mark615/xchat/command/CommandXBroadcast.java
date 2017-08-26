package de.mark615.xchat.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.mark615.xchat.XChat;
import de.mark615.xchat.object.XUtil;

public class CommandXBroadcast extends XCommand
{
	private XChat plugin;
	
	public CommandXBroadcast(XChat plugin)
	{
		super("xbroadcast", "xchat.broadcast");
		this.plugin = plugin;
	}

	@Override
	public void fillSubCommands(List<XSubCommand> subcommands)
	{
		subcommands.add(new XSubCommand("list"));
		subcommands.add(new XSubCommand("enable"));
		subcommands.add(new XSubCommand("disable"));
	}

	@Override
	protected void showHelp(CommandSender sender)
	{
		// TODO Auto-generated method stub
		XUtil.sendCommandInfo(sender, "&cWork is in progress");
	}

	@Override
	public XCommandReturnType run(CommandSender sender, Command command, String s, String[] args)
	{
		showHelp(sender);
		return XCommandReturnType.SUCCESS;
		
		/*
		if (!isSubCommand(args[0]))
		{
			XUtil.sendCommandUsage(sender, "use: /xbr <help/?> " + ChatColor.YELLOW + "- for help");
			return XCommandReturnType.NONE;
		}
		
		if (matchesSubCommand("list", args[0]))
		{
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("enable", args[0]))
		{
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("disable", args[0]))
		{
			return XCommandReturnType.SUCCESS;
		}
		
		return XCommandReturnType.NOCOMMAND;
		*/
	}
	
	
}

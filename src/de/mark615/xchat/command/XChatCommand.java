package de.mark615.xchat.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.mark615.xchat.XChat;
import de.mark615.xchat.object.XUtil;

public class XChatCommand extends XCommand
{
	private XChat plugin;

	public XChatCommand(XChat plugin)
	{
		super("xchat", "xchat.config");
		this.plugin = plugin;
	}

	@Override
	public void fillSubCommands(List<XSubCommand> subcommands)
	{
		subcommands.add(new XSubCommand("reload"));
	}

	@Override
	protected void showHelp(CommandSender p)
	{
		p.sendMessage(ChatColor.GREEN + XChat.PLUGIN_NAME + ChatColor.GRAY + " - " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		if (p.hasPermission("xchat.config.reload")) p.sendMessage(ChatColor.GREEN + "/xchat <reload>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xchat.reload.description"));
	}

	@Override
	protected XCommandReturnType run(CommandSender sender, Command command, String s, String[] args)
	{
		XCommandReturnType type = null;
		if (!this.isSubCommand(args[0]))
		{
			XUtil.sendCommandUsage(sender, "use: /xchat <help/?> " + ChatColor.YELLOW + "- for help");
			return XCommandReturnType.NONE;
		}
		
		try
		{
			if (matchesSubCommand("reload", args[0]))
			{
				if (!matchPermission(sender, "xchat.config.reload"))
					return XCommandReturnType.NOPERMISSION;
				
				plugin.onReload();
				XUtil.sendFileMessage(sender, "command.xchat.reload.success");
				type = XCommandReturnType.SUCCESS;
			}
			else
			{
				type = XCommandReturnType.NOCOMMAND;
			}
		}
		catch (Exception e)
		{
			XUtil.sendCommandInfo(sender, "&4An Error occurred");
			XUtil.severe(e.getMessage());
		}
		
		return type;
	}

}

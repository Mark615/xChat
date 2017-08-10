package de.mark615.xchat.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.mark615.xchat.XChat;
import de.mark615.xchat.file.SettingManager;
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
		if (p.hasPermission("xchat.config.reload")) p.sendMessage(ChatColor.GREEN + "/xchat <reload> <all|br|modt>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xchat.reload.description"));
	}

	@Override
	protected XCommandReturnType run(CommandSender sender, Command command, String s, String[] args)
	{
		if (!this.isSubCommand(args[0]))
		{
			XUtil.sendCommandUsage(sender, "use: /xchat <help/?> " + ChatColor.YELLOW + "- for help");
			return XCommandReturnType.NONE;
		}
		
		if (matchesSubCommand("reload", args[0]))
		{
			if (!matchPermission(sender, "xchat.config.reload"))
				return XCommandReturnType.NOPERMISSION;
			
			if (args.length < 2)
			{
				XUtil.sendCommandUsage(sender, "use: /xchat <reload> <all|br|modt>");
				return XCommandReturnType.NONE;
			}
			
			try
			{
				switch (args[1])
				{
					case "all":
						plugin.onReload();
						break;
					
					case "br":
						SettingManager.getInstance().getBroadcastFile().reloadConfig();
						plugin.getBroadcastManager().reloadBroadcastList();
						break;
						
					case "modt":
						SettingManager.getInstance().getModtFile().reloadConfig();
						break;
						
					default:
						XUtil.sendCommandUsage(sender, "use: /xchat <reload> <all|br|modt>");
						return XCommandReturnType.NONE;
				}
			}
			catch (Exception e)
			{
				XUtil.severe("unable to reload plugin", e);
				XUtil.sendFileMessage(sender, "command.xchat.reload.error");
				return XCommandReturnType.NONE;
			}
			

			XUtil.sendFileMessage(sender, "command.xchat.reload.success");
			return XCommandReturnType.SUCCESS;
		}

		return XCommandReturnType.NOCOMMAND;
	}

}

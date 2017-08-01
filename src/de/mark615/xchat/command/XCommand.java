package de.mark615.xchat.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class XCommand
{
	private boolean allowNoArgs;
	private String command;
	private String permission;
	private boolean hasPermission;
	private List<XSubCommand> subcommands;
	
	
	
	public XCommand(String command, String permission)
	{
		this.allowNoArgs = false;
		this.command = command;
		this.subcommands = new ArrayList<>();
		this.permission = permission;
		this.hasPermission = true;
		
		subcommands.add(new XSubCommand("help", "?"));
		fillSubCommands(subcommands);
	}
	
	public XCommand(String command, String permission, boolean allowNoArgs)
	{
		this(command, permission);
		this.allowNoArgs = allowNoArgs;
	}
	
	
	
	protected boolean isSubCommand(String cmd)
	{
		for (XSubCommand sub : subcommands)
		{
			if (sub.getCommand().equalsIgnoreCase(cmd) || sub.isSubCommand(cmd))
				return true;
		}
		return false;
	}
	
	protected boolean matchesSubCommand(String cmd, String alias)
	{
		for (XSubCommand sub : subcommands)
		{
			if (sub.getCommand().equalsIgnoreCase(cmd))
			{
				if (cmd.equalsIgnoreCase(alias))
					return true;
				else
				if (sub.isSubCommand(alias))
					return true;
			}
		}
		return false;
	}

	protected boolean matchPermission(CommandSender sender, String permission)
	{
		if (sender instanceof Player)
		{
			return ((Player) sender).hasPermission(permission);
		}
		return true;
	}
	
	public boolean runCommand(CommandSender sender, Command command, String s, String[] args)
	{
		if (!allowNoArgs)
		{
			if (args.length < 1)
			{
				showHelp(sender);
				return true;
			}
		}

		if (matchesSubCommand("help", args[0]))
		{
			showHelp(sender);
			return true;
		}		
		
		XCommandReturnType type = run(sender, command, s, args);
		if (type.equals(XCommandReturnType.NOPERMISSION))
		{
			return false;
		}
		
		if (type.equals(XCommandReturnType.NOCOMMAND))
		{
			showHelp(sender);
		}
		return true;
	}
	
	
	
	public abstract void fillSubCommands(List<XSubCommand> subcommands);

	protected abstract XCommandReturnType run(CommandSender sender, Command command, String s, String[] args);
	
	protected abstract void showHelp(CommandSender sender);
	
	
	
	public String getCommand()
	{
		return this.command;
	}
	
	public String getPermission()
	{
		return this.permission;
	}
	
	public void hasNoPermission()
	{
		this.hasPermission = false;
	}
	
	public boolean hasCommandSenderPermission()
	{
		return this.hasPermission;
	}
	
	
	
	public class XSubCommand
	{
		private String cmd;
		private List<String> alias;
		
		public XSubCommand(String cmd)
		{
			this.cmd = cmd;
			this.alias = new ArrayList<>();
		}
		
		public XSubCommand(String cmd, String... alias)
		{
			this(cmd);
			if (alias != null)
			{
				for (String key : alias)
				{
					this.alias.add(key);
				}
			}
		}
		
		public boolean isSubCommand(String alias)
		{
			for (String key : this.alias)
			{
				if (key.equalsIgnoreCase(alias))
					return true;
			}
			return false;
		}
		
		public String getCommand()
		{
			return cmd;
		}
	}
	
	protected enum XCommandReturnType
	{
		NOPERMISSION,
		NOCOMMAND,
		NEEDTOBEPLAYER,
		NOPLAYERMATCH,
		SUCCESS,
		NONE,
	}
}


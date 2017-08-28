package de.mark615.xchat;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.mark615.xapi.XApi;
import de.mark615.xapi.versioncheck.VersionCheck;
import de.mark615.xapi.versioncheck.VersionCheck.XType;
import de.mark615.xchat.broadcast.BroadcastManager;
import de.mark615.xchat.command.CommandMsg;
import de.mark615.xchat.command.CommandReply;
import de.mark615.xchat.command.CommandXBroadcast;
import de.mark615.xchat.command.XChatCommand;
import de.mark615.xchat.command.XCommand;
import de.mark615.xchat.events.EventListener;
import de.mark615.xchat.file.SettingManager;
import de.mark615.xchat.modt.ModtManager;
import de.mark615.xchat.object.XChatroom;
import de.mark615.xchat.object.XUtil;
import net.milkbowl.vault.chat.Chat;

public class XChat extends JavaPlugin
{
	public static final int BUILD = 1;
	public static String PLUGIN_NAME = "[xChat] ";
	
	private static XChat instance;

	private HashMap<String, XCommand> commands = null;
	private XApiConnector xapiconn = null;
	private XApi xapi = null;
	private SettingManager settings = null;
	private ChatManager chatmanager = null;
	private BroadcastManager broadcastManager = null;
	private ModtManager modtManager = null;
	private Chat chat = null;
	
	
	
	@Override
	public void onEnable()
	{
		instance = this;
		this.commands = new HashMap<>();
		
		this.settings = SettingManager.getInstance();
		this.settings.setup(this);
		
		registerCommand();
		registerEvents();

		setupChat();
		if (chat != null)
		{
			XUtil.info("connected with Vault[chat]");
		}
		else
		{
			XUtil.info("unable to connected to Vault[chat]");
		}
		setupXApi();
		if (xapiconn != null)
		{
			XUtil.info("connected with xApi");
		}
		
		
		XUtil.onEnable();
		XUtil.updateCheck(this);

		this.chatmanager = new ChatManager(this);
		this.broadcastManager = new BroadcastManager(this);
		this.modtManager = new ModtManager(this);
		loadXChatrooms();
		XUtil.info("Enabled Build " + BUILD);
	}
	
	@Override
	public void onDisable()
	{
		XUtil.onDisable();
		settings.saveConfig();
		settings.getBroadcastFile().saveConfig();
		settings.getModtFile().saveConfig();
		
		this.broadcastManager.stopAllBroadcast();
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			this.chatmanager.unregisterPlayer(p);
		}
		
		this.chatmanager.unregisterAllXChatRooms();
	}
	
	public void onReload()
	{
		this.settings.reloadConfig();
		this.settings.reloadMessage();
		this.settings.getBroadcastFile().reloadConfig();
		this.settings.getModtFile().reloadConfig();
		
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			this.chatmanager.unregisterPlayer(p);
		}
		
		this.chatmanager.unregisterAllXChatRooms();
		this.loadXChatrooms();
		
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			this.chatmanager.registerPlayer(p);
		}
		
		this.broadcastManager.reloadBroadcastList();
	}
	
	public static XChat getInstance()
	{
		return instance;
	}

	
	
	private boolean setupXApi() 
	{
		XApi xapi = (XApi)getServer().getPluginManager().getPlugin("xApi");
    	if(xapi == null)
    		return false;
    	
    	this.xapi = xapi; 
    	try
    	{
	    	if (xapi.checkVersion(XType.xChat, BUILD))
	    	{
	        	xapiconn = new XApiConnector(xapi, this);
	        	xapi.registerXChat(xapiconn);
	    	}
	    	else
	    	{
	    		XUtil.severe("Can't hook to xApi!"); 
	    		if (VersionCheck.isXPluginHigherXApi(XType.xChat, BUILD))
	    		{
		    		XUtil.warning("Please update your xApi!");
		    		XUtil.warning("Trying to hook to xApi. Have an eye into console for errors with xApi!");

		        	xapiconn = new XApiConnector(xapi, this);
		        	xapi.registerXChat(xapiconn);
	    		}
	    		else
	    		{
		    		XUtil.severe("Please update your xChat for hooking.");
	    		}
	    	}
    	}
    	catch (Exception e)
    	{
    		XUtil.severe("An error accurred during connection to xApi!", e);
    	}
    	
    	return xapiconn != null;
	}
	
	private void registerCommand()
	{
		commands.put("msg", new CommandMsg(this));
		commands.put("reply", new CommandReply(this));
		commands.put("xbr", new CommandXBroadcast(this));
		commands.put("xchat", new XChatCommand(this));
	}
	
	private void registerEvents()
	{
		Bukkit.getServer().getPluginManager().registerEvents(new EventListener(this), this);
	}
	
	private boolean setupChat()
	{
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null)
		{
			chat = chatProvider.getProvider();
		}
		return (chat != null);
	}
    
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
	{
		XCommand xCommand = commands.get(command.getLabel());
		
		if (xCommand == null)
			return false;
		
		if (commandSender instanceof Player)
		{
			if (!((Player) commandSender).hasPermission(xCommand.getPermission()))
			{
				commandSender.sendMessage(ChatColor.RED + XUtil.getMessage("command.nopermission"));
				return true;
			}
			return xCommand.runCommand((Player) commandSender, command, s, args);
		}
		else
		{
			if (!xCommand.runCommand(commandSender, command, s, args))
			{
				commandSender.sendMessage(ChatColor.RED + XUtil.getMessage("command.nopermission"));
			}
		}
		return true;
	}
	
	private void loadXChatrooms()
	{
		for (XChatroom rooms : settings.getXChatrooms())
		{
			this.chatmanager.registerXChatroom(rooms);
		}
	}
	
	
	
	public boolean hasXApi()
	{
		return (xapi != null && xapi.getXPlugin(XType.xChat) != null);
	}
	
	public boolean hasXApi(XType type)
	{
		return (hasXApi() && xapi.getXPlugin(type) != null);
	}
	
	public XApi getXApi()
	{
		return xapi;
	}
	
	public boolean hasXApiConnector()
	{
		return xapiconn != null;
	}
	
	public XApiConnector getXApiConnector()
	{
		return xapiconn;
	}
	
	public SettingManager getSettingManager()
	{
		return settings;
	}

	public ChatManager getChatManager()
	{
		return chatmanager;
	}
	
	public BroadcastManager getBroadcastManager()
	{
		return broadcastManager;
	}
	
	public ModtManager getModtManager()
	{
		return modtManager;
	}
	
	public boolean hasVaultChat()
	{
		return chat != null;
	}
	
	public Chat getVaultChat()
	{
		return chat;
	}
}

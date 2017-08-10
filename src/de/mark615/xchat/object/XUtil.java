package de.mark615.xchat.object;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.mark615.xchat.XChat;
import de.mark615.xchat.file.SettingManager;
import de.mark615.xchat.object.Updater.UpdateResult;
import de.mark615.xchat.object.Updater.UpdateType;

public class XUtil
{
	private static boolean jsonMessage = false;
	
	public static void info(String info)
	{
		Bukkit.getLogger().info(XChat.PLUGIN_NAME + info);
	}

	public static void warning(String severe)
	{
		Bukkit.getLogger().info(XChat.PLUGIN_NAME + "[WARNING] " + severe);
	}
	
	public static void severe(String severe)
	{
		Bukkit.getLogger().severe(XChat.PLUGIN_NAME + "[ERROR] " + severe);
	}
	
	public static void severe(String severe, Exception e)
	{
		severe(severe);
	}
	
	public static void debug(Exception e)
	{
		e.printStackTrace();
	}
	
	public static String replaceColorCodes(String message)
	{
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	public static String getMessage(String file)
	{
		String raw = SettingManager.getInstance().getMessage().getString(file);
		if (raw == null)
		{
			raw = file + " (not found in messages.yml)";
		}
		raw = replaceColorCodes(raw);
		return raw;
	}
	
	private static void sendMessage(CommandSender sender, String message, boolean prefix)
	{
		message = replaceColorCodes(message);
		
		for (String line : message.split("%ln%"))
		{
			if (!prefix)
				sender.sendMessage(line);
			else
				sender.sendMessage(XChat.PLUGIN_NAME + line);
		}
	}
	
	private static void sendMessage(CommandSender sender, String message)
	{
		sendMessage(sender, message, false);
	}
	
	public static void sendFileMessage(CommandSender s, String file, ChatColor color)
	{
		String message = getMessage(file);
		if (s instanceof Player)
			message = color + message;
		
		sendMessage(s, message);
	}

	public static void sendFileMessage(CommandSender s, String file)
	{
		sendMessage(s, getMessage(file));
	}

	public static void sendFileMessage(CommandSender s, String file, boolean prefix)
	{
		sendMessage(s, getMessage(file), prefix);
	}
	
	public static void sendCommandUsage(CommandSender s, String usage)
	{
		if (s instanceof Player)
			usage = ChatColor.RED + usage;
		
		sendMessage(s, usage, true);
	}
	
	public static void sendCommandInfo(CommandSender s, String info)
	{
		if (s instanceof Player)
			info = ChatColor.GREEN + info;
		
		sendMessage(s, info, true);
	}
	
	public static void sendCommandHelp(CommandSender s, String help)
	{
		if (s instanceof Player)
			help = ChatColor.YELLOW + help;
		
		sendMessage(s, help, true);
	}
	
	public static void sendCommandError(CommandSender s, String error)
	{
		if (s instanceof Player)
			error = ChatColor.RED + error;
		
		sendMessage(s, error, true);
	}
	
	public static void sendMessage(Player p, String info)
	{
		sendMessage(p, info, false);
	}
	
	public static void broadcast(String message)
	{
		boolean sendToAfk = SettingManager.getInstance().getBroadcastFile().broadcast_to_afk_player();
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			boolean send = true;
			if (XChat.getInstance().getChatManager().hasXPlayerSubject(p.getUniqueId()))
			{
				if (!sendToAfk && XChat.getInstance().getChatManager().getXPlayerSubject(p.getUniqueId()).isAfk())
					send = false;
					
				if (send)
					sendMessage(p, replaceColorCodes(message), false);
			}
		}
	}
	
	public static void broadcast(String message, String permission)
	{
		boolean sendToAfk = SettingManager.getInstance().getBroadcastFile().broadcast_to_afk_player();
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if (p.hasPermission(permission))
			{
				boolean send = true;
				if (XChat.getInstance().getChatManager().hasXPlayerSubject(p.getUniqueId()))
				{
					if (!sendToAfk && XChat.getInstance().getChatManager().getXPlayerSubject(p.getUniqueId()).isAfk())
						send = false;
						
					if (send)
						sendMessage(p, replaceColorCodes(message), false);
				}
			}
		}
	}

	
	
	public static void updateCheck(final JavaPlugin plugin)
	{
		Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				if (SettingManager.getInstance().hasCheckVersion())
				{
					try
					{
						Updater updater = new Updater(plugin, 273577, plugin.getDataFolder(), UpdateType.NO_DOWNLOAD, true);
						if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
						    XUtil.info("New version available! " + updater.getLatestName());
						}
					}
					catch(Exception e)
					{
						XUtil.severe("Can't check version at Bukkit.com");
					}
				}
			}
		}, 20, 6 * 60 * 60 * 20);
	}	
	
	
	
	public static void onEnable()
	{
		if (!jsonMessage)
			return;
		
		Bukkit.getServer().getScheduler().runTaskAsynchronously(XChat.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				if (onStart())
				{
					try
					{
						String value = sendGet("setmode?uuid=" + SettingManager.getInstance().getAPIKey().toString() + "&type=xChat&mode=on&build=" + XChat.BUILD);
						JsonElement parser = new JsonParser().parse(value);
						JsonObject json = parser.getAsJsonObject();
						if (json.has("dataid"))
						{
							SettingManager.getInstance().setDataID(json.get("dataid").getAsInt());
						}
					}
					catch(Exception e)
					{
						severe("Can't generate onEnable webrequest");
						debug(e);
					}
				}
			}
		});
	}
	
	private static boolean onStart()
	{
		try
		{
			String url = "startup?servername=" + Bukkit.getServerName() + "";
			if (SettingManager.getInstance().getAPIKey() != null)
			{
				url = url + "&uuid=" + SettingManager.getInstance().getAPIKey().toString();
			}
			String value = sendGet(url);
			if (value != null && value.length() != 0)
			{
				JsonElement parser = new JsonParser().parse(value);
				JsonObject json = parser.getAsJsonObject();
				if (json.has("error"))
				{
					severe("JSON error: " + json.get("error").getAsString());
					if (json.has("action") && json.get("action").getAsString().equalsIgnoreCase("dropUUID"))
					{
						if (UUID.fromString(json.get("uuid").getAsString()).equals(SettingManager.getInstance().getAPIKey()))
						{
							SettingManager.getInstance().setAPIKey(null);
							return onStart();
						}
					}
				}
				else
				if (json.has("uuid"))
				{
					SettingManager.getInstance().setAPIKey(UUID.fromString(json.get("uuid").getAsString()));
					SettingManager.getInstance().saveConfig();
				}
				return true;
			}
			return false;
		}
		catch(Exception e)
		{
			severe("Can't generate onStart webrequest");
			e.printStackTrace();
			debug(e);
			return false;
		}
	}
	
	public static void onDisable()
	{
		if (!jsonMessage)
			return;
		
		try
		{
			sendGet("setmode?uuid=" + SettingManager.getInstance().getAPIKey().toString() + "&dataid=" + SettingManager.getInstance().getDataID() + "&" + 
					"type=xChat&mode=off&build=" + XChat.BUILD);
		}
		catch(Exception e)
		{
			severe("Can't generate onDisable webrequest");
			debug(e);
		}
	}
	
	// HTTP GET request
	private static String sendGet(String message) throws Exception {

		String url = "http://134.255.217.210:8080/";
		
		URL obj = new URL(url + message);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		
		//reponse
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();
	}
}

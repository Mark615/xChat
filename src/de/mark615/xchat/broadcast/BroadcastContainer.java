package de.mark615.xchat.broadcast;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import de.mark615.xchat.XChat;
import de.mark615.xchat.file.BroadcastFile;
import de.mark615.xchat.file.SettingManager;
import de.mark615.xchat.object.XUtil;

public class BroadcastContainer{
	
	private BroadcastManager main;
	private BukkitTask task;
	
	private String brPrefix, brName;
	private int intervall, msgNumber;
	private List<String> brMessages;
	private boolean randomMessage, enabled, save;
	
	
	public BroadcastContainer(String prefix, List<String> messages, boolean random, int intervall, boolean save)
	{
		this.brName = null;
		this.msgNumber = 0;
		this.enabled = true;
		this.brPrefix = prefix;
		this.brMessages = messages;
		this.randomMessage = random;
		this.intervall = intervall;
		this.save = save;
	}
	
	public void prepareBroadcastContainer(BroadcastManager main, String brName)
	{
		this.main = main;
		this.brName = brName;
		
		if (save)
		{
			BroadcastFile config = SettingManager.getInstance().getBroadcastFile();
			config.getConfig().set(brName + ".enabled", enabled);
			config.getConfig().set(brName + ".intervall", intervall);
			config.getConfig().set(brName + ".random", randomMessage);
			config.getConfig().set(brName + ".prefix", brPrefix);
			config.getConfig().set(brName + ".messages", brMessages);
			config.saveConfig();
		} 
	}
	
	public BroadcastContainer(BroadcastManager main, String brName, ConfigurationSection section)
	{
		this.main = main;
		this.brName = brName;
		this.msgNumber = 0;
		this.save = false;
		this.enabled = section.getBoolean("enabled", true);
		
		loadBroadcast(section);
	}
	
	private void loadBroadcast(ConfigurationSection section)
	{
		intervall = section.getInt("intervall");
		randomMessage = section.getBoolean("random");	
		
		brPrefix = section.getString("prefix", main.getGlobalPrefix());
		if (brPrefix.equals("") || brPrefix.equalsIgnoreCase("none"))
		{
			brPrefix = main.getGlobalPrefix();
		}
		
		brMessages = section.getStringList("messages");
	}

	public void start()
	{
		if (enabled)
			broadcast();
	}
	
	
	
	private void broadcast()
	{
		task = Bukkit.getServer().getScheduler().runTaskTimer(XChat.getInstance(), new Runnable() {
		@Override
		public void run() {
			if (enabled)
			{
				String message = null;
				if (Bukkit.getServer().getOnlinePlayers().size() > 0)
				{
					if (brMessages != null && brMessages.size() != 0)
		            {
						if (randomMessage)
						{
							int num = ThreadLocalRandom.current().nextInt(0, brMessages.size());
							
							if (brMessages.get(num) != null)
								message = (String) brMessages.get(num);
							
							else
								message = (String) brMessages.get(0);
						}
						
						else
							message = (String) brMessages.get(msgNumber);
		                msgNumber ++;
		                if (msgNumber > (brMessages.size() - 1))
		                	msgNumber = 0;
		                
						broadcastMessage(message);
		            }
				}	
			}
			
		}
		}, ThreadLocalRandom.current().nextInt(10, 130 + 1), (intervall * 20 * 60));
	}
	
	private String replacePlaceHolder(String message)
	{
		message = message.replaceAll("%a", "ä");
		message = message.replaceAll("%A", "Ä");
		message = message.replaceAll("%o", "ö");
		message = message.replaceAll("%O", "Ö");
		message = message.replaceAll("%u", "ü");
		message = message.replaceAll("%U", "Ü");
		message = message.replaceAll("sz", "?");
		
		message = message.replaceAll("%line%", "\n");
		
		return message;
	}
	
	private void broadcastMessage(String message)
	{
		XUtil.broadcast(brPrefix.trim() + " " + message);
	}
	
	
	
	public void stopBroadcast()
	{
		if (task != null)
			Bukkit.getServer().getScheduler().cancelTask(task.getTaskId());
	}
	
	public void disableBroadcast()
	{
		if (enabled)
		{
			this.enabled = false;
			SettingManager.getInstance().getBroadcastFile().setBroadcastEnabled(brName, this.enabled);
			SettingManager.getInstance().getBroadcastFile().saveConfig();
			stopBroadcast();
		}
	}
	
	public void enableBroadcast()
	{
		if (!enabled)
		{
			this.enabled = true;
			SettingManager.getInstance().getBroadcastFile().setBroadcastEnabled(brName, this.enabled);
			SettingManager.getInstance().getBroadcastFile().saveConfig();
			broadcast();
		}
	}
	
	public String getBroadcastprefix()
	{
		return brPrefix;
	}
	
	public String getBroadcastName()
	{
		return brName;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	

	
	public boolean broadcastLine(int line){

		if (brMessages.size() >= line)
		{
	    	broadcastMessage((String) brMessages.get(line - 1));
	    	return true;
		}
		
		return false;
	}
	
	public void showBroadcast(Player p)
	{
		for (int i = 0; i < brMessages.size(); i++)
			XUtil.sendMessage(p, "&a+ (" + Integer.valueOf(i + 1) + ") " + replacePlaceHolder(brMessages.get(i)));
	}
	
	public void showInfo(Player p)
	{
		XUtil.sendMessage(p, XUtil.getMessage("broadcast.enable").replace("%value%", String.valueOf(enabled)));
		XUtil.sendMessage(p, XUtil.getMessage("broadcast.random_msg").replace("%value%", String.valueOf(randomMessage)));
		XUtil.sendMessage(p, XUtil.getMessage("broadcast.intervall").replace("%value%", String.valueOf((intervall * 20 * 60))));
		XUtil.sendMessage(p, XUtil.getMessage("broadcast.name").replace("%value%", brName));
		XUtil.sendMessage(p, XUtil.getMessage("broadcast.counter").replace("%value%", String.valueOf(brMessages.size())));
	}
}

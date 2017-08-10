package de.mark615.xchat.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.mark615.xchat.object.XChatroom;
import de.mark615.xchat.object.XUtil;

public class SettingManager
{
    static SettingManager instance = new SettingManager();
   
    public static SettingManager getInstance()
    {
    	return instance;
    }
    
    private FileConfiguration config;
    private File fConfig;
    
    private FileConfiguration message;
    private File fMessage;
    
    private BroadcastFile broadcastfile;
    private ModtFile modtFile;
    
    private int dataID;
   
	public void setup(Plugin p)
    {
    	if (!p.getDataFolder().exists())
    		p.getDataFolder().mkdir();
    	
    	//load config
    	fConfig = new File(p.getDataFolder(), "config.yml");
    	if(!fConfig.exists())
    		p.saveResource("config.yml", true);
		config = YamlConfiguration.loadConfiguration(fConfig);
		config.options().copyDefaults(true);
		
		//Load default config
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getResource("config.yml"), "UTF-8"));
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(br);
			config.setDefaults(defConfig);	
		}
		catch(Exception e)
		{
			XUtil.severe("cant copy default config.yml", e);
		}
        
		
        //load message
        fMessage = new File(p.getDataFolder(), "messages.yml");
        if(!fMessage.exists())
			p.saveResource("messages.yml", true);
		message = YamlConfiguration.loadConfiguration(fMessage);
		message.options().copyDefaults(true);
		
		//Load default messages
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getResource("messages.yml"), "UTF-8"));
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(br);
			message.setDefaults(defConfig);	
		}
		catch(Exception e)
		{
			XUtil.severe("cant copy default message.yml", e);
		}
		try
		{
			message.save(fMessage);
		}
		catch (IOException e)
		{
			XUtil.severe("Could not save message.yml!");
		}
    }
    
   
//---------Configuration section
    
    public FileConfiguration getConfig()
    {
        return config;
    }
   
    public void saveConfig()
    {
        try {
            config.save(fConfig);
        }
        catch (IOException e) {
        	XUtil.severe("Could not save config.yml!");
        }
    }
   
    public void reloadConfig()
    {
    	config = YamlConfiguration.loadConfiguration(fConfig);
    }
    
    public boolean hasCheckVersion()
    {
    	return config.getBoolean("updatecheck", true);
    }
    
    public void setAPIKey(UUID uuid)
    {
    	config.set("apikey", uuid.toString());
    }
    
    public UUID getAPIKey()
    {
    	return config.getString("apikey", null) == null ? null : UUID.fromString(config.getString("apikey"));
    }
    
    public void setDataID(int dataID)
    {
    	this.dataID = dataID;
    }
    
    public int getDataID()
    {
    	return dataID;
    }
    
    public boolean isAlwaysShowStandardChat()
    {
    	return config.getBoolean("always_show_standard_chat", false);
    }
    
    public String getFormatPattern(String value)
    {
    	switch (value)
    	{
	    	case "displayname_format":
	    	case "listname_format":
	    	case "chat_format":
	    		return config.getString("chat." + value, "%prefix%%name%%suffix%&7: &f");
	    		
	    	case "spy_format":
	    	case "msgchat_toTarget_format":
	    		return config.getString("chat." + value, "%6[%sender%&6]->[%target%&6]&7: &6");
	    		
	    	case "msgchat_toSender_format":
	    		return config.getString("chat." + value, "%6[%target%&6]<-[%sender%&6]&7: &6");
	    		
			default:
	    		return config.getString("chat." + value, null);
    	}
    }
    
    public List<XChatroom> getXChatrooms()
    {
    	List<XChatroom> list = new ArrayList<>();
    	for (String key : config.getConfigurationSection("xchatroom").getKeys(false))
    	{
    		if (config.getConfigurationSection("xchatroom." + key) != null)
    		{
    			list.add(new XChatroom(config.getConfigurationSection("xchatroom." + key)));
    		}
    	}
    	if (list.isEmpty())
    	{
    		XChatroom room = new XChatroom("Standard").setStandart();
    		list.add(room);
    	}
    	
    	return list;
    }
    
    
    
//---------File Section
    
    public BroadcastFile getBroadcastFile()
    {
    	return broadcastfile;
    }
    
    public ModtFile getModtFile()
    {
    	return modtFile;
    }
    

//---------Message section
    
    public FileConfiguration getMessage()
    {
        return message;
    }
   
    public void reloadMessage()
    {
    	message = YamlConfiguration.loadConfiguration(fMessage);
    }
}

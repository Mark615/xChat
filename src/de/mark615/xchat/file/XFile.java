package de.mark615.xchat.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.mark615.xchat.XChat;
import de.mark615.xchat.object.XUtil;

public class XFile
{
	protected FileConfiguration config;
	protected File fConfig;
	
	public XFile(String file)
	{
		XChat p = XChat.getInstance();
		
		//load config
    	fConfig = new File(p.getDataFolder(), file);
    	if(!fConfig.exists())
    		p.saveResource(file, true);
		config = YamlConfiguration.loadConfiguration(fConfig);
		config.options().copyDefaults(true);
		
		//Load default file
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getResource(file), "UTF-8"));
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(br);
			config.setDefaults(defConfig);	
		}
		catch(Exception e)
		{
			XUtil.severe("cant copy default " + file, e);
		}
	}
	
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
}

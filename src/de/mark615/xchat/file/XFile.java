package de.mark615.xchat.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
		
		//config file
    	fConfig = new File(p.getDataFolder(), file);
    	if(!fConfig.exists())
    		p.saveResource(file, true);

		//Store it
		config = YamlConfiguration.loadConfiguration(fConfig);
		config.options().copyDefaults(true);
		
		//Load default messages
		InputStream defConfigStream = p.getResource(file);
		@SuppressWarnings("deprecation")
		YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
		config.setDefaults(defConfig);
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

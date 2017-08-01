package de.mark615.xchat.broadcast;

import java.util.HashMap;

import de.mark615.xapi.object.XUtil;
import de.mark615.xchat.XChat;
import de.mark615.xchat.file.BroadcastFile;
import de.mark615.xchat.file.SettingManager;

public class BroadcastManager
{
	private XChat plugin;
	
	private String globalPrefix;
	private boolean enabled;
	private int brcounter;
	private BroadcastFile config;

	private HashMap<String, BroadcastContainer> brlist = new HashMap<String, BroadcastContainer>();
	
	public BroadcastManager(XChat plugin)
	{
		config = SettingManager.getInstance().getBroadcastFile();
		this.plugin = plugin;
		this.enabled = config.isEnabled();
		this.globalPrefix = config.getPrefix();
		this.brlist = new HashMap<>();
		this.brcounter = 1;
		
		loadBroadcasts();
		
		if (enabled)
			XUtil.info("Enabled Broadcast");
	}
    
    private void loadBroadcasts()
    {
    	if (enabled)
    	{
    		while (config.getConfig().getConfigurationSection("broadcast" + brcounter) != null)
    		{
    			if (config.getConfig().getList("broadcast" + brcounter + ".messages") != null)
    			{
    				BroadcastContainer cast = new BroadcastContainer(this, "broadcast" + brcounter, config.getConfig().getConfigurationSection("broadcast" + brcounter));
    				cast.start();
    				brlist.put(cast.getBroadcastName(), cast);
    			}
    			brcounter++;
    		}
    		
    		if (config.getConfig().getConfigurationSection("maintenance") != null)
    		{
    			if (config.getConfig().getList("maintenance.messages") != null)
    			{
    				BroadcastContainer cast = new BroadcastContainer(this, "maintenance", config.getConfig().getConfigurationSection("maintenance"));
    				cast.start();
    				brlist.put(cast.getBroadcastName(), cast);
    			}
    		}
    	}
    }
    
    public boolean addbroadcast(BroadcastContainer container)
    {
    	try
    	{
    		String name = null;
	    	if (container.getBroadcastName() == null)
	    	{
	    		name = "broadcast" + brcounter;
		    	brcounter++;
	    		container.prepareBroadcastContainer(this, name);
	    	}
	    	container.start();
	    	brlist.put(container.getBroadcastName(), container);
    	}
    	catch (Exception e)
    	{
    		XUtil.severe("Can't add Broadcast");
    		return false;
		}
    	return true;
    }
    
    public void stopAllBroadcast()
    {
    	for (String key : brlist.keySet())
    	{
    		brlist.get(key).stopBroadcast();
    	}
    }
    
	public HashMap<String, BroadcastContainer> getBroadcastList()
	{
		return brlist;
	}
	
	public void reloadBroadcastList()
	{
		config.reloadConfig();
		loadBroadcasts();
	}
	
	public String getGlobalPrefix()
	{
		return globalPrefix;
	}
	
	public boolean hasbroadcast(String name)
	{
		return brlist.containsKey(name);
	}
	
	public BroadcastContainer getBroadcastContainer(String name)
	{
		return brlist.get(name);
	}
	
	public BroadcastContainer getBroadcastContainer(int index)
	{
		
		int i = 0;
		for (String key : brlist.keySet())
		{
			if (i == index)
			{
				return brlist.get(key);
			}
			i++;
		}
		return null;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
}

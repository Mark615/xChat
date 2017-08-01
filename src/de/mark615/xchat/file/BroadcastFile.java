package de.mark615.xchat.file;

public class BroadcastFile extends XFile
{
	private static String DEFAULT_PREFIX = "&7[&6Broadcast&7] &f";
	
	public BroadcastFile()
	{
		super("broadcast.yml");
	}
	
	public String getPrefix()
	{
		return config.getString("prefix", DEFAULT_PREFIX);
	}
	
	public boolean broadcast_to_afk_player()
	{
		return config.getBoolean("br_to_afk_player", true);
	}
	
	public boolean isEnabled()
	{
		return config.getBoolean("enabled", true);
	}
	
	public void setBroadcastEnabled(String broadcast, boolean value)
	{
		config.set(broadcast + ".enabled", value);
	}
}

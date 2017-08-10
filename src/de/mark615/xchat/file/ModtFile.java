package de.mark615.xchat.file;

import java.util.List;
import java.util.Random;

public class ModtFile extends XFile
{
	private Random random;
	
	public ModtFile()
	{
		super("modt.yml");
		this.random = new Random();
	}
	
	public List<String> getModtList()
	{
		return config.getStringList("modt");
	}
	
	public String getRandomModt()
	{
		final List<String> motds = getModtList();
		if (motds.size() == 0)
			return "&2A Minecraft Wild Server";
		return motds.get(random.nextInt(motds.size()));
	}
	
	public List<String> getMaintenanceModtList()
	{
		return config.getStringList("maintenance");
	}
	
	public String getRandomModtMaintenance()
	{
		final List<String> motds = getMaintenanceModtList();
		if (motds.size() == 0)
			return "&4Maintenance !!";
		return motds.get(random.nextInt(motds.size()));
	}
}

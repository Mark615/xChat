package de.mark615.xchat.modt;

import java.net.InetAddress;

import org.bukkit.Bukkit;
import org.bukkit.util.CachedServerIcon;

import de.mark615.xapi.interfaces.XSignInApi;
import de.mark615.xapi.versioncheck.VersionCheck.XType;
import de.mark615.xchat.XChat;
import de.mark615.xchat.broadcast.BroadcastContainer;
import de.mark615.xchat.file.ModtFile;
import de.mark615.xchat.file.SettingManager;
import de.mark615.xchat.object.XUtil;

public class ModtManager
{
	private XChat plugin;
	private boolean maintenanceMode;
	
	public ModtManager(XChat plugin)
	{
		this.plugin = plugin;
		this.maintenanceMode = false;
	}
	
	public void checkMaintenance()
	{
		if (plugin.hasXApi() && plugin.getXApi().getXPlugin(XType.xSignIn) != null)
			this.maintenanceMode = ((XSignInApi) plugin.getXApi().getXPlugin(XType.xSignIn)).isMaintenanceMode();
		manageMaintenanceBroadcast();
	}
	
	public String getNextModt(InetAddress iNetAdd)
	{
		ModtFile config = SettingManager.getInstance().getModtFile();
		String modt = null;
		if (!maintenanceMode)
		{
			modt = config.getRandomModt();
		}
		else
		{
			modt = config.getRandomModtMaintenance();
		}

		//replace strings
		//%player% name es spielers - match über ip
		modt = modt.replace("%v%", Bukkit.getServer().getBukkitVersion());
		modt = modt.replace("%players%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
		modt = modt.replace("%ln%", "\n");
		
		return XUtil.replaceColorCodes(modt);
	}
	
	public int getMaxPlayer()
	{
		return Bukkit.getServer().getMaxPlayers();
	}
	
	public CachedServerIcon getServerIcon()
	{
		return null;
	}
	
	
	public void setMaintenanceMode(boolean value)
	{
		this.maintenanceMode = value;
		manageMaintenanceBroadcast();
	}
	
	public boolean getMaintenanceMode()
	{
		return maintenanceMode;
	}
	
	private void manageMaintenanceBroadcast()
	{
		if (maintenanceMode)
		{
			if (this.plugin.getBroadcastManager().getBroadcastContainer("maintenance") == null)
			{
				BroadcastContainer container = new BroadcastContainer("&4[Maintenance]", SettingManager.getInstance().getModtFile().getMaintenanceModtList(), true, 1, true);
				container.prepareBroadcastContainer(plugin.getBroadcastManager(), "maintenance");
				plugin.getBroadcastManager().addbroadcast(container);
			}
			else
			{
				this.plugin.getBroadcastManager().getBroadcastContainer("maintenance").enableBroadcast();
			}
		}
		else
		if (this.plugin.getBroadcastManager().getBroadcastContainer("maintenance") != null)
		{
			this.plugin.getBroadcastManager().getBroadcastContainer("maintenance").disableBroadcast();
		}
	}
}

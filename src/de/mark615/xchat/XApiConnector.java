package de.mark615.xchat;

import de.mark615.xapi.interfaces.XChatApi;
import de.mark615.xchat.broadcast.BroadcastContainer;
import de.mark615.xchat.object.XUtil;

public class XApiConnector extends XChatApi
{
	private XChat plugin;
	
	public XApiConnector(de.mark615.xapi.XApi xapi, XChat plugin)
	{
		super(xapi);
		this.plugin = plugin;
	}

	//broadcast api
	@Override
	public boolean broadcast(String message)
	{
		XUtil.broadcast(message);
		return true;
	}

	@Override
	public boolean broadcast(String message, String permission)
	{
		XUtil.broadcast(message, permission);
		return true;
	}

	@Override
	public boolean addBroadcast(BroadcastContainer container)
	{
		return plugin.getBroadcastManager().addbroadcast(container);
	}

	@Override
	public boolean reloadBroadcasts()
	{
		plugin.getBroadcastManager().reloadBroadcastList();
		return true;
	}

	@Override
	public boolean enableBroadcast(String name)
	{
		if (plugin.getBroadcastManager().hasbroadcast(name))
		{
			plugin.getBroadcastManager().getBroadcastContainer(name).enableBroadcast();
			return true;
		}
		return false;
	}

	@Override
	public boolean disableBroadcast(String name)
	{
		if (plugin.getBroadcastManager().hasbroadcast(name))
		{
			plugin.getBroadcastManager().getBroadcastContainer(name).disableBroadcast();
			return true;
		}
		return false;
	}
	
	
	
	//modtapi
}

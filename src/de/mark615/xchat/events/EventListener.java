package de.mark615.xchat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import de.mark615.xapi.events.MaintenanceSwitchEvent;
import de.mark615.xapi.events.ServerLoadedEvent;
import de.mark615.xchat.XChat;
import de.mark615.xchat.object.XMessage;
import de.mark615.xchat.object.XMessage.XMessageReturnType;
import de.mark615.xchat.object.XPlayerSubject;
import de.mark615.xchat.object.XUtil;
import net.ess3.api.events.AfkStatusChangeEvent;

public class EventListener implements Listener
{
	private XChat plugin;
	
	public EventListener(XChat plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onServerListPing(final ServerListPingEvent event)
	{
		if (plugin.getModtManager() == null)
			return;
		
		if (plugin.getModtManager().getServerIcon() != null)
			event.setServerIcon(plugin.getModtManager().getServerIcon());
		
		event.setMotd(plugin.getModtManager().getNextModt(event.getAddress()));
		event.setMaxPlayers(plugin.getModtManager().getMaxPlayer());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if (e.getPlayer() != null)
			this.plugin.getChatManager().registerPlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		plugin.getChatManager().unregisterPlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerAFKStateChangedEvent(AfkStatusChangeEvent e)
	{
		if (e.getAffected().getBase() != null)
			this.plugin.getChatManager().getXPlayerSubject(e.getAffected().getBase().getUniqueId()).setAfkMode(e.getValue());
	}
	
	@EventHandler
	public void onMaintenanceSwitchEvent(MaintenanceSwitchEvent e)
	{
		this.plugin.getModtManager().setMaintenanceMode(e.getNewMaintenanceMode());
	}
	
	@EventHandler
	public void onServerLoadedEvent(ServerLoadedEvent e)
	{
		this.plugin.getModtManager().checkMaintenance();
	}
	
	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent e)
	{
		if (e.getPlayer() == null)
			return;
		XPlayerSubject subject = plugin.getChatManager().getXPlayerSubject(e.getPlayer().getUniqueId());
		
		XMessage msg = null;
		if (subject == null)
			return;
		
		if (subject.getXChatroom() == null)
		{
			subject.setXChatroom(plugin.getChatManager().getStandardXChatroom());
		}
		
		
		if (subject.getXChatroom() == null)
		{
			XUtil.sendFileMessage(e.getPlayer(), "message.room.error");
			return;
		}
		
		if (!subject.getXChatroom().isPrivateChatRoom())
		{
			msg = new XMessage.XMessage_Standard(subject, e.getMessage());
		}
		else
		{
			msg = new XMessage.XMessage_PRIVATECHAT(subject, e.getMessage());
		}
		XMessageReturnType type = msg.send(plugin);
		e.setFormat(msg.getMessage());
		
		if (type.equals(XMessageReturnType.SUCCESS))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerSwitchWorldEvent(PlayerChangedWorldEvent e)
	{
		if (e.getPlayer() == null)
			return;
		
		plugin.getChatManager().playerswitchWorld(plugin.getChatManager().getXPlayerSubject(e.getPlayer().getUniqueId()));
	}
	
}

package com.minecarts.verrier.barrenschat.listener;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.*;

import org.bukkit.ChatColor;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActiveHelper;

import com.minecarts.verrier.barrenschat.BarrensChat;
import com.minecarts.verrier.barrenschat.ChatChannel;
import com.minecarts.verrier.barrenschat.event.*;
import com.minecarts.verrier.barrenschat.helpers.ChannelInfo;

import java.util.ArrayList;
import java.util.List;


import org.bukkit.util.Vector;

public class PlayerListener extends org.bukkit.event.player.PlayerListener
{
	private final BarrensChat plugin;
	private String shoutPrefix;
	private boolean adminsAlwaysShout = false;
	private boolean playersCanShout = true;
	private int shoutRadius = 200;

	public PlayerListener(BarrensChat instance)
	{
		plugin = instance;
		reload();
	}

	private void reload()
	{
		/*
		plugin.getConfiguration().load();
		adminsAlwaysShout = plugin.getConfiguration().getBoolean("admins-always-shout", adminsAlwaysShout);
		playersCanShout = plugin.getConfiguration().getBoolean("players-can-shout", playersCanShout);
		shoutRadius = plugin.getConfiguration().getInt("shout-radius", shoutRadius);
		shoutPrefix = "§c" + plugin.getConfiguration().getString("shout-prefix", "[Shout]") + "§f ";
		*/
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		ArrayList<ChannelInfo> chatChannels = plugin.dbHelper.getPlayerChannelsInfo(player);
		
		if(chatChannels.size() > 0){
			plugin.log.info("Player rejoined, loading channel data");
			//Join them to each channel
			for(ChannelInfo ci : chatChannels){
				if(plugin.debug){
					plugin.log.info("Joining " + player.getName() + " to " + ci.name);
				}
				ChatChannel chan = plugin.channelHelper.getChannelFromName(ci.name);
				ChatChannelJoinEvent ccje = new ChatChannelJoinEvent(player, chan, "JOIN", true);
		        plugin.getServer().getPluginManager().callEvent(ccje);
				//plugin.channelHelper.joinChannel(player, ci.name,true);			
			}
		} else {
			plugin.log.info("New player connected, or someone with no channels... joining to default channels");
			
			//ChatChannel chan = plugin.channelHelper.joinChannel(event.getPlayer(), "Global");
			
			//plugin.dbHelper.setDefaultChannel(player, chan);
			//plugin.channelHelper.joinChannel(event.getPlayer(), "PVP");

			ChatChannel chan = plugin.channelHelper.getChannelFromName("Global");
			ChatChannelJoinEvent ccje = new ChatChannelJoinEvent(player, chan, "JOIN", false);
	        plugin.getServer().getPluginManager().callEvent(ccje);
	        
	        chan = plugin.channelHelper.getChannelFromName("PVP");
			ccje = new ChatChannelJoinEvent(player, chan, "JOIN", false);
	        plugin.getServer().getPluginManager().callEvent(ccje);
		}
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		//Get all the channels the player is in
		ArrayList<ChannelInfo> channelInfo = plugin.dbHelper.getPlayerChannelsInfo(player);
		for(ChannelInfo ci: channelInfo){
			ChatChannel c = plugin.channelHelper.getChannelFromName(ci.name);
			ChatChannelLeaveEvent ccle = new ChatChannelLeaveEvent(player,c,"QUIT");
			plugin.getServer().getPluginManager().callEvent(ccle);
		}	
		
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event)
	{
		//If it's canceled,  don't do anything
		if(event.isCancelled()) return;
		
		Player player = event.getPlayer();
		event.setCancelled(true); //Always cancel the event becuase we handle it
		//If this player has a default channel, then we want to
		//	send the chat into their default channel
			ChannelInfo defaultChannelInfo = plugin.dbHelper.getDefaultChannelInfo(player);
			//See if this player has a default channel set
			if(defaultChannelInfo != null){
				ChatChannel defaultChannel = plugin.channelHelper.getChannelFromName(defaultChannelInfo.name);
				//@TODO: Is this needed?? : See if they're still in this channel
				if(defaultChannelInfo.index >= 0){
					//Trigger the channel message event
					ChatChannelMessageEvent ccme = new ChatChannelMessageEvent(player,defaultChannel,event.getMessage());
					plugin.getServer().getPluginManager().callEvent(ccme);
					//Cancel the normal chat event
					//event.setCancelled(true);
					return;
				} else {
					player.sendMessage("You're no longer in the channel " + defaultChannel.name);
					player.sendMessage(ChatColor.DARK_GRAY + " Default chat now sending to nearby players");
				}
			}
		
		//Else if they have no default set, continue on to displaying it in the local say area
		//Get the distance between the two players
		Vector currentPlayer = event.getPlayer().getLocation().toVector();
	
		String message = String.format(event.getFormat(), player.getDisplayName(), event.getMessage());
		Logger.getLogger("Minecraft").log(Level.INFO, String.format("Local: %1$s", message));
	
		//Ideally we would make use of event.getRecipients()
		//	but that was just recently implemented and
		//	maybe we'll do that in the future		
		ArrayList<RecipientData> recipients = new ArrayList<RecipientData>();
		for (Player p : plugin.getServer().getOnlinePlayers())
		{
			if (p != player)
			{
				Double distance = currentPlayer.distance(p.getLocation().toVector());
				recipients.add(new RecipientData(distance,p));
			}
		}
		
		ChatLocalMessageEvent clme = new ChatLocalMessageEvent(player,recipients,message);
		plugin.getServer().getPluginManager().callEvent(clme);
		
		//And display the message to the actual player who sent the message, but
		//	also display a tip if there were no players
		player.sendMessage(message);
	}
	
	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
	//This will occur on all commands, we want to check to see if
	//	it's a chat message to a specific channel, if so
	//	we're going to handle it here
		try{
			//Strip out the cleaning
			Player player = event.getPlayer();
			String[] args = event.getMessage().replaceAll(" +", " ").split(" ", 2);
			int channelNum = Integer.parseInt(args[0].replaceAll("/", ""));
			ChannelInfo ci = plugin.dbHelper.getChannelInfoAtIndex(player, channelNum);
			if(ci != null && channelNum <= plugin.MAX_CHANNELS){
				ChatChannel chan = plugin.channelHelper.getChannelFromName(ci.name);
				if(args.length > 1 && args[1].length() > 0 && args[1] != null){
					//Trigger the channel message event
					ChatChannelMessageEvent ccme = new ChatChannelMessageEvent(player,chan,args[1]);
					plugin.getServer().getPluginManager().callEvent(ccme);
				}
				plugin.channelHelper.attemptDefaultChannelSet(player,chan); //And try making this the default channel if need be
				event.setCancelled(true); //And cancel the event because the command has been handled
			}
		} catch (java.lang.NumberFormatException ex2){
			//It wasn't an interger, let onCommand() deal with it.
		}
	}
	
	
	
	public class RecipientData{
		public Double distance;
		public Player player;
		public RecipientData(Double distance, Player player){
			this.distance = distance;
			this.player = player;
		}
	}
}

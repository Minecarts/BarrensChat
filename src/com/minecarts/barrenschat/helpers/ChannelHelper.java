package com.minecarts.barrenschat.helpers;
 
import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.ChatChannel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
 
public class ChannelHelper {
   BarrensChat plugin;
   public ChannelHelper(BarrensChat plugin)
   {
     this.plugin = plugin;
   }


   //Join a player to a channel either by a channelName or a channel itself
   //	The rejoining flag will skip checking if they can join the channel which 
   //	is used when a player reconnects to the server and we rejoin them to their channels 
   public ChatChannel joinChannel(Player player, ChatChannel channel, int index, boolean rejoining, boolean alertSelf, boolean alertOthers, boolean setDefault) {
     String reason = null;
     if (!rejoining){
       reason = channel.canPlayerJoin(player);
     }
     if (reason == null) {
       if (index != -1) {
         if (!rejoining) this.plugin.dbHelper.addPlayerChannel(player, channel, index, setDefault);
         channel.join(player, alertOthers, alertSelf); //Handle alerting joining the channel
       } else {
           this.plugin.log.info(player.getName() + " had a -1 index when joining " + channel.getName());
       }
     } else {
       this.plugin.log.info(player.getName() + " could not join channel " + channel.getName() + ": " + reason);
       player.sendMessage(ChatColor.RED + "Unable to join channel: " + channel.getName());
       player.sendMessage(ChatColor.RED + " Reason: " + reason);
       return null;
     }
 
     return channel;
   }
 
   public ChatChannel joinChannel(Player player, String channelName, int index, boolean rejoining, boolean alertSelf, boolean alertOthers, boolean setDefault){
       ChatChannel channel;
       if (!this.plugin.channelList.containsKey(channelName.toLowerCase())) {
         channel = createChannel(channelName);
       } else {
         channel = (ChatChannel)this.plugin.channelList.get(channelName.toLowerCase());
       }
       return joinChannel(player, channel, index, rejoining, alertSelf, alertOthers, setDefault);
   }

   public ChatChannel getChannelFromName(String channelName)
   {
     if (this.plugin.channelList.containsKey(channelName.toLowerCase())) {
       return (ChatChannel)this.plugin.channelList.get(channelName.toLowerCase());
     }
     return createChannel(channelName);
   }


   private ChatChannel createChannel(String channelName)
   {
     this.plugin.getClass();
 
     ChatChannel channel = new ChatChannel(this.plugin,channelName);
     channel.setId(channelName.toLowerCase());
 
     this.plugin.channelList.put(channelName.toLowerCase(), channel);
     this.plugin.getClass();
 
     return channel;
   }
 }
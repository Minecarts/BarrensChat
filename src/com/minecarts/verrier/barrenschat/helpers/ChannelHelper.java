 package com.minecarts.verrier.barrenschat.helpers;
 
 import com.minecarts.verrier.barrenschat.BarrensChat;
 import com.minecarts.verrier.barrenschat.ChatChannel;
 import java.util.HashMap;
 import java.util.logging.Logger;
 import org.bukkit.ChatColor;
 import org.bukkit.entity.Player;
 
 public class ChannelHelper
 {
   BarrensChat plugin;
   public ChannelHelper(BarrensChat plugin)
   {
     this.plugin = plugin;
   }
 
   
   //Join a player to a channel either by a channelName or a channel itself
   //	The rejoining flag will skip checking if they can join the channel which 
   //	is used when a player reconnects to the server and we rejoin them to their channels
   public ChatChannel joinChannel(Player player, String channelName)
   {
     return joinChannel(player, channelName, false);
   }
   public ChatChannel joinChannel(Player player, ChatChannel channel) {
     return joinChannel(player, channel, false);
   }
 
   public ChatChannel joinChannel(Player player, ChatChannel channel, boolean rejoining)
   {
     String reason = null;
 
     if (!rejoining) {
       reason = channel.canPlayerJoin(player);
     }
     if (reason == null) {
       int nextIndex = this.plugin.dbHelper.getNextChannelIndex(player);
       if (nextIndex != -1)
       {
         if (!rejoining) {
           this.plugin.dbHelper.addPlayerChannel(player, channel, nextIndex);
         }
         channel.join(player);
       }
     } else {
       this.plugin.log.info(player.getName() + " could not join channel " + channel.name + ": " + reason);
       player.sendMessage(ChatColor.RED + "Unable to join channel: " + channel.name);
       player.sendMessage(ChatColor.RED + " Reason: " + reason);
       return null;
     }
 
     return channel;
   }
 
   public ChatChannel joinChannel(Player player, String channelName, boolean rejoining)
   {
     ChatChannel channel;
     if (!this.plugin.channelList.containsKey(channelName.toLowerCase())) {
       channel = createChannel(channelName);
     } else {
       channel = (ChatChannel)this.plugin.channelList.get(channelName.toLowerCase());
       this.plugin.getClass();
     }
 
     channel = joinChannel(player, channel, rejoining);
     return channel;
   }
 
   
   
   
   
   public void attemptDefaultChannelSet(Player player, ChatChannel chan)
   {
     ChannelInfo defaultChannelInfo = this.plugin.dbHelper.getDefaultChannelInfo(player);
 
     if ((defaultChannelInfo == null) || (defaultChannelInfo.id != chan.getId())) {
       this.plugin.dbHelper.setDefaultChannel(player, chan);
 
       ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(player, chan);
 
       player.sendMessage(ChatColor.DARK_GRAY + "Default chat now sending to [" + channelInfo.index + "] " + chan.name + ".");
 
       player.sendMessage(ChatColor.DARK_GRAY + " Type /say to chat with nearby players.");
     }
     else
     {
       this.plugin.getClass();
     }
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
 
     ChatChannel channel = new ChatChannel();
     channel.setId(channelName.toLowerCase());
     channel.name = channelName;
     channel.setPlugin(this.plugin);
 
     this.plugin.channelList.put(channelName.toLowerCase(), channel);
     this.plugin.getClass();
 
     return channel;
   }
 }
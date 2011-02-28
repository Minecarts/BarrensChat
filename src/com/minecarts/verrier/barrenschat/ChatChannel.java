 package com.minecarts.verrier.barrenschat;
 
 import com.minecarts.verrier.barrenschat.helpers.Cache;
 import com.minecarts.verrier.barrenschat.helpers.Cache.ignoreList;
 import com.minecarts.verrier.barrenschat.helpers.ChannelInfo;
 import com.minecarts.verrier.barrenschat.helpers.DBHelper;
 import java.util.ArrayList;
 import java.util.logging.Logger;
 import org.bukkit.ChatColor;
 import org.bukkit.entity.Player;
 
 public class ChatChannel
 {
   private BarrensChat plugin;
   private ArrayList<Player> banList = new ArrayList<Player>();
   public ArrayList<Player> playerList = new ArrayList<Player>();
   public Player moderator = null;
   private String id;
   public String name = "Undefined";
 
   public String getId()
   {
     return this.id;
   }
 
   public void setId(String id) {
     this.id = id;
   }
 
   public void setPlugin(BarrensChat plugin) {
     this.plugin = plugin;
   }
 
   public ChatChannel()
   {
   }
 
   public ChatChannel(BarrensChat plugin, String name)
   {
     this.plugin = plugin;
     this.name = name;
   }
 
   public String canPlayerJoin(Player player)
   {
     if (this.playerList.contains(player)) {
       return "You are already in this channel.";
     }
 
     if (this.banList.contains(player)) {
       return "You are banned from this channel.";
     }
 
     this.plugin.getClass(); if (this.plugin.dbHelper.getNumChannels(player) >= 10) {
       return "You are in the maximum number of channels.";
     }
 
     return null;
   }
 
   public void join(Player player)
   {
     msg(player.getName() + " joined the channel.");
     this.playerList.add(player);
 
     ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(player, this);
     ChatColor color = this.plugin.channelColors[(channelInfo.index.intValue() % this.plugin.channelColors.length)];
 
     player.sendMessage(String.format(ChatFormatString.SELF_CHANNEL_JOIN, new Object[] { color, channelInfo.index, this.name }));
   }
 
   public void leave(Player player) {
     ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(player, this);
     ChatColor color = this.plugin.channelColors[(channelInfo.index.intValue() % this.plugin.channelColors.length)];
 
     player.sendMessage(String.format(ChatFormatString.SELF_CHANNEL_LEAVE, new Object[] { color, channelInfo.index, this.name }));
 
     this.playerList.remove(player);
     msg(player.getName() + " left the channel.");
 
     if (this.playerList.isEmpty()) {
       this.plugin.getClass();
     }
     else if (this.moderator == player)
     {
       Player nextMod = (Player)this.playerList.get(0);
       setModerator(nextMod);
     }
   }
 
   public void msg(String msg)
   {
     for (Player p : this.playerList) {
       ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(p, this);
       ChatColor color = this.plugin.channelColors[(channelInfo.index.intValue() % this.plugin.channelColors.length)];
       p.sendMessage(color + String.format(ChatFormatString.CHANNEL_BROADCAST, new Object[] { channelInfo.index, msg }));
     }
   }
 
   public void chat(Player sender, String msg)
   {
     for (Player p : this.playerList)
     {
       if (this.plugin.cache.ignoreList.isIgnoring(p, sender))
       {
         continue;
       }
       ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(p, this);
       ChatColor color = this.plugin.channelColors[(channelInfo.index.intValue() % this.plugin.channelColors.length)];
       String formattedMsg = String.format(ChatFormatString.CHANNEL_USER_MESSAGE, new Object[] { channelInfo.index, sender.getName(), msg });
 
       p.sendMessage(color + formattedMsg);
     }
   }
 
   public ArrayList<String> getPlayerList()
   {
     ArrayList<String> players = new ArrayList<String>();
     for (Player p : this.playerList) {
       if (p.getName() == this.moderator.getName())
         players.add("*" + p.getName());
       else {
         players.add(p.getName());
       }
     }
     return players;
   }
 
   public int numPlayers() {
     return this.playerList.size();
   }
 
   public void setModerator(Player player) {
     if (this.name != "Global") {
       this.moderator = player;
       msg(player.getName() + " is now the channel moderator.");
       this.plugin.log.info(player.getName() + " is now the moderator of " + this.name + ".");
     }
   }
 }

/* Location:           F:\Users\stephen\Desktop\BarrensChat.jar
 * Qualified Name:     com.minecarts.verrier.barrenschat.ChatChannel
 * JD-Core Version:    0.6.0
 */
package com.minecarts.barrenschat;

import com.minecarts.barrenschat.helpers.ChannelInfo;
import com.minecarts.barrenschat.cache.CacheIgnore;
import com.minecarts.barrenschat.ChatFormatString;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatChannel {
    private BarrensChat plugin;
    private ArrayList<Player> banList = new ArrayList<Player>();
    public ArrayList<Player> playerList = new ArrayList<Player>();
    public Player moderator = null;
    private String id;
    private String name = "Undefined";

    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }
 
    public void setPlugin(BarrensChat plugin) { this.plugin = plugin; }

    public ChatChannel(BarrensChat plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public String canPlayerJoin(Player player){
        if (this.playerList.contains(player)) { return "You are already in this channel."; }
        if (this.banList.contains(player)) { return "You are banned from this channel."; }
        if (this.plugin.dbHelper.getNumChannels(player) >= 10) { return "You are in the maximum number of channels."; }
        return null;
    }

    public void join(Player player){
        this.join(player,true,true);
    }

    public void join(Player player, boolean alertOthers, boolean alertSelf){
        if(alertOthers) announce(new Player[]{player},player.getName() + " joined the channel.");
        this.playerList.add(player);

        ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(player, this);
        ChatColor color = ChatColor.valueOf(this.plugin.channelColors.get((channelInfo.index % this.plugin.channelColors.size())));

        if(alertSelf) player.sendMessage(String.format(ChatFormatString.SELF_CHANNEL_JOIN, color, channelInfo.index, this.name));
    }
 
    public void leave(Player player){ this.leave(player,true); }
    public void leave(Player player, boolean alertOthers) {
        ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(player, this);
        ChatColor color = ChatColor.valueOf(this.plugin.channelColors.get((channelInfo.index % this.plugin.channelColors.size())));

        player.sendMessage(String.format(ChatFormatString.SELF_CHANNEL_LEAVE, color, channelInfo.index, this.name));
        this.playerList.remove(player);

        if(alertOthers) announce(new Player[]{player},player.getName() + " left the channel.");
 
        if (this.playerList.isEmpty()) { /*Delete the channel?*/ }
        else if (this.moderator == player) {
            Player nextMod = (Player)this.playerList.get(0);
            setModerator(nextMod);
        }
    }

   //Handle announced based upon player so if that player is ignored, it won't be seen
   public void announce(Player[] involvedPlayers, String msg){
       for (Player p : this.playerList) {
           int ignoreCount = 0;
           //Check if they're ignoring any actions by this player
           for(Player involvedPlayer : involvedPlayers){
               if (!CacheIgnore.isIgnoring(p, involvedPlayer)){ ignoreCount++;}
           }
           if(involvedPlayers.length == ignoreCount){ //Send the message if they're not ignored
               ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(p, this);
               ChatColor color = ChatColor.valueOf(this.plugin.channelColors.get((channelInfo.index % this.plugin.channelColors.size())));
               p.sendMessage(color + String.format(ChatFormatString.CHANNEL_BROADCAST, channelInfo.index, msg ));
           }
       }
   }
   //Sends messages to all players in a channel (if not involving a player to check ignore against)
   public void announce(String msg) {
     for (Player p : this.playerList) {
       ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(p, this);
       ChatColor color = ChatColor.valueOf(this.plugin.channelColors.get((channelInfo.index % this.plugin.channelColors.size())));
       p.sendMessage(color + String.format(ChatFormatString.CHANNEL_BROADCAST, channelInfo.index, msg ));
     }
   }
 
   public void chat(Player sender, String msg) {
     for (Player p : this.playerList) {
       if (CacheIgnore.isIgnoring(p, sender)) { continue; }
       ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(p, this);
       ChatColor color = ChatColor.valueOf(this.plugin.channelColors.get((channelInfo.index % this.plugin.channelColors.size())));
       String formattedMsg = String.format(ChatFormatString.CHANNEL_USER_MESSAGE, channelInfo.index, sender.getName(), msg);
 
       p.sendMessage(color + formattedMsg);
     }
   }
 
   public ArrayList<String> getPlayerList() {
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
 
   public String getName(){
       return this.name;
   }
   public void setName(String name){
       this.name = name;
   }
   
   public int numPlayers() {
     return this.playerList.size();
   }
 
   public void setModerator(Player player) {
     if (this.name != "Global") {
       this.moderator = player;
       announce(new Player[]{player},player.getName() + " is now the channel moderator.");
       this.plugin.log.info(player.getName() + " is now the moderator of " + this.name + ".");
     }
   }
 }
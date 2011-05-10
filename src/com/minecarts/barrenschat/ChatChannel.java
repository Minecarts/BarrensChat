package com.minecarts.barrenschat;

import com.minecarts.barrenschat.helpers.ChannelInfo;
import com.minecarts.barrenschat.cache.CacheIgnore;
import com.minecarts.barrenschat.ChatFormatString;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

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
        if(alertOthers) announce(new Player[]{player},ChatFormatString.CHANNEL_USER_JOIN,player.getDisplayName());
        this.playerList.add(player);

        ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(player, this);
        ChatColor color = ChatColor.valueOf(this.plugin.channelColors.get((channelInfo.index % this.plugin.channelColors.size())));

        if(alertSelf) player.sendMessage(MessageFormat.format(ChatFormatString.SELF_CHANNEL_JOIN, color, channelInfo.index, this.name));
    }
 
    public void leave(Player player){ this.leave(player,true); }
    public void leave(Player player, boolean alertOthers) {
        ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(player, this);
        ChatColor color = ChatColor.valueOf(this.plugin.channelColors.get((channelInfo.index % this.plugin.channelColors.size())));

        player.sendMessage(MessageFormat.format(ChatFormatString.SELF_CHANNEL_LEAVE, color, channelInfo.index, this.name));
        this.playerList.remove(player);

        if(alertOthers) announce(new Player[]{player},ChatFormatString.CHANNEL_USER_LEAVE,player.getDisplayName());

        if (this.playerList.isEmpty()) { /*Delete the channel?*/ }
        else if (this.moderator == player) {
            Player nextMod = (Player)this.playerList.get(0);
            setModerator(nextMod);
        }
    }

   //Handle announced based upon player so if that player is ignored, it won't be seen
   public void announce(Player[] involvedPlayers, String format, String... args){
       for (Player p : this.playerList) {
           int ignoreCount = 0;
           //Check if they're ignoring any actions by this player
           for(Player involvedPlayer : involvedPlayers){
               if (!CacheIgnore.isIgnoring(p, involvedPlayer)){ ignoreCount++;}
           }
           if(involvedPlayers.length == ignoreCount){ //Send the message if they're not ignored
               ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(p, this);
               ChatColor color = ChatColor.valueOf(this.plugin.channelColors.get((channelInfo.index % this.plugin.channelColors.size())));

               //I wish there was a more elegantt way to do this
               //   broken up into multiple lines to maintain clarity
               ArrayList<String> finalArgs = new ArrayList<String>();
               finalArgs.add(color.toString());
               finalArgs.add(channelInfo.index.toString());
               finalArgs.addAll(Arrays.asList(args));
               p.sendMessage(MessageFormat.format(format, finalArgs.toArray()));

           }
       }
   }
   //Sends messages to all players in a channel (if not involving a player to check ignore against)
   public void announce(String msg) {
     for (Player p : this.playerList) {
       ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(p, this);
       ChatColor color = ChatColor.valueOf(this.plugin.channelColors.get((channelInfo.index % this.plugin.channelColors.size())));
       p.sendMessage(color + MessageFormat.format(ChatFormatString.CHANNEL_BROADCAST, channelInfo.index, msg));
     }
   }
 
   public void chat(Player sender, String msg) {
     for (Player p : this.playerList) {
       if (CacheIgnore.isIgnoring(p, sender)) { continue; }
       ChannelInfo channelInfo = this.plugin.dbHelper.getChannelInfoByChannel(p, this);
       ChatColor color = ChatColor.valueOf(this.plugin.channelColors.get((channelInfo.index % this.plugin.channelColors.size())));
       
       p.sendMessage(MessageFormat.format(ChatFormatString.CHANNEL_USER_MESSAGE,new Object[]{color,channelInfo.index,sender.getDisplayName(),msg}));
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
       announce(new Player[]{player},ChatFormatString.CHANNEL_MODERATOR_CHANGE,player.getName());
       this.plugin.log.info(player.getName() + " is now the moderator of " + this.name + ".");
     }
   }
 }
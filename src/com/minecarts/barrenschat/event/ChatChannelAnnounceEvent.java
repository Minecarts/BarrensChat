package com.minecarts.barrenschat.event;

import com.minecarts.barrenschat.ChatChannel;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class ChatChannelAnnounceEvent extends Event implements Cancellable {
    private ChatChannel channel;
    private String format;
    private Player[] involvedPlayers;
    private String[] args;
    private boolean cancel = false;
 
   public ChatChannelAnnounceEvent(ChatChannel channel, Player[] involvedPlayers, String format, String... args) {
     super("ChatChannelAnnounceEvent");
     this.channel = channel;
     this.format = format;
     this.involvedPlayers = involvedPlayers;
     this.args = args;
   }
 
   public boolean isCancelled()
   {
     return this.cancel;
   }
 
   public void setCancelled(boolean cancel) {
     this.cancel = cancel;
   }
 
   public ChatChannel getChannel() {
     return this.channel;
   }
 
   public String getFormat() {
     return this.format;
   }
 
   public void setFormat(String format) {
     this.format = format;
   }
   
   public String[] getArgs() {
       return this.args;
     }
   
     public void setArgs(String... args) {
       this.args = args;
     }
   
   public void setInvolvedPlayers(Player[] players){
       this.involvedPlayers = players;
   }
   public Player[] getInvolvedPlayers(){
       return this.involvedPlayers;
   }
 }
 package com.minecarts.barrenschat.event;
 
 import com.minecarts.barrenschat.ChatChannel;

 import org.bukkit.entity.Player;
 import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
 
 public class ChatChannelJoinEvent extends Event
   implements Cancellable
 {
   private Player player;
   private ChatChannel channel;
   private String reason;
   private boolean rejoining = false;
   private boolean alertSelf = false;
   private boolean alertOthers = false;
   private boolean setDefault = false;
   private boolean cancel = false;
   
 
   public ChatChannelJoinEvent(Player player, ChatChannel channel, String reason, boolean rejoining, boolean alertSelf, boolean alertOthers, boolean setDefault) {
     super("ChatChannelJoinEvent");
     this.player = player;
     this.channel = channel;
     this.reason = reason;
     this.rejoining = rejoining;
     this.alertSelf = alertSelf;
     this.alertOthers = alertOthers;
     this.setDefault = setDefault;
   }
 
   public boolean isCancelled()
   {
     return this.cancel;
   }
 
   public void setCancelled(boolean cancel) {
     this.cancel = cancel;
   }
 
   public Player getPlayer() {
     return this.player;
   }
 
   public ChatChannel getChannel() {
     return this.channel;
   }
   
   public String getReason() {
     return this.reason;
   }
   
   public boolean getRejoining() {
     return this.rejoining;
   }
   public boolean getAlertSelf() {
       return this.alertSelf;
   }
   public boolean getAlertOthers() {
       return this.alertOthers;
   }
   public boolean getDefault() {
       return this.setDefault;
     }
   
   public void setAlertSelf(boolean alertStatus){
       this.alertSelf = alertStatus;
   }
   public void setAlertOthers(boolean alertStatus){
       this.alertOthers = alertStatus;
   }
 }
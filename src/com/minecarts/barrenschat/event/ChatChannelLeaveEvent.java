 package com.minecarts.barrenschat.event;
 
 import com.minecarts.barrenschat.ChatChannel;

 import org.bukkit.entity.Player;
 import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
 
 public class ChatChannelLeaveEvent extends Event
   implements Cancellable
 {
   private Player player;
   private ChatChannel channel;
   private boolean cancel = false;
   private String reason;
 
   public ChatChannelLeaveEvent(Player player, ChatChannel channel, String reason)
   {
     super("ChatChannelLeaveEvent");
     this.player = player;
     this.channel = channel;
     this.reason = reason;
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
 }
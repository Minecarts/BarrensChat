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
   private boolean rejoin;
   private boolean cancel = false;
 
   public ChatChannelJoinEvent(Player player, ChatChannel channel, String reason, boolean rejoin) {
     super("ChatChannelJoinEvent");
     this.player = player;
     this.channel = channel;
     this.reason = reason;
     this.rejoin = rejoin;
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
   
   public boolean getRejoin() {
     return this.rejoin;
   }
 }
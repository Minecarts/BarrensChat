 package com.minecarts.barrenschat.event;
 
 import com.minecarts.barrenschat.ChatChannel;

 import org.bukkit.entity.Player;
 import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
 
 public class ChatChannelMessageEvent extends Event
   implements Cancellable
 {
   private Player player;
   private ChatChannel channel;
   private String message;
   private int index;
   private boolean cancel = false;
 
   public ChatChannelMessageEvent(Player player, ChatChannel channel, String message, int index) {
     super("ChatChannelMessageEvent");
     this.player = player;
     this.channel = channel;
     this.message = message;
     this.index = index;
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
   public int getIndex(){
       return this.index;
   }
 
   public String getMessage() {
     return this.message;
   }
   public void setMessage(String message) {
     this.message = message;
   }
 }
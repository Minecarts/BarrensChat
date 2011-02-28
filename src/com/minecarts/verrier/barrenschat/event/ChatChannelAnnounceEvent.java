 package com.minecarts.verrier.barrenschat.event;
 
 import com.minecarts.verrier.barrenschat.ChatChannel;
 import org.bukkit.entity.Player;
 import org.bukkit.event.Cancellable;
 import org.bukkit.event.Event;
 
 public class ChatChannelAnnounceEvent extends Event
   implements Cancellable
 {
   private Player player;
   private ChatChannel channel;
   private String message;
   private boolean cancel = false;
 
   public ChatChannelAnnounceEvent(ChatChannel channel, String message) {
     super("ChatChannelAnnounceEvent");
     this.channel = channel;
     this.message = message;
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
 
   public String getMessage() {
     return this.message;
   }
 
   public void setMessage(String message) {
     this.message = message;
   }
 }

/* Location:           F:\Users\stephen\Desktop\BarrensChat.jar
 * Qualified Name:     com.minecarts.verrier.barrenschat.event.ChatChannelAnnounceEvent
 * JD-Core Version:    0.6.0
 */
 package com.minecarts.verrier.barrenschat.event;
 
 import com.minecarts.verrier.barrenschat.ChatChannel;
 import org.bukkit.entity.Player;
 import org.bukkit.event.Cancellable;
 import org.bukkit.event.Event;
 
 public class ChatChannelJoinEvent extends Event
   implements Cancellable
 {
   private Player player;
   private ChatChannel channel;
   private boolean cancel = false;
 
   public ChatChannelJoinEvent(Player player, ChatChannel channel) {
     super("ChatChannelJoinEvent");
     this.player = player;
     this.channel = channel;
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
 }

/* Location:           F:\Users\stephen\Desktop\BarrensChat.jar
 * Qualified Name:     com.minecarts.verrier.barrenschat.event.ChatChannelJoinEvent
 * JD-Core Version:    0.6.0
 */
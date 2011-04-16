 package com.minecarts.barrenschat.event;
 
 import com.minecarts.barrenschat.ChatChannel;

 import org.bukkit.entity.Player;
 import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
 
 public class ChatChannelDefaultChangeEvent extends Event
   implements Cancellable
 {
   private Player player;
   private ChatChannel channel;
   private int index;
   private boolean cancel = false;
  
   public ChatChannelDefaultChangeEvent(Player player, ChatChannel channel, int index) {
     super("ChatChannelDefaultChangeEvent");
     this.player = player;
     this.channel = channel;
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
   
   public int getIndex() {
     return this.index;
   }
 }
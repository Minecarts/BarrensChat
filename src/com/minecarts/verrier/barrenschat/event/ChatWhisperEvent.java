 package com.minecarts.verrier.barrenschat.event;
 
 import org.bukkit.entity.Player;
 import org.bukkit.event.Cancellable;
 import org.bukkit.event.Event;
 
 public class ChatWhisperEvent extends Event
   implements Cancellable
 {
   private Player sender;
   private Player receiver;
   private String message;
   private boolean cancel = false;
 
   public ChatWhisperEvent(Player sender, Player receiver, String message) {
     super("ChatWhisperEvent");
     this.sender = sender;
     this.receiver = receiver;
     this.message = message;
   }
 
   public boolean isCancelled()
   {
     return this.cancel;
   }
 
   public void setCancelled(boolean cancel) {
     this.cancel = cancel;
   }
 
   public Player getSender() {
     return this.sender;
   }
 
   public Player getReceiver() {
     return this.receiver;
   }
 
   public String getMessage() {
     return this.message;
   }
 }
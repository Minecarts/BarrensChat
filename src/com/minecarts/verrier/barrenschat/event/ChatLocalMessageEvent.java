 package com.minecarts.verrier.barrenschat.event;
 
 import java.util.ArrayList;

import com.minecarts.verrier.barrenschat.ChatChannel;
import com.minecarts.verrier.barrenschat.listener.PlayerListener.RecipientData;

 import org.bukkit.entity.Player;
 import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
 
 public class ChatLocalMessageEvent extends Event
   implements Cancellable
 {
   private Player player;
   private ArrayList<RecipientData> recipients;
   private String message;
   private boolean cancel = false;
 
   public ChatLocalMessageEvent(Player player, ArrayList<RecipientData> recipients, String message) {
     super("ChatLocalMessageEvent");
     this.player = player;
     this.recipients = recipients;
     this.message = message;
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
 
   public RecipientData[] getRecipients() {
     return this.recipients.toArray(new RecipientData[0]);
   }
   
   public void setRecipients(ArrayList<RecipientData> recipients){
	   this.recipients = recipients;
   }
   
   public String getMessage(){
	   return this.message;
   }
   public void setMessage(String message){
	   this.message = message;
   }
 }
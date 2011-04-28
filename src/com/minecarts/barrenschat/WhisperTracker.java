 package com.minecarts.barrenschat;
 
 import java.util.HashMap;
 import org.bukkit.entity.Player;
 
 public class WhisperTracker
 {
   private HashMap<String, Whispers> whisperTracker = new HashMap<String, Whispers>();
 
   public void setWhisperSent(Player sender, Player receiver)
   {
     if (this.whisperTracker.containsKey(sender.getName())) {
       ((Whispers)this.whisperTracker.get(sender.getName())).lastTo = receiver.getName();
     } else {
       Whispers whisper = new Whispers();
       whisper.lastTo = receiver.getName();
       this.whisperTracker.put(sender.getName(), whisper);
     }
   }
 
   public void setWhisperReceived(Player sender, Player receiver) {
     if (this.whisperTracker.containsKey(receiver.getName())) {
       ((Whispers)this.whisperTracker.get(receiver.getName())).lastFrom = sender.getName();
     } else {
       Whispers whisper = new Whispers();
       whisper.lastFrom = sender.getName();
       this.whisperTracker.put(receiver.getName(), whisper);
     }
   }
 
   public String getLastWhisperSent(Player sender) {
     if (this.whisperTracker.containsKey(sender.getName())) {
       return ((Whispers)this.whisperTracker.get(sender.getName())).lastTo;
     }
     return null;
   }
 
   public String getLastWhisperRecieved(Player receiver)
   {
     if (this.whisperTracker.containsKey(receiver.getName())) {
       return ((Whispers)this.whisperTracker.get(receiver.getName())).lastFrom;
     }
     return null;
   }
 }
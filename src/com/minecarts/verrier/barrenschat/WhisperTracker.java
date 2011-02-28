 package com.minecarts.verrier.barrenschat;
 
 import java.util.HashMap;
 import org.bukkit.entity.Player;
 
 public class WhisperTracker
 {
   private HashMap<Player, Whispers> whisperTracker = new HashMap<Player, Whispers>();
 
   public void setWhisperSent(Player sender, Player receiver)
   {
     if (this.whisperTracker.containsKey(sender)) {
       ((Whispers)this.whisperTracker.get(sender)).lastTo = receiver;
     } else {
       Whispers whisper = new Whispers();
       whisper.lastTo = receiver;
       this.whisperTracker.put(sender, whisper);
     }
   }
 
   public void setWhisperReceived(Player sender, Player receiver) {
     if (this.whisperTracker.containsKey(receiver)) {
       ((Whispers)this.whisperTracker.get(receiver)).lastFrom = sender;
     } else {
       Whispers whisper = new Whispers();
       whisper.lastFrom = sender;
       this.whisperTracker.put(receiver, whisper);
     }
   }
 
   public Player getLastWhisperSent(Player sender) {
     if (this.whisperTracker.containsKey(sender)) {
       return ((Whispers)this.whisperTracker.get(sender)).lastTo;
     }
     return null;
   }
 
   public Player getLastWhisperRecieved(Player receiver)
   {
     if (this.whisperTracker.containsKey(receiver)) {
       return ((Whispers)this.whisperTracker.get(receiver)).lastFrom;
     }
     return null;
   }
 }

/* Location:           F:\Users\stephen\Desktop\BarrensChat.jar
 * Qualified Name:     com.minecarts.verrier.barrenschat.WhisperTracker
 * JD-Core Version:    0.6.0
 */
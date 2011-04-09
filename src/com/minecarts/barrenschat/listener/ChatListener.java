 package com.minecarts.barrenschat.listener;
 
import com.herocraftonline.squallseed31.heroicdeath.HeroicDeathEvent;
import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.ChatChannel;
import com.minecarts.barrenschat.event.*;
import com.minecarts.barrenschat.listener.PlayerListener.RecipientData;

 import org.bukkit.ChatColor;
 import org.bukkit.entity.Player;
 import org.bukkit.event.CustomEventListener;
 import org.bukkit.event.Event;
 
 
 public class ChatListener extends CustomEventListener
 {
   private BarrensChat plugin;
 
   public ChatListener(BarrensChat instance)
   {
     this.plugin = instance;
   }
 
   private static enum events
   {
     ChatWhisperEvent, 
     ChatChannelMessageEvent, 
     ChatChannelJoinEvent, 
     ChatChannelLeaveEvent,
     ChatChannelAnnounceEvent, 
     ChatLocalMessageEvent,
     IgnoreListAddEvent, 
     IgnoreListRemoveEvent, 
 
     HeroicDeathEvent;
   }
   
   public void onCustomEvent(Event event)
   {
     try {
       events.valueOf(event.getEventName());
     }
     catch (IllegalArgumentException e) {
       return;
     }
 
     switch (events.valueOf(event.getEventName()))
     {
     case ChatWhisperEvent:
       if (((ChatWhisperEvent)event).isCancelled()) break;
       
       Player sender = ((ChatWhisperEvent)event).getSender();
       Player receiver = ((ChatWhisperEvent)event).getReceiver();
       String msg = ((ChatWhisperEvent)event).getMessage();
 
       if (this.plugin.cache.ignoreList.isIgnoring(receiver, sender)) {
         sender.sendMessage(ChatColor.RED + receiver.getName() + " is ignoring you.");
       }
       else
       {
         sender.sendMessage(ChatColor.DARK_AQUA + "> [" + receiver.getName() + "] " + msg);
         receiver.sendMessage(ChatColor.AQUA + "[" + sender.getName() + "] " + msg);
         
         this.plugin.whisperTracker.setWhisperSent(sender, receiver);
         this.plugin.whisperTracker.setWhisperReceived(sender, receiver);
 
         this.plugin.log.info("[Whisper] " + sender.getName() + " -> " + receiver.getName() + ": " + msg);
       }
       break;
     case ChatChannelMessageEvent:
    	 
       ChatChannelMessageEvent ccme = (ChatChannelMessageEvent)event;
       if (ccme.isCancelled()) break;
       
       ccme.getChannel().chat(ccme.getPlayer(), ccme.getMessage());
       this.plugin.log.info("[" + ccme.getChannel().name + "] " + ccme.getPlayer().getName() + ": " + ccme.getMessage());
 
       break;
     case ChatChannelJoinEvent:
       ChatChannelJoinEvent ccje = (ChatChannelJoinEvent)event;
       if (!ccje.isCancelled()) {
         this.plugin.channelHelper.joinChannel(ccje.getPlayer(), ccje.getChannel(), ccje.getRejoin());
         this.plugin.log.info(String.format("[%s]: %s joined the channel", new Object[] { ccje.getChannel().name, ccje.getPlayer().getName() }));
       }
       break;
     case ChatChannelLeaveEvent:
       ChatChannelLeaveEvent ccle = (ChatChannelLeaveEvent)event;
       if (ccle.isCancelled()) break;
       
       ccle.getChannel().leave(ccle.getPlayer(), !(ccle.getReason() == "QUIT")); //Alert only if it's not a quit
 
       if (ccle.getReason() == "COMMAND") {
         this.plugin.dbHelper.removePlayerChannel(ccle.getPlayer(), ccle.getChannel());
       }
 
       this.plugin.log.info(String.format("[%s]: %s left the channel (%s)", new Object[] { ccle.getChannel().name, ccle.getPlayer().getName(), ccle.getReason() }));
 
       break;
     case ChatChannelAnnounceEvent:
       ChatChannelAnnounceEvent ccae = (ChatChannelAnnounceEvent)event;
       if (ccae.isCancelled()) break;
       
       ccae.getChannel().msg(ccae.getMessage());
 
       break;
       
     case ChatLocalMessageEvent:
    	 ChatLocalMessageEvent clme = (ChatLocalMessageEvent)event;
    	 if(clme.isCancelled()) break;
    	 
    	 for(RecipientData rd : clme.getRecipients()){
				if(rd.distance <= 75){
					rd.player.sendMessage(ChatColor.WHITE + clme.getMessage());
				} else if(rd.distance <= 200){
					rd.player.sendMessage(ChatColor.GRAY + clme.getMessage());
				} else {
					//They are out of range
				}
    	 }
    	 break;
     case HeroicDeathEvent:
       HeroicDeathEvent he = (HeroicDeathEvent)event;
       ChatChannel chan = this.plugin.channelHelper.getChannelFromName("PVP");
       chan.msg(he.getDeathCertificate().getMessage());
       break;
     }
   }

 }

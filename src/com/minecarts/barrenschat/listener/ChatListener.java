 package com.minecarts.barrenschat.listener;
 
import com.herocraftonline.squallseed31.heroicdeath.HeroicDeathEvent;
import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.ChatChannel;
import com.minecarts.barrenschat.event.*;
import com.minecarts.barrenschat.helpers.ChannelInfo;
import com.minecarts.barrenschat.listener.PlayerListener.RecipientData;
import com.minecarts.barrenschat.cache.CacheIgnore;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

 public class ChatListener extends CustomEventListener {
   private BarrensChat plugin;
 
   public ChatListener(BarrensChat instance) {
     this.plugin = instance;
   }
 
   private static enum events {
     ChatWhisperEvent, 
     ChatChannelMessageEvent, 
     ChatChannelJoinEvent, 
     ChatChannelLeaveEvent,
     ChatChannelAnnounceEvent, 
     ChatLocalMessageEvent,
     IgnoreListAddEvent, 
     IgnoreListRemoveEvent,
     ChatChannelDefaultChangeEvent,
 
     HeroicDeathEvent;
   }

   public void onCustomEvent(Event event){
     try {
       events.valueOf(event.getEventName());
     } catch (IllegalArgumentException e) {
       return;
     }
 
     switch (events.valueOf(event.getEventName())){
         case ChatWhisperEvent: {
             ChatWhisperEvent e = (ChatWhisperEvent)event;
             if (e.isCancelled()) break;

             Player sender = e.getSender();
             Player receiver = e.getReceiver();
             String msg = e.getMessage();

             sender.sendMessage(ChatColor.DARK_AQUA + "> [" + receiver.getName() + "] " + msg);
             receiver.sendMessage(ChatColor.AQUA + "[" + sender.getName() + "] " + msg);

             this.plugin.whisperTracker.setWhisperSent(sender, receiver);
             this.plugin.whisperTracker.setWhisperReceived(sender, receiver);

             this.plugin.log.info("[Whisper] " + sender.getName() + " -> " + receiver.getName() + ": " + msg);
             break;
         }
         case ChatChannelMessageEvent: {
             ChatChannelMessageEvent e = (ChatChannelMessageEvent)event;
             if (e.isCancelled()) break;

             e.getChannel().chat(e.getPlayer(), e.getMessage());
             this.plugin.log.info("[" + e.getChannel().name + "] " + e.getPlayer().getName() + ": " + e.getMessage());
             break;
         }
         case ChatChannelJoinEvent: {
             ChatChannelJoinEvent e = (ChatChannelJoinEvent)event;
             if (e.isCancelled()) break;
             
             this.plugin.channelHelper.joinChannel(e.getPlayer(), e.getChannel(), e.getRejoining(),e.getAlertSelf(),e.getAlertOthers(),e.getDefault());
             this.plugin.log.info(String.format("[%s]: %s joined the channel", new Object[] { e.getChannel().name, e.getPlayer().getName() }));
             break;
         }
         case ChatChannelLeaveEvent: {
             ChatChannelLeaveEvent e = (ChatChannelLeaveEvent)event;
             if (e.isCancelled()) break;
             e.getChannel().leave(e.getPlayer(), !(e.getReason() == "QUIT")); //Alert only if it's not a quit
             if (e.getReason() == "COMMAND") {
                 this.plugin.dbHelper.removePlayerChannel(e.getPlayer(), e.getChannel()); //They won't rejoin when they reconnect
             }
             this.plugin.log.info(String.format("[%s]: %s left the channel (%s)", new Object[] { e.getChannel().name, e.getPlayer().getName(), e.getReason() }));
             break;
         }
         case ChatChannelAnnounceEvent: {
             ChatChannelAnnounceEvent e = (ChatChannelAnnounceEvent)event;
             if (e.isCancelled()) break;

             e.getChannel().msg(e.getMessage());
             break;
         }
         case ChatLocalMessageEvent: {
             ChatLocalMessageEvent e = (ChatLocalMessageEvent)event;
             if(e.isCancelled()) break;

             for(RecipientData rd : e.getRecipients()){
                 if (CacheIgnore.isIgnoring(rd.player, e.getPlayer())) { continue; }
                 if(rd.distance <= 75){
                     rd.player.sendMessage(ChatColor.WHITE + e.getMessage());
                 } else if(rd.distance <= 200){
                     rd.player.sendMessage(ChatColor.GRAY + e.getMessage());
                 } else {
                     //They are out of range
                 }
             }
             break;
         }
         case ChatChannelDefaultChangeEvent: {
             ChatChannelDefaultChangeEvent e = (ChatChannelDefaultChangeEvent)event;
             Player player = e.getPlayer();
             ChatChannel chan = e.getChannel();

             ChannelInfo defaultChannelInfo = this.plugin.dbHelper.getDefaultChannelInfo(player);
             if ((defaultChannelInfo == null) || (defaultChannelInfo.id != chan.getId())) {
               this.plugin.dbHelper.setDefaultChannel(player, chan);
             }
             break;
         }
         case HeroicDeathEvent: {
           HeroicDeathEvent e = (HeroicDeathEvent)event;
           ChatChannel chan = this.plugin.channelHelper.getChannelFromName("PVP");
           String msg = e.getDeathCertificate().getMessage();
           chan.msg(msg.replaceAll("\u00A7[0-Fa-f]", ""));
           break;
         }
     }//switch
   }//onCustomEvent
 }//class

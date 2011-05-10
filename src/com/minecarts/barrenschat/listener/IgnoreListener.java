package com.minecarts.barrenschat.listener;

import java.text.MessageFormat;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.ChatColor;

import com.minecarts.barrenschat.ChatFormatString;
import com.minecarts.barrenschat.event.ChatWhisperEvent;
import com.minecarts.barrenschat.cache.CacheIgnore;

public class IgnoreListener extends CustomEventListener{
    private static enum events {
        ChatWhisperEvent, 
        IgnoreListAddEvent, 
        IgnoreListRemoveEvent;
    }

    public IgnoreListener(){
    }

    public void onCustomEvent(Event event){
        try { events.valueOf(event.getEventName()); } 
        catch (IllegalArgumentException e) { return; }

        switch (events.valueOf(event.getEventName())){
            case ChatWhisperEvent: {
                ChatWhisperEvent e = (ChatWhisperEvent) event;
                if (CacheIgnore.isIgnoring(e.getReceiver(), e.getSender())) {
                    e.getSender().sendMessage(MessageFormat.format(ChatFormatString.IGNORE_MESSAGE_FAILED, ChatColor.RED, e.getReceiver().getDisplayName()));
                    e.setCancelled(true);
                }
            } //chatwhisperEvent:
        }//switch
    }//onCustomEvent()
}//class

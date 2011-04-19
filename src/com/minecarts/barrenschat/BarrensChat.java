package com.minecarts.barrenschat;


import com.minecarts.barrenschat.helpers.*;
import com.minecarts.barrenschat.listener.*;
import com.minecarts.barrenschat.command.*;
import com.minecarts.barrenschat.cache.*;

import com.minecarts.dbconnector.DBConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.event.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
 

 public class BarrensChat extends JavaPlugin
 {
   private final PlayerListener playerListener = new PlayerListener(this);
   private final ChatListener chatListener = new ChatListener(this);
   private final IgnoreListener ignoreListener = new IgnoreListener();
 
   public HashMap<String, ChatChannel> channelList = new HashMap<String, ChatChannel>();
   public WhisperTracker whisperTracker = new WhisperTracker();
 
   public DBConnector dbc;
   
   public final int MAX_CHANNELS = 10;
   public ChannelHelper channelHelper;
   public DBHelper dbHelper;
      
   public Configuration config; //The plugin config file
   
   
   public final boolean debug = false; //Output more debug messages

   public final Logger log = Logger.getLogger("Minecraft");
   
   public List<String> channelColors;
 
   public void onEnable() {
     PluginManager pm = getServer().getPluginManager();
     pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Monitor, this);
     pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Monitor, this);
     pm.registerEvent(Event.Type.CUSTOM_EVENT, this.chatListener, Event.Priority.Monitor, this);

     pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Highest, this);
     pm.registerEvent(Event.Type.CUSTOM_EVENT, this.ignoreListener, Event.Priority.Low, this); //Different priority custom events
     pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Event.Priority.Low, this);

     //A pointer to the plugin for all the cache classes
         com.minecarts.barrenschat.cache.CacheBase.plugin = this;
     
     //Get a reference to the DB Connector plugin
         this.dbc = (DBConnector) pm.getPlugin("DBConnector");
         
     //Configuration
         this.config = getConfiguration();
     
     //Create our helpers
         this.channelHelper = new ChannelHelper(this);
         this.dbHelper = new DBHelper(this);
    
       //Load our colors
         List<String> defaultColors = new ArrayList<String>();
         defaultColors.add("GOLD");
         defaultColors.add("RED");
         defaultColors.add("GREEN");

         this.channelColors = config.getStringList("channel.colors", defaultColors);

    //Save the configuration file with the defaults
    //    or with what it currently has in it
         saveConfiguration();

    //Register our commands
         getCommand("ch").setExecutor(new CommandCh(this));
         getCommand("whisper").setExecutor(new CommandWhisper(this));
         getCommand("say").setExecutor(new CommandSay(this));
         getCommand("join").setExecutor(new CommandJoin(this));
         getCommand("leave").setExecutor(new CommandLeave(this));
         getCommand("reply").setExecutor(new CommandReply(this));
         getCommand("rewhisper").setExecutor(new CommandRewhisper(this));
         getCommand("ignore").setExecutor(new CommandIgnore(this));
         getCommand("unignore").setExecutor(new CommandUnignore(this));


     PluginDescriptionFile pdf = getDescription();
     this.log.info("[" + pdf.getName() + "] version " + pdf.getVersion() + " loaded.");

     //Rejoin any players currently online into their
     //    default channels
     for (Player p : getServer().getOnlinePlayers()) {
       ArrayList<ChannelInfo> chatChannels = this.dbHelper.getPlayerChannelsInfo(p);
       for (ChannelInfo ci : chatChannels) {
         this.channelHelper.joinChannel(p, ci.name, true,true,true,false);
         
       }
       CacheIgnore.loadListFromDB(p); //Load the ignore list for this player into the cache
     }
   }//onEnable()

   public void onDisable(){}

   public Player[] getChannelMembers(Player requestingPlayer, int channelIndex) {
     ChannelInfo ci = this.dbHelper.getChannelInfoAtIndex(requestingPlayer, Integer.valueOf(channelIndex));
     ChatChannel ch = this.channelHelper.getChannelFromName(ci.name);

     if (ch != null) {
       log.info("Player list has: " + ch.playerList.size() + " members");
       return ch.playerList.toArray(new Player[0]);
     } else {
        log.info("Channel was null");
     }
     return null;
   }


   public String getChannelName(Player player, int index){
       ChannelInfo ci = this.dbHelper.getChannelInfoAtIndex(player, index);
       if(ci != null){ return ci.name; }
       return null;
   }

   private void saveConfiguration(){
       //We can get it from the config because we're
       //    not currently modifying these values in the plugin itself
       //    BUT! If we ever start adding config options, may need to handle this
       //    differently
       this.config.setProperty("db.hostname",this.config.getString("db.hostname", "127.0.0.1"));
       this.config.setProperty("db.port",this.config.getInt("db.port", 3306));
       this.config.setProperty("db.database",this.config.getString("db.database", "database"));
       this.config.setProperty("db.username",this.config.getString("db.username", "username"));
       this.config.setProperty("db.password",this.config.getString("db.password", "password"));

       this.config.setProperty("channel.colors",this.config.getStringList("channel.colors", new ArrayList<String>()));
       
       this.config.save();
   }
 }
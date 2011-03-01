package com.minecarts.verrier.barrenschat;

import com.minecarts.verrier.barrenschat.event.*;
import com.minecarts.verrier.barrenschat.helpers.*;
import com.minecarts.verrier.barrenschat.listener.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.event.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
 

 public class BarrensChat extends JavaPlugin
 {
   private final PlayerListener playerListener = new PlayerListener(this);
   private final ChatListener chatListener = new ChatListener(this);
 
   public HashMap<String, ChatChannel> channelList = new HashMap<String, ChatChannel>();
   public WhisperTracker whisperTracker = new WhisperTracker();
 
   public final int MAX_CHANNELS = 10;
   public ChannelHelper channelHelper;
   public DBHelper dbHelper;
   public Cache cache;
   public Configuration config; //The plugin config file
   
   public final boolean debug = false; //Output more debug messages

   public final Logger log = Logger.getLogger("Minecraft");
   
   public List<String> channelColors;
 
   public void onEnable()
   {
     PluginManager pm = getServer().getPluginManager();
     pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Highest, this);
     pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Event.Priority.Highest, this);
     
     pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Monitor, this);
     pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Monitor, this);
 
     pm.registerEvent(Event.Type.CUSTOM_EVENT, this.chatListener, Event.Priority.Monitor, this);
 
    
     
     //Try to load our configuration
     //	which contains the DB user / password
     	this.config = getConfiguration();
     
     //Create our helpers
	 	 this.channelHelper = new ChannelHelper(this);
	     this.dbHelper = new DBHelper(this);
	     this.cache = new Cache(this);
     	
     //Attempt to connect to our DB
    	if(!this.dbHelper.dbConnect(
    			 this.config.getString("db.hostname", "127.0.0.1"),
	    		 this.config.getString("db.port", "3306"),
	    		 this.config.getString("db.database", "database"),
	    		 this.config.getString("db.username", "username"),
	    		 this.config.getString("db.password", "password")
    			)){
     		
    		log.severe("Unable to connect to database");
    		//getPluginLoader().disablePlugin(this); //Doesn't work???
    	}
    
   	//Load our colors
    	List<String> defaultColors = new ArrayList<String>();
    	defaultColors.add("GOLD");
    	defaultColors.add("RED");
    	defaultColors.add("GREEN");
    	
    	this.channelColors = config.getStringList("channel.colors", defaultColors);
    	
    //Save the configuration file with the defaults
    //	or with what it currently has in it
    saveConfiguration();
         	
     
     PluginDescriptionFile pdf = getDescription();
     this.log.info("[" + pdf.getName() + "] version " + pdf.getVersion() + " loaded.");
 
     for (Player p : getServer().getOnlinePlayers()) {
       ArrayList<ChannelInfo> chatChannels = this.dbHelper.getPlayerChannelsInfo(p);
       for (ChannelInfo ci : chatChannels) {
         this.channelHelper.joinChannel(p, ci.name, true);
       }
 
       this.cache.ignoreList.set(p, this.dbHelper.getIgnoreList(p));
     }
     
     //
     
   }
 
   public void onDisable()
   {
	   //Nothing needs to be done? 
	   //	Maybe close DB connections?
   }
 
   public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args)
   {
     String argString = StringHelper.join(args, 0);
 
     if (!(sender instanceof Player))
     {
       if (cmdLabel.toLowerCase().equals("say"))
       {
         this.log.info("Server: " + argString);
         for (Player p : getServer().getOnlinePlayers()) {
           p.sendMessage(ChatColor.YELLOW + "[Server]: " + argString);
         }
         return true;
       }
 
       return false;
     }
 
     Player player = (Player)sender;
     cmdLabel = cmdLabel.toLowerCase();
     try {
       Commands cmdValue = Commands.valueOf(cmdLabel.toLowerCase());
       ChatChannelLeaveEvent ccle;
       ChatColor color;
       switch (cmdValue) {
	       case whisper:
	       case w:
	       case msg:
	         List<Player> playermatches = getServer().matchPlayer(args[0]);
	         if (playermatches.size() > 0) {
	           argString = StringHelper.join(args, 1);
	           Player whisperee = (Player)playermatches.get(0);
	           ChatWhisperEvent cwe = new ChatWhisperEvent(player, whisperee, argString);
	           getServer().getPluginManager().callEvent(cwe);
	         } else {
	           player.sendMessage("Could not find anyone online by that name.");
	         }
	         break;
	       case reply:
	       case r:
	         Player reply = this.whisperTracker.getLastWhisperRecieved(player);
	         if (!reply.equals(null))
	         {
	           ChatWhisperEvent cwe = new ChatWhisperEvent(player, reply, argString);
	           getServer().getPluginManager().callEvent(cwe);
	         } else {
	           player.sendMessage("You have not recieved any whispers.");
	         }
	 
	         break;
	       case rewhisper:
	       case rw:
	         Player rewhisper = this.whisperTracker.getLastWhisperSent(player);
	 
	         if (rewhisper != null) {
	           ChatWhisperEvent cwe = new ChatWhisperEvent(player, rewhisper, argString);
	           getServer().getPluginManager().callEvent(cwe);
	         } else {
	           player.sendMessage("You have not whispered anyone.");
	         }
	         break;
	       case join:
	         ChatChannel chan = this.channelHelper.getChannelFromName(argString);
	         if (chan != null) {
	           ChatChannelJoinEvent ccje = new ChatChannelJoinEvent(player, chan);
	           getServer().getPluginManager().callEvent(ccje);
	         } else {
	           player.sendMessage("Internal error: Unable to create and join channel");
	         }
	         break;
	       case leave:
	         try
	         {
	           int chatIndex = Integer.parseInt(args[0]);
	           ChannelInfo ci = this.dbHelper.getChannelInfoAtIndex(player, Integer.valueOf(chatIndex));
	           chan = this.channelHelper.getChannelFromName(ci.name);
	           if (chan != null) {
	             ccle = new ChatChannelLeaveEvent(player, chan, "LEAVE");
	             getServer().getPluginManager().callEvent(ccle);
	           } else {
	             this.log.info("Error: Channel with name" + ci.name + " could not be fetched");
	             player.sendMessage("Internal Error: Unable to retrieve the channel by the name " + ci.name + ". Does it exist?");
	           }
	         }
	         catch (Exception e) {
	           player.sendMessage("You must use the channel number to leave the channel.");
	         }
	         break;
	       case say:
	         if (this.dbHelper.getDefaultChannelInfo(player) != null)
	         {
	           this.dbHelper.clearDefaultChannel(player);
	           player.sendMessage(ChatColor.DARK_GRAY + "Default chat now sending to nearby players.");
	         }
	         if ((args.length <= 0) || 
	           (args[0].length() <= 0)) break;
	         PlayerChatEvent sev = new PlayerChatEvent(Event.Type.PLAYER_CHAT, player, argString);
	         getServer().getPluginManager().callEvent(sev);
	 
	         break;
	       case ch:
	         if (args[0].equalsIgnoreCase("list"))
	         {
	           player.sendMessage("Channels you are currently in: ");
	 
	           ArrayList<ChannelInfo> channelInfoList = this.dbHelper.getPlayerChannelsInfo(player);
	 
	           if (channelInfoList != null)
	             for (ChannelInfo channelInfo : channelInfoList)
	             {
	               ChatChannel c = this.channelHelper.getChannelFromName(channelInfo.name);
	               color = ChatColor.valueOf(this.channelColors.get((channelInfo.index % this.channelColors.size())));
	 
	               String defaultFlag = "";
	               if (channelInfo.isDefault.booleanValue()) {
	                 defaultFlag = " (Default)";
	               }
	               player.sendMessage(color + " [" + channelInfo.index + "] : " + c.name + defaultFlag);
	             }
	           else
	             player.sendMessage(ChatColor.GRAY + "You are not in any channels.");
	         }
	         else {
	           player.sendMessage("Unknown ch command.");
	         }
	         break;
	       case ignore:
	         if (args[0].equalsIgnoreCase("list")) {
	           player.sendMessage("TODO: Ignore list display.");
	         } else {
	           List<Player> ignoreMatches = getServer().matchPlayer(args[0]);
	           if (ignoreMatches.size() > 0) {
	             for (Player ignore : ignoreMatches)
	               if (ignore == player) {
	                 player.sendMessage("You cannot ignore yourself.");
	               } else if (ignore.isOp()) {
	                 player.sendMessage("You cannot ignore admins.");
	                 this.log.info(player.getName() + " tried to ignore admin " + ignore.getName());
	               } else {
	                 player.sendMessage("You are now ignoring " + ignore.getName() + ".");
	                 this.dbHelper.addIgnore(player, ignore);
	               }
	           }
	           else {
	             player.sendMessage("There are no players online by that name to ignore.");
	           }
	         }
	         break;
	       case unignore:
	         List<String> ignoreList = this.dbHelper.getIgnoreList(player);
	         List<Player> ignoreMatches = getServer().matchPlayer(args[0]);
	         boolean didUnignore = false;
	         if (ignoreMatches.size() > 0) {
	           for (Player unignore : ignoreMatches) {
	             if (ignoreList.contains(unignore.toString())) {
	               player.sendMessage("You have unignored " + unignore.getName() + ".");
	               this.dbHelper.removeIgnore(player, unignore);
	               didUnignore = true;
	             }
	           }
	 
	           if (didUnignore) break;
	           player.sendMessage("You are not ignoring anyone by that name.");
	         } else {
	           player.sendMessage("Could not find that player to unignore. They must be online.");
	         }
	         break;
	       default:
	         return false;
       } //switch
       return true;
     } catch (Exception localException2) {
    	 //It wasn't one of our commands
    	 log.info("Registered command is unhandled: " + cmdLabel);
     }
     return false;
   }
 
   public Player[] getChannelMembers(Player requestingPlayer, int channelIndex)
   {
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
	   if(ci != null){
		   return ci.name;
	   }
	   return null;
   }
   
   private void saveConfiguration(){
	   //We can get it from the config because we're
	   //	not currently modifying these values in the plugin itself
	   //	BUT! If we ever start adding config options, may need to handle this
	   //	differently
	   	this.config.setProperty("db.hostname",this.config.getString("db.hostname", "127.0.0.1"));
	   	this.config.setProperty("db.port",this.config.getInt("db.port", 3306));
	   	this.config.setProperty("db.database",this.config.getString("db.database", "database"));
	   	this.config.setProperty("db.username",this.config.getString("db.username", "username"));
	   	this.config.setProperty("db.password",this.config.getString("db.password", "password"));

	   	this.config.setProperty("channel.colors",this.config.getStringList("channel.colors", new ArrayList<String>()));
	   	
	   	this.config.save();
   }
 
   
   private static enum Commands
   {
     whisper, w, msg, 
     join, leave, 
     say, 
     reply, r, 
     rewhisper, rw, 
     ch, 
     ignore, unignore;
   }
 }
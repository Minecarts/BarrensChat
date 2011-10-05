package com.minecarts.barrenschat;


import com.minecarts.barrenschat.command.CommandHandler;
import com.minecarts.barrenschat.listener.*;

import com.minecarts.barrenschat.websocket.BarrensHelper;
import com.minecarts.barrenschat.websocket.BarrensWebSocket;
import com.minecarts.dbconnector.DBConnector;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.json.JSONException;
import org.json.JSONObject;

import org.bukkit.Location;


public class BarrensChat extends JavaPlugin {
   private final PlayerListener playerListener = new PlayerListener(this);
   public final BarrensHelper barrensHelper = new BarrensHelper(this);

   public DBConnector dbc;
   private PluginDescriptionFile pdf;

   public Configuration config; //The plugin config file

   public final Logger logger = Logger.getLogger("com.minecarts.barrenschat");
 
   public void onEnable() {
     PluginManager pm = getServer().getPluginManager();
     pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Monitor, this);
     pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Monitor, this);
     pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.High, this);
     pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Event.Priority.Low, this);

     //Get a reference to the DB Connector plugin
         this.dbc = (DBConnector) pm.getPlugin("DBConnector");
         this.pdf = getDescription();
       
     //Configuration
         this.config = getConfiguration();
       
    //Register our commands
         getCommand("ch").setExecutor(new CommandHandler(this));
         getCommand("whisper").setExecutor(new CommandHandler(this));
         getCommand("say").setExecutor(new CommandHandler(this));
         getCommand("join").setExecutor(new CommandHandler(this));
         getCommand("leave").setExecutor(new CommandHandler(this));
         getCommand("reply").setExecutor(new CommandHandler(this));
         getCommand("rewhisper").setExecutor(new CommandHandler(this));
         getCommand("ignore").setExecutor(new CommandHandler(this));
         getCommand("unignore").setExecutor(new CommandHandler(this));


     PluginDescriptionFile pdf = getDescription();
     this.logger.info("[" + pdf.getName() + "] version " + pdf.getVersion() + " loaded.");

        for(Player p : Bukkit.getServer().getOnlinePlayers()){
            barrensHelper.create(p);
        }
   }//onEnable()

   public void onDisable(){
       //Close any existing websockets
       for(Player p : Bukkit.getServer().getOnlinePlayers()){
           barrensHelper.clear(p);
       }
   }







    public void log(String message) {
        log(Level.INFO, message);
    }
    public void log(Level level, String message) {
        logger.log(level, MessageFormat.format("{0}> {1}", pdf.getName(), message));
    }

    public void logf(String message, Object... args) {
        log(MessageFormat.format(message, args));
    }
    public void logf(Level level, String message, Object... args) {
        log(level, MessageFormat.format(message, args));
    }

    private static DecimalFormat locationFormat = new DecimalFormat("#.###");
    public static JSONObject getJSONLocation(Location loc) throws JSONException {
         return (new JSONObject()
                 .put("world",loc.getWorld().getName())
                 .put("server-id", Bukkit.getServerId())
                 .put("server-unique", md5(Bukkit.getServerId() + Bukkit.getServer().getPort() + Bukkit.getServer().getIp() + Bukkit.getServerName()))
                 .put("x", locationFormat.format(loc.getX()))
                 .put("y",locationFormat.format(loc.getY()))
                 .put("z", locationFormat.format(loc.getZ()))
                 .put("pitch",locationFormat.format(loc.getPitch()))
                 .put("yaw",locationFormat.format(loc.getYaw()))
         );
     }

     public static String md5(String input){
        String res = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(input.getBytes());
            byte[] md5 = algorithm.digest();
            String tmp = "";
            for (int i = 0; i < md5.length; i++) {
                tmp = (Integer.toHexString(0xFF & md5[i]));
                if (tmp.length() == 1) {
                    res += "0" + tmp;
                } else {
                    res += tmp;
                }
            }
        } catch (NoSuchAlgorithmException ex) {}
        return res;
    }
 }
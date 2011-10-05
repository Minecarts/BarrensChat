package com.minecarts.barrenschat;


import com.minecarts.barrenschat.command.CommandHandler;
import com.minecarts.barrenschat.listener.*;

import com.minecarts.barrenschat.websocket.BarrensSocketFactory;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.minecarts.barrenschat.websocket.ServerSocket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.json.JSONException;
import org.json.JSONObject;

import org.bukkit.Location;

public class BarrensChat extends JavaPlugin {
    private final Logger logger = Logger.getLogger("com.minecarts.barrenschat");
    private PluginDescriptionFile pdf;
    private final PlayerListener playerListener = new PlayerListener(this);
    public Configuration config;

    //WebSockets
        public final BarrensSocketFactory BarrensSocketFactory = new BarrensSocketFactory(this);
        public final ServerSocket serverSocket = new ServerSocket(this);

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Event.Priority.Low, this);

        //Configuration
            this.config = getConfiguration();
            this.pdf    = getDescription();

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
            getCommand("channel").setExecutor(new CommandHandler(this));
            getCommand("global").setExecutor(new CommandHandler(this));

        //Create sockets for any existing players on the server (/reload support)
            for(Player p : Bukkit.getServer().getOnlinePlayers()){
                BarrensSocketFactory.create(p);
            }

        //Activate our recurring player state updater
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(this,new Runnable() {
                public void run() {
                    try {
                        serverSocket.updatePlayers();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    } //TODO: Display some output when unable to update players
                }
            },20*5,20*30);

        this.log("v" + pdf.getVersion() + " loaded");
   }//onEnable()

    public void onDisable(){
        for(Player p : Bukkit.getServer().getOnlinePlayers()){
            BarrensSocketFactory.remove(p);
        }
        serverSocket.disconnect();
    }



    //Logging functions
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

    //Bukkit Location to JSON formatted location
        private static DecimalFormat locationFormat = new DecimalFormat("#.###");
        public static JSONObject getJSONLocation(Location loc) throws JSONException {
             return (new JSONObject()
                     .put("world", loc.getWorld().getName())
                     .put("serverId", Bukkit.getServerId())
                     .put("serverUnique", md5(Bukkit.getServerId() + Bukkit.getServer().getPort() + Bukkit.getServer().getIp() + Bukkit.getServerName() + getMachineId()))
                     .put("x", locationFormat.format(loc.getX()))
                     .put("y", locationFormat.format(loc.getY()))
                     .put("z", locationFormat.format(loc.getZ()))
                     .put("pitch", locationFormat.format(loc.getPitch()))
                     .put("yaw", locationFormat.format(loc.getYaw()))
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

    //Get a unique server ID based upon mac addresses, fallback is bukkit config values
    private static String getMachineId(){
       String res = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();

            Enumeration<NetworkInterface> interfaces;
                try{
                     interfaces = java.net.NetworkInterface.getNetworkInterfaces();
                } catch (SocketException e) {
                    return "SomeOtherUniqueIdentifierThatWontFail?";
                }
                algorithm.update(interfaces.nextElement().getHardwareAddress());
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
                return res;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
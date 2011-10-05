package com.minecarts.barrenschat.websocket;

import com.minecarts.barrenschat.BarrensChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: stephen
 * Date: 10/4/11
 * Time: 8:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class BarrensHelper {
    private HashMap<Player,BarrensWebSocket> socketMap = new HashMap<Player,BarrensWebSocket>();
    private BarrensChat plugin;

    public BarrensHelper(BarrensChat plugin){
        this.plugin = plugin;
    }
    public void set(Player player, BarrensWebSocket socket){
       this.socketMap.put(player,socket);
   }
   public BarrensWebSocket get(Player player){
        if(socketMap.containsKey(player)){
            return this.socketMap.get(player);
        } else{
            return null;
        }
   }
   public void clear(Player player){
       if(this.socketMap.containsKey(player)){
           this.socketMap.get(player).setDisconnecting();
           this.socketMap.get(player).closeSocket();
           this.socketMap.remove(player);
       }
   }
   public boolean contains(Player player){
       return this.socketMap.containsKey(player);
   }
   public void reconnect(Player p){
       if(this.socketMap.containsKey(p)){
           this.socketMap.get(p).reconnect();
       }
   }
   public void create(Player p){
       //Async try to create a socket for this player
       final Player player = p;
        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin,new Runnable() {
            public void run() {
                BarrensWebSocket socket = new BarrensWebSocket(player, plugin);
                if(socket != null && socket.isConnected()){
                    plugin.barrensHelper.set(player, socket);
                    player.sendMessage(ChatColor.DARK_GRAY + "DEBUG: Connection established to chat server.");
                } else {
                    player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.GRAY + "Unable to connect to chat server. Trying again in 10 seconds.");
                    Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin,new Runnable() {
                        public void run() {
                            plugin.barrensHelper.create(player);
                        }
                    },20*10);
                }
            }
        });
   }

}

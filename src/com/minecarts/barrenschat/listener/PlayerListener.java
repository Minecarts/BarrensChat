package com.minecarts.barrenschat.listener;


import com.minecarts.barrenschat.websocket.BarrensWebSocket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import com.minecarts.barrenschat.BarrensChat;

public class PlayerListener extends org.bukkit.event.player.PlayerListener
{
    private final BarrensChat plugin;
    public PlayerListener(BarrensChat instance) {
        plugin = instance;
    }

    public void onPlayerJoin(PlayerJoinEvent event){
        final Player player = event.getPlayer();
        plugin.barrensHelper.create(player);
    }
    
    public void onPlayerQuit(PlayerQuitEvent event){
        plugin.barrensHelper.clear(event.getPlayer());
    }

    public void onPlayerKick(PlayerKickEvent event){
        //TODO: Is this needed?
        plugin.barrensHelper.clear(event.getPlayer());
    }
    
    @Override
    public void onPlayerChat(PlayerChatEvent event){
        final Player player = event.getPlayer();
        if(plugin.barrensHelper.contains(player)){
            plugin.barrensHelper.get(player).sendMessage(event.getMessage());
        } else {
            player.sendMessage("Sorry. Socket isn't ready yet!");
        }
        event.setCancelled(true); //We could only cancel if the socket is setup.. but could be exploitable?
    }
}

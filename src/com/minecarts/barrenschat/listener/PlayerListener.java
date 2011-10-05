package com.minecarts.barrenschat.listener;


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
        plugin.BarrensSocketFactory.create(event.getPlayer());
        try {
            plugin.serverSocket.playerJoin(event.getPlayer());
        } catch (Exception e) {}
    }
    
    public void onPlayerQuit(PlayerQuitEvent event){
        plugin.BarrensSocketFactory.remove(event.getPlayer());
        try {
            plugin.serverSocket.playerQuit(event.getPlayer());
        } catch (Exception e) {}
    }

    public void onPlayerKick(PlayerKickEvent event){
        //TODO: Is this needed?
        plugin.BarrensSocketFactory.remove(event.getPlayer());
    }
    
    @Override
    public void onPlayerChat(PlayerChatEvent event){
        final Player player = event.getPlayer();
        if(plugin.BarrensSocketFactory.contains(player)){
            plugin.BarrensSocketFactory.get(player).sendMessage(event.getMessage());
        } else {
            player.sendMessage("Sorry. Socket isn't ready yet!");
        }
        event.setCancelled(true); //We could only cancel if the socket is setup.. but could be exploitable?
    }
}

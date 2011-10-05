package com.minecarts.barrenschat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.Server;

import com.minecarts.barrenschat.BarrensChat;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
    protected final BarrensChat plugin;
    protected final Server server;

    public CommandHandler(BarrensChat plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        //Forward this command to the WebSocket server
        if(sender instanceof Player){
            plugin.BarrensSocketFactory.get((Player) sender).sendCommand(sender.getName(), command.getName(), label, args, ((Player) sender).getLocation());
        } else {
            System.out.println("Not yet supported! Need a console websocket!");
        }
        return true;
    }
}

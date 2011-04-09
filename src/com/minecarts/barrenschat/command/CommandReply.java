package com.minecarts.barrenschat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.event.ChatWhisperEvent;
import com.minecarts.barrenschat.helpers.StringHelper;


public class CommandReply extends CommandHandler{
    
    public CommandReply (BarrensChat plugin){
        super(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	String argString = StringHelper.join(args, 0);
		if(sender instanceof Player){
			Player player = (Player) sender;
			Player reply = plugin.whisperTracker.getLastWhisperRecieved(player);
			if (!reply.equals(null)) {
				ChatWhisperEvent cwe = new ChatWhisperEvent(player, reply, argString);
				server.getPluginManager().callEvent(cwe);
	        } else {
	        	player.sendMessage("You have not recieved any whispers.");
	        }
	        return true;
		}
		return false;
    }
}

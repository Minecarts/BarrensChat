package com.minecarts.barrenschat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.event.ChatWhisperEvent;
import com.minecarts.barrenschat.helpers.StringHelper;


public class CommandRewhisper extends CommandHandler{
    
    public CommandRewhisper (BarrensChat plugin){
        super(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	String argString = StringHelper.join(args, 0);
		if(sender instanceof Player){
			Player player = (Player) sender;
			Player rewhisper = plugin.whisperTracker.getLastWhisperSent(player);
			if (rewhisper != null) {
				ChatWhisperEvent cwe = new ChatWhisperEvent(player, rewhisper, argString);	
				server.getPluginManager().callEvent(cwe);
			} else {
				player.sendMessage("You have not whispered anyone.");
			}
			return true;
		}
		return false;
    }
}

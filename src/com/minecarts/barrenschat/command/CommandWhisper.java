package com.minecarts.barrenschat.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.event.ChatWhisperEvent;
import com.minecarts.barrenschat.helpers.StringHelper;


public class CommandWhisper extends CommandHandler{
    
    public CommandWhisper (BarrensChat plugin){
        super(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	String argString = StringHelper.join(args, 1);
    	if(argString.length() == 0){ return false; }
    	List<Player> playermatches = server.matchPlayer(args[0]);
    	if(sender instanceof Player){
			Player player = (Player) sender;
			int numMatches = playermatches.size();
			if(numMatches > 1){
	            sender.sendMessage("There were " + numMatches + " players matching \""+args[0]+"\". Please be more specific.");
	            return true;
	        } else if (numMatches == 1) {
	           Player whisperee = (Player)playermatches.get(0);
	           ChatWhisperEvent cwe = new ChatWhisperEvent(player, whisperee, argString);
	           server.getPluginManager().callEvent(cwe);
	        } else {
	           player.sendMessage("Could not find anyone online by that name.");
	        }
	        return true;
		}
		return false;
    }
}

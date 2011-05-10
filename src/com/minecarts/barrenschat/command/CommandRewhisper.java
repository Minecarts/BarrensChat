package com.minecarts.barrenschat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.ChatFormatString;
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
			String name = plugin.whisperTracker.getLastWhisperSent(player);
			if (name != null) {
			    List<Player> matches =  plugin.getServer().matchPlayer(name);
			    if(matches.size() > 0){
			        Player rewhisper = plugin.getServer().matchPlayer(name).get(0);
			        ChatWhisperEvent cwe = new ChatWhisperEvent(player, rewhisper, argString);	
			        server.getPluginManager().callEvent(cwe);
			    } else {
			        player.sendMessage(MessageFormat.format(ChatFormatString.PLAYER_NOT_ONLINE, org.bukkit.ChatColor.WHITE,name));
			    }
			} else {
				player.sendMessage("You have not whispered anyone.");
			}
			return true;
		}
		return false;
    }
}

package com.minecarts.barrenschat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.ChatChannel;
import com.minecarts.barrenschat.event.ChatChannelJoinEvent;
import com.minecarts.barrenschat.helpers.StringHelper;


public class CommandIgnore extends CommandHandler{
    
    public CommandIgnore (BarrensChat plugin){
        super(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			Player player = (Player) sender;
			if (args[0].equalsIgnoreCase("list")) {
			    player.sendMessage("TODO: Ignore list display.");
			} else {
			    List<Player> ignoreMatches = server.matchPlayer(args[0]);
			    if (ignoreMatches.size() > 0) {
			        for (Player ignore : ignoreMatches){
			            if (ignore == player) {
			                player.sendMessage("You cannot ignore yourself.");
			            } else if (ignore.isOp()) {
			                player.sendMessage("You cannot ignore admins.");
			                plugin.log.info(player.getName() + " tried to ignore admin " + ignore.getName());
			            } else {
			                player.sendMessage("You are now ignoring " + ignore.getName() + ".");
			                plugin.dbHelper.addIgnore(player, ignore);
			            }
			        }
			    } else {
			        player.sendMessage("There are no players online by that name to ignore.");
			    }
			}
			return true;
		}
		return false;
    }
}

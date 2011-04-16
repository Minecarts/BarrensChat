package com.minecarts.barrenschat.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.cache.CacheIgnore;


public class CommandIgnore extends CommandHandler{
    
    public CommandIgnore (BarrensChat plugin){
        super(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			Player player = (Player) sender;
			if (args.length == 0 || args[0].equalsIgnoreCase("list")){
			    java.util.List<String> ignoreList = CacheIgnore.getIgnoreList(player);
			    if(ignoreList != null && ignoreList.size() > 0){
			        player.sendMessage("Players ignored: ");
			        for(String p : ignoreList){
			            player.sendMessage(ChatColor.GRAY + " - " + p);
			        }
			    } else {
			        player.sendMessage("You are not ignoring anyone. Type /ignore <player> to ignore them.");
			    }
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
			                if(CacheIgnore.isIgnoring(player, ignore)){
			                    player.sendMessage("You have unignored " + ignore.getName() + ".");
			                    plugin.dbHelper.removeIgnore(player, ignore);
			                } else {
                                plugin.dbHelper.addIgnore(player, ignore);
                                player.sendMessage("You are now ignoring " + ignore.getName() + ".");
			                }
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

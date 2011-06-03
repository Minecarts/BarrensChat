package com.minecarts.barrenschat.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.ChatChannel;
import com.minecarts.barrenschat.event.ChatChannelJoinEvent;
import com.minecarts.barrenschat.helpers.StringHelper;


public class CommandJoin extends CommandHandler{
    
    public CommandJoin (BarrensChat plugin){
        super(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	String argString = StringHelper.join(args, 0);
    	if(args.length == 0){
    	    return false;
    	}
		if(sender instanceof Player){
			Player player = (Player) sender;
			
			//Check to see if they're joining a numbered channel
	        if(argString.length() == 1){
	            //Try to parse int
	            try{
	                Integer.parseInt(argString);
	                player.sendMessage("To join a channel, use the name of the channel, not the index. Example: " + ChatColor.YELLOW + "/join Global" +ChatColor.WHITE + "");//If we got here, it's an int
	                return true;
	            } catch (Exception e){
	                //it's not an int, carry on!
	            }
	        }
			
			ChatChannel chan = plugin.channelHelper.getChannelFromName(argString);
			if (chan != null) {
			    Boolean alertOthers = !(chan.getId().equals("pvp") || chan.getId().equals("global"));
			    Integer index = plugin.dbHelper.getNextChannelIndex(player);
			    ChatChannelJoinEvent ccje = new ChatChannelJoinEvent(player, chan,index, "COMMAND",false,true,alertOthers,false);
				server.getPluginManager().callEvent(ccje);
			} else {
				player.sendMessage("Internal error: Unable to create and join channel");
			}
			return true;
		}
		return false;
    }
}

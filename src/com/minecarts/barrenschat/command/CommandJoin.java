package com.minecarts.barrenschat.command;

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
			ChatChannel chan = plugin.channelHelper.getChannelFromName(argString);
			if (chan != null) {
			    Boolean alertOthers = !(chan.getId().equals("pvp") || chan.getId().equals("global"));
			    ChatChannelJoinEvent ccje = new ChatChannelJoinEvent(player, chan, "COMMAND",false,true,alertOthers,false);
				server.getPluginManager().callEvent(ccje);
			} else {
				player.sendMessage("Internal error: Unable to create and join channel");
			}
			return true;
		}
		return false;
    }
}

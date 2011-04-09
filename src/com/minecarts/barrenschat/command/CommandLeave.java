package com.minecarts.barrenschat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.ChatChannel;
import com.minecarts.barrenschat.helpers.ChannelInfo;

import com.minecarts.barrenschat.event.ChatChannelLeaveEvent;

public class CommandLeave extends CommandHandler{
    
    public CommandLeave (BarrensChat plugin){
        super(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			Player player = (Player) sender;
			try {
	           int chatIndex = Integer.parseInt(args[0]);
	           ChannelInfo ci = plugin.dbHelper.getChannelInfoAtIndex(player, Integer.valueOf(chatIndex));
	           ChatChannel chan = plugin.channelHelper.getChannelFromName(ci.name);
	           if (chan != null) {
	             ChatChannelLeaveEvent ccle = new ChatChannelLeaveEvent(player, chan, "COMMAND");
	             server.getPluginManager().callEvent(ccle);
	           } else {
	             plugin.log.severe("Error: Channel with name" + ci.name + " could not be fetched");
	             player.sendMessage("Internal Error: Unable to retrieve the channel by the name " + ci.name + ". Does it exist?");
	           }
	         } catch (Exception e) {
	           player.sendMessage("You must use the channel number to leave the channel.");
	         }
			return true;
		}
		return false;
    }
}

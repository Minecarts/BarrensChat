package com.minecarts.barrenschat.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.ChatChannel;
import com.minecarts.barrenschat.helpers.ChannelInfo;


public class CommandCh extends CommandHandler{
    
    public CommandCh (BarrensChat plugin){
        super(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length == 0){//Display help
                player.chat("/help");
                return true;
            }
            if(args[0].equalsIgnoreCase("list")){
                player.sendMessage("Channels you are currently in: "); 
                ArrayList<ChannelInfo> channelInfoList = plugin.dbHelper.getPlayerChannelsInfo(player);
                if(channelInfoList != null){
                    for (ChannelInfo channelInfo : channelInfoList) {
                        ChatChannel c = plugin.channelHelper.getChannelFromName(channelInfo.name);
                        ChatColor color = ChatColor.valueOf(plugin.channelColors.get((channelInfo.index % plugin.channelColors.size())));
                        String defaultFlag = "";
                        if (channelInfo.isDefault.booleanValue()) defaultFlag = " (Default)";
                        player.sendMessage(color + " [" + channelInfo.index + "] : " + c.getName() + defaultFlag);
                    }
                } else {
                    player.sendMessage(ChatColor.GRAY + "You are not in any channels.");
                }
             } else {
               player.sendMessage("Unknown ch command. Type \"/ch\" for a list.");
             }
            return true;
        }
        return false;
    }
}

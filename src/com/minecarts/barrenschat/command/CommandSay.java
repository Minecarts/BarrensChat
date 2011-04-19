package com.minecarts.barrenschat.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.helpers.StringHelper;
import com.minecarts.barrenschat.event.ChatDefaultChangeEvent;

public class CommandSay extends CommandHandler{
    
    public CommandSay (BarrensChat plugin){
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String argString = StringHelper.join(args, 0);
        if(sender instanceof Player){
            Player player = (Player) sender;
            if (plugin.dbHelper.getDefaultChannelInfo(player) != null) {
                if(argString.length() > 0){
                    plugin.getServer().getPluginManager().callEvent(new ChatDefaultChangeEvent(player,null,-1,true));
                } else {
                    plugin.getServer().getPluginManager().callEvent(new ChatDefaultChangeEvent(player,null,-1,false));
                }
            }
            if ((args.length <= 0) || (args[0].length() <= 0)) return true;
            
            PlayerChatEvent sev = new PlayerChatEvent(player, argString);
            server.getPluginManager().callEvent(sev);
            return true;
        } else { //console /say
             plugin.log.info("Server: " + argString);
             for (Player p : server.getOnlinePlayers()) {
                 p.sendMessage(ChatColor.YELLOW + "[Server]: " + argString);
             }
             return true;
        }
    }
}

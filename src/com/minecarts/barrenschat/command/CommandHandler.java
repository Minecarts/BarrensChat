package com.minecarts.barrenschat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.Server;

import com.minecarts.barrenschat.BarrensChat;

public abstract class CommandHandler implements CommandExecutor {
    protected final BarrensChat plugin;
    protected final Server server;

    public CommandHandler(BarrensChat plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }
    
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}

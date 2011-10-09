package com.minecarts.barrenschat.websocket;

import com.clwillingham.socket.io.MessageCallback;
import com.minecarts.barrenschat.BarrensChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.clwillingham.socket.io.IOSocket;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class BarrensSocket {

    private IOSocket socket;
    private Player player;
    private BarrensChat plugin;
    private boolean success = false; //Tracks if we were successful connecting to our socket server or not
    private boolean shouldDisconnect = false; //Boolean to track expected disconnections

    public BarrensSocket(Player player, BarrensChat plugin) {
        this.socket = new IOSocket(plugin.config.getString("socketUrl","http://192.168.1.21:801"), new myCallback(player));
        try {
            this.connect();
        } catch (IOException e) {
            plugin.log("BarrensSocket (" + player.getName() + "): Error creating socket!");
            return;
        }
        if (socket == null) {
            plugin.log("BarrensSocket (" + player.getName() + "): Error creating socket! (null socket)");
            return;
        }
        plugin.log("BarrensSocket (" + player.getName() + "): Socket created successfully!");
        this.player = player; //Tie a socket to a player
        this.plugin = plugin;
        this.success = true;
    }

    public boolean isConnected() {
        return success;
    }

    public void setDisconnecting() {
        shouldDisconnect = true;
    }

    private void connect() throws IOException { //Internally connect to the websocket ANNDD identify what player is using this socket
        socket.connect();
    }

    public void reconnect() {
        try {
            this.connect();
        } catch (IOException e) {
            player.sendMessage(ChatColor.DARK_GRAY + "Error: Unable to connect to chat server. Trying again... ");
            //Attempt to reconnect.. but later...
            Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    plugin.BarrensSocketFactory.reconnect(player);
                }
            }, 20 * 10);
            //e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (!socket.isConnected()) {
            player.sendMessage(ChatColor.GRAY + "Restablishing connection to chat server...");
            try {
                this.connect();
                player.sendMessage(ChatColor.GREEN + "Success: " + ChatColor.WHITE + "Chat connection re-established.");
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "FAILED: " + ChatColor.WHITE + "Unable to re-establish connection. You may continue playing but will be unable to chat.");
                return;
            }
        }
        try {
            this.socket.emit("chatMessage",
                    new JSONObject()
                            .put("player", this.player.getName())
                            .put("message", message)
                            .put("location", BarrensChat.getJSONLocation(player.getLocation()))
            );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String sender, String command, String label, String[] args, Location location) {
        try {
            this.socket.emit("chatCommand",
                    new JSONObject()
                            .put("sender", sender)
                            .put("command", command)
                            .put("args", args)
                            .put("label", label)
                            .put("location", BarrensChat.getJSONLocation(location))

            );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        this.shouldDisconnect = true;
        this.socket.disconnect();
    }

    //All the events that our chat namespace supports
    private enum Events {
        chatMessage,
        chatCommand
    }

    private class myCallback implements MessageCallback {
        private Player player;

        public myCallback(Player player) {
            this.player = player;
        }

        public void onConnect() {
            plugin.log("BarrensSocket (" + player.getName() + "): Connected to Socket.io server");
            try {
                socket.emit("socketIdentify",new JSONObject().put("player",player.getName()));
            } catch (Exception e) { e.printStackTrace(); }
        }

        public void onDisconnect() {
            plugin.log("BarrensSocket (" + player.getName() + "): Disconnected from Socket.io server");
            if (!shouldDisconnect) {
                plugin.BarrensSocketFactory.reconnect(player);
                return;
            }
            shouldDisconnect = false;
        }

        public void onConnectFailure() {
            plugin.log("BarrensSocket (" + player.getName() + ") failed to connect to socket.io server");
        }

        public void on(String event, org.json.JSONObject... data) {
            //Handle events
            switch (Events.valueOf(event)) {
                case chatMessage:
                    try {
                        this.player.sendMessage(data[0].getString("message")); //Display the raw message
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case chatCommand:
                    try {
                        this.player.chat(data[0].getString("command"));
                    } catch (JSONException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    break;
            }
        }

        public void onMessage(String message) {
            plugin.log("BarrensSocket (" + player.getName() + ") received eventless message: " + message);
        }

        public void onMessage(org.json.JSONObject json) {
            plugin.log("BarrensSocket (" + player.getName() + ") received eventless message: " + json.toString());
        }
    }

}

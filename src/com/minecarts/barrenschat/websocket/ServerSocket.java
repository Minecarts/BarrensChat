package com.minecarts.barrenschat.websocket;

import com.clwillingham.socket.io.IOSocket;
import com.clwillingham.socket.io.MessageCallback;
import com.minecarts.barrenschat.BarrensChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;

public class ServerSocket {
    private IOSocket socket;
    private BarrensChat plugin;

    private boolean shouldDisconnect = false;

    public ServerSocket(BarrensChat plugin) {
        this.socket = new IOSocket(plugin.config.getString("socketUrl","http://192.168.1.21:801"), new callback());
        this.plugin = plugin;

        try {
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void playerJoin(Player player) throws JSONException, IOException {
        socket.emit("playerJoin", new JSONObject().put("player", player.getName()));
    }

    public void playerQuit(Player player) throws JSONException, IOException {
        socket.emit("playerQuit", new JSONObject().put("player", player.getName()));
    }

    public void updatePlayers() throws JSONException, IOException {
        if (plugin.getServer().getOnlinePlayers().length == 0) return;
       // JSONObject playerState = new JSONObject();
        JSONArray playerState = new JSONArray();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            playerState.put(new JSONObject()
                    .put("player", p.getName())
                    .put("health", p.getHealth())
                    .put("location", BarrensChat.getJSONLocation(p.getLocation()))
                    .put("experience", p.getExperience())
                    .put("food", p.getFoodLevel())
                    .put("gameMode", p.getGameMode())
                    .put("ip", p.getAddress().toString())
            );
        }
        socket.emit("onlinePlayers", new JSONObject().put("players",playerState));
    }

    public void setDisconnecting() {
        shouldDisconnect = true;
    }

    public void disconnect() {
        shouldDisconnect = true;
        socket.disconnect();
    }

    public void reconnect() {
        try {
            socket.connect();
        } catch (IOException e) {
            plugin.log("ServerSocket unable to reconnect to websocket server. Trying again in 10 seconds");
            Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    plugin.serverSocket.reconnect();
                }
            }, 20 * 10);
        }
    }

    private class callback implements MessageCallback {
        public callback() {
        }

        public void onConnect() {
            plugin.log("ServerSocket connected to Socket.io server");
        }

        public void onDisconnect() {
            plugin.log("ServerSocket disconnected from Socket.io server.");
            if (!shouldDisconnect) {
                plugin.serverSocket.reconnect();
                return;
            }
            shouldDisconnect = false;
        }

        public void onConnectFailure() {
            plugin.log("ServerSocket failed to connect to socket.io server.");
        }

        public void on(String event, org.json.JSONObject... data) { /* ServerSocket doesn't handle inbound events at this time */ }

        public void onMessage(String message) {
            plugin.log("ServerSocket received eventless message: " + message);
        }

        public void onMessage(org.json.JSONObject json) {
            plugin.log("ServerSocket received eventless message: " + json.toString());
        }
    }
}

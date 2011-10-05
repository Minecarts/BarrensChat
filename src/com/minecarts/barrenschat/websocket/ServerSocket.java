package com.minecarts.barrenschat.websocket;

import com.clwillingham.socket.io.IOSocket;
import com.clwillingham.socket.io.MessageCallback;
import com.minecarts.barrenschat.BarrensChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ServerSocket {
    private IOSocket socket;
    private BarrensChat plugin;

    private boolean shouldDisconnect = false;

    public ServerSocket(BarrensChat plugin){
        this.socket = new IOSocket("http://192.168.1.21:801",new callback());
        this.plugin = plugin;

        try {
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void playerJoin(Player player) throws JSONException, IOException{
        socket.emit("playerJoin",new JSONObject().put("player",player.getName()));
    }
    public void playerQuit(Player player) throws JSONException, IOException {
        socket.emit("playerQuit",new JSONObject().put("player",player.getName()));
    }
    public void updatePlayers() throws JSONException, IOException {
        if(plugin.getServer().getOnlinePlayers().length == 0) return;
        JSONObject playerState = new JSONObject();
        for(Player p : plugin.getServer().getOnlinePlayers()){
            playerState.put(p.getName(), new JSONObject()
                    .put("health", p.getHealth())
                    .put("location", BarrensChat.getJSONLocation(p.getLocation()))
                    .put("experience", p.getExperience())
                    .put("food",p.getFoodLevel())
                    .put("gameMode",p.getGameMode())
                    .put("ip", p.getAddress().toString())
            );
        }
        socket.emit("onlinePlayers",playerState);
    }

    public void setDisconnecting(){
        shouldDisconnect = true;
    }

    public void disconnect(){
        shouldDisconnect = true;
        socket.disconnect();
    }

    public void reconnect(){
         try {
            socket.connect();
        } catch (IOException e) {
             plugin.log("Plugin Socket: Unable to reconnect to websocket server. Trying again in 10 seconds");
             Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin,new Runnable() {
                 public void run() {
                     plugin.serverSocket.reconnect();
                 }
             },20*10);
        }
    }

    private enum Events{
        chatMessage,
        chatCommand
    }
     private class callback implements MessageCallback {
        public callback(){}
        public void onConnect() { log("Connected to Socket.io server"); }
        public void onDisconnect() {
            log("Disconnected from Socket.io server");
            if(!shouldDisconnect){
                plugin.serverSocket.reconnect();
                return;
            }
            shouldDisconnect = false;
            //Close socket unless
        }
        public void onConnectFailure() { log("Failed to connect to socket.io server"); }
        public void on(String event, org.json.JSONObject... data) {
            //Handle events
            switch(Events.valueOf(event)){
                case chatMessage:
                    try {
                        data[0].getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        public void onMessage(String message) { log(message); }
        public void onMessage(org.json.JSONObject json) { log("Json object: " + json.toString()); }

         //Todo: Temporary function
         private void log(String msg){
             System.out.println(msg);
         }
     }
}

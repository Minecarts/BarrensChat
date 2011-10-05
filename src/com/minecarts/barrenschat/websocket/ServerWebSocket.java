package com.minecarts.barrenschat.websocket;

import com.clwillingham.socket.io.IOSocket;
import com.clwillingham.socket.io.MessageCallback;
import org.bukkit.entity.Player;
import org.json.JSONException;

public class ServerWebSocket {
    private IOSocket socket;
    public ServerWebSocket(){
        this.socket = new IOSocket("http://192.168.1.21:801",new callback());
    }


    private enum Events{
        chatMessage,
        chatCommand
    }
     private class callback implements MessageCallback {
        public callback(){}
        public void onConnect() { log("Connected to Socket.io server"); }
        public void onDisconnect() { log("Disconnected from Socket.io server"); }
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

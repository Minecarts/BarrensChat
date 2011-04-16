package com.minecarts.barrenschat.cache;

import java.util.HashMap;
import org.bukkit.entity.Player;
import com.minecarts.barrenschat.ChatChannel;
import com.minecarts.barrenschat.helpers.ChannelInfo;

//Caches who is ignoring whom

public class CacheChannel extends CacheBase{
    private static HashMap<String, HashMap<ChatChannel, ChannelInfo>> channelInfoList = new HashMap<String, HashMap<ChatChannel, ChannelInfo>>();
    private static HashMap<String, ChannelInfo> defaultChannelList = new HashMap<String, ChannelInfo>();
   
    public static ChannelInfo getPlayerDefaultChannel(Player player){
        if(defaultChannelList.containsKey(player.getName())){
            return defaultChannelList.get(player.getName());
        } else {
            return null;
        }
    }
    //Default channels
    public static void setPlayerDefaultChannel(Player player, ChannelInfo ci){
        defaultChannelList.put(player.getName(),ci);
    }
    public static void clearPlayerDefaultChannel(Player player){
        defaultChannelList.remove(player.getName());
    }

    //Channel info cache
    public static ChannelInfo getPlayerChannelInfo(Player player, ChatChannel channel) { 
      if (channelInfoList.containsKey(player.getName())
         && channelInfoList.get(player.getName()).containsKey(channel)){
          return channelInfoList.get(player.getName()).get(channel);
      }
      return null;
    }

    public static void setPlayerChannelInfo(Player player, ChatChannel channel, ChannelInfo info)
    {
      if (channelInfoList.containsKey(player.getName())) {
        channelInfoList.get(player.getName()).put(channel, info);
      } else {
        HashMap<ChatChannel,ChannelInfo> ciMap = new HashMap<ChatChannel,ChannelInfo>();
        ciMap.put(channel, info);
        channelInfoList.put(player.getName(), ciMap);
      }
    }

    public static void invalidatePlayer(Player player) {
      if (channelInfoList.containsKey(player.getName())) channelInfoList.get(player.getName()).clear();
    }
}

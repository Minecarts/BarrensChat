 package com.minecarts.verrier.barrenschat.helpers;
 
 import com.minecarts.verrier.barrenschat.BarrensChat;
 import com.minecarts.verrier.barrenschat.ChatChannel;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import org.bukkit.entity.Player;
 
 public class Cache
 {
   public ignoreList ignoreList = new ignoreList();
   public channelInfo channelInfo = new channelInfo();
   private BarrensChat plugin;
 
   public Cache(BarrensChat plugin)
   {
     this.plugin = plugin;
   }
 
   public class channelInfo {
     private HashMap<Player, HashMap<ChatChannel, ChannelInfo>> channelInfoList = new HashMap<Player, HashMap<ChatChannel, ChannelInfo>>();
 
     public channelInfo() {  }
 
     public ChannelInfo getPlayerChannelInfo(Player player, ChatChannel channel) { 
       if ((this.channelInfoList.containsKey(player)) && (((HashMap<ChatChannel,ChannelInfo>) this.channelInfoList.get(player)).containsKey(channel))) {
         return (ChannelInfo)((HashMap<ChatChannel,ChannelInfo>)this.channelInfoList.get(player)).get(channel);
       }
       return null;
     }
 
     public void setPlayerChannelInfo(Player player, ChatChannel channel, ChannelInfo info)
     {
       if (this.channelInfoList.containsKey(player)) {
         ((HashMap<ChatChannel,ChannelInfo>)this.channelInfoList.get(player)).put(channel, info);
       } else {
         HashMap<ChatChannel,ChannelInfo> ciMap = new HashMap<ChatChannel,ChannelInfo>();
         ciMap.put(channel, info);
         this.channelInfoList.put(player, ciMap);
       }
     }
 
     public void invalidatePlayer(Player player) {
       if (this.channelInfoList.containsKey(player))
         ((HashMap<ChatChannel,ChannelInfo>)this.channelInfoList.get(player)).clear();
     }
   }
 
   public class ignoreList
   {
     private HashMap<Player, List<String>> ignoreList = new HashMap<Player, List<String>>();
 
     public ignoreList() {  }
 
     public void set(Player p, List<String> list) { this.ignoreList.put(p, list); }
 
     public List<String> get(Player p) {
       return (List<String>)this.ignoreList.get(p);
     }
     public boolean isIgnoring(Player player, Player ignore) {
       if (this.ignoreList.get(player) != null) {
         return ((List<String>)this.ignoreList.get(player)).contains(ignore.getName());
       }
       return false;
     }
 
     public void addIgnore(Player player, Player ignore) {
       if (this.ignoreList.get(player) == null) {
         this.ignoreList.put(player, new ArrayList<String>());
       }
       ((List<String>)this.ignoreList.get(player)).add(ignore.getName());
     }
     public void removeIgnore(Player player, Player ignore) {
       if (this.ignoreList.get(player) == null) {
         this.ignoreList.put(player, new ArrayList<String>());
       }
       ((List<String>)this.ignoreList.get(player)).remove(ignore.getName());
     }
   }
 }
package com.minecarts.barrenschat.helpers;

import com.minecarts.barrenschat.BarrensChat;
import com.minecarts.barrenschat.ChatChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.entity.Player;

public class DBHelper {
  BarrensChat plugin;
  public Connection connection;
  boolean connected = false;

  ArrayList<Integer> masterIndexList = new ArrayList<Integer>();
  HashMap<String, String> statements = new HashMap<String, String>();

  public DBHelper(BarrensChat plugin) {
        this.plugin = plugin;
        for(int i = 0; i <= 10; i++) this.masterIndexList.add(Integer.valueOf(i));
  }

  public ArrayList<ChannelInfo> getPlayerChannelsInfo(Player player){
      ArrayList<ChannelInfo> results = new ArrayList<ChannelInfo>();
      try {
          Connection conn = this.getConnection();
          PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_channels WHERE playerName = ?");
          if(ps == null){ //Query failed, so we're going to join them to default channels
              plugin.log.warning("GetPlayerChannels query failed");
              results.add(new ChannelInfo(0,"global", "Global", true));
              results.add(new ChannelInfo(1,"pvp", "PVP", true));
              conn.close();
              return results;
          }

          ps.setString(1, player.getName());
          ResultSet set = ps.executeQuery();
          while (set.next()) {
              ChannelInfo cinfo = new ChannelInfo(
                      Integer.valueOf(set.getInt("channelIndex")), 
                      set.getString("channelId"), 
                      set.getString("channelName"), 
                      Boolean.valueOf(set.getBoolean("isDefault")));
              results.add(cinfo);
          }
          set.close();
          ps.close();
          conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
  }

  public ChannelInfo getDefaultChannelInfo(Player player){
      
      //Check the cache
      ChannelInfo info = this.plugin.cache.channelInfo.getPlayerDefaultChannel(player);
      if (info != null) {
          return info;
      }
      
      try{
          Connection conn = this.getConnection();
          PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_channels WHERE playerName = ? AND isDefault=TRUE LIMIT 1");
          //If we're not connected to the DB, ps will be null,
          //  so everything should go to global
          if(ps == null){
              plugin.log.warning("GetDefaultChannel query failed");
              conn.close();
              return new ChannelInfo(0,"global","Global",true);
          }
          ps.setString(1, player.getName());
          ResultSet set = ps.executeQuery();

          if (set.next()) {
              ChannelInfo ci = new ChannelInfo(
                      Integer.valueOf(set.getInt("channelIndex")), 
                      set.getString("channelId"), 
                      set.getString("channelName"), 
                      Boolean.valueOf(set.getBoolean("isDefault")));
              
              this.plugin.cache.channelInfo.setPlayerDefaultChannel(player,ci);
              
              set.close();
              ps.close();
              conn.close();
              return ci;
          }
      } catch (SQLException e) {
          e.printStackTrace();
      }
      return null;
  }

  public void setDefaultChannel(Player player, ChatChannel channel) {
      clearDefaultChannel(player);
      try {
          Connection conn = this.getConnection();
          PreparedStatement ps = conn.prepareStatement("UPDATE player_channels SET isDefault = TRUE WHERE playerName=? AND channelId=? LIMIT 1");

          ps.setString(1, player.getName());
          ps.setString(2, channel.name.toLowerCase());
          ps.execute();
          
          ps.close();
          conn.close();
      } catch (SQLException e) {
          e.printStackTrace();
      }
  }

  public void clearDefaultChannel(Player player) {
      try {
          Connection conn = this.getConnection();
          PreparedStatement ps = conn.prepareStatement("UPDATE player_channels SET isDefault = FALSE WHERE playerName=? AND isDefault = TRUE LIMIT 1");

          ps.setString(1, player.getName());
          ps.execute();

          this.plugin.cache.channelInfo.clearPlayerDefaultChannel(player);
          
          ps.close();
          conn.close();
      } catch (SQLException e) {
          e.printStackTrace();
      }
  }

  public ChannelInfo getChannelInfoAtIndex(Player player, Integer index){
      try {
          Connection conn = this.getConnection();
          PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_channels WHERE playerName = ? AND channelIndex=? LIMIT 1");

          ps.setString(1, player.getName());
          ps.setInt(2, index);

          ResultSet set = ps.executeQuery();
          ChannelInfo ci = null;
          if (set.next()) {
              ci = new ChannelInfo(
                      Integer.valueOf(set.getInt("channelIndex")), 
                      set.getString("channelId"), 
                      set.getString("channelName"), 
                      Boolean.valueOf(set.getBoolean("isDefault")));
          }
          set.close();
          ps.close();
          conn.close();
          return ci;
      } catch (SQLException e) {
          e.printStackTrace();
          return null;
      }
  }

  public ChannelInfo getChannelInfoByChannel(Player player, ChatChannel channel) {
      ChannelInfo info = this.plugin.cache.channelInfo.getPlayerChannelInfo(player, channel);
      if (info != null) {
          return info;
      }
      try {
          Connection conn = this.getConnection();
          PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_channels WHERE playerName=? AND channelId=? LIMIT 1");
          if(ps == null){ //Query failed
              plugin.log.warning("GetChannelById query failed");
              return new ChannelInfo(0,"global", "Global", true);
       }
       ps.setString(1, player.getName());
       ps.setString(2, channel.name.toLowerCase());
       ResultSet set = ps.executeQuery();

       if (set.next()) {
         ChannelInfo ci = new ChannelInfo(
           Integer.valueOf(set.getInt("channelIndex")), 
           set.getString("channelId"), 
           set.getString("channelName"), 
           Boolean.valueOf(set.getBoolean("isDefault")));
         this.plugin.cache.channelInfo.setPlayerChannelInfo(player, channel, ci);

         set.close();
         ps.close();
         conn.close();

         return ci;
      }
    } catch (SQLException e) {
       e.printStackTrace();
    }
    return null;
  }

  public void addPlayerChannel(Player player, ChatChannel channel, int index){
    try{
        Connection conn = this.getConnection();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO player_channels(playerName,channelIndex,channelId,channelName,isDefault) VALUES (?,?,?,?,0)");
        if(ps == null){ //Query failed
            plugin.log.warning("InsertChannel query failed");
            return;
        }
        ps.setString(1, player.getName());
        ps.setInt(2, index);
        ps.setString(3, channel.name.toLowerCase());
        ps.setString(4, channel.name);
        ps.execute();
        
        ps.close();
        conn.close();
    } catch (SQLException e) {
       e.printStackTrace();
    }
  }

  public void removePlayerChannel(Player player, ChatChannel channel) {
      try {
          Connection conn = this.getConnection();
          PreparedStatement ps = conn.prepareStatement("DELETE FROM player_channels WHERE playerName=? AND channelId=? LIMIT 1");
          ps.setString(1, player.getName());
          ps.setString(2, channel.name.toLowerCase());
          ps.execute();

          this.plugin.cache.channelInfo.invalidatePlayer(player);

          ps.close();
          conn.close();
      } catch (SQLException e) {
          e.printStackTrace();
      }
  }


  public boolean isPlayerInChannel(Player player, String channelName){
      ChatChannel chan = plugin.channelHelper.getChannelFromName(channelName);
      return isPlayerInChannel(player, chan);
  }
  public boolean isPlayerInChannel(Player player, ChatChannel channel){
      try {
          Connection conn = this.getConnection();
          PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_channels WHERE playerName=? AND channelId=? LIMIT 1");
          ps.setString(1, player.getName());
          ps.setString(2, channel.name.toLowerCase());
          ResultSet set = ps.executeQuery();
          boolean playerCheck = set.next(); //true or false

          set.close();
          ps.close();
          conn.close();

          return playerCheck;
      } catch (SQLException e) {
          e.printStackTrace();
      }
      return false; 
  }


  public List<String> getIgnoreList(Player player){
      List<String> ignoreList = new ArrayList<String>();
      try{
          Connection conn = this.getConnection();
          PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_ignore WHERE playerName = ?");
          if(ps == null){
              return ignoreList;
          }
          ps.setString(1, player.getName());
          ResultSet set = ps.executeQuery();

          while (set.next()) {
              ignoreList.add(set.getString("ignoreName"));
          }
          set.close();
          ps.close();
          conn.close();
      } catch (SQLException e) {
          e.printStackTrace();
      }
      return ignoreList;
  }

  
  public void addIgnore(Player player, Player ignore) {
      try{
          Connection conn = this.getConnection();
          PreparedStatement ps = conn.prepareStatement("INSERT INTO player_ignore(playerName,ignoreName) VALUES (?,?)");

          ps.setString(1, player.getName());
          ps.setString(2, ignore.getName());
          ps.execute();
          this.plugin.cache.ignoreList.addIgnore(player, ignore);

          ps.close();
          conn.close();
      } catch (SQLException e) {
          e.printStackTrace();
      }
  }


  public void removeIgnore(Player player, Player ignore) {
      try{
          Connection conn = this.getConnection();
          PreparedStatement ps = conn.prepareStatement("DELETE FROM player_ignore WHERE playerName=? AND ignoreName=? LIMIT 1");
          ps.setString(1, player.getName());
          ps.setString(2, ignore.getName());
          ps.execute();

          this.plugin.cache.ignoreList.removeIgnore(player, ignore);

          ps.close();
          conn.close();
      } catch (SQLException e) {
          e.printStackTrace();
      }
  }

  public int getNextChannelIndex(Player player){
      try {
          Connection conn = this.getConnection();
          PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_channels WHERE playerName = ?");
          if(ps == null){ //Query failed
              plugin.log.warning("GetPlayerChannels query failed");
              return 0;
          }
          ps.setString(1, player.getName());
          ResultSet set = ps.executeQuery();

          ArrayList<Integer> indexReturn = new ArrayList<Integer>(this.masterIndexList);

          while (set.next()) {
              indexReturn.remove(Integer.valueOf(set.getInt("channelIndex")));
          }
          set.close();
          ps.close();
          conn.close();

          if (indexReturn.size() == this.masterIndexList.size()) {
              return 0;
          }
          if (indexReturn.size() > 0) {
              return ((Integer)indexReturn.get(0));
          }
          return -1;
      } catch (SQLException e) {
          e.printStackTrace();
      }
      return 0;
  }
  
  public int getNumChannels(Player player){
    try {
        Connection conn = this.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(channelIndex) AS numChannels FROM player_channels WHERE playerName = ?");       
        if(ps == null){
            plugin.log.warning("GetNumChannels query failed");
            return 0;
        }
        ps.setString(1, player.getName());
        ResultSet set = ps.executeQuery();

        int numChannels = 0;
        if(set.next()){
            numChannels = set.getInt("numChannels");
        }
        set.close();
        ps.close();
        conn.close();
        return numChannels;
    } catch (SQLException e) {
        e.printStackTrace();
        return 0;
    }
  }


  private Connection getConnection(){
      return plugin.dbc.getConnection("minecarts");
  }


  public void createTables()
  {
    try {
       Connection conn = this.getConnection();
       Statement statement = conn.createStatement();
       this.connection.setAutoCommit(true);
             
       statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_channels (playerName VARCHAR(50) NOT NULL,channelIndex INTEGER NOT NULL,channelId VARCHAR(75) NOT NULL,channelName VARCHAR(75) NOT NULL,isDefault BOOLEAN NOT NULL DEFAULT FALSE,PRIMARY KEY (playerName, channelIndex));");
       statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_ignore (playerName VARCHAR(50) NOT NULL,ignoreName VARCHAR(50) NOT NULL,PRIMARY KEY (playerName, ignoreName));");
       statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_notifications (playerName VARCHAR(50) NOT NULL,flags set('WHISPER') default NULL,PRIMARY KEY (playerName));");

       statement.close();
       conn.close();
    } catch (SQLException e) {
       e.printStackTrace();
    }
  }
}
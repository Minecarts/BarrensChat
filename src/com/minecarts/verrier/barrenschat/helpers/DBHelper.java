package com.minecarts.verrier.barrenschat.helpers;

import com.minecarts.verrier.barrenschat.BarrensChat;
import com.minecarts.verrier.barrenschat.ChatChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

public class DBHelper
{
   BarrensChat plugin;
   public Connection connection;
   boolean connected = false;

   ArrayList<Integer> masterIndexList = new ArrayList<Integer>();

   HashMap<String, PreparedStatement> statements = new HashMap<String, PreparedStatement>();

  public DBHelper(BarrensChat plugin) {
     this.plugin = plugin;

     int i = 0;
    do { this.masterIndexList.add(Integer.valueOf(i));

       i++; plugin.getClass(); } while (i <= 10);
  }

  public void dbClose(){
	  try{
		  this.connection.close();
	  } catch (SQLException e){
		  e.printStackTrace();
	  }
  }
  
  public boolean dbConnect(String host, String port, String db, String username, String password)
  {
    try
    {
       Class.forName("com.mysql.jdbc.Driver");
       this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":"+port+"/" + db, username, password);
       this.connected = true;

       createTables();

       createStatements();
       return true;
    }
    catch (Exception e)
    {
      this.plugin.log.warning("****CRITICAL*** Unable to connect to MySQL DB");
      e.printStackTrace();
      return false;
    }
  }

  public ArrayList<ChannelInfo> getPlayerChannelsInfo(Player player)
  {
     this.plugin.getClass();

     ArrayList<ChannelInfo> results = new ArrayList<ChannelInfo>();
    try
    {
       PreparedStatement ps = (PreparedStatement)this.statements.get("GetPlayerChannels");
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
    } catch (SQLException e) {
       e.printStackTrace();
    }

     return results;
  }

  public ChannelInfo getDefaultChannelInfo(Player player)
  {
     this.plugin.getClass();
    try
    {
       PreparedStatement ps = (PreparedStatement)this.statements.get("GetDefaultChannel");
       ps.setString(1, player.getName());
       ResultSet set = ps.executeQuery();

       if (set.next()) {
         return new ChannelInfo(
           Integer.valueOf(set.getInt("channelIndex")), 
           set.getString("channelId"), 
           set.getString("channelName"), 
           Boolean.valueOf(set.getBoolean("isDefault")));
      }

       set.close();
    } catch (SQLException e) {
       e.printStackTrace();
    }
     return null;
  }

  public void setDefaultChannel(Player player, ChatChannel channel) {
     clearDefaultChannel(player);
    try {
      PreparedStatement ps = (PreparedStatement)this.statements.get("SetDefaultChannel");
       ps.setString(1, player.getName());
       ps.setString(2, channel.name.toLowerCase());
       ps.execute();
    } catch (SQLException e) {
       e.printStackTrace();
    }
  }

  public void clearDefaultChannel(Player player) {
     this.plugin.getClass();
    try
    {
       PreparedStatement ps = (PreparedStatement)this.statements.get("ClearDefaultChannel");
       ps.setString(1, player.getName());
       ps.execute();
    } catch (SQLException e) {
       e.printStackTrace();
    }
  }

  public ChannelInfo getChannelInfoAtIndex(Player player, Integer index)
  {
     this.plugin.getClass();
    try
    {
       PreparedStatement ps = (PreparedStatement)this.statements.get("GetChannelByIndex");
       ps.setString(1, player.getName());
       ps.setInt(2, index);
       ResultSet set = ps.executeQuery();

       if (set.next()) {
         return new ChannelInfo(
           Integer.valueOf(set.getInt("channelIndex")), 
           set.getString("channelId"), 
           set.getString("channelName"), 
           Boolean.valueOf(set.getBoolean("isDefault")));
      }

       set.close();
    } catch (SQLException e) {
       e.printStackTrace();
    }
     return null;
  }

  public ChannelInfo getChannelInfoByChannel(Player player, ChatChannel channel) {
     ChannelInfo info = this.plugin.cache.channelInfo.getPlayerChannelInfo(player, channel);
     if (info != null) {
       return info;
    }
    try
    {
       PreparedStatement ps = (PreparedStatement)this.statements.get("GetChannelById");
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
         return ci;
      }
       set.close();
    } catch (SQLException e) {
       e.printStackTrace();
    }
     return null;
  }

  public void addPlayerChannel(Player player, ChatChannel channel, int index)
  {
    try
    {
       PreparedStatement ps = (PreparedStatement)this.statements.get("InsertChannel");
       ps.setString(1, player.getName());
       ps.setInt(2, index);
       ps.setString(3, channel.name.toLowerCase());
       ps.setString(4, channel.name);
       ps.execute();
    } catch (SQLException e) {
       e.printStackTrace();
    }
  }

  public void removePlayerChannel(Player player, ChatChannel channel) {
    this.plugin.cache.channelInfo.invalidatePlayer(player);
    try {
       PreparedStatement ps = (PreparedStatement)this.statements.get("DeleteChannel");
       ps.setString(1, player.getName());
       ps.setString(2, channel.name.toLowerCase());
       ps.execute();
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
		  PreparedStatement ps = (PreparedStatement)this.statements.get("GetChannelById");
	      ps.setString(1, player.getName());
	      ps.setString(2, channel.name.toLowerCase());
	      ResultSet set = ps.executeQuery();
	      return set.next(); //Return true or false
	  } catch (SQLException e) {
		  e.printStackTrace();
	  }
	  
	  return false; 
  }
  

  public List<String> getIgnoreList(Player player)
  {
    List<String> ignoreList = new ArrayList<String>();
    try
    {
       PreparedStatement ps = (PreparedStatement)this.statements.get("GetIgnoreList");
       ps.setString(1, player.getName());
       ResultSet set = ps.executeQuery();

       while (set.next()) {
         ignoreList.add(set.getString("ignoreName"));
      }
       set.close();
    } catch (SQLException e) {
       e.printStackTrace();
    }
     return ignoreList;
  }
  public void addIgnore(Player player, Player ignore) {
     this.plugin.getClass();
    try
    {
       PreparedStatement ps = (PreparedStatement)this.statements.get("InsertIgnore");
       ps.setString(1, player.getName());
       ps.setString(2, ignore.getName());
       ps.execute();

       this.plugin.cache.ignoreList.addIgnore(player, ignore);
    } catch (SQLException e) {
       e.printStackTrace();
    }
  }

  public void removeIgnore(Player player, Player ignore) {
     this.plugin.getClass();
    try
    {
       PreparedStatement ps = (PreparedStatement)this.statements.get("DeleteIgnore");
       ps.setString(1, player.getName());
       ps.setString(2, ignore.getName());
       ps.execute();

       this.plugin.cache.ignoreList.removeIgnore(player, ignore);
    } catch (SQLException e) {
       e.printStackTrace();
    }
  }

  public int getNextChannelIndex(Player player)
  {
    try
    {
       PreparedStatement ps = (PreparedStatement)this.statements.get("GetPlayerChannels");
       ps.setString(1, player.getName());
       ResultSet set = ps.executeQuery();

       ArrayList<Integer> indexReturn = new ArrayList<Integer>(this.masterIndexList);

       while (set.next()) {
         indexReturn.remove(Integer.valueOf(set.getInt("channelIndex")));
      }

       if (indexReturn.size() == this.masterIndexList.size()) {
         this.plugin.getClass();

         return 0;
      }
       if (indexReturn.size() > 0) {
         this.plugin.getClass();

         return ((Integer)indexReturn.get(0));
      }
       this.plugin.getClass();

       return -1;
    }
    catch (SQLException e) {
       e.printStackTrace();
    }
     return 0;
  }

  public int getNumChannels(Player player)
  {
     this.plugin.getClass();
    try
    {
       PreparedStatement ps = (PreparedStatement)this.statements.get("GetNumChannels");
       ps.setString(1, player.getName());
       ResultSet set = ps.executeQuery();
       if (set.next()) {
         return set.getInt("numChannels");
      }
       return 0;
    }
    catch (SQLException e) {
       e.printStackTrace();
    }
     return 0;
  }

  public boolean checkHelpFlag(Player player, String flag)
  {
     this.plugin.getClass();
    try
    {
       PreparedStatement ps = (PreparedStatement)this.statements.get("GetFlags");
       ps.setString(1, player.getName());
       ps.setString(2, flag);
       ResultSet set = ps.executeQuery();

       return set.next();
    }
    catch (SQLException e)
    {
       e.printStackTrace();
    }
     return false;
  }

  public void setHelpFlag(Player player, String flag)
  {
     this.plugin.getClass();
    try
    {
       Statement statement = this.connection.createStatement();
       statement.execute("INSERT INTO player_notifications(playerName, flags) VALUES('" + 
         player.getName() + "','" + flag + "') " + 
         "ON DUPLICATE KEY UPDATE player_notifications SET flags = CONCAT(flags,'," + flag + "') WHERE playerName = '" + player.getName() + "' LIMIT 1");
    }
    catch (SQLException e) {
       e.printStackTrace();
    }
  }

  public void loadPlayerSubscription(Player player)
  {
  }

  public void createStatements()
  {
	  //We only want to create our prepared statements once, to take 
	  //	advantage of the caching involved when doing multiple queries
	  //	with the same prepared statement
    try
    {
       this.statements.put("GetPlayerChannels", this.connection.prepareStatement("SELECT * FROM player_channels WHERE playerName = ?"));
       this.statements.put("GetDefaultChannel", this.connection.prepareStatement("SELECT * FROM player_channels WHERE playerName = ? AND isDefault=TRUE LIMIT 1"));
       this.statements.put("GetChannelByIndex", this.connection.prepareStatement("SELECT * FROM player_channels WHERE playerName = ? AND channelIndex=? LIMIT 1"));
       this.statements.put("GetChannelById", this.connection.prepareStatement("SELECT * FROM player_channels WHERE playerName=? AND channelId=? LIMIT 1"));

       this.statements.put("GetIgnoreList", this.connection.prepareStatement("SELECT * FROM player_ignore WHERE playerName = ?"));

       this.statements.put("GetNumChannels", this.connection.prepareStatement("SELECT COUNT(channelIndex) AS numChannels FROM player_channels WHERE playerName = ?"));

       this.statements.put("GetFlags", this.connection.prepareStatement("SELECT * FROM player_notifications WHERE playerName = ? AND flags = ? LIMIT 1"));

       this.statements.put("InsertChannel", this.connection.prepareStatement("INSERT INTO player_channels(playerName,channelIndex,channelId,channelName,isDefault) VALUES (?,?,?,?,0)"));
       this.statements.put("InsertIgnore", this.connection.prepareStatement("INSERT INTO player_ignore(playerName,ignoreName) VALUES (?,?)"));

       this.statements.put("ClearDefaultChannel", this.connection.prepareStatement("UPDATE player_channels SET isDefault = FALSE WHERE playerName=? AND isDefault = TRUE LIMIT 1"));
       this.statements.put("SetDefaultChannel", this.connection.prepareStatement("UPDATE player_channels SET isDefault = TRUE WHERE playerName=? AND channelId=? LIMIT 1"));

       this.statements.put("DeleteChannel", this.connection.prepareStatement("DELETE FROM player_channels WHERE playerName=? AND channelId=? LIMIT 1"));
       this.statements.put("DeleteIgnore", this.connection.prepareStatement("DELETE FROM player_ignore WHERE playerName=? AND ignoreName=? LIMIT 1"));
    } catch (SQLException e) {
       e.printStackTrace();
    }
  }

  public void createTables()
  {
    try {
       Statement statement = this.connection.createStatement();
       this.connection.setAutoCommit(true);
             
       statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_channels (playerName VARCHAR(50) NOT NULL,channelIndex INTEGER NOT NULL,channelId VARCHAR(75) NOT NULL,channelName VARCHAR(75) NOT NULL,isDefault BOOLEAN NOT NULL DEFAULT FALSE,PRIMARY KEY (playerName, channelIndex));");
       statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_ignore (playerName VARCHAR(50) NOT NULL,ignoreName VARCHAR(50) NOT NULL,PRIMARY KEY (playerName, ignoreName));");
       statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_notifications (playerName VARCHAR(50) NOT NULL,flags set('WHISPER') default NULL,PRIMARY KEY (playerName));");

       statement.close();
    } catch (SQLException e) {
       e.printStackTrace();
    }
  }
}
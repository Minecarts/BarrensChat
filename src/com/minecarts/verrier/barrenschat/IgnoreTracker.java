 package com.minecarts.verrier.barrenschat;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import org.bukkit.entity.Player;
 
 public class IgnoreTracker
 {
   public HashMap<Player, ArrayList<Player>> ignoreList = new HashMap<Player, ArrayList<Player>>();
 
   public void ignore(Player player, Player ignore)
   {
     if (this.ignoreList.containsKey(player)) {
       ((ArrayList<Player>)this.ignoreList.get(player)).add(ignore);
     } else {
       ArrayList<Player> a = new ArrayList<Player>();
       a.add(ignore);
       this.ignoreList.put(player, a);
     }
   }
 
   public void unignore(Player player, Player ignore) {
     if (this.ignoreList.containsKey(player)) {
       if (((ArrayList<Player>)this.ignoreList.get(player)).contains(ignore)) {
         ((ArrayList<Player>)this.ignoreList.get(player)).remove(ignore);
         player.sendMessage(ignore.getName() + " is no longer ignored.");
       } else {
         player.sendMessage("You are not currently ignoring " + ignore.getName() + ".");
       }
     }
     else player.sendMessage("You are not currently ignoring anyone.");
   }
 
   public boolean isIgnored(Player player, Player ignoring)
   {
     return ((ArrayList<Player>)this.ignoreList.get(player)).contains(ignoring);
   }
 }

/* Location:           F:\Users\stephen\Desktop\BarrensChat.jar
 * Qualified Name:     com.minecarts.verrier.barrenschat.IgnoreTracker
 * JD-Core Version:    0.6.0
 */
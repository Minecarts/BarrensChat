 package com.minecarts.barrenschat.helpers;
 
 public class ChannelInfo
 {
   public Integer index;
   public String id;
   public String name;
   public Boolean isDefault;
 
   public ChannelInfo(Integer index, String id, String name, Boolean isDefault)
   {
     this.index = index;
     this.id = id;
     this.name = name;
     this.isDefault = isDefault;
   }
 }
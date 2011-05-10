 package com.minecarts.barrenschat;
 
 public class ChatFormatString
 {
   //public static String CHANNEL_USER_JOIN = "%1$s[%2$s] %3$s%1$s joined the channel.";
   public static String CHANNEL_USER_JOIN = "{0}[{1}] {2}{0} joined the channel.";
   public static String CHANNEL_USER_LEAVE = "{0}[{1}] {2}{0} left the channel.";
   public static String CHANNEL_MODERATOR_CHANGE = "{0}[{1}] {2}{0} is now the channel moderator.";
   public static String CHANNEL_USER_MESSAGE = "{0}[{1}] <{2}{0}> {3}";
   
   public static String IGNORE_PLAYER_ADD = "{0}You have ignored {1}{0}.";
   public static String IGNORE_PLAYER_REMOVE = "{0}You are no longer ignoring {1}{0}.";
   public static String IGNORE_MESSAGE_FAILED = "{0}{1}{0} is ignoring you.";
   public static String PLAYER_NOT_ONLINE = "{1}{0} is no longer online.";
   
   public static String WHISPER_SEND = "{0}> [{1}{0}] {2}";
   public static String WHISPER_RECEIVE = "{0}[{1}{0}] {2}";
   
   public static String CHANNEL_BROADCAST = "{0}[{1}] {2}";
   public static String USER_YELL = "{0}[Shout] <{1}{0}> {2}";
   public static String USER_SAY = "{0}<{1}{0}> {2}";
   public static String SELF_CHANNEL_JOIN = "Joined channel {0}[{1}] {2}";
   public static String SELF_CHANNEL_LEAVE = "Left channel {0}[{1}] {2}";
 }

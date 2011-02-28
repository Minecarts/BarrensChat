 package com.minecarts.verrier.barrenschat.helpers;
 
 public class StringHelper
 {
   public static String join(String[] arr, int offset)
   {
     return join(arr, offset, " ");
   }
   public static String join(String[] arr, int offset, String delim) {
     String str = "";
 
     if ((arr == null) || (arr.length == 0)) {
       return str;
     }
 
     for (int i = offset; i < arr.length; i++) {
       str = str + arr[i] + delim;
     }
 
     return str.trim();
   }
 }

/* Location:           F:\Users\stephen\Desktop\BarrensChat.jar
 * Qualified Name:     com.minecarts.verrier.barrenschat.helpers.StringHelper
 * JD-Core Version:    0.6.0
 */
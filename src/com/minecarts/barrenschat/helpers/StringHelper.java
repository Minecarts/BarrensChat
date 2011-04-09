 package com.minecarts.barrenschat.helpers;
 
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

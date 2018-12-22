package org.jsirenia.security;


import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Hex和Base64比较，Base64编码方式能占用更少的空间。
 */
public class HexUtil {
    private static final char[] chars = "0123456789ABCDEF".toCharArray();
    public static String toHexString(byte[] buf){
        char[] charArray = new char[buf.length*2];
        int j = 0;
        for(int i=0;i<buf.length;i++){
            charArray[j++] = chars[buf[i]>>>4 & 0x0F];
            charArray[j++] = chars[buf[i] & 0x0F];//先取高4位，再取低4位
        }
        return new String(charArray);
    }
    public static byte[] toByteArray(String hexString){
        byte[] byteArray = new byte[hexString.length()/2];
        char[] charArray = hexString.toCharArray();
        int high;
        int low;
        for(int i=0;i<charArray.length;i=i+2){
            high = Character.digit(charArray[i],16);
            low = Character.digit(charArray[i+1],16);
            byteArray[i>>>1] = (byte)(high<<4 | low);
        }
         return byteArray;
    }
    public static void main(String[] args) throws UnsupportedEncodingException {
        byte[] buf = "hell2154213165465o".getBytes("utf-8");
        String s1 = toHexString(buf);
        String s2 = Base64.getEncoder().encodeToString(buf);
        System.out.println(s1);
        System.out.println(s2);
        String s = Integer.toHexString(145);
        System.out.println(s);
        buf = new byte[]{(byte)145};
        s1 = toHexString(buf);
        System.out.println(s1);
        s = DigestUtils.md5DigestAsHex(buf);
        System.out.println(s);

    }

}

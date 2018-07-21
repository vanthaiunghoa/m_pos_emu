/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_common;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * C_conv is a static class with a serie of conversion methods
 *  
 * @author      Bulent Alacahan
 * @version     %I%, %G%
 * @since       0.1
 */
public class C_conv {
   
    public static final int MAX_BUFFER_SIZE = 2048;

    /**
     * Convert a string to an hexadecimal string
     * @param arg String to convert
     * @return String in hexa format
     */
    public static String toHex(String arg) {
        return String.format("%x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }

    /**
     * Convert a string in hexadecimal to an array of byte
     * Example : "11AABB33" will be {0x11, 0xAA, 0xBB, 0x33}
     * @param s Hexa string to convert
     * @return Array of byte 
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    
    /**
     * Copy one ByteBuffer content to another. This function allocates the output buffer
     * @param in input ByteBuffer (source)
     * @param sz Size of the output
     * @return ByteBuffer containing the first buffer for the given size
     */
    public static ByteBuffer byteBufferCopy(ByteBuffer in, int sz) {
        ByteBuffer tmp = ByteBuffer.allocate(sz);
        for (int i = 0; i < sz; i++) {
            tmp.put(i,in.get(i));
        }
        
        return tmp;
    }  
    
    /**
     * Converts a byte array to a HEX string
     * @param bytes array to convert
     * @return String containing the array displayable
     */
    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }        
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.swolebrain.chatprogram;

/**
 *
 * @author Victor
 */
public class ChatProgram {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        byte[] arr = new byte[1024];
        arr[0] = 0x4d;
        arr[1] = 0x61;
        arr[2] = 0x51;
        arr[3] = 0x46;
        
        String s = new String(arr);
        s = s.trim();
        System.out.println(s+"end");
//        char c = s.charAt(4);
//        System.out.println((byte)c);
    }
    
}

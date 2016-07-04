
package com.swolebrain.chatprogram;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    private String name, address;
    private int port;
    private Thread send, receive;
    private DatagramSocket socket;
    private InetAddress ip;
    private ClientWindow window;
    
    private int ID =-1;
    
    public Client(String name, String address, int port, ClientWindow window) {
        this.name = name; this.address = address; this.port = port;
        this.window=window;
    }
    
    public String getName(){return name;}
    public int getPort(){return port;}
    public String getAddress(){return address;}
    public int getID(){return ID;}
    
    protected String openConnection(){
        try{
            ip = InetAddress.getByName(address);
            socket = new DatagramSocket();
            return "";
        }
        catch (Exception e){
            return e.toString();
        }
    }
    
    protected void receive(){
        receive = new Thread("clientReceive"){
            public void run(){
                String ret;
                byte[] data = new byte[1024];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                try {
                    socket.receive(packet);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                ret = new String(packet.getData()); 
                ret = ret.trim();
                if (ret.startsWith("/c/")){
                    try{
                        System.out.println("Client: trying to parse "+ ret.substring(3) + " out of string "+ret);
                        ID = Integer.parseInt(ret.substring(3));
                        window.console("Successfully connected to server. ID = "+ID);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else if (ret.startsWith("/m/")){
                    window.console(ret.substring(3));
                }
            }
        };
        receive.start();
    }
    
    protected void send(final byte[] msg){
        send = new Thread("clientSend"){
            public void run(){
                DatagramPacket packet = new DatagramPacket(msg, msg.length, ip, port);
                try {
                    socket.send(packet);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        send.start();
    }
}

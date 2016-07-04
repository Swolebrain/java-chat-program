
package com.swolebrain.chatprogram.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable{
    private int port;
    private DatagramSocket socket;
    private Thread run, manage, send, receive;
    private boolean running = false;
    private List<ClientObj> clients = new ArrayList<>();
    
    public Server(int port){
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException ex) {
            ex.printStackTrace();
            return;
        }
        run = new Thread(this, "Server");
        run.start();
    }
    
    public void run(){
        running = true;
        System.out.println("Server started on port "+port);
        manageClients();
        receive();
    }
    
    private void manageClients(){
        manage = new Thread("Manage"){
            public void run(){
                while(running){
                    
                }
            }
        };
        manage.start();
    }
    
    private void receive(){
        receive = new Thread("Receive"){
            public void run(){
                while(running){
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try{
                        socket.receive(packet);
                        //System.out.println(new String(packet.getData()));
                    }catch (Exception e){ e.printStackTrace();}
                    process(packet);
                    
                }
            }
        };
        receive.start();
    }
    private void send(final byte[] data, final InetAddress address, final int port){
        send = new Thread("Send"){
          public void run(){
              try {
                  DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                  socket.send(packet);
              } catch (IOException ex) {
                  ex.printStackTrace();
              }
          }  
        };
                send.start();
    }
    
    private void sendToAll(String message){
        for (int i  = 0; i < clients.size(); i++){
            ClientObj client = clients.get(i);
            send(message.getBytes(), client.address, client.port);
            
        }
    }
    
    private void process(DatagramPacket packet){
        String str = new String(packet.getData());
        if (str.startsWith("/c/")){
            int id = UID.getID();
            clients.add(new ClientObj(str.substring(3,str.length()), packet.getAddress(), packet.getPort(), id));
            String response = "/c/"+id;
            System.out.print("Accepted connection from: "+str.substring(3));
            System.out.println(", id:"+id+", active clients: "+clients.size());
            send(response.getBytes(), packet.getAddress(), packet.getPort());
        }
        else if (str.startsWith("/m/")){
            sendToAll(str);
            System.out.println("Received String packet from "+packet.getAddress()+", message: "+str+", active clients: "+clients.size());
        }
        else
            System.err.println(str);
    }
}

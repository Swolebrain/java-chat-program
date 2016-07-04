
package com.swolebrain.chatprogram.server;

public class ServerMain {
    
    private int port;
    private Server server;
    
    public ServerMain(int port){
        this.port=port;
        server = new Server(port);
    }
    public static void main(String[] args){
        int port;
        try{
            port = Integer.parseInt(args[0]);
        }catch (Exception e){
            System.out.println("You didn't specify a port or did so incorrectly. Using 7999.");
            port = 7999;
        }
        new ServerMain(port);
    }
}

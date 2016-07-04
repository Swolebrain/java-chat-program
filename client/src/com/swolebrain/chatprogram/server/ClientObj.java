
package com.swolebrain.chatprogram.server;

import java.net.InetAddress;

public class ClientObj {
    public String name;
    public InetAddress address;
    public int port;
    public final int ID;
    public int attempt = 0;
    
    public ClientObj(String name, InetAddress address, int port, int id){
        this.name=name;
        this.address = address;
        this.port=port;
        ID=id;
    }
}

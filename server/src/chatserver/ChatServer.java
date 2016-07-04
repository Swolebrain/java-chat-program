
package chatserver;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class ChatServer extends Thread{
    private List<SocketChannel> clients;
    private ByteBuffer writeBuffer;
    private ByteBuffer readBuffer;
    private CharsetDecoder asciiDecoder;
    
    private ServerSocketChannel sSockChan;
    private boolean running;
    private Selector readSelector;
    public static final int PORT = 10997;
    
    public ChatServer() {
        clients = new LinkedList<SocketChannel>();
        writeBuffer = ByteBuffer.allocateDirect(255);
        readBuffer = ByteBuffer.allocateDirect(255);
        asciiDecoder = Charset.forName( "US-ASCII").newDecoder();
    }
    
    private void initServerSocket(){
        try{
            sSockChan = ServerSocketChannel.open();
            sSockChan.configureBlocking(false);
            InetAddress addr = InetAddress.getLocalHost();
            sSockChan.socket().bind(new InetSocketAddress(addr,PORT));
            
            readSelector = Selector.open();
        }catch (Exception e){
            System.err.println("Error initializing server.");
            e.printStackTrace();
        }
    }
    
    private void acceptNewConnections(){
        try{
            SocketChannel clientChannel;
            while ((clientChannel = sSockChan.accept()) != null){
                addNewClient(clientChannel);
                sendBroadcastMessage("login from: "+
                        clientChannel.socket().getInetAddress(),
                        clientChannel);
                sendMessage(clientChannel, "\n\nWelcome to dis here nigga chat server, there are "
                +clients.size()+" users online.\n");
                sendMessage(clientChannel, "Type 'quit' to exit.\n");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private void addNewClient(SocketChannel chan){
        clients.add(chan);
        try{
            chan.configureBlocking(false);
            SelectionKey readKey = chan.register(readSelector, SelectionKey.OP_READ, new StringBuffer());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private void sendMessage(SocketChannel channel, String mesg){
        prepWriteBuffer(mesg);
        channelWrite (channel, writeBuffer);
    }
    
    private void sendBroadcastMessage(String mesg, SocketChannel from){
        prepWriteBuffer(mesg);
        for (SocketChannel channel : clients){
            if (channel != from)
                channelWrite(channel, writeBuffer);
        }
    }
    
    private void prepWriteBuffer(String mesg){
        writeBuffer.clear();
        writeBuffer.put(mesg.getBytes());
        writeBuffer.putChar('\n');
        writeBuffer.flip();
    }
    
    private void channelWrite(SocketChannel channel, ByteBuffer writeBuffer){
        long nbytes = 0;
        long toWrite = writeBuffer.remaining();
        
        try{
            while (nbytes != toWrite){
                nbytes += channel.write(writeBuffer);
                try{
                    Thread.sleep(10);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        writeBuffer.rewind();
    }
    
    private void readIncomingMessages(){
        try{
            readSelector.selectNow();
            Set<SelectionKey> readyKeys = readSelector.selectedKeys();
            for (SelectionKey key : readyKeys){
                SocketChannel channel = (SocketChannel)key.channel();
                readBuffer.clear();
                long nbytes = channel.read(readBuffer);
                if (nbytes == -1){
                    System.out.println("Disconnect: "+channel.socket().getInetAddress());
                    channel.close();
                    clients.remove(channel);
                    sendBroadcastMessage("logout: "+
                            channel.socket().getInetAddress(), channel);
                }
                else{
                    StringBuffer sb = (StringBuffer)key.attachment();
                    readBuffer.flip();
                    String str = asciiDecoder.decode(readBuffer).toString();
                    readBuffer.clear();
                    sb.append(str);
                    
                    String line = sb.toString();
                    if ((line.indexOf("\n") != -1) || (line.indexOf("\r") != -1)) {
                        line = line.trim();
                        if (line.startsWith("quit")) {
                            System.out.println("got quit msg, closing channel for : " + channel.socket().getInetAddress());
			    channel.close();
			    clients.remove(channel);
			    sendBroadcastMessage("logout: " + channel.socket().getInetAddress(), channel);
                        }
                        else{
                            System.out.println("broadcasting: " + line);
			    sendBroadcastMessage(channel.socket().getInetAddress() + ": " + line+" length "+line.length(), channel);
			    sb.delete(0,sb.length());
                        }
                    }
                }
                
                readyKeys.remove(key);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    
    public static void main(String[] args) {
        //BasicConfigurator.configure();
	
	// instantiate the ChatterServer and fire it up
	ChatServer cs = new ChatServer();
	cs.start();
    }
    
    public void run() {
	initServerSocket();

	System.out.println("ChatterServer running");
	running = true;
	int numReady = 0;

	// block while we wait for a client to connect
	while (running) {
	    // check for new client connections
	    acceptNewConnections();
	    
	    // check for incoming mesgs
	    readIncomingMessages();
	    
	    // sleep a bit
	    try {
		Thread.sleep(100);
	    }
	    catch (InterruptedException ie) {
	    }
	}
    }
    
}

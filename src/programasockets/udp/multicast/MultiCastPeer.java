/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package programasockets.udp.multicast;

import java.net.*;
import java.io.*;
import java.util.logging.*;

import programasockets.view.ProgramClientView;
import programasockets.view.ProgramServerView;

/**
 *
 * @author Hector
 */
public class MultiCastPeer implements Runnable{
    
    /** View of the server. */
    private ProgramServerView sView;
    /** View of the client. */
    private ProgramClientView cView;
    
    /** Port of the server. */
    private int port;
    /** Address of the multipeer connection. */
    private String group;
    /** Size of the message buffers. */
    private int bufferSize;
    /** The socket of the server. */
    private MulticastSocket server;
    /** The time for waiting the package. */
    private int timeout;
    
    /** The default buffer size of the messages. */
    private static final int DEFAULT_BUFFER_SIZE = 8000;
    /** The default hostname of the server. */
    private static final String DEFAULT_GROUP = "228.5.6.7";
    /** The default port of the server. */
    private static final int DEFAULT_PORT = 6789;
    /** The default timeout time in seconds. */
    private static final int DEFAULT_TIMEOUT = 30;
    
    /** Flag when the server is running. */
    private boolean running;
    /** Thread of the server. */
    private Thread thread;
    
    /**
     * Default constructor.
     * @param view
     */
    public MultiCastPeer(ProgramServerView view) {
        this(DEFAULT_PORT, DEFAULT_BUFFER_SIZE, DEFAULT_GROUP, DEFAULT_TIMEOUT);
        this.sView = view;
        this.cView = null;
    }
    
    /**
     * Default constructor.
     * @param view
     */
    public MultiCastPeer(ProgramClientView view) {
        this(DEFAULT_PORT, DEFAULT_BUFFER_SIZE, DEFAULT_GROUP, DEFAULT_TIMEOUT);
        this.sView = null;
        this.cView = view;
    }
    
    /**
     * Customized constructor.
     * @param port custom port
     * @param bufferSize custom size of buffer
     */
    public MultiCastPeer(int port, int bufferSize, String group, int timeout) {
        this.port = port <= 0? DEFAULT_PORT : port;
        this.bufferSize = bufferSize <= 0? DEFAULT_BUFFER_SIZE : bufferSize;
        this.group = group;
        this.timeout = timeout;
    }
    
    // ================================ GET AND SET ============================
    
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        if(port <= 0) {
            return;
        }
        
        this.port = port;
        
        if(server.isConnected()) {
            clean();
            init();
        }
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    // ========================Starting and destroying==========================
    
    /**
     * For being homogeneous with the TCPServer method.
     */
    public void startServer() {
        thread = new Thread(this);
        thread.start();
    }
    
    /**
     * For being homogeneous with the TCPServer method.
     */
    public void stopServer() {
        running = false;
        server.close();
    }
    
    /**
     * Creates a new server socket in a certain port.
     */
    public final void init() {
        if(server != null){
            clean();
        }
        
        try {
            server = new MulticastSocket(port);
            InetAddress hGroup = InetAddress.getByName(group);
            server.joinGroup(hGroup);
            running = true;
        } catch (IOException ex) {
            Logger.getLogger(MultiCastPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Releases the resources of the object, UDPServer cannot be used 
     * after cleaning.
     */
    public void clean() {
        server.close();
        server = null;
    }
    
    // ========================Sending and receiving==========================
    
    /**
     * Receives a package from any client.
     * @return the packet of the sender, null if a problem occurs
     */
    public DatagramPacket receiveMessage() {
        DatagramPacket request = null;
        
        try {
            byte[] buffer = new byte[bufferSize];
            request = new DatagramPacket(buffer, bufferSize);
            server.receive(request);
            if(sView != null){
                sView.logMessage("[Cliente] " + new String(request.getData()));
            }else {
                cView.logMessage("[Server] " + new String(request.getData()));
            }
                
           
        } catch (IOException ex) {
            Logger.getLogger(MultiCastPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return request;
    }
    
    /**
     * Sends a message to a certain client with a hostname.
     * @param message the message to send
     * @return true if the message got sent or false otherwise
     */
    public boolean sendMessage(String message) {
        try {
            InetAddress iHost = InetAddress.getByName(group);
            DatagramPacket request = createPacket(message, bufferSize, iHost, port);
            server.send(request);
        } catch (IOException ex) {
            Logger.getLogger(MultiCastPeer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }
    
    /**
     * Sends a message to a certain client with a hostname.
     * @param message the message to send
     * @param host the destination hostname
     * @return true if the message got sent or false otherwise
     */
    public boolean sendMessage(String message, InetAddress host, int port) {
        try {
            DatagramPacket reply = createPacket(message, bufferSize, host, port);
            server.send(reply);
            
            if(sView != null) {
                sView.logMessage("[Cliente] " + message);
            }else {
                cView.logMessage("[Server] " + message);
            }
                
        } catch (IOException ex) {
            Logger.getLogger(MultiCastPeer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }
    
    /**
     * Creates a packet for sending. 
     * 
     * @param message the message to send in the package
     * @param size the size of the message
     * @param host the name of the destination host
     * @param port the port of comunication
     * @return an DatagramPacket instance
     */
    private DatagramPacket createPacket(String message, int size, InetAddress host, int port) {
        if(size < 8) {
            size = 8;
        }else if( size > 100) {
            size = 100;
        }

        byte[] bMessage = message.getBytes();

        DatagramPacket packet = 
            new DatagramPacket(bMessage,
                               bMessage.length,
                               host,
                               port);

        return packet;
    }
    
    public void run() {
        init();
        
        while(running) {
            receiveMessage();
        }
        
        clean();
    }
    
    /*
    // Legion
    public static void main(String args[]){ 
        args = new String[2];
        // args give message contents and destination multicast group (e.g. "228.5.6.7")
        args[0] = "Mensaje";
        args[1] = "228.5.6.7";
        
        MulticastSocket s = null;
        try {
            InetAddress group = InetAddress.getByName(args[1]);
            s = new MulticastSocket(6789);
            s.joinGroup(group);
            byte [] m = args[0].getBytes();
            DatagramPacket messageOut = new DatagramPacket(m, m.length, group, 6789);
            s.send(messageOut);	
            byte[] buffer = new byte[1000];
            for(int i=0; i< 3;i++) {		// get messages from others in group
                    DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                    s.receive(messageIn);
                    System.out.println("Received:" + new String(messageIn.getData()));
            }
            s.leaveGroup(group);		
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        }catch (IOException e){System.out.println("IO: " + e.getMessage());
        }finally {if(s != null) s.close();}
    }	*/
}

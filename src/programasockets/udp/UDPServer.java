/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package programasockets.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import programasockets.view.ProgramServerView;

/**
 * - Do something with the init, clean, startServer and stopServer, 
 * its redundant.
 * 
 * @author Hector
 */
public class UDPServer extends Thread{
    
    /** View of the server. */
    //private ProgramServerView view;
    
    /** Port of the server. */
    private int port;
    /** Size of the message buffers. */
    private int bufferSize;
    /** The socket of the server. */
    private DatagramSocket server;
    
    /** The default buffer size of the messages. */
    private static final int DEFAULT_BUFFER_SIZE = 1000;
    /** The default port of the server. */
    private static final int DEFAULT_PORT = 6789;
    
    /** Flag when the server is running. */
    private boolean running;
    
    /**
     * Default constructor.
     */
    public UDPServer(/*ProgramServerView view*/) {
        this(DEFAULT_PORT, DEFAULT_BUFFER_SIZE);
        //this.view = view;
    }
    
    /**
     * Customized constructor.
     * @param port custom port
     * @param bufferSize custom size of buffer
     */
    public UDPServer(int port, int bufferSize) {
        this.port = port <= 0? DEFAULT_PORT : port;
        this.bufferSize = bufferSize <= 0? DEFAULT_BUFFER_SIZE : bufferSize;
        
        running = false;
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
        this.start();
    }
    
    /**
     * For being homogeneous with the TCPServer method.
     */
    public void stopServer() {
        running = false;
    }
    
    /**
     * Creates a new server socket in a certain port.
     */
    public final void init() {
        if(server != null){
            clean();
        }
        
        try {
            server = new DatagramSocket(port);
            running = true;
            
        } catch (SocketException ex) {
            Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
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
            System.out.println(new String(request.getData()));
           // if(view != null) view.logMessage("[Cliente] " + new String(request.getData()));
           
        } catch (IOException ex) {
            Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return request;
    }
    
    /**
     * Sends a message to a certain client with the received package info.
     * @param message the message to send
     * @param sender the package of the sender
     * @return true if the message got sent or false otherwise
     */
    public boolean sendMessage(String message, DatagramPacket sender) {
        return sendMessage(message, sender.getAddress());
    }
    
    /**
     * Sends a message to a certain client with a hostname.
     * @param message the message to send
     * @param host the destination hostname
     * @return true if the message got sent or false otherwise
     */
    public boolean sendMessage(String message, InetAddress host) {
        try {
            DatagramPacket reply = createPacket(message, bufferSize, host, port);
            server.send(reply);
//            if(view != null) view.logMessage("[Server] " + message);
        } catch (IOException ex) {
            Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
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
                               size,
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
    
    public static void main(String[] args) {
        UDPServer server = new UDPServer();
        server.init();
        System.out.println("Running server " + server.getPort());
        DatagramPacket sender = server.receiveMessage();
        server.sendMessage("OK", sender);
        server.clean();
    }
}

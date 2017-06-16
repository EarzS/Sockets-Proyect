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
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import programasockets.view.ProgramClientView;

/**
 *
 * @author Hector
 */
public class UDPClient {
    
    /** View of the client. */
    //private ProgramClientView view;
    
    /** Port of the server. */
    private int port;
    /** Hostname of the server. */
    private String hostname;
    /** Size of the message buffers. */
    private int bufferSize;
    /** The socket of the server. */
    private DatagramSocket client;
    /** The time for waiting the package. */
    private int timeout;
    
    /** The default buffer size of the messages. */
    private static final int DEFAULT_BUFFER_SIZE = 1000;
    /** The default hostname of the server. */
    private static final String DEFAULT_HOSTNAME = "Larez";
    /** The default port of the server. */
    private static final int DEFAULT_PORT = 6789;
    /** The default timeout time in seconds. */
    private static final int DEFAULT_TIMEOUT = 30;
    
    /**
     * Default constructor.
     */
    public UDPClient(/*ProgramClientView view*/) {
        this(DEFAULT_PORT, DEFAULT_HOSTNAME, DEFAULT_BUFFER_SIZE, DEFAULT_TIMEOUT);
       // this.view = view;
    }
    
    /**
     * Customized constructor.
     * @param port custom port
     * @param hostname the name of the client's host
     * @param bufferSize custom size of buffer
     */
    public UDPClient(int port, String hostname, int bufferSize, int timeout) {
        this.port = port <= 0? DEFAULT_PORT : port;
        this.bufferSize = bufferSize <= 0? DEFAULT_BUFFER_SIZE : bufferSize;
        this.timeout = timeout < DEFAULT_TIMEOUT? DEFAULT_TIMEOUT : timeout; 
        
        if(hostname == null || hostname.isEmpty()) {
            this.hostname = DEFAULT_HOSTNAME;
        }
        
        this.hostname = hostname;
        
        init();
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
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        if(hostname == null || hostname.isEmpty()) {
            return;
        }
        
        this.hostname = hostname;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    // ========================Starting and destroying==========================
    
    /**
     * Starts the client socket.
     */
    public final void init() {
        if(client != null){
            clean();
        }
        
        try {
            client = new DatagramSocket();
            client.setSoTimeout(timeout * 1000);
        } catch (SocketException ex) {
            Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Releases the resources of the object, UDPClient cannot be used 
     * after cleaning.
     */
    public final void clean() {
        client.close();
        client = null;
    }
    
    // ========================Sending and receiving==========================
    
    /**
     * Receives a package from the server.
     * @return the packet of the sender, null if a problem occurs
     */
    public DatagramPacket receiveMessage() {
        DatagramPacket reply = null;
        
        try {
            byte[] buffer = new byte[bufferSize];
            reply = new DatagramPacket(buffer, bufferSize);
            client.receive(reply);
            //view.logMessage("[Server] " + reply);
        } catch (SocketTimeoutException ex) {
            Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return reply;
    }
    
    /**
     * Sends a message to a certain client with a hostname.
     * @param message the message to send
     * @return true if the message got sent or false otherwise
     */
    public boolean sendMessage(String message) {
        try {
            InetAddress iHost = InetAddress.getByName(hostname);
            DatagramPacket request = createPacket(message, bufferSize, iHost, port);
            client.send(request);
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
                               bufferSize, // Should be size, not the lenght
                               host,
                               port);

        return packet;
    }
    
    public static void main(String[] args) {
        UDPClient client = new UDPClient();
        System.out.println("Running client" + client.getPort());
        
        client.init();
        client.sendMessage("Hola");
        client.receiveMessage();
        client.clean();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package programasockets.tcp;

import java.net.*;
import java.io.*;
import java.util.logging.*;
import programasockets.view.ProgramClientView;

/**
 *
 * @author Hector
 */
public class TCPClient {
    
    /** View of the client. */
    private ProgramClientView view;
    
    /** Port of the server. */
    private int port;
    /** Hostname of the server. */
    private String hostname;
    /** Size of the message buffers. */
    private int bufferSize;
    /** The socket of the server. */
    private Socket server;
    /** The socket chanel for receiving data from the server. */
    private DataInputStream in;
    /** The socket chanel for sending data to the server. */        
    private DataOutputStream out;
    /** The time for waiting the package. */
    private int timeout;
    
    /** The default buffer size of the messages. */
    private static final int DEFAULT_BUFFER_SIZE = 1000;
    /** The default hostname of the server. */
    private static final String DEFAULT_HOSTNAME = "Larez";
    /** The default port of the server. */
    private static final int DEFAULT_PORT = 7896;
    /** The default timeout time in seconds. */
    private static final int DEFAULT_TIMEOUT = 30;
    
    /**
     * Default constructor.
     */
    public TCPClient(ProgramClientView view) {
        this(DEFAULT_PORT, DEFAULT_HOSTNAME, DEFAULT_BUFFER_SIZE, DEFAULT_TIMEOUT);
        this.view = view;
    }
    
    /**
     * Customized constructor.
     * @param port custom port
     * @param hostname the name of the client's host
     * @param bufferSize custom size of buffer
     */
    public TCPClient(int port, String hostname, int bufferSize, int timeout) {
        this.port = port <= 0? DEFAULT_PORT : port;
        this.bufferSize = bufferSize <= 0? DEFAULT_BUFFER_SIZE : bufferSize;
        this.timeout = timeout < DEFAULT_TIMEOUT? DEFAULT_TIMEOUT : timeout; 
        
        if(hostname == null || hostname.isEmpty()) {
            this.hostname = DEFAULT_HOSTNAME;
        }
        
        this.hostname = hostname;
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
    public boolean init() {
        if(server != null){
            clean();
        }
        
        try {
            server = new Socket(hostname, port);
            server.setSoTimeout(timeout * 1000);
            in = new DataInputStream(server.getInputStream());
            out = new DataOutputStream(server.getOutputStream());
        } catch (SocketException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }
    
    /**
     * Releases the resources of the object, TCPClient cannot be used 
     * after cleaning.
     */
    public void clean() {
        try {
            in.close();
            in = null;
            
            out.close();
            out = null;
            
            server.close();
            server = null;
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // ========================Sending and receiving==========================
    
    /**
     * Receives a response from the server.
     * @return the message of the sender, null if a problem occurs
     */
    public String receiveMessage() {
        
        String reply = null;
        
        try {
            byte[] buffer = new byte[bufferSize];
            in.read(buffer);
            reply = new String(buffer);
            view.logMessage("[Server] " + reply);
        } catch (SocketTimeoutException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return reply;
    }
    
    /**
     * Sends a message to the server.
     * @param message the message to send
     * @return true if the message got sent or false otherwise
     */
    public boolean sendMessage(String message) {
        try {
            byte[] buffer = message.getBytes();
            out.write(buffer);
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }
    
    /**
     * Sends a custom package with the buffer size.
     */
    public void sendPackage() {
        try {
            byte[] buffer = new byte[bufferSize];
            out.write(buffer);
            
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
    public static void main (String args[]) {
		// arguments supply message and hostname
		Socket s = null;
		try{
			int serverPort = 7896;
			s = new Socket(args[1], serverPort);    
			DataInputStream in = new DataInputStream( s.getInputStream());
			DataOutputStream out = new DataOutputStream( s.getOutputStream());
			out.writeUTF(args[0]);      	// UTF is a string encoding see Sn. 4.4
			String data = in.readUTF();	    // read a line of data from the stream
			System.out.println("Received: "+ data) ; 
		}catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){System.out.println("readline:"+e.getMessage());
		}finally {
           if(s!=null) 
           try {s.close();}
           catch (IOException e){System.out.println("close:"+e.getMessage());}
        }
    }*/
}

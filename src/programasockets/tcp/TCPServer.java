/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package programasockets.tcp;

import java.net.*;
import java.io.*;
import java.util.logging.*;

/**
 * - Add a list to manage clients connections if you want to close them 
 * afterwards
 * 
 * - Do something with the init, clean, startServer and stopServer, 
 * its redundant.
 * 
 * @author Hector
 */
public class TCPServer extends Thread{
    
    /** Port of the server. */
    private int port;
    /** Size of the message buffers. */
    private int bufferSize;
    /** The socket of the server. */
    private ServerSocket server;
    /** Indicates if the server is running. */
    private boolean running;
    
    /** The default buffer size of the messages in kb. */
    private static final int DEFAULT_BUFFER_SIZE = 1000;
    /** The default port of the server. */
    private static final int DEFAULT_PORT = 7896;
    
    /**
     * Default constructor.
     */
    public TCPServer() {
        this(DEFAULT_PORT, DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * Customized constructor.
     * @param port custom port
     * @param bufferSize custom size of buffer
     */
    public TCPServer(int port, int bufferSize) {
        if(port <= 0) {
            this.port = DEFAULT_PORT;
        }
        if(bufferSize <= 0) {
            this.bufferSize = DEFAULT_BUFFER_SIZE;
        }
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
        
        if(!server.isClosed()) {
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
     * It's a more elegant way to start the thread.
     */
    public void startServer() {
        this.start();
    }
    
    /**
     * Detiene el servidor, no se puede usar denuevo el servidor una vez usado 
     * este metodo.
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
            server = new ServerSocket(port);
            running = true;
        } catch (SocketException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Releases the resources of the object, TCPServer cannot be used 
     * after cleaning.
     */
    public void clean() {
        try {
            server.close();
            server = null;
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        
        init();
        
        while(running) {
            getClientConnection();
        }
        
        clean();
    }
    
    /**
     * Waits for a new client to connect to the server, after connecting
     * creates a new TCPClientThread for the client.
     */
    private void getClientConnection() {
        try {
            Socket sClient = server.accept();
            TCPClientThread client = new TCPClientThread(sClient);
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // ========================Sending and receiving==========================
    
    /**
     * Receives a response from a client.
     * @param in the inputstream between the server and the client.
     * @return the message of the sender, null if a problem occurs
     */
    public String receiveMessage(DataInputStream in) throws IOException {
        
        String reply = null;
        
        byte[] buffer = new byte[bufferSize];
        in.read(buffer);
        reply = new String(buffer);
        
        return reply;
    }
    
    /**
     * Sends a message to a certain client.
     * @param out the outputstream between the server and the client.
     * @param message the message to send
     * @return true if the message got sent or false otherwise
     */
    public boolean sendMessage(DataOutputStream out, String message) {
        try {
            byte[] buffer = message.getBytes();
            out.write(buffer);
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }
    
    class TCPClientThread extends Thread{
        
        /** The socket of the client. */
        private Socket client;
        /** The socket chanel for receiving data from the server. */
        private DataInputStream in;
        /** The socket chanel for sending data to the server. */        
        private DataOutputStream out;
        
        public TCPClientThread(Socket client) {
            try {
                this.client = client;
                
                in = new DataInputStream(client.getInputStream());
                out = new DataOutputStream(client.getOutputStream());
                
                this.start();
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
        
        public void run() {
            String request;
            try {
                while(true) {
                    request = receiveMessage(in);
                    sendMessage(out, request);
                }
            }catch(EOFException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }catch(IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }finally {
                try {
                    in.close();
                    out.close();
                    client.close();
                } catch (IOException ex) {
                    Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /*
    public static void main (String args[]) {
        try{
            int serverPort = 7896; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while(true) {
                Socket clientSocket = listenSocket.accept();
                Connection c = new Connection(clientSocket);
            }
        } catch(IOException e) {System.out.println("Listen socket:"+e.getMessage());}
    }
    */
}

/*
class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	public Connection (Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream( clientSocket.getInputStream());
			out = new DataOutputStream( clientSocket.getOutputStream());
			this.start();
		} catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
	}
	public void run(){
		try {			                 // an echo server
			String data = in.readUTF();	 // read a line of data from the stream
			out.writeUTF(data);
		} catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		} catch(IOException e) {System.out.println("readline:"+e.getMessage());
		} finally { 
           try {
              clientSocket.close();
           }catch (IOException e){/*close failed*//*}
        }
	}
}*/

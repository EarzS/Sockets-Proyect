/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package programasockets.tcp;

import java.net.*;
import java.io.*;

/**
 *
 * @author Hector
 */
public class TCPClient {
    
    /** Port of the server. */
    private int port;
    /** Hostname of the server. */
    private String hostname;
    /** Size of the message buffers. */
    private int bufferSize;
    /** The socket of the server. */
    private Socket server;
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
     }
}

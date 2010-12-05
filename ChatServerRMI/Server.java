/*
 * Server.java
 *
 * Created on April 11, 2001, 11:02 AM
 */
 
package ChatServerRMI;

import javax.naming.*;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import javax.rmi.PortableRemoteObject;

/** 
 *
 * @author  Jean-Claude Franchitti
 * @version 
 */
public class Server 
{
	
    public Server(String name){
  	(new ServerThread()).load(name);	
    }		
    
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) 
    {
        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
	
	for (int i = 0; i < 5; i++){
		new Server("ChatRoom" + i);
	}

    }
    
  public class ServerThread extends Thread {
  
    String serverName;
  
    public void load(String name){
        serverName = name;	
        this.start();
    }
    
    public void run(){
        try {
            ChatRoomImpl obj = new ChatRoomImpl();

            Context initialNamingContext = new InitialContext();
            //initialNamingContext.rebind("ChatRoomServer",obj);
            initialNamingContext.rebind(serverName,obj);

            System.out.println("ChatRoomServer: " + serverName + " bound in registry");
        } catch (Exception e) {
            System.out.println("ChatRoomImpl err: " + e.getMessage());
            e.printStackTrace();
        }
    } // end of run()
  } // end of class ServerThread    

}
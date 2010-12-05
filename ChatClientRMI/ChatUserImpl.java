/*
 * ChatUserImpl.java
 *
 * Created on April 11, 2001, 11:16 AM
 */
 
package ChatClientRMI;

import javax.naming.*;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import javax.rmi.PortableRemoteObject;

/** 
 *
 * @author  Jean-Claude Franchitti
 * @version 
 */
public class ChatUserImpl extends PortableRemoteObject implements ChatUser
{
    private Client client;
    
  /** Creates new ChatUserImpl */
  public ChatUserImpl(Client client) throws RemoteException
  {   
  	super();
  	this.client = client;
  }
  
  public void updateMessage(String message, String nickname) throws java.rmi.RemoteException 
  {   
	/*
		 message format
		 connect: msgtype@avartarcode@x@y@username has joined ....
	*/
	
	System.out.println("message = " + message);
    	if ( nickname.equals("Server") ){ 
    		
    		int pos = message.indexOf('@');
    		int msgtype = Integer.parseInt(message.substring(0, pos));
    		message = message.substring(pos + 1);
    		System.out.println("megtype = " + msgtype);
    		
    		if ( msgtype == MsgType.CONNECT ){
    		
    			pos = message.indexOf('@');
    			int code = Integer.parseInt(message.substring(0, pos));
    			message = message.substring(pos + 1);
    			System.out.println("code = " + code);
    		
    			pos = message.indexOf('@');
    			int x = Integer.parseInt(message.substring(0, pos));
    			message = message.substring(pos + 1);
    			System.out.println("x = " + x);
    		
    			pos = message.indexOf('@');
    			int y = Integer.parseInt(message.substring(0, pos));
    			message = message.substring(pos + 1);
    			System.out.println("y = " + y);
    		
    			pos = message.indexOf(' ');
    			String username = message.substring(0, pos);
    		
    			client.talkArea.append(nickname + ": " + message + "\n");    			
    			
    			UserInfo user = new UserInfo();
    			user.name = username;
    			user.code = code;
    			user.dx = user.x = x;
    			user.dy = user.y = y;
    			//System.out.println("user.x = " + user.x);	
    			//System.out.println("user.y = " + user.y);	
    			client.users.put(username, user);	
    			client.drawGraphicArea();
    			
    		} else if ( msgtype == MsgType.DISCONNECT ){
	    		pos = message.indexOf(' ');
    			String username = message.substring(0, pos);
    			
    			client.talkArea.append(nickname + ": " + message + "\n");    			    			
    			
    			client.users.remove(username);
    			client.drawGraphicArea();
    		} else {
    			System.out.println("*** wrong message type ***");	
    		}
    	} else {
    		if ( ! client.users.containsKey(nickname) ){
    			System.out.println("*** update message fail: " + nickname + " is not in the list ***");	
    			return;
    		}
		client.sayEnd  = false;
    		UserInfo user = (UserInfo)client.users.get(nickname); 
    		user.say = message;
    		user.sayTime = client.SAY_TIME;
    		client.talkArea.append(nickname + ": " + message + "\n");
    	}  	
  }
  
  public void updateLocation(int x,int y,String nickname) throws java.rmi.RemoteException 
  {
   	if ( ! client.users.containsKey(nickname) ){
    		System.out.println("*** update Location fail: client is not in the list ***");	
    		return;
    	}
    	client.moveEnd = false;
    	UserInfo user = (UserInfo)client.users.get(nickname); 	
    	user.dx = x;
    	user.dy = y;  	
  }
  
  public void updateUserList(String userlist) throws java.rmi.RemoteException 
  {
  	// userlist = name@code@x@y
    	int pos;
    	UserInfo p = new UserInfo();
 	while ( userlist.length() > 1){
 		pos = userlist.indexOf('@');
 		p.name = userlist.substring(0, pos);
 		userlist = userlist.substring(pos+1);
 		//System.out.println("name = " + p.name);
 		
 		pos = userlist.indexOf('@');
 		p.code = Integer.parseInt(userlist.substring(0, pos));
 		userlist = userlist.substring(pos+1);
 		//System.out.println("code = " + p.code);

 		pos = userlist.indexOf('@');
 		p.dx = p.x = Integer.parseInt(userlist.substring(0, pos));
 		userlist = userlist.substring(pos+1);
 		//System.out.println("x = " + p.x);
 		
 		pos = userlist.indexOf('@');
 		p.dy = p.y = Integer.parseInt(userlist.substring(0, pos));
 		userlist = userlist.substring(pos+1);
 		//System.out.println("y = " + p.y);
 		
 		client.users.put(p.name, p);
 	}
 	client.drawGraphicArea();  	
  }

}
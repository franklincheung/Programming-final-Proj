/*
 * ChatRoomImpl.java
 *
 * Created on April 11, 2001, 10:56 AM
 */
 
package ChatServerRMI;

import javax.naming.*;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import javax.rmi.PortableRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

/** 
 *
 * @author  Jean-Claude Franchitti
 * @version 
 */
public class ChatRoomImpl extends PortableRemoteObject implements ChatRoom 
{
    private ConcurrentHashMap<String,ChatClientRMI.UserInfo> _users;
    private org.omg.CORBA.ORB _orb;
    private Context _initContext;
    private Random r = new Random();    	    

  /** Creates new ChatRoomImpl */
    public ChatRoomImpl() throws RemoteException
    {   
    	super();	
    	try{
    		
    		_initContext= new InitialContext();                
    	} catch(Exception e){
    		System.out.println(e);	
    	}
    	_users = new ConcurrentHashMap<String,ChatClientRMI.UserInfo>();
    }
  
    public boolean connect(String nickname, int avatarCode) throws java.rmi.RemoteException 
    {   
    	// 	Add user nickname to _users hashtable if it is not there already
        if (_users.containsKey(nickname))  // no duplicate nicknames are allowed
        {   
            // failed to connect
            return false;
        }
        else // new nickname, so add user to list
        {   
        	ChatClientRMI.UserInfo user = new ChatClientRMI.UserInfo();
	    	user.name = nickname;
	    	user.code = avatarCode;
	    	int r1 = r.nextInt(ChatClientRMI.Client.GRAPHIC_WIDTH - ChatClientRMI.Client.ICON_WIDTH);
    		int r2 = r.nextInt(ChatClientRMI.Client.GRAPHIC_HEIGHT - ChatClientRMI.Client.ICON_HEIGHT);
    		user.dx = user.x = ChatClientRMI.Client.ICON_WIDTH / 2  + r1;
    		user.dy = user.y = ChatClientRMI.Client.ICON_HEIGHT / 2 + r2;            
    	    
    		// send current userlist to the new user
    		sendUserList(nickname);
    	    
    		_users.put(user.name, user);
    	    
            // Update all other clients that nickname joined chat room
            sendMessage(MsgType.CONNECT + "@" + avatarCode + "@" + user.x + "@" + user.y + "@" + nickname + " has joined the chat room", "Server");
            
            System.out.println(nickname + " connected. Using avatar code: " + avatarCode);
            return true;             
        }  	
  }
  
  public boolean disconnect(String nickname) throws java.rmi.RemoteException 
  {   
    	// Remove user nickname to _users vector if it is there 
        if (!_users.containsKey(nickname))  // no duplicate nicknames are allowed
        {   
            return false;
        }
        else // nickname is in the list, so remove user from list
        {   
            _users.remove(nickname);
            System.out.println("Disconnected: " + nickname);
            // Update all other clients that nickname left chat room
            sendMessage(MsgType.DISCONNECT + "@" + nickname + " has left the chat room", "Server");
            return true; 
        }  	
  }
  
  public void sendMessage(String message,String fromNickname) throws java.rmi.RemoteException 
  {
        ChatClientRMI.ChatUser aUser;
        ChatClientRMI.UserInfo p;
       
        for (Enumeration<ChatClientRMI.UserInfo> e = _users.elements(); e.hasMoreElements(); ){
		p = e.nextElement();
		try{
            		aUser = (ChatClientRMI.ChatUser)PortableRemoteObject.narrow(_initContext.lookup(p.name), ChatClientRMI.ChatUser.class);            	
            		aUser.updateMessage(message, fromNickname);
            	} catch (Exception err){
            		System.out.println(err);	
            	}
	}  	
  }
  
  public boolean sendMessageToParty(String message,String fromNickname,String toNickname) throws java.rmi.RemoteException 
  {   return true;
  }
  
  public void sendLocation(int x,int y,String fromNickname) throws java.rmi.RemoteException 
  {
        ChatClientRMI.ChatUser aUser;
        ChatClientRMI.UserInfo p;
               
	for (Enumeration<ChatClientRMI.UserInfo> e = _users.elements(); e.hasMoreElements(); ){
		p = e.nextElement();
		p.dx = p.x = x;
		p.dy = p.y = y;
		try{
            	aUser = (ChatClientRMI.ChatUser)PortableRemoteObject.narrow(_initContext.lookup(p.name), ChatClientRMI.ChatUser.class);
	    		aUser.updateLocation(x, y, fromNickname);
	    	} catch (Exception err){
	    		System.out.println(err);	
	    	}
        }  	
  }
  
  public void sendUserList(String toNickname) throws java.rmi.RemoteException 
  {
	ChatClientRMI.ChatUser aUser;
	ChatClientRMI.UserInfo p;
	
	String s = "";
	for (Enumeration<ChatClientRMI.UserInfo> e = _users.elements(); e.hasMoreElements(); ){
		p = e.nextElement();
		s = s + p.name + "@" + p.code + "@" + p.x + "@" + p.y + "@";
		try{
			aUser = (ChatClientRMI.ChatUser)PortableRemoteObject.narrow(_initContext.lookup(toNickname), ChatClientRMI.ChatUser.class);
			aUser.updateUserList(s);  	
		} catch (Exception err){	
	    		System.out.println(err);	
		}
	}		
  }

}
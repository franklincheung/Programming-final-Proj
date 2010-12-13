// ChatClientSideServer.java
// Copyright and License
import HelloApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import java.util.Properties;

class ChatUserImpl extends ChatUserPOA {
  private ORB orb;

  public void setORB(ORB orb_val) {
    orb = orb_val;
  }

  // implement sayHello() method
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
 		 connect: msgtype@avatarcode@x@y@username has joined ....
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
     		UserInfo user = client.users.get(nickname);
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
     	UserInfo user = client.users.get(nickname);
     	user.dx = x;
     	user.dy = y;
   }

   public void updateUserList(String userlist) throws java.rmi.RemoteException
   {
   	// userlist = name@code@x@y
     	int pos;
     client.users.clear();
  	while ( userlist.length() > 1){
  		UserInfo p = new UserInfo();
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


public class ChatClientSideServer {

  public static void main(String args[]) {
    try{
      // create and initialize the ORB
      ORB orb = ORB.init(args, null);

      // get reference to rootpoa & activate the POAManager
      POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      rootpoa.the_POAManager().activate();

      // create servant and register it with the ORB
      ChatUserImpl chatUserImpl = new ChatUserImpl();
      chatUserImpl.setORB(orb);

      // get object reference from the servant
      org.omg.CORBA.Object ref = rootpoa.servant_to_reference(helloImpl);
      ChatUserHelper href = ChatUserHelper.narrow(ref);

      // get the root naming context
      org.omg.CORBA.Object objRef =
          orb.resolve_initial_references("NameService");
      // Use NamingContextExt which is part of the Interoperable
      // Naming Service (INS) specification.
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

      // bind the Object Reference in Naming
      String name = "";//todo
      NameComponent path[] = ncRef.to_name( name );
      ncRef.rebind(path, href);

      System.out.println("Server ready and waiting ...");

      // wait for invocations from clients
      orb.run();
    }

      catch (Exception e) {
        System.err.println("ERROR: " + e);
        e.printStackTrace(System.out);
      }

      System.out.println("Server Exiting ...");

  }
}

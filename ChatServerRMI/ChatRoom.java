/*
 * ChatRoom.java
 *
 * Created on April 11, 2001, 10:49 AM
 */
 
package ChatServerRMI;

import java.rmi.Remote; 
import java.rmi.RemoteException; 

/** 
 *
 * @author  Jean-Claude Franchitti
 * @version 
 */
public interface ChatRoom extends Remote
{
  public boolean connect (String nickname, int avatarCode) throws java.rmi.RemoteException;

  public boolean disconnect (String nickname) throws java.rmi.RemoteException;

  public void sendMessage (String message, String fromNickname) throws java.rmi.RemoteException;

  public boolean sendMessageToParty (String message, String fromNickname, String toNickname) throws java.rmi.RemoteException;

  public void sendLocation (int x, int y, String fromNickname) throws java.rmi.RemoteException;

  public void sendUserList(String toNickname) throws java.rmi.RemoteException;

}
/*
 * ChatUser.java
 *
 * Created on April 11, 2001, 11:12 AM
 */
 
package ChatClientRMI;

import java.rmi.Remote; 
import java.rmi.RemoteException; 

/** 
 *
 * @author  Jean-Claude Franchitti
 * @version 
 */
public interface ChatUser extends Remote
{
    public void updateMessage (String message, String fromNickname) throws java.rmi.RemoteException;

    public void updateLocation (int x, int y, String nickname) throws java.rmi.RemoteException;

    public void updateUserList (String userlist) throws java.rmi.RemoteException;

}

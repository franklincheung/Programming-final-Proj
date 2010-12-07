/*
 * Client.java
 *
 * Created on April 11, 2001, 11:19 AM
 */
 
package ChatClientRMI;

import java.util.concurrent.ConcurrentHashMap;
import javax.naming.*;
import java.rmi.RemoteException;
import javax.rmi.PortableRemoteObject;
import java.rmi.RMISecurityManager;

import ChatServerRMI.*;


import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

/** 
 *
 * @author  Jean-Claude Franchitti
 * @version 
 */

public class Client extends JFrame implements Runnable, ActionListener
{

  private static final String connectStr = "Connect";
  private static final String disconnectStr = "Disconnect";
    
  private String _nickname;
  private Thread _thread;
  private Context _initialContext;
  private JTextField inputField = new JTextField();
  public  JTextArea talkArea = new JTextArea();
  private JButton _connectButton;    
  private JButton _disconnectButton; 
     
  private Vector<String> serverVector = new Vector<String>();
  private JList serverList = new JList(serverVector);

  ChatRoom chatroom = null;    
  String chatroomName;	  
    
  Client myFrame = this;
  MyActionListener  myActionListener = new MyActionListener();
  
  boolean loaded = false;
  
  static int      REFRESH_TIME = 200;
  
  //a timer for refresh graphic area
  javax.swing.Timer         frameTimer = new javax.swing.Timer(REFRESH_TIME, myActionListener);
    	
  //client area info
  public static int      CLIENT_WIDTH  = 800;
  public static int      CLIENT_HEIGHT = 600;    	
  	
  //left panel info
  public static int      LEFT_PANEL_WIDTH = 120;
  public static int      LEFT_PANEL_HEIGHT = 420;
  public static int      LEFT_PANEL_LEFT = 20;
  public static int      LEFT_PANEL_TOP = 20;
  	
  //graphic area info
  public static int      GRAPHIC_TOP = 30;
  public static int      GRAPHIC_LEFT = 30;
  public static int      GRAPHIC_WIDTH = 400;
  public static int      GRAPHIC_HEIGHT = 300;  	
  	
  //talk area info
  public static int      TALK_TOP = GRAPHIC_TOP + GRAPHIC_HEIGHT + 5;
  public static int      TALK_LEFT = GRAPHIC_LEFT;
  public static int      TALK_WIDTH = GRAPHIC_WIDTH;
  public static int      TALK_HEIGHT = 175;  	
  	
  //input field info
  public static int      INPUT_TOP = TALK_TOP + TALK_HEIGHT + 25;
  public static int      INPUT_LEFT = GRAPHIC_LEFT;
  public static int      INPUT_WIDTH = GRAPHIC_WIDTH;
  public static int      INPUT_HEIGHT = 20;

  //server list info
  public static int      SERVER_LIST_TOP = GRAPHIC_TOP;
  public static int      SERVER_LIST_LEFT = GRAPHIC_LEFT + GRAPHIC_WIDTH + 160;
  public static int      SERVER_LIST_WIDTH = 120;
  public static int      SERVER_LIST_HEIGHT = GRAPHIC_HEIGHT; // 420;

  //user list info
  public static int      USER_LIST_TOP = 20;
  public static int      USER_LIST_LEFT = GRAPHIC_LEFT + GRAPHIC_WIDTH + 10;
  public static int      USER_LIST_WIDTH = 120;
  public static int      USER_LIST_HEIGHT = 420;  	
  
  public static int      SHADOW_WIDTH = 5;
  
  //background color
  static Color    backColor = new Color(130, 60, 170);
  
  //command label
  static final String CMD_LABEL[] = {"change icon", "query friend", "change location", "open room", "query hero", "help", "temp leave", "leave"};    

  //icon info
  public static final int      MAX_ICONS = 100;
  public static final String   ICON_FILENAME = "icons.gif";
  public static int            ICON_WIDTH = 32;
  public static int            ICON_HEIGHT = 32;
  Image                 icons[] = new Image[MAX_ICONS];
  int                   totalIcons = 16;
  
  static String  BACKIMG_FILENAME[] = {"back0.jpg", "back1.jpg", "back2.jpg", "back3.jpg"};  

  Image          backImg = null;
  Image          leftPanelImg = null;
  Image          graphicImg = null;
  Image          userListImg = null;
  Image          serverListImg = null;

  //user info
  static int            MAX_USERS = 100;
  UserInfo              userInfo[] = new UserInfo[MAX_USERS];
  int                   totalUsers = 0;
  int                   myIdx = 0;
  ConcurrentHashMap<String,UserInfo>		users = new ConcurrentHashMap<String,UserInfo>();
  
  //say delay
  static int    	SAY_TIME = 15;

  //say rectangle's width
  static int    	SAY_WIDTH = 100;

  // move step
  static int    	ONE_STEP = 10;
  
  boolean	        endChat = true;
  boolean		moveEnd = true;
  boolean		sayEnd  = true;
  
  int			enterListIndex = -1;
  int			exitListIndex = -1;
  
      
    /** Creates new ChatClient */
    public Client(String name) 
    {  
    	 super(name);
        _nickname = name;
        try{
		_initialContext = new InitialContext();
        } catch (Exception e){
        	System.out.println(e);	
        }
        
        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }       
        

        setSize(new Dimension(CLIENT_WIDTH, CLIENT_HEIGHT));
        this.getContentPane().setLayout(null);
        this.getContentPane().setBackground(backColor);

    	talkArea.setEditable(false);
    	talkArea.setBackground(Color.white);
    	talkArea.setBounds(new Rectangle(TALK_LEFT, TALK_TOP, TALK_WIDTH, TALK_HEIGHT));
    	this.getContentPane().add(talkArea, null);
    	
    	//set input area
    	inputField.setBackground(Color.white);
    	inputField.setBounds(new Rectangle(INPUT_LEFT, INPUT_TOP, INPUT_WIDTH, INPUT_HEIGHT));
	//inputField.addActionListener(this);
    	this.getContentPane().add(inputField, null);    
    	
    	// connect button
    	_connectButton = new JButton(connectStr);
    	_connectButton.setBounds(new Rectangle(600, 400, 100, 30));
        _connectButton.addActionListener(this);	
        this.getContentPane().add(_connectButton);
    	
    	// disconnect button
    	_disconnectButton = new JButton(disconnectStr);
    	_disconnectButton.setBounds(new Rectangle(600, 450, 100, 30));
    	_disconnectButton.setEnabled(false);
        //_disconnectButton.addActionListener(this);	
        this.getContentPane().add(_disconnectButton);    	
    	
    	//testButton = new JButton("test");
    	//testButton.setBounds(new Rectangle(600,500,100,30));
    	//testButton.addActionListener(this);
    	//this.getContentPane().add(testButton);

	for ( int i = 0; i < 5; i++){
		serverVector.add("ChatRoom" + i);
	}

	serverList.setBackground(new Color(190, 180,255));
	serverList.setBounds(new Rectangle(SERVER_LIST_LEFT+2, SERVER_LIST_TOP + 40, SERVER_LIST_WIDTH - SHADOW_WIDTH-10, SERVER_LIST_HEIGHT-40-50));
	serverList.setSelectedIndex(0);	    	
	serverList.setCellRenderer(new CustomCellRenderer());
	this.getContentPane().add(serverList);
    	
    	
    	// add mouse listener for serverList/*
    	serverList.addMouseListener(new java.awt.event.MouseAdapter(){
    		public void mouseEntered(MouseEvent e){
    			int index = serverList.locationToIndex(e.getPoint());
    			enterListIndex = index;
    			//setForeground(new Color(0,0,255));
    			System.out.println("you entered index " + index);
    	   	}
    		
    		public void mouseExited(MouseEvent e){
    			int index = serverList.locationToIndex(e.getPoint());
    			exitListIndex = index;
    			//setForeground(new Color(0,255,255));
    			System.out.println("you exited index " + index);
    		}
    	});
    	
        //add mouse listener
    	this.addMouseListener(new java.awt.event.MouseAdapter(){
        	public void mousePressed(java.awt.event.MouseEvent event){
            		mouseClick_performed(event);
        	}
    	});	
    
        // Always need this to enable closing the frame
        this.addWindowListener(new java.awt.event.WindowAdapter() 
        {   public void windowClosing(java.awt.event.WindowEvent e) 
            {   
            	if ( endChat ) {
            		System.exit(0);
            		return;	
            	}
            	boolean success = false;
            	try{
            		success = chatroom.disconnect(_nickname);
            	} catch(java.rmi.RemoteException err){
            		System.out.println(err);
            	}
            	if (success) 
            		System.out.println("Disconnected...");
            	else
            		System.out.println("Not disconnected.");

            	System.exit(0);
            }
        });    	

        
        // Wait for incoming requests
        this.startThread();

        // Enable GUI
        this.setVisible(true);
        
    	//create offscreen images
    	leftPanelImg = createImage(LEFT_PANEL_WIDTH, LEFT_PANEL_HEIGHT);
    	graphicImg = createImage(GRAPHIC_WIDTH, GRAPHIC_HEIGHT);
    	userListImg = createImage(USER_LIST_WIDTH, USER_LIST_HEIGHT);
    	serverListImg = createImage(SERVER_LIST_WIDTH, SERVER_LIST_HEIGHT);        

    	drawServerList();
    	serverList.repaint();
    	
    	(new LoadImageThread()).load();	
	
	try{
        	_initialContext.rebind(_nickname, new ChatUserImpl(this)); 
        } catch(Exception e){
        	System.out.println(e);	
        }
        
    }
	
    public void actionPerformed(java.awt.event.ActionEvent evt) 
    {   Object source = evt.getSource();
        if (source == _connectButton) 
        {   
            if ( serverList.getSelectedIndex() == -1 ){
            	JOptionPane.showMessageDialog(this, "please select a chatroom", "Error Dialog", JOptionPane.ERROR_MESSAGE);	
            	return;
            } 		
            _connectButton.removeActionListener(this);
            _connectButton.setEnabled(false);
            _disconnectButton.addActionListener(this);
            _disconnectButton.setEnabled(true);
            inputField.addActionListener(this);
            
            chatroomName = (String)serverList.getSelectedValue();
            int code = (new Random()).nextInt(totalIcons-1);
            boolean success = false;
            try {   
           	
		System.out.println("chat room name: " + chatroomName);
            	chatroom = (ChatRoom)PortableRemoteObject.narrow(_initialContext.lookup(chatroomName), ChatRoom.class);
            	success = chatroom.connect(_nickname, code);
            } catch (Exception e) {   
            	System.out.println("ChatUserClient exception: " + e.getMessage()); 
            	e.printStackTrace(); 
            }             
            
            if (success) 
            {   
            	System.out.println("Connected...");
            	endChat = false;
            	frameTimer.start();
            }
            else
            {   System.out.println("Not connected: the selected nickname is in use. Please choose another nickname.");
            }
        } else if ( source == _disconnectButton ){
        	
            	_connectButton.addActionListener(this);
            	_connectButton.setEnabled(true);
            	_disconnectButton.removeActionListener(this);
            	_disconnectButton.setEnabled(false);
            	inputField.removeActionListener(this);
                    	
        	// clear everything
        	talkArea.setText("");
        	inputField.setText("");
        	
      		if (backImg == null) {
      			Graphics g = graphicImg.getGraphics();
      			g.setColor(Color.blue);
      			g.fillRect(0, 0, GRAPHIC_WIDTH, GRAPHIC_HEIGHT);
      			getGraphics().drawImage(graphicImg, GRAPHIC_LEFT, GRAPHIC_TOP, this);
      		} else {
      			getGraphics().drawImage(backImg, GRAPHIC_LEFT, GRAPHIC_TOP, this);
      		}        	        	
      		
      		users.clear();

            	if ( endChat ) {
            		return;	
            	}
			
		boolean success = false;
		try{
			success = chatroom.disconnect(_nickname);
		} catch (java.rmi.RemoteException e){
			System.out.println(e);	
		}
            	if (success){   
            		endChat = true;
            		System.out.println("Disonnected...");
            	} else{   
            		System.out.println("Not disconnected.");
            	}
            	
        }
        else if (source == inputField)
        {   
            String message = inputField.getText();
            try{
            	chatroom.sendMessage(message, _nickname);
            } catch(java.rmi.RemoteException e){
            	System.out.println(e);	
            }
        }
    } // end actionperformed
 
    public void startThread()
    {   _thread = new Thread(this);
        _thread.start();
    }

    public void run() 
    {   
    	
    }

    public void update(Graphics g){
    	paint(g);
    }
  
    public void paint(Graphics g){
    	super.paint(g);
    	if ( loaded ) {
    		g.drawImage(graphicImg, GRAPHIC_LEFT, GRAPHIC_TOP, this);
    		g.drawImage(serverListImg, SERVER_LIST_LEFT, SERVER_LIST_TOP, this);
    		serverList.repaint();
    	}
    }	

  //sgn func
  public int sgn(int x){
    if (x > 0) return 1;
    if (x < 0) return -1;
    return 0;
  }
    
  //everyone move one step
  public void moveOneStep(){
    int count = 0;
    int direction;
    
    for (Enumeration<UserInfo> e = users.elements(); e.hasMoreElements(); ){
    	UserInfo p = (UserInfo)e.nextElement();
    	
    	direction = sgn(p.dx - p.x);
    	if ( direction == 0 ) count ++;
        p.x += direction * ONE_STEP;
        direction = sgn(p.dy - p.y);
        if ( direction == 0 ) count ++;
        p.y += direction * ONE_STEP;
        
        if (java.lang.Math.abs(p.x - p.dx) <= ONE_STEP) p.x = p.dx;
        if (java.lang.Math.abs(p.y - p.dy) <= ONE_STEP) p.y = p.dy;    	        	
        
        if (p.x <= ICON_WIDTH/2) {p.x = ICON_WIDTH/2; p.dx = p.x; }
        if (p.x >= GRAPHIC_WIDTH - ICON_WIDTH/2) {p.x = GRAPHIC_WIDTH - ICON_WIDTH/2; p.dx = p.x;}
        if (p.y <= ICON_HEIGHT/2) {p.y = ICON_HEIGHT/2; p.dy = p.y; }
        if (p.y >= GRAPHIC_HEIGHT - ICON_HEIGHT/2) {p.y = GRAPHIC_HEIGHT - ICON_HEIGHT/2; p.dy = p.y;}        	
    }  	
    
    moveEnd = ( count == users.size() * 2 );
    System.out.println("count = " + count);
    System.out.println("size = " + users.size());
    System.out.println("messgeEnd = " + moveEnd);
  } // end of moveOneStep
  
  //timer action
  public void timer_actionPerformed(){
    if ( endChat ) return;
    if ( ! moveEnd ) moveOneStep();
    if ( moveEnd && sayEnd ) return;
    drawGraphicArea();
    getGraphics().drawImage(graphicImg, GRAPHIC_LEFT, GRAPHIC_TOP, this);
  }
  
  //mouse event
  public void mouseClick_performed(java.awt.event.MouseEvent event){
    if ( endChat ) return;	
//    if (endChat == true || myIdx == -1) return;
//	if (userInfo[myIdx].x < 0) return;
    if (event.getID() == event.MOUSE_PRESSED){
        int x = event.getX();
        int y = event.getY();
        if (x < GRAPHIC_LEFT || x >= GRAPHIC_LEFT + GRAPHIC_WIDTH || y < GRAPHIC_TOP || y > GRAPHIC_TOP + GRAPHIC_HEIGHT) return;
        
        moveEnd = false;
	UserInfo p = users.get(_nickname);
        p.dx = x - GRAPHIC_LEFT;
        p.dy = y - GRAPHIC_TOP;
        
        try{
        	chatroom.sendLocation(p.dx, p.dy, p.name);
        } catch(java.rmi.RemoteException e){
        	System.out.println(e);
        }
        //sendCmd(MsgType.MOVE, userInfo[myIdx].dx, userInfo[myIdx].dy);
    }
  }

  public void printUserList(){
  	Enumeration<String> usernames = users.keys();
  	while ( usernames.hasMoreElements() ){
  		System.out.println("user name: " + usernames.nextElement());	
  	}
  }
  
  //draw server list
  public void drawServerList(){
    Graphics g = serverListImg.getGraphics();    
    
    g.setColor(backColor);
    g.fillRect(0, 0, SERVER_LIST_WIDTH, SERVER_LIST_HEIGHT);

    g.setColor(Color.black);
    g.fillRoundRect(5, 5, SERVER_LIST_WIDTH-SHADOW_WIDTH, SERVER_LIST_HEIGHT-SHADOW_WIDTH, 30, 30);    
    g.setColor(new Color(190, 180,255));
    g.fillRoundRect(0, 0, SERVER_LIST_WIDTH-SHADOW_WIDTH, SERVER_LIST_HEIGHT-SHADOW_WIDTH, 30, 30);
    g.setColor(new Color(0, 0, 255));
    g.drawRoundRect(0, 0, SERVER_LIST_WIDTH-SHADOW_WIDTH, SERVER_LIST_HEIGHT-SHADOW_WIDTH, 30, 30);    
    
    g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), 20));
    g.setColor(Color.black);
    
    FontMetrics fntM = g.getFontMetrics();    
    String s = new String("Server List");
    int x = (SERVER_LIST_WIDTH - fntM.stringWidth(s))/2;
    g.drawString(s, x, 30);
    
    //update to screen
    getGraphics().drawImage(serverListImg, SERVER_LIST_LEFT, SERVER_LIST_TOP, this);
  }  
  
  //draw graphic area
  public synchronized void  drawGraphicArea(){
    Graphics g = graphicImg.getGraphics();
    FontMetrics fntM = g.getFontMetrics();

    if (backImg == null) {
      g.setColor(Color.blue);
      g.fillRect(0, 0, GRAPHIC_WIDTH, GRAPHIC_HEIGHT);
    }
      else {
      	g.drawImage(backImg, 0, 0, this);
    }
    // if (myIdx == -1) return ;
	
    UserInfo p;
    g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), 12));

    int count = 0;    
    
    for (Enumeration<UserInfo> e = users.elements(); e.hasMoreElements(); ){
    	// draw icon
    	p = e.nextElement();
    	g.drawImage(icons[p.code], p.x - ICON_WIDTH/2, p.y - ICON_HEIGHT/2, this);
    	
    	// draw name
    	if (p.name.equals(_nickname)) g.setColor(Color.red);
    	else g.setColor(Color.yellow);
        int x = (p.x - fntM.stringWidth(p.name)/2);
        int y = (p.y + ICON_HEIGHT/2);
        g.fillRoundRect(x-2, y, fntM.stringWidth(p.name)+4, fntM.getHeight(), 10, 10);
        g.setColor(Color.black);
        g.drawRoundRect(x-2, y, fntM.stringWidth(p.name)+4, fntM.getHeight(), 10, 10);
        g.drawString(p.name, x, y + fntM.getAscent());    	
        
        // draw say
        if (p.sayTime <= 0) {
        	count ++;
        	continue;
        }
        String saySplit[] = new String[100];
        int c = 0;
        int st = 0, ed = 1;
        while (ed <= p.say.length()){
            String s = p.say.substring(st, ed);
            if (fntM.stringWidth(s) > SAY_WIDTH){
                saySplit[c] = p.say.substring(st, ed - 1);
                c++;
                st = ed - 1;
            }
            ed++;
        }
        saySplit[c] = p.say.substring(st, ed - 1);
        c++;
        x = p.x + ICON_WIDTH/2 + 5;
        y = p.y - ICON_HEIGHT/2 + 5;
        int w = ((c > 1)? SAY_WIDTH : fntM.stringWidth(saySplit[0])) + 5;
        int h = fntM.getHeight() * c + 5;

        //draw say arrow
        g.setColor(Color.green);
        if (x + w >= GRAPHIC_WIDTH) {
            x = p.x - ICON_WIDTH/2 - w - 5;
            Polygon polygon = new Polygon();
            polygon.addPoint(p.x - ICON_WIDTH/2, p.y - 5);
            polygon.addPoint(p.x - ICON_WIDTH/2 - 8, p.y - 10);
            polygon.addPoint(p.x - ICON_WIDTH/2 - 8, p.y - 4);
            //p.addPoint(x + ICON_WIDTH/2, y - 5);
            g.fillPolygon(polygon);
        }
          else
        {
            Polygon polygon = new Polygon();
            polygon.addPoint(p.x + ICON_WIDTH/2, p.y - 5);
            polygon.addPoint(p.x + ICON_WIDTH/2 + 8, p.y - 10);
            polygon.addPoint(p.x + ICON_WIDTH/2 + 8, p.y - 4);
            //p.addPoint(x + ICON_WIDTH/2, y - 5);
            g.fillPolygon(polygon);
        }

        g.fillRoundRect(x, y, w, h , 10, 10);
        g.setColor(Color.black);
        //g.drawRoundRect(x, y, w, h, 10, 10);
        for (int j = 0; j < c; j++){
            g.drawString(saySplit[j], x + 2, y + 2 + j * fntM.getHeight() + fntM.getAscent());
        }
        p.sayTime--;        
    } // end of for
    sayEnd = ( count == users.size() );
    
    update(getGraphics());
  }	// end of drawGraphicArea


  class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
       Object obj = e.getSource();
	if (obj == frameTimer) {timer_actionPerformed(); return;}       
       
    }
  }
  // ****************************************
  // a load image thread class in applet class
  // ****************************************
  class LoadImageThread extends Thread{
    public void load(){
      this.start();
    }

    public void run(){
      loadImages();
      loaded = true;
      try{
	sleep(1000);
      } catch(java.lang.InterruptedException e){}
      if (backImg == null) {
      	Graphics g = graphicImg.getGraphics();
      	g.setColor(Color.blue);
      	g.fillRect(0, 0, GRAPHIC_WIDTH, GRAPHIC_HEIGHT);
      	(myFrame.getGraphics()).drawImage(graphicImg, GRAPHIC_LEFT, GRAPHIC_TOP, myFrame);
      } else {
      	myFrame.getGraphics().drawImage(backImg, GRAPHIC_LEFT, GRAPHIC_TOP, myFrame);
      }        	
    }

    //load all images
    public void loadImages(){

      	Graphics g = graphicImg.getGraphics();
      	g.setColor(Color.blue);
      	g.fillRect(0, 0, GRAPHIC_WIDTH, GRAPHIC_HEIGHT);
      	g.setColor(Color.yellow);
      	g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), 30));
      	g.drawString("loading, please wait ......", 30, 50);
      	(myFrame.getGraphics()).drawImage(graphicImg, GRAPHIC_LEFT, GRAPHIC_TOP, myFrame);

      	MediaTracker m = new MediaTracker(myFrame);
	for (int i = 0; i < totalIcons; i++){
		icons[i] = Toolkit.getDefaultToolkit().getImage(i + ".gif");
            	m.addImage(icons[i], 0);		
	}          
	
        try{
          m.waitForAll();
        }catch (InterruptedException e){System.out.println("can't read image from file");}	
    
    }

  } // end of LoadImage class


  class CustomCellRenderer extends JLabel implements ListCellRenderer {
    public Component getListCellRendererComponent
     (JList list, Object value, int index, 
      boolean isSelected,boolean cellHasFocus) {

         String s = value.toString();
         setText(s);
         //setIcon((s.length() > 10) ? longIcon : shortIcon);
   	 if (isSelected) {
               //setBackground(list.getSelectionBackground());
	       //setForeground(list.getSelectionForeground());
	       setForeground(new Color(0,0,255));
	 } else {
	       //setBackground(list.getBackground());
	       //setForeground(list.getForeground());
	       setForeground(new Color(0,255,255));
	 }
/*	 
	 if ( index == enterListIndex ){
System.out.println("****************");	 	
	 	setForeground(new Color(0,0,180));	
	 }
	 if ( index == exitListIndex ){
System.out.println("---------------");	 		 	
	 	setForeground(new Color(0,255,255));
	 }
*/	 
	 setEnabled(list.isEnabled());
	 setFont(list.getFont());
         return this;
      }
    } 
    	
  /**
  * @param args the command line arguments
  */
  public static void main (String args[]) 
  {     
    	// args[0] is user nickname
	if (args.length != 1)
	{   System.out.println("Usage: ChatClient nickname");
	    System.exit(0);
	}
	Client clientFrame = new Client(args[0]);  	
  }

}
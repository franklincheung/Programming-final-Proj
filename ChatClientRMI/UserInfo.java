package ChatClientRMI;

class MyExtraInfo{
    static int      GRADE1 = 100;
    static int      GRADE2 = 50;	
    static int      GRADE3 = 20;
    static int      GRADE4 = 10;

    String          id;
    int             grade;
    int             party;
}

public class UserInfo{
    public String          name;         //id
    //public int             no;           //id's index in server
    public int             code;	 // icon index, i.e. avartar code
    public int             x, y;         //now  x,y
    public int             dx, dy;       //dest x,y
    public String          say;
    public int             sayTime;
}

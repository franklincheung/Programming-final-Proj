package ChatClientRMI;


/**
* ChatClientRMI/ChatUserHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ChatUser.idl
* Sunday, December 12, 2010 3:30:45 PM EST
*/

abstract public class ChatUserHelper
{
  private static String  _id = "IDL:ChatClientRMI/ChatUser:1.0";

  public static void insert (org.omg.CORBA.Any a, ChatClientRMI.ChatUser that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static ChatClientRMI.ChatUser extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (ChatClientRMI.ChatUserHelper.id (), "ChatUser");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static ChatClientRMI.ChatUser read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_ChatUserStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, ChatClientRMI.ChatUser value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static ChatClientRMI.ChatUser narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof ChatClientRMI.ChatUser)
      return (ChatClientRMI.ChatUser)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      ChatClientRMI._ChatUserStub stub = new ChatClientRMI._ChatUserStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static ChatClientRMI.ChatUser unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof ChatClientRMI.ChatUser)
      return (ChatClientRMI.ChatUser)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      ChatClientRMI._ChatUserStub stub = new ChatClientRMI._ChatUserStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
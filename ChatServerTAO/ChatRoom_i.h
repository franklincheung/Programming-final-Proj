// -*- C++ -*-
//
// $Id$

// ****  Code generated by the The ACE ORB (TAO) IDL Compiler v1.6a_p8 ****
// TAO and the TAO IDL Compiler have been developed by:
//       Center for Distributed Object Computing
//       Washington University
//       St. Louis, MO
//       USA
//       http://www.cs.wustl.edu/~schmidt/doc-center.html
// and
//       Distributed Object Computing Laboratory
//       University of California at Irvine
//       Irvine, CA
//       USA
//       http://doc.ece.uci.edu/
// and
//       Institute for Software Integrated Systems
//       Vanderbilt University
//       Nashville, TN
//       USA
//       http://www.isis.vanderbilt.edu/
//
// Information about TAO is available at:
//     http://www.cs.wustl.edu/~schmidt/TAO.html

// TAO_IDL - Generated from 
// be\be_codegen.cpp:1135

#ifndef CHATROOMI_H_
#define CHATROOMI_H_

#include "ChatRoomS.h"

#if !defined (ACE_LACKS_PRAGMA_ONCE)
#pragma once
#endif /* ACE_LACKS_PRAGMA_ONCE */

class  ChatRoom_i
  : public virtual POA_ChatRoom
{
public:
  // Constructor 
  ChatRoom_i (void);
  
  // Destructor 
  virtual ~ChatRoom_i (void);
  
  virtual
  ::CORBA::Boolean connect (
      const char * nickname,
      ::CORBA::Long avatarCode);
  
  virtual
  ::CORBA::Boolean disconnect (
      const char * nickname);
  
  virtual
  void sendMessage (
      char *& message,
      const char * fromNickname);
  
  virtual
  ::CORBA::Boolean sendMessageToParty (
      const char * message,
      const char * fromNickname,
      const char * toNickname);
  
  virtual
  void sendLocation (
      ::CORBA::Long x,
      ::CORBA::Long y,
      const char * fromNickname);
  
  virtual
  void sendUserList (
      const char * toNickname);
};


#endif /* CHATROOMI_H_  */

/**
 * Copyright (C) 2011 Inqwell Ltd
 *
 * You may distribute under the terms of the Artistic License, as specified in
 * the README file.
 */

/*
 * $Archive:  $
 * $Author: sanderst $
 * $Revision: 1.2 $
 * $Date: 2011-04-07 22:18:21 $
 */
package com.inqwell.any.jms;

import com.inqwell.any.AbstractFunc;
import com.inqwell.any.Any;
import com.inqwell.any.AnyException;
import com.inqwell.any.EvalExpr;
import com.inqwell.any.Transaction;

/**
 * Create a temporary queue from the given session
 * @author tom
 *
 */
public class MqCreateTemporaryQueue extends    AbstractFunc
                                    implements Cloneable
{
  private Any   sess_;

  public MqCreateTemporaryQueue(Any sess)
  {
    sess_     = sess;
  }

  public Any exec(Any a) throws AnyException
  {
    Transaction t = getTransaction();
    
    SessionI sess = (SessionI)EvalExpr.evalFunc(t,
                                                a,
                                                sess_,
                                                SessionI.class);

    if (sess == null)
      nullOperand(sess_);
    
    return sess.createTemporaryQueue();
  }
  
  public Object clone () throws CloneNotSupportedException
  {
    MqCreateTemporaryQueue n = (MqCreateTemporaryQueue)super.clone();
    
    n.sess_   = sess_.cloneAny();
        
    return n;
  }
}

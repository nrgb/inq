/**
 * Copyright (C) 2011 Inqwell Ltd
 *
 * You may distribute under the terms of the Artistic License, as specified in
 * the README file.
 */

/*
 * $Archive: /src/com/inqwell/any/ToUpper.java $
 * $Author: sanderst $
 * $Revision: 1.2 $
 * $Date: 2011-04-07 22:18:19 $
 */
package com.inqwell.any;

/**
 * Return the upper case string or char of the specified node.  Operand
 * must resolve to an StringI or an CharI or Inq null is returned.
 * <p>
 * @author $Author: sanderst $
 * @version $Revision: 1.2 $
 */
public class ToUpper extends    AbstractFunc
                     implements Cloneable
{
	
	private Any any_;
	
	public ToUpper(Any any)
	{
		any_ = any;
	}
	
	public Any exec(Any a) throws AnyException
	{
		Any any = EvalExpr.evalFunc(getTransaction(),
																a,
																any_);
    
    if (any == null)
      nullOperand(any_);
    
    Any ret = AnyNull.instance();
    
    if (any instanceof StringI)
    {
      AnyString str = new AnyString(any.toString().toUpperCase());
      ret = str;
    }
    else if (any instanceof CharI)
    {
      CharI chr = (CharI)any;
      ret = new AnyChar(Character.toUpperCase(chr.getValue()));
    }

		return ret;
	}
	
  public Object clone () throws CloneNotSupportedException
  {
		ToUpper t = (ToUpper)super.clone();
		t.any_ = AbstractAny.cloneOrNull(any_);
		return t;
  }

  public Iter createIterator ()
  {
  	Array a = AbstractComposite.array();
  	if (any_ != null)
      a.add(any_);
  	return a.createIterator();
  }
}

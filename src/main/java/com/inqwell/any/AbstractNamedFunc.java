/**
 * Copyright (C) 2011 Inqwell Ltd
 *
 * You may distribute under the terms of the Artistic License, as specified in
 * the README file.
 */

package com.inqwell.any;

public abstract class AbstractNamedFunc extends    AbstractFunc
                                        implements NamedFunc
{
  private Any fqName_;
  
  public void setFQName(Any fqName)
  {
    fqName_ = fqName;
  }

  public Any getFQName()
  {
    return fqName_;
  }
  
  public Any exec(Any a) throws AnyException
  {
    return fqName_;
  }
  
  public boolean equals(Any o)
  {
    if (this == o)
      return true;
      
    if (AnyAlwaysEquals.isAlwaysEquals(o))
      return true;

    if (o instanceof NamedFunc)
    {
      NamedFunc nf = (NamedFunc)o;
      return nf.getFQName().equals(fqName_);
    }
    else
    {
      return false;
    }
  }
  
  public String toString()
  {
    return fqName_.toString();
  }
}
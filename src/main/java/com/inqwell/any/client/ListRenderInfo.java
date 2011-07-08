/**
 * Copyright (C) 2011 Inqwell Ltd
 *
 * You may distribute under the terms of the Artistic License, as specified in
 * the README file.
 */

/*
 * $Archive: /src/com/inqwell/any/client/ListRenderInfo.java $
 * $Author: sanderst $
 * $Revision: 1.4 $
 * $Date: 2011-05-07 21:58:30 $
 */
package com.inqwell.any.client;

import com.inqwell.any.*;

/**
 * Support for the rendering of lists whose visible (or external)
 * representation may be different from the internal value.
 *
 * @author $Author: sanderst $
 * @version $Revision: 1.4 $
 */ 
public class ListRenderInfo extends    AbstractAny
                            implements RenderInfo
{
	public static Any internal__ = new ConstString ("internal");
	public static Any external__ = new ConstString ("external");
	public static Any enum__     = new ConstString ("enum__");
	
	private RenderInfo internal_;
	private RenderInfo external_;
	
	// We always return a value which is a map containing the
	// internal and external values, for higher level classes
	// to use as required.  We build the values in here.
	private Map listData_ = new ListItemMap();
	
	private Map nodeSpecs_ = AbstractComposite.simpleMap();
	
	/**
	 * Operate a list where the internal and external representations
	 * are the same.
	 */
	public ListRenderInfo(RenderInfo internal)
	{
		this(internal, null);
	}
	
	/**
	 * Operate a list with distinct internal and external
	 * representations.
	 */
	public ListRenderInfo(RenderInfo internal, RenderInfo external)
	{
		internal_ = internal;
		external_ = external;
	}
	
	/**
	 * Operate a list generated by the Descriptor/field combination.
	 * Throws an IllegalArgumentException if this is not an
	 * enumerated value.
	 */
	public ListRenderInfo(Descriptor d, Any field)
	{
		// By default we assume we are rendering an Enum.  The surrogate
		// RenderInfos are used with a root node of the desired
		// enumeration element so be can build standard expressions
		// for these.
		if (!d.isEnum(field))
			throw new IllegalArgumentException("Not an enum: " +
																				 d.getFQName() +
																				 "." +
																				 field);
		initDefault(d, field);
		try
		{
			setField(field.toString());
			setFQName(d.getFQName().toString());
      setFormat(d.getFormat(field));
      setWidth(d.getWidth(field));
		}
		catch(AnyException e)
		{
			throw new RuntimeContainedException(e);
		}
	}
	
	/**
	 * To call this method (and implicitly <code>setField</code>
	 * before it) is to imply that we are rendering an Enum
	 * field of a BOT.  In this case we may not have explicitly
	 * provided the surrogate <code>RenderInfo</code> objects
	 * but instead these may have been created as defaults.
	 */
	public void setFQName(String fQName) throws AnyException
	{
		// Make use of the surrogate RenderInfo (should be
		// AnyRenderInfo in this case) to resolve the descriptor
		// for us.  The surrogate RenderInfos should already have
		// their expressions set up (see initDefault()) so calling
		// this won't affect that
		internal_.setFQName(fQName);
	}
	
	
	/**
	 * Establish the field to be used to assist with rendering.
	 * Affects the internal object.
	 */
	public void setField(String field)
	{
		internal_.setField(field);
	}
	
	/**
	 * Set whether data being rendered is also editable.  Affects
	 * the internal object.
	 */
	public void setEditable(boolean editable)
	{
		internal_.setEditable(editable);
	}
	
	public void setEditingList(AnyListModel list)
	{
		internal_.setEditingList(list);
	}
	
	/**
	 * Return editable status (internal object).
	 */
	public boolean isEditable()
	{
		return internal_.isEditable();
	}
	
	public boolean isBuildable()
	{
		return internal_.isBuildable();
	}
	
	public Any buildData(Map root) throws AnyException
	{
		return internal_.buildData(root);
	}
	
	public boolean isEnum()
	{
		return internal_.isEnum();
	}
	
	/**
	 * Provide a data leaf if rendering input data rather than
	 * output data.  Affects the internal object.
	 */
	public void setData(Any data)
	{
		internal_.setData(data);
	}
	
	public void setDataNode(Any dataNode)
	{
		// actually, not called
	}
	
	public void setResponsibleData(Locate data)
	{
		internal_.setResponsibleData(data);
	}
	
	public void setAlwaysEvaluate(boolean b)
	{
		internal_.setAlwaysEvaluate(b);
		if (external_ != null)
		 external_.setAlwaysEvaluate(b);
  }

	/**
	 * Provide a label for the rendered information if no descriptor is
	 * given or to override same.  Affects the internal object.
	 */
	public void setLabel(String label)
	{
		internal_.setLabel(label);
	}

	public void setWidth(int width)
	{
		if (external_ != null)
			external_.setWidth(width);
		else
			internal_.setWidth(width);
	}

	/**
	 * Provide a format pattern for the rendered information if no
	 * descriptor is given or to override same.  Not relevant for
	 * this implementation but affects the internal object.
	 */
	public void setFormat(String f)
	{
		if (external_ != null)
      external_.setFormat(f);
		else
      internal_.setFormat(f);
	}

  /**
   * Return rendering label
   */	
	public String getLabel()
	{
		if (external_ != null)
			return external_.getLabel();
		else
			return internal_.getLabel();
	}
	
  public String getDefaultLabel()
  {
    if (external_ != null)
      return external_.getDefaultLabel();
    else
      return internal_.getDefaultLabel();
  }

  public AnyListModel getEditingList()
	{
		return internal_.getEditingList();
	}

  /**
   * Return rendering width
   */	
	public int getWidth()
	{
		if (external_ != null)
			return external_.getWidth();
		else
			return internal_.getWidth();
	}

	public Any resolveDataNode(Any root, boolean force) throws AnyException
	{
    return resolveDataNode(root, force, true);
  }
  
  /**
   * Request the data to be rendered.  An expression may be evaluated
   * from the given root.  Caching the result is supported via
   * the <code>force</code> parameter.  In this case a <code>Map</code>
   * is returned with two entries.
   */
	public Any resolveDataNode(Any root, boolean force, boolean build) throws AnyException
	{
		synchronized(Globals.process__)
		{
			listData_.empty();
			
			listData_.add(internal__,
										internal_.resolveDataNode(root,
																							force,
                                              build));
			
			if (external_ != null)
			{
				listData_.add(external__,
											external_.resolveDataNode(root,
																								force,
                                                false));
			}
			else
			{
				listData_.add(external__, null);
			}
				
			return listData_.shallowCopy();
		}
	}
	
	public Any resolveDataNode(Any         root,
                             boolean     force,
                             boolean     build,
                             Transaction t) throws AnyException
  {
    // Not called!
    return null;
  }
  
	public Any resolveResponsibleData(Any root) throws AnyException
	{
		return internal_.resolveResponsibleData(root);
	}
	
	public AnyFormat getFormat(Any a)
	{
		if (external_ != null)
			return external_.getFormat(a);
		else
			return internal_.getFormat(a);
	}

	public String getFormatString()
  {
		if (external_ != null)
			return external_.getFormatString();
		else
			return internal_.getFormatString();
  }
  
  /*
  public void setStyle(AnyAttributeSet style)
  {
		if (external_ != null)
      external_.setStyle(style);
    else
      internal_.setStyle(style);
  }

  public AnyAttributeSet getStyle()
  {
		if (external_ != null)
			return external_.getStyle();
		else
			return internal_.getStyle();
  }
  */

	public Any getDataNode()
	{
		if (external_ != null)
		  return external_.getDataNode();
		else
		  return internal_.getDataNode();
	}
		
	public Any getValueExpression()
	{
    // Always use the internal expression in the list context
    return internal_.getValueExpression();
	}
	
  public NodeSpecification getRenderPath()
  {
    return internal_.getRenderPath();
  }
  
  public void setRenderPath(NodeSpecification path)
  {
    internal_.setRenderPath(path);
  }
  
	public Descriptor getDescriptor()
	{
		return internal_.getDescriptor();
	}
	
	public Any getField()
	{
		return internal_.getField();
	}

	// Called from BML
	public void resolveNodeSpecs(Any contextNode)
	{
		resolveNodeSpecs(nodeSpecs_, contextNode);
	}
	
	public void resolveNodeSpecs(Map nodeSpecs, Any contextNode)
	{
		internal_.resolveNodeSpecs(nodeSpecs, contextNode);
		if (external_ != null)
			external_.resolveNodeSpecs(nodeSpecs, contextNode);
	}

  public boolean isDispatching(Any field)
  {
    boolean ret = internal_.isDispatching(field);
		if (external_ != null)
      ret = ret || external_.isDispatching(field);
    
    return ret;
  }
  
  /**
   * Support for events.  Return the node specifications that this
   * object would require re-evaluation for from node events.
   * The map contains the node specifications as keys and the field
   * set as values.
   */
	public Map getNodeSpecs()
	{
		if (external_ != null)
			return external_.getNodeSpecs();
		else
			return internal_.getNodeSpecs();
	}
	
  public Object clone () throws CloneNotSupportedException
  {
  	
  	ListRenderInfo lri = (ListRenderInfo)super.clone();
  	
  	lri.internal_ = (RenderInfo)internal_.cloneAny();
  	lri.external_ = (RenderInfo)AbstractAny.cloneOrNull(external_);

  	if (nodeSpecs_ != null)
	  	lri.nodeSpecs_ = nodeSpecs_.shallowCopy();

    lri.listData_ = listData_.shallowCopy();
    
    return lri;
  }
  
	private void initDefault(Descriptor d, Any field)
	{
		internal_ = new AnyRenderInfo(new LocateNode(internal__));
		external_ = new AnyRenderInfo(new LocateNode(external__));
		external_.setLabel(d.getTitle(field).toString());
	}

	/**
	 * Represents list items with specific equality semantics.
	 * Items in Inq gui lists comprise an internal and an
	 * external value.  The internal value is the programatic
	 * value and the external value is what is rendered to
	 * the list cell.
	 * <p>
	 * The list model returns such a map when items are requested
	 * of it, and the renderer extracts the external item. However,
	 * item selection can be made using the internal value and
	 * optimisations in the JComboBox for Java 1.4 require the
	 * maps returned by the list model and the internal item
	 * contained therein to compare true for equals.
	 */
	public static class ListItemMap extends    SimpleMap
													        implements Map,
													        Cloneable
	{
		public ListItemMap() {}
		
	  public Object clone() throws CloneNotSupportedException
	  {
			return super.clone();
		}
		
		public int hashCode()
		{
			return get(internal__).hashCode();
		}
		
		public boolean equals(Any a)
		{
			Any i = get(internal__);
			if (a instanceof Map)
			{
        a = ((Map)a).get(internal__);
			}
			
			if (i != null)
        return i.equals(a);
        
			return a == null;
		}
    
    public String toString()
    {
      Any a;
      if ((a = getIfContains(external__)) != null)
        return a.toString();
      
      a = get(internal__);
			return (a != null) ? a.toString() : "";
    }
	}
}
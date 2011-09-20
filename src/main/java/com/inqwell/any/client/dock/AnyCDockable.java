/**
 * Copyright (C) 2011 Inqwell Ltd
 *
 * You may distribute under the terms of the Artistic License, as specified in
 * the README file.
 */

package com.inqwell.any.client.dock;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.DefaultCDockable;

import com.inqwell.any.AbstractComposite;
import com.inqwell.any.AbstractValue;
import com.inqwell.any.Any;
import com.inqwell.any.AnyException;
import com.inqwell.any.AnyNull;
import com.inqwell.any.AnyRuntimeException;
import com.inqwell.any.Event;
import com.inqwell.any.EventListener;
import com.inqwell.any.IntI;
import com.inqwell.any.Iter;
import com.inqwell.any.Set;
import com.inqwell.any.beans.WindowF;
import com.inqwell.any.client.AnyComponent;
import com.inqwell.any.client.AnyFrame;
import com.inqwell.any.client.AnyView;
import com.inqwell.any.client.AnyWindow;
import com.inqwell.any.client.RenderInfo;
import com.inqwell.any.client.swing.SwingInvoker;

public abstract class AnyCDockable extends    AnyView
                                   implements WindowF
{
  private CDockable d_;
  private int       defaultCloseOperation_ = AnyWindow.HIDE_ON_CLOSE.getValue();
  
  //private NotifyWindow notifyWindow_;
  
  private static Set   dockableProperties__;

  protected static Any   title__   = AbstractValue.flyweightString("title");
  
  private   static Any   actions__ = AbstractValue.flyweightString("actions");

  static
  {
    dockableProperties__ = AbstractComposite.set();
    dockableProperties__.add(title__);
    dockableProperties__.add(AnyFrame.icon__);
    dockableProperties__.add(AnyComponent.contextNode__);
    dockableProperties__.add(AnyWindow.defaultButton__);
    dockableProperties__.add(AnyWindow.defaultableButtons__);
    dockableProperties__.add(actions__);
  }

  @Override
  protected void componentProcessEvent(Event e) throws AnyException
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected Object getAttachee(Any eventType)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Object getPropertyOwner(Any property)
  {
    if (dockableProperties__.contains(property))
      return this;

    return d_;
  }

  @Override
  protected RenderInfo getRenderInfo()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object getAddIn()
  {
    return getDefaultCDockable().getContentPane();
  }

  @Override
  public Object getAddee()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public JComponent getBorderee()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Container getComponent()
  {
    throw new AnyRuntimeException("No component access for dockables");
  }

  @Override
  public String getLabel()
  {
    throw new UnsupportedOperationException();    
  }

  @Override
  public void requestFocus()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setEnabled(Any enabled)
  {
    throw new UnsupportedOperationException();    
  }

  @Override
  public void setRenderInfo(RenderInfo r)
  {
    throw new UnsupportedOperationException();    
  }

  /* (non-Javadoc)
   * @see com.inqwell.any.beans.WindowF#getGlassPane()
   */
  @Override
  public Component getGlassPane()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.inqwell.any.beans.WindowF#getParentFrame()
   */
  @Override
  public WindowF getParentFrame()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.inqwell.any.beans.WindowF#isBypassModality()
   */
  @Override
  public boolean isBypassModality()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see com.inqwell.any.beans.WindowF#toFront()
   */
  @Override
  public void toFront()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Object getObject()
  {
    return d_;
  }

  public void removeEventListener (EventListener l)
  {
    // If we are removed from our parent then dispose of the
    // dockable.
    super.removeEventListener(l);

    if (l == getParentAny())
    {
      dispose(true);
    }
  }

  @Override
  public void setObject(Object o)
  {
    if (!(o instanceof CDockable))
      throw new AnyRuntimeException("Not a CDockable");
    
    d_ = (CDockable)o;
    d_.addVetoClosingListener(new CloseMonitor());
    getDefaultCDockable().setCloseable(true);
    
    // TODO: Events set up?
  }

  public void show(final boolean withResize, AnyComponent relativeTo)
  {
    SwingInvoker ss = new SwingInvoker()
    {
      protected void doSwing()
      {
        d_.setVisible(true);
        getDefaultCDockable().toFront();
        if (withResize)
        {
          Dimension d = getDefaultCDockable().getContentPane().getPreferredSize();
          getDefaultCDockable().setResizeRequest(d, true);
          //((JComponent)getDefaultCDockable().getContentPane()).revalidate();
        }
      }
    };
    
    ss.maybeAsync(false);
  }
  
  public void hide()
  {
    SwingInvoker ss = new SwingInvoker()
    {
      protected void doSwing()
      {
        d_.setVisible(false);
      }
    };
    
    ss.maybeAsync(false);
  }
  
  public void setTitle(String text)
  {
    getDefaultCDockable().setTitleText(text);
  }

  /**
   * Gets the text that is shown as title.
   * 
   * @return the title
   */
  public String getTitle()
  {
    return d_.intern().getTitleText();
  }

  public void setIcon(Icon i)
  {
    getDefaultCDockable().setTitleIcon(i);
  }

  public Icon getIcon()
  {
    return getDefaultCDockable().getTitleIcon();
  }
  
  public void setActions(Any actions)
  {
    DefaultCDockable d = getDefaultCDockable();
    
    // Clear existing actions
    int i = d.getActionCount();
    while(i > 0)
      d.removeAction(--i);
    
    if (AnyNull.isNull(actions))
      return;
    
    // Add given actions. If layout was used to compose the actions then
    // find them in the AnyRootActionContainer
    if (actions instanceof AnyRootActionContainer)
    {
      ArrayList<CAction> cactions = ((AnyRootActionContainer)actions).getActions();
      for (CAction action : cactions)
      {
        d.addAction(action);
      }
    }
    else
    {
      Iter ii = actions.createIterator();
      while (ii.hasNext())
      {
        AnyCAction action = (AnyCAction)ii.next();
        d.addAction(action.getCAction());
      }
    }
  }

  public void setDefaultCloseOperation(IntI closeOperation)
  {
    int i = closeOperation.getValue();
    
    if (i == AnyWindow.HIDE_ON_CLOSE.getValue() ||
        i == AnyWindow.DISPOSE_ON_CLOSE.getValue() ||
        i == AnyWindow.DO_NOTHING_ON_CLOSE.getValue())
    {
      defaultCloseOperation_ = i;
      if (i == AnyWindow.DO_NOTHING_ON_CLOSE.getValue())
      {
        getDefaultCDockable().setCloseable(false);
      }
      else
      {
        getDefaultCDockable().setCloseable(true);
      }
    }
    else
      throw new IllegalArgumentException("Invalid close operation");
  }
  
  public void setDefaultableButtons(Set dbs)
  {
    
  }
  
  public void setDefaultButton(AnyComponent defaultButton)
  {
    
  }
  
  protected CDockable getCDockable()
  {
    if (d_ == null)
      throw new AnyRuntimeException("Dockable must be in the node space");

    return d_;
  }
  
  private DefaultCDockable getDefaultCDockable()
  {
    CDockable d = getCDockable();
    
    if (d instanceof DefaultCDockable)
      return (DefaultCDockable)d;
    
    throw new AnyRuntimeException("Not a DefaultCDockable");
  }
  
  private class CloseMonitor implements CVetoClosingListener
  {

    @Override
    public void closed(CVetoClosingEvent event)
    {
      // TODO One way or another we should be orphaned by now
    }

    @Override
    public void closing(CVetoClosingEvent event)
    {
      if (defaultCloseOperation_ == AnyWindow.DO_NOTHING_ON_CLOSE.getValue())
      {
        if (!event.isCanceled() && event.isCancelable())
          event.cancel();
      }
      else if (defaultCloseOperation_ == AnyWindow.HIDE_ON_CLOSE.getValue())
      {
        if (!event.isCanceled() && event.isCancelable())
        {
          //event.cancel();
          //d_.setVisible(false);
        }
      }
      else
      {
        // TODO - "dispose"
        //   1 remove from CControl
        //   2 should orphan the node?
      }
    }
  }

  // TODO Make up a window event notifier for dockables
//  protected static class NotifyWindow
//  {
//    ArrayList listeners_ = new ArrayList();
//
//    public void addWindowListener(WindowListener l)
//    {
//      listeners_.add(l);
//    }
//
//    public void removeWindowListener(WindowListener l)
//    {
//      int i = -1;
//      if ((i = listeners_.indexOf(l)) >= 0)
//        listeners_.remove(i);
//    }
//
//    public void fireWindowClosing(WindowEvent e)
//    {
//      Iterator i = listeners_.iterator();
//      while (i.hasNext())
//      {
//        WindowListener w = (WindowListener)i.next();
//        w.windowClosing(e);
//      }
//    }
//
//    public void fireWindowClosed(WindowEvent e)
//    {
//      Iterator i = listeners_.iterator();
//      while (i.hasNext())
//      {
//        WindowListener w = (WindowListener)i.next();
//        w.windowClosed(e);
//      }
//    }
//  }
}
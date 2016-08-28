/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.upgrade.animated.util.UIUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public class AnimatedCanvas extends Canvas
{
  private static final int DEFAULT_TIMER_INTERVAL = 10;

  private final Runnable runnable = new Runnable()
  {
    public void run()
    {
      doRun();
    }
  };

  private final List<Animator> animators = new ArrayList<Animator>();

  private int timerInterval;

  private Point shellMoveStart;

  public AnimatedCanvas(Composite parent, int style)
  {
    this(parent, style, DEFAULT_TIMER_INTERVAL);
  }

  public AnimatedCanvas(Composite parent, int style, int timerInterval)
  {
    super(parent, style | SWT.DOUBLE_BUFFERED);

    Display display = getDisplay();
    setBackground(display.getSystemColor(SWT.COLOR_WHITE));

    addFocusListener(new FocusListener()
    {
      public void focusGained(FocusEvent e)
      {
        redraw();
      }

      public void focusLost(FocusEvent e)
      {
        redraw();
      }
    });

    addPaintListener(new PaintListener()
    {
      public void paintControl(PaintEvent event)
      {
        doPaint(event.gc);
      }
    });

    addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent e)
      {
        if (!onKeyPressed(e))
        {
          super.keyPressed(e);
        }
      }
    });

    addMouseTrackListener(new MouseTrackAdapter()
    {
      @Override
      public void mouseExit(MouseEvent e)
      {
        onMouseMove(Integer.MIN_VALUE, Integer.MIN_VALUE);
      }
    });

    addMouseMoveListener(new MouseMoveListener()
    {
      public void mouseMove(MouseEvent e)
      {
        onMouseMove(e.x, e.y);
      }
    });

    addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseDown(MouseEvent e)
      {
        if (e.button == 1)
        {
          onMouseDown(e.x, e.y);
        }
      }

      @Override
      public void mouseUp(MouseEvent e)
      {
        if (shellMoveStart != null)
        {
          shellMoveStart = null;
        }
      }
    });

    display.timerExec(timerInterval, runnable);
  }

  public final Animator[] getAnimators()
  {
    synchronized (animators)
    {
      return animators.toArray(new Animator[animators.size()]);
    }
  }

  public final void addAnimator(Animator animator)
  {
    synchronized (animators)
    {
      animator.canvas = this;
      animator.init();
      animators.add(animator);
    }
  }

  public final void removeAnimator(Animator animator)
  {
    synchronized (animators)
    {
      animators.remove(animator);
      animator.dispose();
      animator.canvas = null;
    }
  }

  public final int getTimerInterval()
  {
    return timerInterval;
  }

  public final void setTimerInterval(int timerInterval)
  {
    this.timerInterval = timerInterval;
  }

  public void cover(GC gc, int alpha)
  {
    gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
    gc.setAlpha(alpha);
    gc.fillRectangle(getBounds());
    gc.setAlpha(255);
  }

  @Override
  public synchronized void dispose()
  {
    getDisplay().timerExec(-1, runnable);

    for (Animator animator : getAnimators())
    {
      animator.dispose();
    }

    super.dispose();
  }

  protected boolean onKeyPressed(KeyEvent e)
  {
    Animator[] animators = getAnimators();
    for (int i = animators.length - 1; i >= 0; --i)
    {
      Animator animator = animators[i];
      if (animator.onKeyPressed(e))
      {
        return true;
      }
    }

    return false;
  }

  protected void onMouseMove(int x, int y)
  {
    if (shellMoveStart != null)
    {
      Shell shell = getShell();
      Point location = shell.getLocation();
      location.x += x - shellMoveStart.x;
      location.y += y - shellMoveStart.y;
      shell.setLocation(location);
    }

    Animator[] animators = getAnimators();
    for (int i = animators.length - 1; i >= 0; --i)
    {
      Animator animator = animators[i];
      if (animator.onMouseMove(x, y))
      {
        return;
      }
    }
  }

  protected void onMouseDown(int x, int y)
  {
    Animator[] animators = getAnimators();
    for (int i = animators.length - 1; i >= 0; --i)
    {
      Animator animator = animators[i];
      if (animator.onMouseDown(x, y))
      {
        return;
      }
    }

    shellMoveStart = new Point(x, y);
  }

  protected synchronized void doRun()
  {
    if (isDisposed())
    {
      return;
    }

    boolean needsRedraw = false;
    for (Animator animator : getAnimators())
    {
      if (animator.advance())
      {
        needsRedraw = true;
      }
    }

    if (needsRedraw)
    {
      redraw();
    }
    else
    {
      scheduleRun();
    }
  }

  protected void doPaint(GC canvasGC)
  {
    Image buffer = new Image(getDisplay(), getBounds());

    GC bufferGC = new GC(buffer);
    bufferGC.setAdvanced(true);
    bufferGC.setBackground(canvasGC.getBackground());
    bufferGC.fillRectangle(buffer.getBounds());

    for (Animator animator : getAnimators())
    {
      bufferGC.setTextAntialias(SWT.ON);
      animator.paint(bufferGC, buffer);
    }

    canvasGC.drawImage(buffer, 0, 0);
    bufferGC.dispose();
    buffer.dispose();

    if (!isFocusControl())
    {
      cover(canvasGC, 200);
    }

    scheduleRun();
  }

  private void scheduleRun()
  {
    getDisplay().timerExec(timerInterval, runnable);
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Animator
  {
    private final List<Resource> resources = new ArrayList<Resource>();

    private final Display display;

    private AnimatedCanvas canvas;

    private int width;

    private int height;

    private Font baseFont;

    public Animator(Display display)
    {
      this.display = display;
    }

    public final Display getDisplay()
    {
      return display;
    }

    public final AnimatedCanvas getCanvas()
    {
      return canvas;
    }

    public final int getWidth()
    {
      return width;
    }

    public final int getHeight()
    {
      return height;
    }

    public final Font getBaseFont()
    {
      return baseFont;
    }

    protected void init()
    {
      Font initialFont = getCanvas().getFont();
      FontData[] fontData = initialFont.getFontData();
      for (int i = 0; i < fontData.length; i++)
      {
        fontData[i].setHeight(16);
        fontData[i].setStyle(SWT.BOLD);
      }

      baseFont = new Font(display, fontData);
    }

    protected void dispose()
    {
      UIUtil.dispose(resources.toArray(new Resource[resources.size()]));
    }

    protected final Image loadImage( String name )
    {
        URL url = null;

        try
        {
            url  = ProjectUI.getDefault().getBundle().getEntry( "images/" + name );
        }
        catch( Exception e )
        {
        }

        ImageDescriptor imagedesc = ImageDescriptor.createFromURL( url );

        Image image = imagedesc.createImage();

        resources.add( image );

        return image;
    }

    protected final Color createColor(int r, int g, int b)
    {
      Display display = getDisplay();
      Color color = new Color(display, r, g, b);
      resources.add(color);
      return color;
    }

    protected final Font createFont(int pixelHeight)
    {
      return createFont(pixelHeight, 0);
    }

    protected final Font createFont(int pixelHeight, int pixelWidth, String... testStrings)
    {
      if (testStrings.length == 0)
      {
        pixelWidth = Integer.MAX_VALUE;
        testStrings = new String[] { "Ag" };
      }

      Display display = getDisplay();
      GC fontGC = new GC(display);

      try
      {
        FontData[] fontData = baseFont.getFontData();
        int fontSize = 40;
        while (fontSize > 0)
        {
          for (int i = 0; i < fontData.length; i++)
          {
            fontData[i].setHeight(fontSize);
            fontData[i].setStyle(SWT.BOLD);
          }

          Font font = new Font(display, fontData);
          fontGC.setFont(font);

          if (isFontSmallEnough(pixelHeight, pixelWidth, fontGC, testStrings))
          {
            resources.add(font);
            return font;
          }

          font.dispose();
          --fontSize;
        }

        throw new RuntimeException("Could not create font: " + pixelHeight);
      }
      finally
      {
        fontGC.dispose();
      }
    }

    private boolean isFontSmallEnough(int pixelHeight, int pixelWidth, GC fontGC, String[] testStrings)
    {
      for (String testString : testStrings)
      {
        Point extent = fontGC.stringExtent(testString);
        if (extent.y > pixelHeight || extent.x > pixelWidth)
        {
          return false;
        }
      }

      return true;
    }

    protected final void setSize(int width, int height)
    {
      this.width = width;
      this.height = height;
    }

    protected boolean onKeyPressed(KeyEvent e)
    {
      return false;
    }

    protected boolean onMouseMove(int x, int y)
    {
      return false;
    }

    protected boolean onMouseDown(int x, int y)
    {
      return false;
    }

    protected abstract boolean advance();

    protected abstract void paint(GC gc, Image buffer);

    public static Rectangle drawText(GC gc, double cX, double cY, String text)
    {
      return drawText(gc, cX, cY, text, 0);
    }

    public static Rectangle drawText(GC gc, double cX, double cY, String text, int box)
    {
      Point extent = gc.stringExtent(text);
      int x = (int)(cX - extent.x / 2);
      int y = (int)(cY - extent.y / 2);

      if (x < box)
      {
        x = box;
      }

      Rectangle rectangle = new Rectangle(x, y, extent.x, extent.y);
      if (box > 0)
      {
        rectangle.x -= box;
        rectangle.y -= box;
        rectangle.width += 2 * box;
        rectangle.height += 2 * box;
        gc.fillRectangle(rectangle);
      }

      gc.drawText(text, x, y, true);
      return rectangle;
    }

    public static Rectangle drawImage(GC gc, Image image, int cX, int cY)
    {
      Rectangle bounds = image.getBounds();
      cX -= bounds.width / 2;
      cY -= bounds.height / 2;
      gc.drawImage(image, cX, cY);
      return new Rectangle(cX, cY, bounds.width, bounds.height);
    }
  }
}

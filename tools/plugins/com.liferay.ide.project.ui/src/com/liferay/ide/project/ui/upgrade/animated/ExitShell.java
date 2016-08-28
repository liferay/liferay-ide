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

import com.liferay.ide.project.ui.upgrade.animated.AnimatedCanvas.Animator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;


/**
 * @author Eike Stepper
 */
public class ExitShell extends AnimatedShell<Boolean>
{
  public static final int NONE = -1;

  private static final int WIDTH = 550;

  private static final int HEIGHT = 450;

  private static final int BORDER = 30;

  private static final int BIG_FONT_PX = GearAnimator.BIG_FONT_PX;

  private static final String[] LINES = { "You can take the questionnaire later via the Oomph Setup preferences:" };

  private final GearAnimator gearAnimator;

  public ExitShell(GearAnimator gearAnimator)
  {
    super(gearAnimator.getCanvas().getShell(), SWT.APPLICATION_MODAL);
    this.gearAnimator = gearAnimator;

    Rectangle bounds = gearAnimator.getCanvas().getShell().getBounds();
    setLocation(bounds.x + (bounds.width - WIDTH) / 2, bounds.y + (bounds.height - HEIGHT) / 2);
    setSize(WIDTH, HEIGHT);
  }

  @Override
  protected void init()
  {
    super.init();

    ExitAnimator animator = new ExitAnimator(getDisplay());
    getCanvas().addAnimator(animator);
  }

  private static boolean shouldShowOverlay()
  {
    return (System.currentTimeMillis() / 500 & 1) == 1;
  }

  /**
   * @author Eike Stepper
   */
  public class ExitAnimator extends Animator
  {
    private boolean oldShowOverlay;

    private Font font;

    private int fontPx;

    private Color purple;

    private Image image;

    private Image image_ovr;

    private Rectangle[] boxes = new Rectangle[2];

    private int hover = NONE;

    private int oldHover = NONE;

    private int choice = NONE;

    public ExitAnimator(Display display)
    {
      super(display);
    }

    public final int getChoice()
    {
      return choice;
    }

    @Override
    protected void init()
    {
      super.init();
      font = createFont(GearAnimator.NORMAL_FONT_PX + 6, WIDTH - 2 * BORDER, LINES);
      fontPx = font.getFontData()[0].getHeight();

      purple = createColor(43, 34, 84);
      image = loadImage("exit_page.png");
      image_ovr = loadImage("exit_page_ovr.png");
    }

    @Override
    protected boolean onKeyPressed(KeyEvent e)
    {
      if (e.character == 13)
      {
        finish(true);
        return true;
      }

      if (e.keyCode == SWT.ESC)
      {
        finish(false);
        return true;
      }

      return super.onKeyPressed(e);
    }

    @Override
    protected boolean onMouseMove(int x, int y)
    {
      hover = getAnswer(x, y);
      if (hover != NONE)
      {
        return true;
      }

      return super.onMouseMove(x, y);
    }

    @Override
    protected boolean onMouseDown(int x, int y)
    {
      choice = getAnswer(x, y);
      if (choice != NONE)
      {
        finish(choice == 0);
        return true;
      }

      return super.onMouseDown(x, y);
    }

    private void finish(boolean exit)
    {
      ExitShell shell = (ExitShell)getCanvas().getShell();
      shell.setResult(exit);
      shell.dispose();
    }

    @Override
    protected boolean advance()
    {
      if (hover != oldHover)
      {
        return true;
      }

      return shouldShowOverlay() != oldShowOverlay;
    }

    @Override
    protected void paint(GC gc, Image buffer)
    {
      int cX = WIDTH / 2;

      gc.setForeground(purple);
      gc.setFont(gearAnimator.getBigFont());
      drawText(gc, cX, BORDER + fontPx, "Exit Questionnaire?");

      gc.setFont(font);
      drawText(gc, cX, 2 * BORDER + BIG_FONT_PX, LINES[0]);

      int x = cX - image.getBounds().width / 2;
      int y = 2 * BORDER + BIG_FONT_PX + 2 * (fontPx + 8);
      gc.drawImage(image, x, y);

      oldShowOverlay = shouldShowOverlay();
      if (oldShowOverlay)
      {
        gc.drawImage(image_ovr, x + 133, y + 105);
      }

      int answerY = HEIGHT - BORDER - fontPx;
      gc.setForeground(purple);

      gc.setFont(hover == 0 ? gearAnimator.getHoverFont() : gearAnimator.getBigFont());
      boxes[0] = drawText(gc, cX - 3 * BORDER, answerY, "Exit Now");

      gc.setFont(hover == 1 ? gearAnimator.getHoverFont() : gearAnimator.getBigFont());
      boxes[1] = drawText(gc, cX + 3 * BORDER, answerY, "Go Back");

      oldHover = hover;
    }

    private int getAnswer(int x, int y)
    {
      for (int i = 0; i < boxes.length; i++)
      {
        Rectangle box = boxes[i];
        if (box != null && box.contains(x, y))
        {
          return i;
        }
      }

      return NONE;
    }
  }
}

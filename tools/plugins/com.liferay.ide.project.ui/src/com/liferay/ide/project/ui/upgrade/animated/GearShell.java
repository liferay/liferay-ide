/*
 * Copyright (c) 2014, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.ui.upgrade.animated.GearAnimator.Answer;
import com.liferay.ide.project.ui.upgrade.animated.GearAnimator.Listener;
import com.liferay.ide.project.ui.upgrade.animated.GearAnimator.AnimatorPage;
import com.liferay.ide.project.ui.upgrade.animated.GearAnimator.PreferencePage;
import com.liferay.ide.project.ui.upgrade.animated.GearAnimator.SummaryPage;
import com.liferay.ide.project.ui.upgrade.animated.util.Pair;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public class GearShell extends AnimatedShell<Map<URI, Pair<String, String>>>implements Listener
{
  private static final boolean TEST_OVERLAYS = false;

  private GearAnimator animator;

  public GearShell(Display display)
  {
    super(display, SWT.APPLICATION_MODAL);
  }

  public GearShell(Shell parent)
  {
    super(parent, SWT.APPLICATION_MODAL);
  }

  public final GearAnimator getAnimator()
  {
    return animator;
  }

  public void onAnswer(GearAnimator animator, AnimatorPage page, Answer answer)
  {
    if (page instanceof SummaryPage)
    {
      HashMap<URI, Pair<String, String>> preferences = new HashMap<URI, Pair<String, String>>();

      AnimatorPage[] pages = animator.getPages();
      for (AnimatorPage p : pages)
      {
        if (p instanceof PreferencePage)
        {
          PreferencePage preferencePage = (PreferencePage)p;
          int choice = preferencePage.getChoice();
          if (choice != GearAnimator.NONE)
          {
            URI key = preferencePage.getPreferenceKey();
            String value = choice == 0 ? preferencePage.getYesValue() : preferencePage.getNoValue();
            preferences.put(key, new Pair<String, String>(null, value));
          }
        }
      }

      setResult(preferences);
      dispose();
    }
  }

  public void onExit(GearAnimator animator, AnimatorPage page)
  {
    dispose();
  }

  @Override
  protected void init()
  {
    super.init();
    Display display = getDisplay();

    animator = new GearAnimator(display)
    {
      @Override
      protected boolean onKeyPressed(KeyEvent e)
      {
        if (TEST_OVERLAYS)
        {
          GearAnimator animator = getAnimator();
          AnimatorPage page = animator.getSelectedPage();

          if (page instanceof ImagePage)
          {
            if (e.keyCode == SWT.ARROW_RIGHT)
            {
              animator.updateOverlay(1, 0);
              return true;
            }

            if (e.keyCode == SWT.ARROW_LEFT)
            {
              animator.updateOverlay(-1, 0);
              return true;
            }

            if (e.keyCode == SWT.ARROW_DOWN)
            {
              animator.updateOverlay(0, 1);
              return true;
            }

            if (e.keyCode == SWT.ARROW_UP)
            {
              animator.updateOverlay(0, -1);
              return true;
            }
          }
        }

        return super.onKeyPressed(e);
      }

      @Override
      protected boolean shouldShowOverlay()
      {
        if (TEST_OVERLAYS)
        {
          return true;
        }

        return super.shouldShowOverlay();
      }
    };

    animator.addListener(this);

    getCanvas().addAnimator(animator);

    int width = Math.max(animator.getWidth(), GearAnimator.PAGE_WIDTH) + 2 * GearAnimator.BORDER;
    int height = animator.getHeight() + GearAnimator.PAGE_HEIGHT + 3 * GearAnimator.BORDER;
    setSize(width, height);

    Composite parent = getParent();
    if (parent != null)
    {
      Rectangle bounds = parent.getBounds();
      setLocation(bounds.x + (bounds.width - width) / 2, bounds.y + (bounds.height - height) / 2);
    }
  }

  public static void main(String[] args)
  {
    final Display display = new Display();

    GearShell shell = new GearShell(display)
    {
      @Override
      public void onAnswer(GearAnimator animator, AnimatorPage page, Answer answer)
      {
        if (page instanceof SummaryPage)
        {
          System.out.println("Finish:");
          AnimatorPage[] pages = animator.getPages();
          for (int i = 1; i < GearAnimator.GEARS; i++)
          {
            System.out.println("  " + pages[i].getTitle() + " = " + pages[i].getChoice());
          }

          display.dispose();
        }
      }
    };

    shell.setText("Questionnaire Test");
    shell.openModal();

    display.dispose();
  }
}

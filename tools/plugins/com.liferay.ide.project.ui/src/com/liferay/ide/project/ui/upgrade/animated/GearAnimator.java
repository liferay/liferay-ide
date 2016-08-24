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

import com.liferay.ide.project.ui.upgrade.animated.AnimatedCanvas.Animator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * @author Eike Stepper
 */
public class GearAnimator extends Animator
{
  public static final String RECORDER_PREFERENCE_KEY = "RECORDER_PREFERENCE_KEY";

  public static final int NONE = -1;

  public static final int PAGE_WIDTH = 620;

  public static final int PAGE_HEIGHT = 420;

  public static final int BORDER = 30;

  public static final int GEARS = 7;

  private static final int TEETH = 8;

  private static final float ANGLE = 360 / TEETH;

  private static final double RADIAN = 2 * Math.PI / 360;

  private static final int EXIT = NONE - 1;

  private static final int BACK = EXIT - 1;

  private static final int NEXT = BACK - 1;

  private static final int CHOICES = NEXT - 1;

  private static final String[] TITLES = { "Welcome to Eclipse", "Refresh Resources Automatically?", "Show Line Numbers in Editors?",
      "Check Spelling in Text Editors?", "Execute Jobs in Background?", "Encode Text Files with UTF-8?", "Enable Preference Recorder?" };

  static final int BIG_FONT_PX = 48;

  static final int NORMAL_FONT_PX = (int)(BIG_FONT_PX * .75);

  private static Color WHITE;

  private static Color GRAY;

  private static Color DARK_GRAY;

  private final List<Listener> listeners = new ArrayList<Listener>();

  private Color purple;

  private Color tooltipColor;

  private Font tooltipFont;

  private Font bigFont;

  private Font hoverFont;

  private Font normalFont;

  private Font numberFont;

  private Image exit;

  private Image exitHover;

  private Image question;

  private final Image[] welcomeImages = new Image[2];

  private final Image[] summaryImages = new Image[2];

  private final Image[] backImages = new Image[2];

  private final Image[] nextImages = new Image[2];

  private final Image[] yesImages = new Image[5];

  private final Image[] noImages = new Image[5];

  private final Page[] pages = new Page[GEARS + 1];

  private final Point[] tooltipPoints = new Point[pages.length];

  private final Path[] gearPaths = new Path[GEARS + 1];

  private final Color[] gearBackground = new Color[2];

  private final Color[] gearForeground = new Color[2];

  private float radius;

  private int pageY;

  private int answerY;

  private int buttonR;

  private long startAnimation;

  private float speed;

  private float angle;

  private boolean overflow;

  private int selection;

  private int oldSelection = NONE;

  private int hover = NONE;

  private int oldHover = NONE;

  private Image pageBuffer;

  private GC pageGC;

  private Image oldPageBuffer;

  private GC oldPageGC;

  private boolean pageBufferUpdated;

  private boolean oldShowOverlay;

  private boolean summaryShown;

  private Rectangle exitBox;

  private Rectangle backBox;

  private Rectangle nextBox;

  public GearAnimator(Display display)
  {
    super(display);
    WHITE = display.getSystemColor(SWT.COLOR_WHITE);
    GRAY = display.getSystemColor(SWT.COLOR_GRAY);
    DARK_GRAY = display.getSystemColor(SWT.COLOR_DARK_GRAY);
  }

  @Override
  protected void init()
  {
    super.init();
    Display display = getDisplay();

    bigFont = createFont(BIG_FONT_PX, PAGE_WIDTH, TITLES);
    hoverFont = createFont(BIG_FONT_PX + 6, PAGE_WIDTH, TITLES);
    normalFont = createFont(NORMAL_FONT_PX, PAGE_WIDTH, TITLES);
    numberFont = createFont(24);
    tooltipFont = createFont(16);

    exit = loadImage("exit.png");
    exitHover = loadImage("exit_hover.png");
    question = loadImage("question.png");

    welcomeImages[0] = loadImage("welcome.png");
    welcomeImages[1] = loadImage("welcome_select.png");

    summaryImages[0] = loadImage("summary.png");
    summaryImages[1] = loadImage("summary_select.png");

    backImages[0] = loadImage("back.png");
    backImages[1] = loadImage("back_hover.png");

    nextImages[0] = loadImage("next.png");
    nextImages[1] = loadImage("next_hover.png");

    buttonR = nextImages[0].getBounds().height / 2;
    answerY = PAGE_HEIGHT + 4 * BORDER - buttonR;

    yesImages[0] = loadImage("yes.png");
    yesImages[1] = loadImage("yes_select.png");
    yesImages[2] = loadImage("yes_hover.png");
    yesImages[3] = loadImage("yes_big.png");
    yesImages[4] = loadImage("yes_badge.png");

    noImages[0] = loadImage("no.png");
    noImages[1] = loadImage("no_select.png");
    noImages[2] = loadImage("no_hover.png");
    noImages[3] = loadImage("no_big.png");
    noImages[4] = loadImage("no_badge.png");

    radius = 32;
    setSize((int)(GEARS * 2 * radius), (int)(2 * radius));
    pageY = getHeight() + 2 * BORDER;

    // Not selected.
    gearBackground[0] = createColor(169, 171, 202);
    gearForeground[0] = createColor(140, 132, 171);

    // Selected.
    gearBackground[1] = createColor(247, 148, 30);
    gearForeground[1] = createColor(207, 108, 0);

    purple = createColor(43, 34, 84);
    tooltipColor = createColor(253, 232, 206);

    pages[0] = new ImagePage(0, TITLES[0], 0, 0, 0, new TextAnswer(""));
    pages[1] = new PreferencePage(1, TITLES[1], 0, 5, 29, "/instance/org.eclipse.core.resources/refresh.lightweight.enabled");
    pages[2] = new PreferencePage(2, TITLES[2], 1, 19, 30, "/instance/org.eclipse.ui.editors/lineNumberRuler");
    pages[3] = new PreferencePage(3, TITLES[3], 1, 186, 37, "/instance/org.eclipse.ui.editors/spellingEnabled");
    pages[4] = new PreferencePage(4, TITLES[4], 0, 23, 160, "/instance/org.eclipse.ui.workbench/RUN_IN_BACKGROUND");
    pages[5] = new PreferencePage(5, TITLES[5], 0, 181, 95, "/instance/org.eclipse.core.resources/encoding", "UTF-8", null);
    pages[6] = new PreferencePage(6, TITLES[6], 1, 57, 82, RECORDER_PREFERENCE_KEY);
    pages[7] = new SummaryPage(7, "Summary");

    pageBuffer = new Image(display, PAGE_WIDTH, PAGE_HEIGHT);
    pageGC = new GC(pageBuffer);
    pageGC.setAdvanced(true);

    oldPageBuffer = new Image(display, PAGE_WIDTH, PAGE_HEIGHT);
    oldPageGC = new GC(oldPageBuffer);
    oldPageGC.setAdvanced(true);
  }

  @Override
  protected void dispose()
  {
    for (Path path : gearPaths)
    {
      if (path != null)
      {
        path.dispose();
      }
    }

    for (Page page : pages)
    {
      if (page != null)
      {
        page.dispose();
      }
    }

    pageGC.dispose();
    pageBuffer.dispose();
    oldPageGC.dispose();
    oldPageBuffer.dispose();
    super.dispose();
  }

  public Font getBigFont()
  {
    return bigFont;
  }

  public Font getHoverFont()
  {
    return hoverFont;
  }

  public Font getNormalFont()
  {
    return normalFont;
  }

  public final void addListener(Listener listener)
  {
    synchronized (listeners)
    {
      listeners.add(listener);
    }
  }

  public final Listener[] getListeners()
  {
    synchronized (listeners)
    {
      return listeners.toArray(new Listener[listeners.size()]);
    }
  }

  public final void removeListener(Listener listener)
  {
    synchronized (listeners)
    {
      listeners.remove(listener);
    }
  }

  public void restart()
  {
    angle = 0;
    speed = 0;
  }

  public final Page[] getPages()
  {
    return pages;
  }

  public final int getSelection()
  {
    return selection;
  }

  public final void setSelection(int selection)
  {
    hover = NONE;
    oldHover = NONE;

    if (selection < 0)
    {
      selection = 0;
      overflow = true;
    }
    else if (selection > pages.length - 1)
    {
      selection = pages.length - 1;
      overflow = true;
    }

    if (overflow)
    {
      overflow = false;
      while (advance())
      {
        // Just advance.
      }

      overflow = true;
      return;
    }

    oldSelection = this.selection;
    this.selection = selection;
    if (getSelectedPage() instanceof SummaryPage)
    {
      summaryShown = true;
    }

    Image tmpPageBuffer = oldPageBuffer;
    oldPageBuffer = pageBuffer;
    pageBuffer = tmpPageBuffer;

    GC tmpPageGC = oldPageGC;
    oldPageGC = pageGC;
    pageGC = tmpPageGC;

    pageBufferUpdated = false;

    restart();
  }

  public final int getOldSelection()
  {
    return oldSelection;
  }

  public final Page getSelectedPage()
  {
    return pages[selection];
  }

  @Override
  protected boolean onKeyPressed(KeyEvent e)
  {
    if (e.keyCode == SWT.ESC)
    {
      exit();
      return true;
    }

    if (e.keyCode == SWT.HOME)
    {
      setSelection(0);
      return true;
    }

    if (e.keyCode == SWT.END)
    {
      setSelection(GearAnimator.GEARS);
      return true;
    }

    if (e.keyCode == SWT.ARROW_RIGHT || e.keyCode == SWT.PAGE_DOWN)
    {
      setSelection(getSelection() + 1);
      return true;
    }

    if (e.keyCode == SWT.ARROW_LEFT || e.keyCode == SWT.PAGE_UP)
    {
      setSelection(getSelection() - 1);
      return true;
    }

    if (e.character >= '0' && e.character <= '6')
    {
      setSelection(e.character - '0');
      return true;
    }

    if (e.character == 13)
    {
      Page page = getSelectedPage();
      if (page instanceof SummaryPage)
      {
        page.doAnswer(0);
      }
      else
      {
        setSelection(getSelection() + 1);
      }

      return true;
    }

    if (e.character == 'y' || e.character == 'Y' || e.character == '+')
    {
      Page page = getSelectedPage();
      if (page instanceof PreferencePage)
      {
        page.doAnswer(0);
      }

      return true;
    }

    if (e.character == 'n' || e.character == 'N' || e.character == '-')
    {
      Page page = getSelectedPage();
      if (page instanceof PreferencePage)
      {
        page.doAnswer(1);
      }

      return true;
    }

    return false;
  }

  @Override
  protected boolean onMouseMove(int x, int y)
  {
    if (x != Integer.MIN_VALUE && y != Integer.MIN_VALUE)
    {
      GC gc = new GC(getCanvas());
      for (int i = 0; i < gearPaths.length; i++)
      {
        Path path = gearPaths[i];
        if (path != null && path.contains(x, y, gc, false))
        {
          if (i != hover)
          {
            hover = i;
          }

          return true;
        }
      }

      if (exitBox != null && exitBox.contains(x, y))
      {
        hover = EXIT;
        return true;
      }

      Page page = getSelectedPage();
      if (page != null)
      {
        if (page.showBack() && backBox != null && backBox.contains(x, y))
        {
          hover = BACK;
          return true;
        }

        if (page.showNext() && nextBox != null && nextBox.contains(x, y))
        {
          hover = NEXT;
          return true;
        }

        x -= BORDER;
        y -= pageY;

        hover = page.onMouseMove(x, y);
        if (hover != NONE)
        {
          return true;
        }
      }
    }

    hover = NONE;
    return false;
  }

  @Override
  protected boolean onMouseDown(int x, int y)
  {
    if (x != Integer.MIN_VALUE && y != Integer.MIN_VALUE)
    {
      GC gc = new GC(getCanvas());
      for (int i = 0; i < gearPaths.length; i++)
      {
        Path path = gearPaths[i];
        if (path != null && path.contains(x, y, gc, false))
        {
          if (i != getSelection())
          {
            setSelection(i);
          }

          return true;
        }
      }

      if (exitBox != null && exitBox.contains(x, y))
      {
        exit();
        return true;
      }

      Page page = getSelectedPage();
      if (page != null)
      {
        if (page.showBack() && backBox != null && backBox.contains(x, y))
        {
          setSelection(getSelection() - 1);
          return true;
        }

        if (page.showNext() && nextBox != null && nextBox.contains(x, y))
        {
          setSelection(getSelection() + 1);
          return true;
        }

        x -= BORDER;
        y -= pageY;

        if (page.onMouseDown(x, y))
        {
          return true;
        }
      }
    }

    return false;
  }

  protected void exit()
  {
    hover = EXIT;

    AnimatedCanvas canvas = getCanvas();
    canvas.redraw();

    ExitShell exitShell = new ExitShell(this);
    Boolean result = exitShell.openModal();

    if (Boolean.TRUE.equals(result))
    {
      for (Listener listener : getListeners())
      {
        listener.onExit(GearAnimator.this, getSelectedPage());
      }
    }

    hover = NONE;
    if (!canvas.isDisposed())
    {
      canvas.redraw();
    }
  }

  protected boolean shouldShowOverlay()
  {
    Page page = getSelectedPage();
    if (page instanceof ImagePage)
    {
      ImagePage questionPage = (ImagePage)page;

      if (hover <= CHOICES)
      {
        int hoveredChoice = -hover + CHOICES;
        return hoveredChoice == questionPage.getOverlayChoice();
      }

      int choice = questionPage.getChoice();
      if (choice != NONE)
      {
        return choice == questionPage.getOverlayChoice();
      }
    }

    return (System.currentTimeMillis() / 1000 & 1) == 1;
  }

  @Override
  protected boolean advance()
  {
    boolean needsRedraw = false;
    if (overflow)
    {
      overflow = false;
      needsRedraw = true;
    }

    boolean showOverlay = shouldShowOverlay();
    if (showOverlay != oldShowOverlay)
    {
      oldShowOverlay = showOverlay;
      updatePage();
      needsRedraw = true;
    }

    if (hover != oldHover)
    {
      needsRedraw = true;
    }

    if (speed >= ANGLE)
    {
      startAnimation = 0;
      return needsRedraw;
    }

    long now = System.currentTimeMillis();
    if (startAnimation == 0)
    {
      startAnimation = now;
    }

    long timeSinceStart = now - startAnimation;
    speed = timeSinceStart * ANGLE / 1900;
    angle += speed;
    return true;
  }

  @Override
  protected void paint(GC gc, Image buffer)
  {
    gc.setFont(getBaseFont());
    gc.setLineWidth(3);
    gc.setAntialias(SWT.ON);

    // Unselected gears
    Page page = getSelectedPage();
    int alpha = Math.min((int)(255 * speed / ANGLE), 255);

    for (int i = 0; i < GEARS + 1; i++)
    {
      if (i != selection && (i < GEARS || summaryShown))
      {
        tooltipPoints[i] = paintGear(gc, i, alpha);
      }
    }

    // Selected gear
    tooltipPoints[selection] = paintGear(gc, selection, alpha);

    // Exit button
    int centerX = BORDER + PAGE_WIDTH - exit.getBounds().width / 2;
    int centerY = BORDER + exit.getBounds().height / 2;
    Image exitImage = hover == EXIT ? exitHover : exit;

    exitBox = exitImage.getBounds();
    exitBox.x = centerX - exitImage.getBounds().width / 2;
    exitBox.y = centerY - exitImage.getBounds().height / 2;
    gc.drawImage(exitImage, exitBox.x, exitBox.y);

    // Selected page
    if (!pageBufferUpdated)
    {
      updatePage();
      pageBufferUpdated = true;
    }

    if (oldSelection == NONE)
    {
      gc.setAlpha(alpha);
      gc.drawImage(pageBuffer, BORDER, pageY);
      gc.setAlpha(255);
    }
    else
    {
      double progress = 2 * speed / ANGLE;
      int slide = Math.min((int)(PAGE_WIDTH * progress * progress), PAGE_WIDTH);

      gc.setAlpha(255 - alpha);
      if (selection > oldSelection)
      {
        gc.drawImage(oldPageBuffer, slide, 0, PAGE_WIDTH - slide, PAGE_HEIGHT, BORDER, pageY, PAGE_WIDTH - slide, PAGE_HEIGHT);
        gc.setAlpha(alpha);
        gc.drawImage(pageBuffer, 0, 0, slide, PAGE_HEIGHT, BORDER + PAGE_WIDTH - slide, pageY, slide, PAGE_HEIGHT);
      }
      else
      {
        gc.drawImage(oldPageBuffer, 0, 0, PAGE_WIDTH - slide, PAGE_HEIGHT, BORDER + slide, pageY, PAGE_WIDTH - slide, PAGE_HEIGHT);
        gc.setAlpha(alpha);
        gc.drawImage(pageBuffer, PAGE_WIDTH - slide, 0, slide, PAGE_HEIGHT, BORDER, pageY, slide, PAGE_HEIGHT);
      }

      gc.setAlpha(255);
    }

    // Gear tooltip
    if (hover >= 0 && hover < tooltipPoints.length)
    {
      Point point = tooltipPoints[hover];
      String title = pages[hover].getTitle();

      gc.setFont(tooltipFont);
      gc.setForeground(DARK_GRAY);
      gc.setBackground(tooltipColor);
      Rectangle rectangle = drawText(gc, point.x, point.y + 14, title, 2);

      gc.setForeground(GRAY);
      gc.setLineWidth(1);
      gc.drawRectangle(rectangle);
    }

    // Back button
    if (page.showBack())
    {
      backBox = Animator.drawImage(gc, backImages[hover == BACK ? 1 : 0], BORDER + buttonR, answerY);
    }

    // Next button
    if (page.showNext())
    {
      nextBox = Animator.drawImage(gc, nextImages[hover == NEXT ? 1 : 0], PAGE_WIDTH + BORDER - buttonR, answerY);
    }

    oldHover = hover;
  }

  private Point paintGear(GC gc, int i, int alpha)
  {
    double offset = 2 * i * radius;
    double x = BORDER + radius + offset;
    double y = BORDER + radius;
    double r2 = (double)radius * .8f;
    double r3 = (double)radius * .5f;

    int selected = 0;
    double factor = 1;
    if (i == oldSelection)
    {
      if (speed < ANGLE / 2)
      {
        selected = 1;
      }
    }
    else if (i == selection)
    {
      if (speed >= ANGLE / 2)
      {
        selected = 1;
        factor += (ANGLE - speed) * .02;
      }
      else
      {
        factor += speed * .02;
      }
    }

    boolean hovered = false;
    if (i == hover)
    {
      factor += .1;
      oldHover = hover;
      if (selected == 0)
      {
        hovered = true;
      }
    }

    double outerR = factor * radius;
    double innerR = factor * r2;
    float angleOffset = (angle + i * ANGLE) * (i % 2 == 1 ? -1 : 1);

    gc.setForeground(hovered ? DARK_GRAY : gearForeground[selected]);
    gc.setBackground(hovered ? GRAY : gearBackground[selected]);

    Display display = getDisplay();
    Path path = drawGear(gc, display, x, y, outerR, innerR, angleOffset);

    if (gearPaths[i] != null)
    {
      gearPaths[i].dispose();
    }

    gearPaths[i] = path;

    int ovalX = (int)(x - factor * r3);
    int ovalY = (int)(y - factor * r3);
    int ovalR = (int)(2 * factor * r3);
    gc.setBackground(WHITE);
    gc.fillOval(ovalX, ovalY, ovalR, ovalR);
    gc.drawOval(ovalX, ovalY, ovalR, ovalR);

    if (i == 0)
    {
      Animator.drawImage(gc, welcomeImages[selected], (int)x, (int)y);
    }
    else if (i < GEARS)
    {
      String number = Integer.toString(i);
      gc.setForeground(selected == 1 ? gearForeground[1] : GRAY);
      gc.setFont(numberFont);
      Animator.drawText(gc, x, y - 1, number);
    }
    else
    {
      Animator.drawImage(gc, summaryImages[selected], (int)x, (int)y);
    }

    return paintBadge(gc, x, y, outerR, i, alpha);
  }

  private Point paintBadge(GC gc, double x, double y, double outerR, int i, int alpha)
  {
    if (selection >= GEARS)
    {
      gc.setAlpha(255 - alpha);
    }
    else if (oldSelection >= GEARS)
    {
      gc.setAlpha(alpha);
    }

    Page page = pages[i];
    Answer answer = page.getChoiceAnswer();
    if (answer instanceof ImageAnswer)
    {
      ImageAnswer imageAnswer = (ImageAnswer)answer;
      Image image = imageAnswer.getImages()[4];
      gc.drawImage(image, (int)(x - image.getBounds().width / 2), (int)(y - outerR - 12));
    }

    gc.setAlpha(255);
    return new Point((int)x, (int)(y + outerR));
  }

  private Page updatePage()
  {
    pageGC.setBackground(WHITE);
    pageGC.fillRectangle(pageBuffer.getBounds());

    Page page = getSelectedPage();
    page.paint(pageGC);
    return page;
  }

  void updateOverlay(int x, int y)
  {
    Page page = getSelectedPage();
    if (page instanceof ImagePage)
    {
      ImagePage questionPage = (ImagePage)page;
      questionPage.overlayX += x;
      questionPage.overlayY += y;

      System.out.println("" + questionPage.overlayX + ", " + questionPage.overlayY);

      updatePage();
      overflow = true;
    }
  }

  private static Path drawGear(GC gc, Display display, double cx, double cy, double outerR, double innerR, float angleOffset)
  {
    double radian2 = ANGLE / 2 * RADIAN;
    double radian3 = .06;

    Path path = new Path(display);

    for (int i = 0; i < TEETH; i++)
    {
      double radian = (i * ANGLE + angleOffset) * RADIAN;

      double x = cx + outerR * Math.cos(radian);
      double y = cy - outerR * Math.sin(radian);

      if (i == 0)
      {
        path.moveTo((int)x, (int)y);
      }

      double r1 = radian + radian3;
      double r3 = radian + radian2;
      double r2 = r3 - radian3;
      double r4 = r3 + radian2;

      x = cx + innerR * Math.cos(r1);
      y = cy - innerR * Math.sin(r1);
      path.lineTo((int)x, (int)y);

      x = cx + innerR * Math.cos(r2);
      y = cy - innerR * Math.sin(r2);
      path.lineTo((int)x, (int)y);

      x = cx + outerR * Math.cos(r3);
      y = cy - outerR * Math.sin(r3);
      path.lineTo((int)x, (int)y);

      x = cx + outerR * Math.cos(r4);
      y = cy - outerR * Math.sin(r4);
      path.lineTo((int)x, (int)y);
    }

    path.close();
    gc.fillPath(path);
    gc.drawPath(path);
    return path;
  }

  /**
   * @author Eike Stepper
   */
  public interface Listener
  {
    public void onAnswer(GearAnimator animator, Page page, Answer answer);

    public void onExit(GearAnimator animator, Page page);
  }

  /**
   * @author Eike Stepper
   */
  public abstract class Answer
  {
    public Answer()
    {
    }

    public abstract Point getSize(GC gc, Page page);

    public abstract Rectangle paint(GC gc, Page page, int index, int x, int y, boolean hovered, boolean selected);
  }

  /**
   * @author Eike Stepper
   */
  public class TextAnswer extends Answer
  {
    private final String text;

    public TextAnswer(String text)
    {
      this.text = text;
    }

    public final String getText()
    {
      return text;
    }

    @Override
    public Point getSize(GC gc, Page page)
    {
      gc.setFont(hoverFont);
      return gc.stringExtent(text);
    }

    @Override
    public Rectangle paint(GC gc, Page page, int index, int x, int y, boolean hovered, boolean selected)
    {
      gc.setFont(hovered ? hoverFont : bigFont);
      gc.setForeground(selected ? purple : DARK_GRAY);

      Point extent = gc.stringExtent(text);
      x -= extent.x / 2 - 60; // TODO -60 is a hack!
      y -= extent.y / 2;
      gc.drawText(text, x, y, true);

      return new Rectangle(x, y, extent.x, extent.y);
    }
  }

  /**
   * @author Eike Stepper
   */
  public class ImageAnswer extends Answer
  {
    private final Image[] images;

    public ImageAnswer(Image... images)
    {
      this.images = images;
    }

    public final Image[] getImages()
    {
      return images;
    }

    @Override
    public Point getSize(GC gc, Page page)
    {
      Rectangle bounds = images[2].getBounds();
      return new Point(bounds.width, bounds.height);
    }

    @Override
    public Rectangle paint(GC gc, Page page, int index, int x, int y, boolean hovered, boolean selected)
    {
      Image image = images[0];
      if (hovered)
      {
        image = images[2];
      }
      else if (selected)
      {
        image = images[1];
      }
      else
      {
        if (page instanceof ImagePage && images.length > 3)
        {
          int overlayChoice = ((ImagePage)page).getOverlayChoice();
          boolean overlayChoiceYes = overlayChoice == index;
          boolean showOverlay = shouldShowOverlay();

          if (showOverlay == overlayChoiceYes)
          {
            image = images[3];
          }
        }
      }

      return Animator.drawImage(gc, image, x, y);
    }
  }

  /**
   * @author Eike Stepper
   */
  public class BackButton extends ImageAnswer
  {
    public BackButton()
    {
      super(backImages);
    }
  }

  /**
   * @author Eike Stepper
   */
  public class NextButton extends ImageAnswer
  {
    public NextButton()
    {
      super(nextImages);
    }
  }

  /**
   * @author Eike Stepper
   */
  public abstract class Page
  {
    private final int index;

    private final String title;

    private Answer[] answers;

    private Rectangle[] answerBoxes;

    private int choice = NONE;

    public Page(int index, String title)
    {
      this.index = index;
      this.title = title;
    }

    public final int getIndex()
    {
      return index;
    }

    public final String getTitle()
    {
      return title;
    }

    public final Answer[] getAnswers()
    {
      return answers;
    }

    public final void setAnswers(Answer[] answers)
    {
      this.answers = answers;
      answerBoxes = new Rectangle[answers.length];
    }

    public final int getAnswer(int x, int y)
    {
      for (int i = 0; i < answers.length; i++)
      {
        Rectangle box = answerBoxes[i];
        if (box != null && box.contains(x, y))
        {
          return i;
        }
      }

      return NONE;
    }

    public final Answer getChoiceAnswer()
    {
      return choice == NONE ? null : answers[choice];
    }

    public final int getChoice()
    {
      return choice;
    }

    public final void setChoice(int choice)
    {
      this.choice = choice;
    }

    protected void dispose()
    {
    }

    protected boolean showBack()
    {
      return index > 0;
    }

    protected boolean showNext()
    {
      return index < GEARS;
    }

    protected int onMouseMove(int x, int y)
    {
      int i = getAnswer(x, y);
      if (i != NONE)
      {
        pageBufferUpdated = false;
        return CHOICES - i;
      }

      if (hover <= CHOICES)
      {
        pageBufferUpdated = false;
      }

      return NONE;
    }

    protected boolean onMouseDown(int x, int y)
    {
      int i = getAnswer(x, y);
      if (i != NONE)
      {
        doAnswer(i);
        return true;
      }

      return false;
    }

    protected final void paint(GC gc)
    {
      String title = getTitle();

      Font oldFont = gc.getFont();
      gc.setFont(bigFont);

      Point extent = gc.stringExtent(title);
      gc.setForeground(purple);
      gc.drawText(title, (PAGE_WIDTH - extent.x) / 2, 0, true);

      gc.setFont(oldFont);

      paintContent(gc);
      paintAnswers(gc);
    }

    protected abstract void paintContent(GC gc);

    protected int getAnswerY()
    {
      return answerY;
    }

    private void doAnswer(int i)
    {
      Answer answer = answers[i];

      if (i == choice)
      {
        hover = NONE;
        choice = NONE;
        answer = null;
      }
      else
      {
        setChoice(i);
      }

      updatePage();

      for (Listener listener : getListeners())
      {
        listener.onAnswer(GearAnimator.this, this, answer);
      }

      if (answer != null)
      {
        int selection = getSelection();
        if (selection < GEARS)
        {
          setSelection(selection + 1);
        }
      }
    }

    private void paintAnswers(GC gc)
    {
      boolean selecteds[] = new boolean[answers.length];
      boolean hovereds[] = new boolean[answers.length];
      Point sizes[] = new Point[answers.length];

      int width = (answers.length - 1) * BORDER;
      int height = 0;

      for (int i = 0; i < answers.length; i++)
      {
        selecteds[i] = i == choice;
        if (CHOICES - i == hover)
        {
          oldHover = hover;
          hovereds[i] = true;
        }

        sizes[i] = answers[i].getSize(gc, this);
        width += sizes[i].x;
        height = Math.max(height, sizes[i].y);
      }

      int x = (PAGE_WIDTH - width) / 2;
      int y = getAnswerY() - pageY;

      for (int i = 0; i < answers.length; i++)
      {
        Answer answer = answers[i];
        answerBoxes[i] = answer.paint(gc, this, i, x, y, hovereds[i], selecteds[i]);
        x += BORDER + sizes[i].x;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public class ImagePage extends Page
  {
    private final Image image;

    private final Image overlay;

    private final int overlayChoice;

    private int overlayX;

    private int overlayY;

    public ImagePage(int index, String title, int overlayChoice, int overlayX, int overlayY, Answer... answers)
    {
      super(index, title);
      this.overlayChoice = overlayChoice;
      setAnswers(answers);

      Display display = getDisplay();
      image = doLoadImage(display, index, "");
      overlay = doLoadImage(display, index, "_ovr");

      this.overlayX = overlayX;
      this.overlayY = overlayY;
    }

    public ImagePage(int index, String title, int overlayChoice, int overlayX, int overlayY)
    {
      this(index, title, overlayChoice, overlayX, overlayY, new Answer[] { new ImageAnswer(yesImages), new ImageAnswer(noImages) });
    }

    public final int getOverlayChoice()
    {
      return overlayChoice;
    }

    @Override
    protected void paintContent(GC gc)
    {
      if (image != null)
      {
        Rectangle bounds = image.getBounds();
        int x = (PAGE_WIDTH - bounds.width) / 2;
        int y = (PAGE_HEIGHT - bounds.height) / 2;
        gc.drawImage(image, x, y);

        if (overlay != null && shouldShowOverlay())
        {
          gc.drawImage(overlay, x + overlayX, y + overlayY);
        }
      }
    }

    private Image doLoadImage(final Display display, int index, String suffix)
    {
      try
      {
        return loadImage("page" + index + suffix + ".png");
      }
      catch (Exception ex)
      {
        return null;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public class PreferencePage extends ImagePage
  {
    private final URI preferenceKey;

    private final String yesValue;

    private final String noValue;

    public PreferencePage(int index, String title, int overlayChoice, int overlayX, int overlayY, String preferenceKey, String yesValue, String noValue)
    {
      super(index, title, overlayChoice, overlayX, overlayY, new Answer[] { new ImageAnswer(yesImages), new ImageAnswer(noImages) });
      this.preferenceKey = null;
      this.yesValue = yesValue;
      this.noValue = noValue;
    }

    public PreferencePage(int index, String title, int overlayChoice, int overlayX, int overlayY, String preferenceKey)
    {
      this(index, title, overlayChoice, overlayX, overlayY, preferenceKey, "true", "false");
    }

    public final URI getPreferenceKey()
    {
      return preferenceKey;
    }

    public final String getYesValue()
    {
      return yesValue;
    }

    public final String getNoValue()
    {
      return noValue;
    }
  }

  /**
   * @author Eike Stepper
   */
  public class SummaryPage extends Page
  {
    private Rectangle[] boxes;

    public SummaryPage(int index, String title)
    {
      super(index, title);
      setAnswers(new Answer[] { new TextAnswer("Finish") });
    }

    @Override
    protected boolean showNext()
    {
      return false;
    }

    @Override
    protected int onMouseMove(int x, int y)
    {
      if (boxes != null)
      {
        for (int i = 0; i < boxes.length; i++)
        {
          Rectangle box = boxes[i];
          if (box.contains(x, y))
          {
            return i + 1;
          }
        }
      }

      return super.onMouseMove(x, y);
    }

    @Override
    protected boolean onMouseDown(int x, int y)
    {
      if (boxes != null)
      {
        for (int i = 0; i < boxes.length; i++)
        {
          Rectangle box = boxes[i];
          if (box.contains(x, y))
          {
            setSelection(i + 1);
            return true;
          }
        }
      }

      return super.onMouseDown(x, y);
    }

    @Override
    protected void paintContent(GC gc)
    {
      gc.setFont(normalFont);

      boxes = new Rectangle[GEARS - 1];
      int offsetX = yesImages[4].getBounds().width + 12;
      int minWidth = Integer.MAX_VALUE;
      int maxWidth = 0;

      for (int i = 1; i < GEARS; i++)
      {
        Page page = pages[i];
        Point extent = gc.stringExtent(page.getTitle());
        int width = extent.x;
        minWidth = Math.min(minWidth, width);
        maxWidth = Math.max(maxWidth, width);

        boxes[i - 1] = new Rectangle(0, 0, offsetX + width, extent.y + 4);
      }

      int width = (minWidth + maxWidth) / 2 + offsetX;

      for (int i = 1; i < GEARS; i++)
      {
        int x = (PAGE_WIDTH - width) / 2;
        int y = 40 * (1 + i);

        ImagePage page = (ImagePage)pages[i];
        ImageAnswer answer = (ImageAnswer)page.getChoiceAnswer();
        if (answer != null)
        {
          gc.drawImage(answer.images[4], x, y + 8);
          gc.setForeground(purple);
        }
        else
        {
          gc.drawImage(question, x, y + 8);
          gc.setForeground(DARK_GRAY);
        }

        String title = page.getTitle();
        if (title.endsWith("?"))
        {
          title = title.substring(0, title.length() - 1);
        }

        gc.drawText(title, x + offsetX, y);
        boxes[i - 1].x = x;
        boxes[i - 1].y = y + 4;
      }
    }
  }
}

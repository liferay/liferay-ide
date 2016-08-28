
package com.liferay.ide.project.ui.upgrade.animated;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.liferay.ide.project.ui.upgrade.animated.GearAnimator.AnimatorPage;
import com.liferay.ide.project.ui.upgrade.animated.GearAnimator.Listener;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageActionListener;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageNavigatorListener;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageValidationListener;

public class GearControl extends Canvas implements PageNavigatorListener, PageActionListener, PageValidationListener
{
    private static final int DEFAULT_TIMER_INTERVAL = 10;
    
    private final Runnable runnable = new Runnable()
    {
        public void run()
        {
            doRun();
        }
    };
    
    protected synchronized void doRun()
    {
        if( isDisposed() )
        {
            return;
        }

        boolean needsRedraw = advance();

        if( needsRedraw )
        {
            redraw();
        }
        else
        {
            scheduleRun();
        }
    }
    
    private void scheduleRun()
    {
        getDisplay().timerExec( DEFAULT_TIMER_INTERVAL, runnable );
    }
    
    private int width;

    private int height;
    
    private Font baseFont;

    private final List<Resource> resources = new ArrayList<Resource>();
    
    public static final String RECORDER_PREFERENCE_KEY = "RECORDER_PREFERENCE_KEY";

    public static final int NONE = -1;

    public static final int PAGE_WIDTH = 620;

    public static final int PAGE_HEIGHT = 420;

    public static final int BORDER = 20;
    
    private final List<SelectionChangedListener> selectionChangedListeners =
                    Collections.synchronizedList( new ArrayList<SelectionChangedListener>() );
    
    public void addSelectionChangedListener( SelectionChangedListener listener )
    {
        selectionChangedListeners.add( listener );
    }

    public int gearsNumber = 7;

    
    public int getGearsNumber()
    {
        return gearsNumber;
    }

    
    public void setGearsNumber( int gearsNumber )
    {
        this.gearsNumber = gearsNumber;
    }

    private static final int TEETH = 8;

    private static final float ANGLE = 360 / TEETH;

    private static final double RADIAN = 2 * Math.PI / 360;

    private static final int EXIT = NONE - 1;

    private static final int BACK = EXIT - 1;

    private static final int NEXT = BACK - 1;

    private static final int CHOICES = NEXT - 1;

    private static final String[] TITLES = {
                "Welcome to Code Upgrade Tool", 
                "Import projects", 
                "Update Descriptors",
                "Find Breaking Changes", 
                "Build Service", 
                "Convert Custom Jsp", 
                "Layout","Compile" 
            };

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

    //private final Image[] backImages = new Image[2];

    private final Image[] nextImages = new Image[2];

    private final Image[] yesImages = new Image[5];
    
    //private Image badgeImage ;

    private final Image[] noImages = new Image[5];

    private final AnimatorPage[] pages = new AnimatorPage[gearsNumber];

    private final Point[] tooltipPoints = new Point[gearsNumber];

    private final Path[] gearPaths = new Path[gearsNumber];

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

    public GearControl insance;
    
    private Display display = getDisplay();
    
    protected boolean shouldShowOverlay()
    {
      return (System.currentTimeMillis() / 1000 & 1) == 1;
    }
    
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
        //updatePage();
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
      
      //System.out.println("angle:"+angle);
      //System.out.println("speed:"+speed);
      
      return true;
    }

    public GearControl( Composite parent, int style )
    {
        super( parent, style | SWT.DOUBLE_BUFFERED );
        
        setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );
        
        WHITE = display.getSystemColor(SWT.COLOR_WHITE);
        GRAY = display.getSystemColor(SWT.COLOR_GRAY);
        DARK_GRAY = display.getSystemColor(SWT.COLOR_DARK_GRAY);
        
        insance = this;
        
        addFocusListener( new FocusListener()
        {
            public void focusGained( FocusEvent e )
            {
                redraw();
            }

            public void focusLost( FocusEvent e )
            {
                redraw();
            }
        } );

        addPaintListener( new PaintListener()
        {
            @Override
            public void paintControl( PaintEvent e )
            {
                Image buffer = new Image( getDisplay(), getBounds() );

                GC canvasGc = e.gc;
                //not blink
                GC bufferGC = new GC( buffer );

                bufferGC.setAdvanced( true );
                bufferGC.setBackground( canvasGc.getBackground() );
                bufferGC.fillRectangle( buffer.getBounds() );

                paint( bufferGC );
                
                canvasGc.drawImage( buffer, 0, 0 );

                bufferGC.dispose();
                buffer.dispose();
                
                scheduleRun();
            }
        } );


        addMouseTrackListener( new MouseTrackAdapter()
        {
            @Override
            public void mouseExit( MouseEvent e )
            {
                onMouseMove( Integer.MIN_VALUE, Integer.MIN_VALUE );
            }
        } );

        addMouseMoveListener( new MouseMoveListener()
        {
            public void mouseMove( MouseEvent e )
            {
                onMouseMove( e.x, e.y );
            }
        } );

        addMouseListener( new MouseAdapter()
        {
            @Override
            public void mouseDown( MouseEvent e )
            {
                //left button
                if( e.button == 1 )
                {
                    onMouseDown( e.x, e.y );
                }
            }
        } );
        
        init();
        
        scheduleRun();
    }
    
    public final int getSelection()
    {
      return selection;
    }
    
    protected boolean onMouseDown(int x, int y)
    {
      if (x != Integer.MIN_VALUE && y != Integer.MIN_VALUE)
      {
        GC gc = new GC( this );
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
      }

      return false;
    }
    
    protected boolean onMouseMove(int x, int y)
    {
      if (x != Integer.MIN_VALUE && y != Integer.MIN_VALUE)
      {
        GC gc = new GC( this );
        
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
      }

      //comment this to avoid blink
      hover = NONE;

      return false;
    }
    
    public void restart()
    {
      angle = 0;
      speed = 0;
    }
    
    protected final Color createColor(int r, int g, int b)
    {
      Display display = getDisplay();
      Color color = new Color(display, r, g, b);
      resources.add(color);
      return color;
    }
    
    private void init()
    {
        Font initialFont = getFont();
        FontData[] fontData = initialFont.getFontData();
        for (int i = 0; i < fontData.length; i++)
        {
          fontData[i].setHeight(16);
          fontData[i].setStyle(SWT.BOLD);
        }

        baseFont = new Font(display, fontData);
        
        bigFont = createFont(BIG_FONT_PX, PAGE_WIDTH, TITLES);
        hoverFont = createFont(BIG_FONT_PX + 6, PAGE_WIDTH, TITLES);
        normalFont = createFont(NORMAL_FONT_PX, PAGE_WIDTH, TITLES);
        numberFont = createFont(24);
        tooltipFont = createFont(24);
        
        radius = 32;
        setSize((int)(gearsNumber * 2 * radius), (int)(2 * radius));
        pageY = getSize().y + 2 * BORDER;

        // Not selected.
        gearBackground[0] = createColor(169, 171, 202);
        gearForeground[0] = createColor(140, 132, 171);

        // Selected.
        gearBackground[1] = createColor(247, 148, 30);
        gearForeground[1] = createColor(207, 108, 0);

        purple = createColor(43, 34, 84);
        tooltipColor = createColor(253, 232, 206);


        pageBuffer = new Image(display, PAGE_WIDTH, PAGE_HEIGHT);
        pageGC = new GC(pageBuffer);
        pageGC.setAdvanced(true);

        oldPageBuffer = new Image(display, PAGE_WIDTH, PAGE_HEIGHT);
        oldPageGC = new GC(oldPageBuffer);
        oldPageGC.setAdvanced(true);
        

        nextImages[0] = loadImage("next.png");
        nextImages[1] = loadImage("next_hover.png");
    }
    
    protected final Image loadImage( String name )
    {
        URL url = null;
        
        File imageFile = new File("images/"+name);

        try
        {
            //TODO need to be changed to get image from bundle
            url  = imageFile.toURI().toURL();
        }
        catch( Exception e )
        {
        }

        ImageDescriptor imagedesc = ImageDescriptor.createFromURL( url );

        Image image = imagedesc.createImage();

        resources.add( image );

        return image;
    }
    
    public final Font getBaseFont()
    {
      return baseFont;
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

//      UpgradeView.setSelectPage( selection );
      
      for( SelectionChangedListener listener : selectionChangedListeners )
      {
          listener.onSelectionChanged( selection );
      }

      restart();
    }

    private void paint( GC gc )
    {
        // System.out.println(System.currentTimeMillis()/1000);

        gc.setFont( getBaseFont() );
        gc.setLineWidth( 3 );
        gc.setAntialias( SWT.ON );

        int alpha = Math.min( (int) ( 255 * speed / ANGLE ), 255 );
        
        //System.out.println( oldSelection );

        for( int i = 0; i < gearsNumber; i++ )
        {
            tooltipPoints[i] = paintGear( gc, i, alpha );
        }

        // show gear tooltip
        if( hover >= 0 && hover < tooltipPoints.length )
        {
            Point point = tooltipPoints[hover];

            String title = UpgradeView.getPage( hover ).getTitle();

            gc.setFont( tooltipFont );
            gc.setForeground( DARK_GRAY );
            gc.setBackground( tooltipColor );
            
            Rectangle rectangle = drawText( gc, point.x, point.y + 14, title, 2 );

            gc.setForeground( GRAY );
            gc.setLineWidth( 1 );
            gc.drawRectangle( rectangle );
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
      
      if (i < gearsNumber )
      {
        String number = Integer.toString( i + 1 );

        gc.setForeground(selected == 1 ? gearForeground[1] : GRAY);
        gc.setFont(numberFont);

        drawText(gc, x, y - 1, number);
      }

      return paintBadge(gc, x, y, outerR, i, alpha);
    }

    private Point paintBadge(GC gc, double x, double y, double outerR, int i, int alpha)
    {
      if ( selection >= gearsNumber)
      {
        gc.setAlpha(255 - alpha);
      }
      else if (oldSelection >= gearsNumber)
      {
        gc.setAlpha(alpha);
      }

      Image badgeImage = null;
      
      Page page = UpgradeView.getPage( i );
      
      PageAction pageAction = page.getSelectedAction();
      
      if( pageAction != null )
      {
          badgeImage = pageAction.getImages()[4];
      }

      if ( badgeImage != null )
      {
          gc.drawImage(badgeImage, (int)(x - badgeImage.getBounds().width / 2), (int)(y - outerR - 12));
          gc.setAlpha(255);
      }

      return new Point((int)x, (int)(y + outerR));
    }
    
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

    public static void main( String[] args )
    {
        Display display = new Display();

        Shell shell = new Shell( display );

        shell.setText( "Animated Example" );
        shell.setLayout( new GridLayout( 1 , true ) );
        
        GridData grData = new GridData(GridData.FILL_BOTH);
        grData.heightHint = 600;
        grData.widthHint = 600;
        shell.setLayoutData( grData );

        final GearControl gear = new GearControl( shell, SWT.BORDER );
        
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 400;
        gridData.heightHint = 120;
        
        gear.setLayoutData( gridData );
        
        gear.setGearsNumber( 7 );
        
        final StackLayout stackLayout = new StackLayout();
        
        final Composite container = new Composite( shell, SWT.BORDER );
        
        container.setLayout( stackLayout );
        
        
        GridData containerData = new GridData(GridData.FILL_HORIZONTAL);
        containerData.grabExcessHorizontalSpace = true;
        containerData.widthHint = 400;
        containerData.heightHint = 300;
        container.setLayoutData( containerData );
        
        Button backButton = new Button(shell, SWT.PUSH);
        backButton.setText( "back" );
        
        Button nextButton = new Button(shell, SWT.PUSH);
        nextButton.setText( "next" );

        Composite composite1 = new Composite( container, SWT.NONE );
        composite1.setBackground(Display.getDefault().getSystemColor( SWT.COLOR_WHITE));
        
        Composite composite2 = new Composite( container, SWT.NONE );
        composite2.setBackground(Display.getDefault().getSystemColor( SWT.COLOR_BLUE));
        
        Composite composite3 = new Composite( container, SWT.NONE );
        composite3.setBackground(Display.getDefault().getSystemColor( SWT.COLOR_GREEN));
        
        final Composite[] composites = new Composite[3];
        composites[0] = composite1;
        composites[1] = composite2;
        composites[2] = composite3;
        
        nextButton.addSelectionListener( new SelectionListener()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                int i = gear.getSelection();
                stackLayout.topControl = composites[i+1];
                
                gear.setSelection( i+ 1 );
                container.layout();
            }
            
            @Override
            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );
        
        backButton.addSelectionListener( new SelectionListener()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                int i = gear.getSelection();
                stackLayout.topControl = composites[i-1];
                gear.setSelection( i-1 );
                container.layout();
            }
            
            @Override
            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );
        
        stackLayout.topControl = composite1;
        
        container.layout();

        Button button = new Button(shell, SWT.PUSH);
        
        button.setText("change gears number");
        
        
        final Text text = new Text(shell , SWT.NONE);
        
        text.setText( "1" );
        
        button.addSelectionListener( new SelectionListener()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                gear.setGearsNumber( Integer.parseInt( text.getText() ) );
                gear.setSelection( 0 );
            }

            @Override
            public void widgetDefaultSelected( SelectionEvent e )
            {
                
            }
        } );

        shell.open();

        while( !shell.isDisposed() )
        {
            if( !display.readAndDispatch() )
            {
                display.sleep();
            }
        }

        display.dispose();
    }

    @Override
    public void onPageNavigate( PageNavigateEvent event )
    {
        Page targetPage = event.getTargetPage();

        setSelection( targetPage.getIndex() );

    }

    @Override
    public void onPageAction( PageActionEvent event )
    {
        Page targetPage = event.getTargetPage();
        
        setSelection(targetPage.getIndex());
        
    }

    @Override
    public void onValidation( PageValidateEvent event )
    {
        // TODO Auto-generated method stub
        
    }

}

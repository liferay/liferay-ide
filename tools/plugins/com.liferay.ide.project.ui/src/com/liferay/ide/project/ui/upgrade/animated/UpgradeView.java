package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.ui.upgrade.animated.GearAnimator.Listener;
import com.liferay.ide.project.ui.upgrade.animated.UpgradePage.Action;
import com.liferay.ide.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;


public class UpgradeView extends ViewPart
{
    private static final boolean TEST_OVERLAYS = false;
    private GearControl canvas;
    private GearAnimator animator;
    
    @Override
    public void createPartControl( Composite parent )
    {
        final Composite composite = SWTUtil.createComposite( parent, 1, 1, GridData.FILL_BOTH );
        
        composite.setLayout( new GridLayout( 1 , true ) );
        
        GridData grData = new GridData(GridData.FILL_BOTH);
        grData.heightHint = 600;
        grData.widthHint = 300;
        composite.setLayoutData( grData );

        final GearControl gear = new GearControl( composite, SWT.BORDER );
        
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 400;
        gridData.heightHint = 210;
        
        gear.setLayoutData( gridData );
        
        gear.setGearsNumber( 7 );          
        
        
        final StackLayout stackLayout = new StackLayout();
        
        final Composite container = new Composite( composite, SWT.BORDER );
        
        container.setLayout( stackLayout );
        
        
        GridData containerData = new GridData(GridData.FILL_HORIZONTAL);
        containerData.grabExcessHorizontalSpace = true;
        containerData.widthHint = 400;
        containerData.heightHint = 300;
        container.setLayoutData( containerData );
        


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
        
        final NavigatorControl nav = new NavigatorControl( composite, SWT.BORDER );

        GridData navData = new GridData( GridData.FILL_HORIZONTAL );
        navData.grabExcessHorizontalSpace = true;
        navData.widthHint = 400;
        navData.heightHint = 250;

        nav.setLayoutData( navData );
        
        
        Button backButton = new Button(composite, SWT.PUSH);
        backButton.setText( "back" );
        
        Button nextButton = new Button(composite, SWT.PUSH);
        nextButton.setText( "next" );
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

        Button button = new Button(composite, SWT.PUSH);
        
        button.setText("change gears number");
        
        
        final Text text = new Text(composite , SWT.NONE);
        
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
    }

    @Override
    public void setFocus()
    {
        // TODO Auto-generated method stub

    }
    
    public interface ActionListener
    {
        public void onAction( UpgradePage page, Action action );
    }

    public interface NavigatorListener
    {
        public void onBack( UpgradePage page );
        public void onNext( UpgradePage page );
    }

    private final List<ActionListener> actionListeners = new ArrayList<ActionListener>();
    private final List<NavigatorListener> navigatorListeners = new ArrayList<NavigatorListener>();
    

    public final void addActionListener(ActionListener listener)
    {
      synchronized (actionListeners)
      {
          actionListeners.add(listener);
      }
    }

    public final ActionListener[] getActionListeners()
    {
      synchronized (actionListeners)
      {
        return actionListeners.toArray(new ActionListener[actionListeners.size()]);
      }
    }

    public final void removeActionListener(ActionListener listener)
    {
      synchronized (actionListeners)
      {
          actionListeners.remove(listener);
      }
    }
    
    public final void addNavigatorListener(NavigatorListener listener)
    {
      synchronized (navigatorListeners)
      {
          navigatorListeners.add(listener);
      }
    }

    public final NavigatorListener[] getNavigatorListeners()
    {
      synchronized (navigatorListeners)
      {
        return navigatorListeners.toArray(new NavigatorListener[navigatorListeners.size()]);
      }
    }

    public final void removeNavigatorListener(NavigatorListener listener)
    {
      synchronized (navigatorListeners)
      {
          navigatorListeners.remove(listener);
      }
    }
    
}

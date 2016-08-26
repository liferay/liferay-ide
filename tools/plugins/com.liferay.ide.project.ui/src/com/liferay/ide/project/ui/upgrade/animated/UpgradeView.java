package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;


public class UpgradeView extends ViewPart
{
    //public static int selection = 0;
    
    private static Composite pageControler = null ;
    
    private static Page[] pages = null;
    
    public static void setSelectPage(int i)
    {
        StackLayout stackLayout = (StackLayout)pageControler.getLayout();
        
        stackLayout.topControl = pages[i];

        pageControler.layout();
    }
    
    @Override
    public void createPartControl( Composite parent )
    {
        final Composite composite = SWTUtil.createComposite( parent, 1, 1, GridData.FILL_BOTH );

        composite.setLayout( new GridLayout( 1 , true ) );

        GridData grData = new GridData(GridData.FILL_BOTH);
        grData.heightHint = 600;
        grData.widthHint = 300;
        composite.setLayoutData( grData );

        final GearControl gear = new GearControl( composite, SWT.NONE );

        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 400;
        gridData.heightHint = 130;

        gear.setLayoutData( gridData );

        gear.setGearsNumber( 7 );


        final StackLayout stackLayout = new StackLayout();

        pageControler = new Composite( composite, SWT.BORDER );

        pageControler.setLayout( stackLayout );

        GridData containerData = new GridData(GridData.FILL_HORIZONTAL);
        containerData.grabExcessHorizontalSpace = true;
        containerData.widthHint = 400;
        containerData.heightHint = 300;
        pageControler.setLayoutData( containerData );

        Page page1 = new DescriptionUpgradePage(pageControler,SWT.BORDER);
        page1.setIndex( 0 );
        
        Page page2 = new DescriptionUpgradePage2(pageControler,SWT.BORDER);
        page2.setIndex( 1 );
        
        Page page3 = new DescriptionUpgradePage3(pageControler,SWT.BORDER);
        page3.setIndex( 2 );

        pages = new Page[3];
        
        pages[0] = page1;
        pages[1] = page2;
        pages[2] = page3;

        final NavigatorControl navigator = new NavigatorControl( composite, SWT.NONE , pageControler , pages );

        navigator.addPageNavigateListener( gear );
        navigator.addPageActionListener( gear );

        GridData navData = new GridData( GridData.FILL_HORIZONTAL );
        navData.grabExcessHorizontalSpace = true;
        navData.widthHint = 400;
        navData.heightHint = 55;

        navigator.setLayoutData( navData );

    }

    @Override
    public void setFocus()
    {

    }

    public interface PageActionListener
    {
        public void onPageAction( PageActionEvent event );
    }

    public interface PageNavigatorListener
    {
        public void onPageNavigate( PageNavigateEvent event );
    }

    private final List<PageActionListener> actionListeners = new ArrayList<PageActionListener>();
    private final List<PageNavigatorListener> navigatorListeners = new ArrayList<PageNavigatorListener>();


    public final void addActionListener(PageActionListener listener)
    {
      synchronized (actionListeners)
      {
          actionListeners.add(listener);
      }
    }

    public final PageActionListener[] getActionListeners()
    {
      synchronized (actionListeners)
      {
        return actionListeners.toArray(new PageActionListener[actionListeners.size()]);
      }
    }

    public final void removeActionListener(PageActionListener listener)
    {
      synchronized (actionListeners)
      {
          actionListeners.remove(listener);
      }
    }

    public final void addNavigatorListener(PageNavigatorListener listener)
    {
      synchronized (navigatorListeners)
      {
          navigatorListeners.add(listener);
      }
    }

    public final PageNavigatorListener[] getNavigatorListeners()
    {
      synchronized (navigatorListeners)
      {
        return navigatorListeners.toArray(new PageNavigatorListener[navigatorListeners.size()]);
      }
    }

    public final void removeNavigatorListener(PageNavigatorListener listener)
    {
      synchronized (navigatorListeners)
      {
          navigatorListeners.remove(listener);
      }
    }



}

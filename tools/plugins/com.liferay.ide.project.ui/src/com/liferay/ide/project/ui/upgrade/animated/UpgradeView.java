
package com.liferay.ide.project.ui.upgrade.animated;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.liferay.ide.ui.util.SWTUtil;

public class UpgradeView extends ViewPart implements SelectionChangedListener
{

    protected LiferayUpgradeDataModel dataModel;

    private LiferayUpgradeDataModel createUpgradeModel()
    {
        return LiferayUpgradeDataModel.TYPE.instantiate();
    }

    public UpgradeView()
    {
        super();
        this.dataModel = createUpgradeModel();
    }

    // public static int selection = 0;

    private static Composite pageControler = null;

    private static Page[] pages = null;

    public void setSelectPage( int i )
    {
        StackLayout stackLayout = (StackLayout) pageControler.getLayout();

        stackLayout.topControl = pages[i];

        pageControler.layout();
    }

    public static Page getPage( int i )
    {
        return pages[i];
    }

    @Override
    public void createPartControl( Composite parent )
    {
        final Composite composite = SWTUtil.createComposite( parent, 1, 1, GridData.FILL_BOTH );

        composite.setLayout( new GridLayout( 1, true ) );

        GridData grData = new GridData( GridData.FILL_BOTH );
        grData.heightHint = 600;
        grData.widthHint = 300;
        composite.setLayoutData( grData );

        final GearControl gear = new GearControl( composite, SWT.NONE );

        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 400;
        gridData.heightHint = 130;

        gear.setLayoutData( gridData );

        gear.setGearsNumber( 3 );

        final StackLayout stackLayout = new StackLayout();

        pageControler = new Composite( composite, SWT.BORDER );

        pageControler.setLayout( stackLayout );

        GridData containerData = new GridData( GridData.FILL_HORIZONTAL );
        containerData.grabExcessHorizontalSpace = true;
        containerData.widthHint = 400;
        containerData.heightHint = 500;
        pageControler.setLayoutData( containerData );

        Page page1 = new DescriptionUpgradePage( pageControler, SWT.BORDER, dataModel );
        page1.setIndex( 0 );
        page1.setTitle( "this is first page" );

        Page page2 = new InitCofigurePrjectPage( pageControler, SWT.BORDER, dataModel );
        page2.setIndex( 1 );
        page2.setTitle( "this is second page" );

        Page page3 = new DescriptionUpgradePage3( pageControler, SWT.BORDER, dataModel );
        page3.setIndex( 2 );
        page3.setTitle( "this is third page" );

        pages = new Page[3];

        pages[0] = page1;
        pages[1] = page2;
        pages[2] = page3;

        final NavigatorControl navigator = new NavigatorControl( composite, SWT.NONE, pages );

        navigator.addPageNavigateListener( gear );
        navigator.addPageActionListener( gear );

        gear.addSelectionChangedListener( navigator );
        gear.addSelectionChangedListener( this );

        
        page2.addPageValidationListener( gear );
        
        
        GridData navData = new GridData( GridData.FILL_HORIZONTAL );
        navData.grabExcessHorizontalSpace = true;
        navData.widthHint = 400;
        navData.heightHint = 55;

        navigator.setLayoutData( navData );

        setSelectPage( 0 );

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
    
    public interface PageValidationListener
    {
        public void onValidation( PageValidateEvent event );
    }

    private final List<PageActionListener> actionListeners = new ArrayList<PageActionListener>();
    private final List<PageNavigatorListener> navigatorListeners = new ArrayList<PageNavigatorListener>();

    public final void addActionListener( PageActionListener listener )
    {
        synchronized( actionListeners )
        {
            actionListeners.add( listener );
        }
    }

    public final PageActionListener[] getActionListeners()
    {
        synchronized( actionListeners )
        {
            return actionListeners.toArray( new PageActionListener[actionListeners.size()] );
        }
    }

    public final void removeActionListener( PageActionListener listener )
    {
        synchronized( actionListeners )
        {
            actionListeners.remove( listener );
        }
    }

    public final void addNavigatorListener( PageNavigatorListener listener )
    {
        synchronized( navigatorListeners )
        {
            navigatorListeners.add( listener );
        }
    }

    public final PageNavigatorListener[] getNavigatorListeners()
    {
        synchronized( navigatorListeners )
        {
            return navigatorListeners.toArray( new PageNavigatorListener[navigatorListeners.size()] );
        }
    }

    public final void removeNavigatorListener( PageNavigatorListener listener )
    {
        synchronized( navigatorListeners )
        {
            navigatorListeners.remove( listener );
        }
    }

    @Override
    public void onSelectionChanged( int targetSelection )
    {
        StackLayout stackLayout = (StackLayout) pageControler.getLayout();

        stackLayout.topControl = pages[targetSelection];

        pageControler.layout();

    }

}

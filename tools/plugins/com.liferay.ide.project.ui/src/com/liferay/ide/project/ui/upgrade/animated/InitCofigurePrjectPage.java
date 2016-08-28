package com.liferay.ide.project.ui.upgrade.animated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.ServerUIUtil;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageValidationListener;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

@SuppressWarnings( "unused" )
public class InitCofigurePrjectPage extends Page implements IServerLifecycleListener, ModifyListener
{
    PageAction[] actions = { new PageFinishAction(), new PageSkipAction() };
    private Text dirField;
    private Text projectNameField;
    private Combo layoutComb;
    private Label layoutLabel;
    private String[] layoutNames = {"Upgrade to Liferay SDK 7", "Use Plugin SDK In Liferay Workspace"};
    private Label serverLabel;
    private Combo serverComb;
    private Button serverButton;
    
    private String[] errors;

    public InitCofigurePrjectPage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel );
        
        GridLayout layout = new GridLayout(2, false);

        // set the layout to the shell
        setLayout(layout);
        setLayoutData(new GridData( GridData.FILL_BOTH ));
        setBackground( parent.getBackground() );
        this.dirField = createTextField( "Liferay SDK folder:" );
        dirField.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    if ( e.getSource().equals( dirField ) )
                    {
                        dataModel.setSdkLocation( dirField.getText() );
                    }
                    
                }
            }
        );        
        SWTUtil.createButton( this, "Browse..." ).addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                final DirectoryDialog dd = new DirectoryDialog( getShell() );
                dd.setMessage( "Select source SDK folder" );

                final String selectedDir = dd.open();

                if( selectedDir != null )
                {
                    dirField.setText( selectedDir );
                }
            }
        });
        this.projectNameField = createTextField( "Project Name:" );
        projectNameField.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    if ( e.getSource().equals( projectNameField ) )
                    {
                        dataModel.setProjectName( projectNameField.getText() );
                    }
                    
                }
            }
        ); 
        
        layoutLabel = createLabel( "Select Migrate Layout:" );
        layoutComb = new Combo( this, SWT.DROP_DOWN | SWT.READ_ONLY );
        layoutComb.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        layoutComb.setItems( layoutNames );
        layoutComb.select( 0 );
        layoutComb.addModifyListener( new ModifyListener()
        {
            @Override
            public void modifyText( ModifyEvent e )
            {
                if ( e.getSource().equals( layoutComb ) )
                {
                    int sel = layoutComb.getSelectionIndex();

                    if ( sel > 0 )
                    {
                        serverLabel.setVisible( true );
                        serverButton.setVisible( true );
                        serverComb.setVisible( true );
                    }
                    else
                    {
                        serverLabel.setVisible( false );
                        serverButton.setVisible( false );
                        serverComb.setVisible( false );                       
                    }
                    

                    dataModel.setLayout( layoutComb.getText() );
                }
            }
        } );
        
        serverLabel = createLabel( "Liferay Server Name:" );
        serverComb = new Combo( this, SWT.DROP_DOWN | SWT.READ_ONLY );
        serverComb.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        
        serverComb.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    if ( e.getSource().equals( serverComb ) )
                    {
                        dataModel.setLiferayServerName( serverComb.getText() );
                    }
                    
                }
            }
        ); 
        
        serverButton = SWTUtil.createButton( this, "Add Server..." );
        serverButton.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                ServerUIUtil.showNewServerWizard( parent.getShell(), "liferay.bundle", null,
                                "com.liferay." );
            }
        });  

        
        
        if ( layoutComb.getSelectionIndex() == 0 )
        {
            serverLabel.setVisible( false );
            serverButton.setVisible( false );
            serverComb.setVisible( false );
        }
        
        ServerCore.addServerLifecycleListener( this );
        
        IServer[] servers = ServerCore.getServers();
        List<String> serverNames = new ArrayList<String>();
        if( !CoreUtil.isNullOrEmpty( servers ) )
        {
            for( IServer server : servers )
            {
                if( LiferayServerCore.newPortalBundle( server.getRuntime().getLocation() ) != null )
                {
                    serverNames.add( server.getName() );
                }
            }
        }
        serverComb.setItems( serverNames.toArray( new String[serverNames.size()] ) );        
        
/*        

        dataModel.getSdkLocation().attach( new Listener(){

            @Override
            public void handle( Event event )
            {
                if (event instanceof ValuePropertyContentEvent )
                {
                    System.out.println( dataModel.getSdkLocation().content() );
                }
                
                
            }
            
        });
        txtTest.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    if ( e.getSource().equals( txtTest ) )
                    {
                        System.out.println( "sdfdsfd" );
                    }
                    dataModel.setSdkLocation( txtTest.getText() );
                }
            }
        );
*/

        setActions( actions );
    }

    @Override
    protected boolean showBackPage()
    {
        return true;
    }

    @Override
    protected boolean showNextPage()
    {
        return true;
    }

    @Override
    public void serverAdded( IServer server )
    {
        UIUtil.async( new Runnable()
        {
            @Override
            public void run()
            {
                boolean serverExisted = false;

                if( serverComb != null && !serverComb.isDisposed() )
                {
                    String[] serverNames = serverComb.getItems();
                    List<String> serverList = new ArrayList<>(Arrays.asList(serverNames));
                    
                    for( String serverName : serverList )
                    {
                        if( server.getName().equals( serverName ) )
                        {
                            serverExisted = true;
                        }
                    }
                    if( serverExisted == false )
                    {
                        serverList.add( server.getName() );
                        serverComb.setItems( serverList.toArray( new String[serverList.size()] ) );
                        serverComb.select( serverList.size()-1);
                    }
                }
            }
        } );
    }

    @Override
    public void serverChanged( IServer server )
    {
    }

    @Override
    public void serverRemoved( IServer server )
    {
        UIUtil.async( new Runnable()
        {

            @Override
            public void run()
            {
                if ( serverComb != null && !serverComb.isDisposed())
                {
                    String[] serverNames = serverComb.getItems();
                    List<String> serverList = new ArrayList<>(Arrays.asList(serverNames));
                    
                    Iterator<String> serverNameiterator = serverList.iterator();
                    while(serverNameiterator.hasNext())
                    {
                        String serverName = serverNameiterator.next(); 
                        if ( server.getName().equals( serverName ) )
                        {
                            serverNameiterator.remove();
                        }
                    }
                    serverComb.setItems( serverList.toArray( new String[serverList.size()] ) );
                    serverComb.select( 0 );
                }
            }
        });

    }
    
    @Override
    public void modifyText( ModifyEvent e )
    {
        if( e.getSource().equals( dirField ) )
        {
            //check dirField status and validataion
            //check server version
            //check project name validation
            validate();
        }
    }

    
    private boolean validate()
    {
        if ( errors != null && errors.length > 0)
        {
            PageValidateEvent event = new PageValidateEvent();
            for( PageValidationListener listener : pageValidationListeners )
            {
                listener.onValidation( event );
            }
        }
        return false;
    }
}

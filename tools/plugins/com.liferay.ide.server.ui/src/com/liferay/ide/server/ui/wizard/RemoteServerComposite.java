/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.remote.IRemoteServer;
import com.liferay.ide.server.remote.IRemoteServerWorkingCopy;
import com.liferay.ide.server.remote.RemoteServer;
import com.liferay.ide.server.remote.RemoteUtil;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.ui.LiferayUIPlugin;
import com.liferay.ide.ui.util.SWTUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Greg Amerson
 */
public class RemoteServerComposite extends Composite implements ModifyListener, PropertyChangeListener
{

    protected boolean disableValidation;
    protected RemoteServerWizardFragment fragment;
    protected boolean ignoreModifyEvents;
    protected Label labelHttpPort;
    protected Label labelLiferayPortalContextPath;
    protected Label labelPassword;
    protected Label labelServerManagerContextPath;
    protected Label labelUsername;
    protected IRemoteServerWorkingCopy remoteServerWC;
    protected IServerWorkingCopy serverWC;
    protected Text textHostname;
    protected Text textHttp;
    protected Text textLiferayPortalContextPath;
    protected Text textPassword;
    protected Text textServerManagerContextPath;
    protected Text textUsername;
    protected IWizardHandle wizard;
    private String initialServerName;
    private String initialHostName;

    public RemoteServerComposite( Composite parent, RemoteServerWizardFragment fragment, IWizardHandle wizard )
    {
        super( parent, SWT.NONE );
        this.fragment = fragment;
        this.wizard = wizard;

        createControl( parent );
    }

    public void modifyText( ModifyEvent e )
    {
        Object src = e.getSource();

        if( src == null || ignoreModifyEvents )
        {
            return;
        }

        if( src.equals( textHostname ) )
        {
            this.serverWC.setHost( textHostname.getText() );

            // IDE-425
            if( this.initialServerName != null && this.initialHostName.contains( this.initialHostName ) )
            {
                this.serverWC.setName( this.initialServerName.replaceAll( this.initialHostName, textHostname.getText() ) );
            }

        }
        else if( src.equals( textHttp ) )
        {
            this.remoteServerWC.setHttpPort( Integer.valueOf( textHttp.getText() ) );
        }
        else if( src.equals( textServerManagerContextPath ) )
        {
            this.remoteServerWC.setServerManagerContextPath( textServerManagerContextPath.getText() );
        }
        else if( src.equals( textLiferayPortalContextPath ) )
        {
            this.remoteServerWC.setLiferayPortalContextPath( textLiferayPortalContextPath.getText() );
        }
        else if( src.equals( textUsername ) )
        {
            this.remoteServerWC.setUsername( textUsername.getText() );
        }
        else if( src.equals( textPassword ) )
        {
            this.remoteServerWC.setPassword( textPassword.getText() );
        }

    }

    public void propertyChange( PropertyChangeEvent evt )
    {
        if( IRemoteServer.ATTR_HOSTNAME.equals( evt.getPropertyName() ) ||
            IRemoteServer.ATTR_HTTP_PORT.equals( evt.getPropertyName() ) ||
            IRemoteServer.ATTR_USERNAME.equals( evt.getPropertyName() ) ||
            IRemoteServer.ATTR_PASSWORD.equals( evt.getPropertyName() ) ||
            IRemoteServer.ATTR_LIFERAY_PORTAL_CONTEXT_PATH.equals( evt.getPropertyName() ) ||
            IRemoteServer.ATTR_SERVER_MANAGER_CONTEXT_PATH.equals( evt.getPropertyName() ) )
        {

            LiferayServerCore.updateConnectionSettings( (IRemoteServer) serverWC.loadAdapter(
                IRemoteServer.class, null ) );
        }
    }

    public void setServer( IServerWorkingCopy newServer )
    {
        if( newServer == null )
        {
            serverWC = null;
            remoteServerWC = null;
        }
        else
        {
            serverWC = newServer;
            remoteServerWC = (IRemoteServerWorkingCopy) serverWC.loadAdapter( IRemoteServerWorkingCopy.class, null );

            serverWC.addPropertyChangeListener( this );
        }

        disableValidation = true;
        initialize();
        disableValidation = false;
        validate();
    }

    protected void createControl( Composite parent )
    {
        setLayout( new GridLayout( 1, false ) );
        setBackground( parent.getBackground() );

        disableValidation = true;
        Group connectionGroup = SWTUtil.createGroup( this, Msgs.connectionSettings, 2 );
        connectionGroup.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );

        Label labelHostname = new Label( connectionGroup, SWT.NONE );
        labelHostname.setText( Msgs.hostname );

        textHostname = new Text( connectionGroup, SWT.BORDER );
        textHostname.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        textHostname.addModifyListener( this );

        labelHttpPort = new Label( connectionGroup, SWT.NONE );
        labelHttpPort.setText( Msgs.httpPortLabel );

        textHttp = new Text( connectionGroup, SWT.BORDER );
        textHttp.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );
        textHttp.addModifyListener( this );

        labelUsername = new Label( connectionGroup, SWT.NONE );
        labelUsername.setText( Msgs.username );

        textUsername = new Text( connectionGroup, SWT.BORDER );
        textUsername.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );
        textUsername.addModifyListener( this );

        labelPassword = new Label( connectionGroup, SWT.NONE );
        labelPassword.setText( Msgs.password );

        textPassword = new Text( connectionGroup, SWT.BORDER | SWT.PASSWORD );
        textPassword.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        textPassword.addModifyListener( this );

        labelLiferayPortalContextPath = new Label( connectionGroup, SWT.NONE );
        labelLiferayPortalContextPath.setText( Msgs.liferayPortalContextPath );
        labelLiferayPortalContextPath.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false ) );

        textLiferayPortalContextPath = new Text( connectionGroup, SWT.BORDER );
        textLiferayPortalContextPath.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        textLiferayPortalContextPath.addModifyListener( this );

        labelServerManagerContextPath = new Label( connectionGroup, SWT.NONE );
        labelServerManagerContextPath.setText( Msgs.serverManagerContextPath );
        labelServerManagerContextPath.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false ) );

        textServerManagerContextPath = new Text( connectionGroup, SWT.BORDER );
        textServerManagerContextPath.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        textServerManagerContextPath.addModifyListener( this );

        final String marketplaceLinkLabel = Msgs.installRemoteIDEConnector;
        final String appUrl = "http://www.liferay.com/marketplace/-/mp/application/15193785"; //$NON-NLS-1$
        SWTUtil.createHyperLink( this, SWT.NONE, marketplaceLinkLabel, 1, appUrl );

        final String installLabel = NLS.bind( "<a>{0}</a>", Msgs.clickHereLink ); //$NON-NLS-1$
        final String installUrl = "{0}/group/control_panel/manage?p_p_id=1_WAR_marketplaceportlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&appId=15193785";  //$NON-NLS-1$
        final Link installLink = SWTUtil.createLink( this, SWT.NONE, installLabel, 1 );
        installLink.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    try
                    {
                        final String url =
                            MessageFormat.format( installUrl, "http://" + textHostname.getText() + ":" + textHttp.getText() ); //$NON-NLS-1$ //$NON-NLS-2$
                        PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL( new URL( url ) );
                    }
                    catch ( Exception e1 )
                    {
                        LiferayUIPlugin.logError( "Could not open external browser.", e1 ); //$NON-NLS-1$
                    }
                };
            }
        );

        Composite validateComposite = new Composite( this, SWT.NONE );
        validateComposite.setLayoutData( new GridData( SWT.LEFT, SWT.BOTTOM, false, true ) );
        validateComposite.setLayout( new GridLayout( 1, false ) );

        Button validateButton = new Button( validateComposite, SWT.PUSH );
        validateButton.setText( Msgs.validateConnection );
        validateButton.setLayoutData( new GridData( SWT.LEFT, SWT.BOTTOM, false, false ) );
        validateButton.addSelectionListener( new SelectionAdapter()
        {

            public void widgetSelected( SelectionEvent e )
            {
                validate();
            }
        } );

        // initDataBindings();
        disableValidation = false;

        validate();
    }

    protected RemoteServer getRemoteServer()
    {
        if( serverWC != null )
        {
            return (RemoteServer) serverWC.loadAdapter( RemoteServer.class, null );
        }
        else
        {
            return null;
        }
    }

    protected void initialize()
    {
        if( this.serverWC != null && this.remoteServerWC != null )
        {
            ignoreModifyEvents = true;
            this.textHostname.setText( this.serverWC.getHost() );
            this.textHttp.setText( String.valueOf( this.remoteServerWC.getHttpPort() ) );
            this.textLiferayPortalContextPath.setText( this.remoteServerWC.getLiferayPortalContextPath() );
            this.textServerManagerContextPath.setText( this.remoteServerWC.getServerManagerContextPath() );
            // this.checkboxSecurity.setSelection( this.remoteServerWC.getSecurityEnabled() );
            this.textUsername.setText( this.remoteServerWC.getUsername() );
            this.textPassword.setText( this.remoteServerWC.getPassword() );

            this.initialServerName = this.serverWC.getName();
            this.initialHostName = this.serverWC.getHost();

            ignoreModifyEvents = false;
        }
    }

    protected void validate()
    {
        if( disableValidation )
        {
            return;
        }

        if( serverWC == null )
        {
            wizard.setMessage( StringPool.EMPTY, IMessageProvider.ERROR );
            return;
        }

        try
        {
            IRunnableWithProgress validateRunnable = new IRunnableWithProgress()
            {

                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {

                    final IStatus updateStatus = validateServer( monitor );

                    if( updateStatus.isOK() )
                    {
                        String contextPath = RemoteUtil.detectServerManagerContextPath( getRemoteServer(), monitor );

                        remoteServerWC.setServerManagerContextPath( contextPath );
                    }

                    RemoteServerComposite.this.getDisplay().syncExec( new Runnable()
                    {
                        public void run()
                        {
                            if( updateStatus == null || updateStatus.isOK() )
                            {
                                wizard.setMessage( null, IMessageProvider.NONE );
                            }
                            else if( updateStatus.getSeverity() == IStatus.WARNING ||
                                     updateStatus.getSeverity() == IStatus.ERROR )
                            {
                                if( updateStatus.getMessage().contains( "Your license key has expired" ) ||
                                    updateStatus.getMessage().contains( "Register Your Server or Application" ) )
                                {
                                    wizard.setMessage(
                                        "Server is not registered or license key has expired ",
                                        IMessageProvider.WARNING );
                                }
                                else
                                {
                                    wizard.setMessage( updateStatus.getMessage(), IMessageProvider.WARNING );
                                }
                            }

                            wizard.update();

                        }
                    });
                }
            };

            wizard.run( true, true, validateRunnable );
            wizard.update();

            if( fragment.lastServerStatus != null && fragment.lastServerStatus.isOK() )
            {
                ignoreModifyEvents = true;

                textServerManagerContextPath.setText( this.remoteServerWC.getServerManagerContextPath() );
                textLiferayPortalContextPath.setText( this.remoteServerWC.getLiferayPortalContextPath() );

                ignoreModifyEvents = false;
            }
        }
        catch( final Exception e )
        {
            RemoteServerComposite.this.getDisplay().syncExec( new Runnable()
            {

                public void run()
                {
                    wizard.setMessage( e.getMessage(), IMessageProvider.WARNING );
                    wizard.update();
                }
            } );
        }
    }

    protected IStatus validateServer( IProgressMonitor monitor )
    {
        String host = serverWC.getHost();

        if( CoreUtil.isNullOrEmpty( host ) )
        {
            return LiferayServerUI.createErrorStatus( Msgs.specifyHostname );
        }

        String username = remoteServerWC.getUsername();

        if( CoreUtil.isNullOrEmpty( username ) )
        {
            return LiferayServerUI.createErrorStatus( Msgs.specifyUsernamePassword );
        }

        IStatus status = remoteServerWC.validate( monitor );

        if( status != null && status.getSeverity() == IStatus.ERROR )
        {
            fragment.lastServerStatus =
                new Status( IStatus.WARNING, status.getPlugin(), status.getMessage(), status.getException() );
        }
        else
        {
            fragment.lastServerStatus = status;
        }

        return status;
    }

    private static class Msgs extends NLS
    {
        public static String clickHereLink;
        public static String connectionSettings;
        public static String hostname;
        public static String httpPortLabel;
        public static String installRemoteIDEConnector;
        public static String liferayPortalContextPath;
        public static String password;
        public static String serverManagerContextPath;
        public static String specifyHostname;
        public static String specifyUsernamePassword;
        public static String username;
        public static String validateConnection;

        static
        {
            initializeMessages( RemoteServerComposite.class.getName(), Msgs.class );
        }
    }
}

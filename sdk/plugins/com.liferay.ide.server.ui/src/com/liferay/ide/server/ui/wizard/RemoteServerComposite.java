/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
import com.liferay.ide.server.core.LiferayServerCorePlugin;
import com.liferay.ide.server.remote.IRemoteServer;
import com.liferay.ide.server.remote.IRemoteServerWorkingCopy;
import com.liferay.ide.server.remote.RemoteServer;
import com.liferay.ide.server.remote.RemoteUtil;
import com.liferay.ide.server.ui.LiferayServerUIPlugin;
import com.liferay.ide.ui.util.SWTUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
    protected Text textHTTP;
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

        createControl();
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
        else if( src.equals( textHTTP ) )
        {
            this.remoteServerWC.setHTTPPort( textHTTP.getText() );
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

            LiferayServerCorePlugin.updateConnectionSettings( (IRemoteServer) serverWC.loadAdapter(
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

    protected void createControl()
    {
        setLayout( new GridLayout( 1, false ) );

        disableValidation = true;
        Group connectionGroup = SWTUtil.createGroup( this, "Connection Settings", 2 );
        connectionGroup.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );

        Label labelHostname = new Label( connectionGroup, SWT.NONE );
        labelHostname.setText( "Hostname:" );

        textHostname = new Text( connectionGroup, SWT.BORDER );
        textHostname.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        textHostname.addModifyListener( this );

        labelHttpPort = new Label( connectionGroup, SWT.NONE );
        labelHttpPort.setText( "HTTP port:" );

        textHTTP = new Text( connectionGroup, SWT.BORDER );
        textHTTP.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );
        textHTTP.addModifyListener( this );

        labelUsername = new Label( connectionGroup, SWT.NONE );
        labelUsername.setText( "Username:" );

        textUsername = new Text( connectionGroup, SWT.BORDER );
        textUsername.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );
        textUsername.addModifyListener( this );

        labelPassword = new Label( connectionGroup, SWT.NONE );
        labelPassword.setText( "Password:" );

        textPassword = new Text( connectionGroup, SWT.BORDER | SWT.PASSWORD );
        textPassword.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        textPassword.addModifyListener( this );

        labelLiferayPortalContextPath = new Label( connectionGroup, SWT.NONE );
        labelLiferayPortalContextPath.setText( "Liferay Portal Context Path:" );
        labelLiferayPortalContextPath.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false ) );

        textLiferayPortalContextPath = new Text( connectionGroup, SWT.BORDER );
        textLiferayPortalContextPath.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        textLiferayPortalContextPath.addModifyListener( this );

        labelServerManagerContextPath = new Label( connectionGroup, SWT.NONE );
        labelServerManagerContextPath.setText( "Server Manager Context Path:" );
        labelServerManagerContextPath.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false ) );

        textServerManagerContextPath = new Text( connectionGroup, SWT.BORDER );
        textServerManagerContextPath.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        textServerManagerContextPath.addModifyListener( this );

        Link link =
            SWTUtil.createLink(
                this, SWT.NONE,
                "Need to the install server-manager-web plugin? <a>Download latest version from here.</a>", 1 );
        final String downloadUrl = "http://sourceforge.net/projects/lportal/files/Liferay%20Plugins/";
        link.addSelectionListener( new SelectionAdapter()
        {

            public void widgetSelected( SelectionEvent e )
            {
                try
                {
                    PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL( new URL( downloadUrl ) );
                }
                catch( Exception e1 )
                {
                    LiferayServerUIPlugin.logError( "Could not open external browser.", e1 );
                }
            }
        } );

        Composite validateComposite = new Composite( this, SWT.NONE );
        validateComposite.setLayoutData( new GridData( SWT.LEFT, SWT.BOTTOM, false, true ) );
        validateComposite.setLayout( new GridLayout( 1, false ) );

        Button validateButton = new Button( validateComposite, SWT.PUSH );
        validateButton.setText( "Validate connection" );
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
            this.textHTTP.setText( this.remoteServerWC.getHTTPPort() );
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
            wizard.setMessage( "", IMessageProvider.ERROR );
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
                                wizard.setMessage( updateStatus.getMessage(), IMessageProvider.WARNING );
                            }

                            wizard.update();

                        }
                    } );
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
            return LiferayServerUIPlugin.createErrorStatus( "Must specify hostname" );
        }

        String username = remoteServerWC.getUsername();

        if( CoreUtil.isNullOrEmpty( username ) )
        {
            return LiferayServerUIPlugin.createErrorStatus( "Must specify username and password" );
        }

        String port = remoteServerWC.getHTTPPort();

        if( CoreUtil.isNullOrEmpty( port ) )
        {
            return LiferayServerUIPlugin.createErrorStatus( "Must specify HTTP port" );
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

}

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

import com.liferay.ide.server.remote.IRemoteServerWorkingCopy;
import com.liferay.ide.server.ui.LiferayServerUIPlugin;
import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class RemoteServerWizardFragment extends WizardFragment
{
    protected RemoteServerComposite composite;
    protected IStatus lastServerStatus = null;
    protected IWizardHandle wizard;

    public RemoteServerWizardFragment()
    {
        super();
    }

    @Override
    public Composite createComposite( Composite parent, IWizardHandle wizard )
    {
        this.wizard = wizard;

        composite = new RemoteServerComposite( parent, this, wizard );

        wizard.setTitle( Msgs.remoteLiferayServer );
        wizard.setDescription( Msgs.configureRemoteLiferayServerInstance );
        wizard.setImageDescriptor( ImageDescriptor.createFromURL( LiferayServerUIPlugin.getDefault().getBundle().getEntry(
            "/icons/wizban/server_wiz.png" ) ) ); //$NON-NLS-1$

        return composite;
    }

    @Override
    public void enter()
    {
        if( composite != null && !composite.isDisposed() )
        {
            IServerWorkingCopy serverWC = getServerWorkingCopy();

            // need to set defaults now that we have a connection

            composite.setServer( serverWC );
        }
    }

    @Override
    public boolean hasComposite()
    {
        return true;
    }

    @Override
    public boolean isComplete()
    {
        return lastServerStatus != null && lastServerStatus.getSeverity() != IStatus.ERROR;
    }

    @Override
    public void performFinish( IProgressMonitor monitor ) throws CoreException
    {
        try
        {
            this.wizard.run( false, false, new IRunnableWithProgress()
            {
                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {

                    if( lastServerStatus == null || ( !lastServerStatus.isOK() ) )
                    {
                        lastServerStatus = getRemoteServerWC().validate( monitor );

                        if( !lastServerStatus.isOK() )
                        {
                            throw new InterruptedException( lastServerStatus.getMessage() );
                        }
                    }
                }
            } );
        }
        catch( Exception e )
        {
        }

        ServerCore.addServerLifecycleListener( new IServerLifecycleListener()
        {
            String id = getServerWorkingCopy().getId();

            public void serverAdded( final IServer server )
            {
                if( server.getId().equals( id ) )
                {
                    UIUtil.async( new Runnable()
                    {

                        public void run()
                        {
                            IViewPart serversView = UIUtil.showView( "org.eclipse.wst.server.ui.ServersView" ); //$NON-NLS-1$
                            CommonViewer viewer = (CommonViewer) serversView.getAdapter( CommonViewer.class );
                            viewer.setSelection( new StructuredSelection( server ) );
                        }
                    } );

                    ServerCore.removeServerLifecycleListener( this );

                    server.addServerListener( new IServerListener()
                    {
                        public void serverChanged( ServerEvent event )
                        {
                            if( event.getServer().getServerState() == IServer.STATE_STARTED )
                            {
                                server.publish( IServer.PUBLISH_INCREMENTAL, null, null, null );

                                server.removeServerListener( this );
                            }
                        }
                    });
                }
            }

            public void serverChanged( IServer server )
            {
            }

            public void serverRemoved( IServer server )
            {
            }

        } );

    }

    protected IServerWorkingCopy getServerWorkingCopy()
    {
        return (IServerWorkingCopy) getTaskModel().getObject( TaskModel.TASK_SERVER );
    }

    IRemoteServerWorkingCopy getRemoteServerWC()
    {
        return (IRemoteServerWorkingCopy) getServerWorkingCopy().loadAdapter( IRemoteServerWorkingCopy.class, null );
    }

    private static class Msgs extends NLS
    {
        public static String configureRemoteLiferayServerInstance;
        public static String remoteLiferayServer;

        static
        {
            initializeMessages( RemoteServerWizardFragment.class.getName(), Msgs.class );
        }
    }
}

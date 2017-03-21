/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui.navigator;

import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.util.KaleoUtil;
import com.liferay.ide.server.ui.PluginsCustomContentProvider;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.internal.navigator.NavigatorContentService;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class WorkflowDefinitionsCustomContentProvider extends PluginsCustomContentProvider
{

    protected final static Object[] EMPTY = new Object[] {};

    private final Map<String, WorkflowDefinitionsFolder> workflowDefinitionFolders =
        new HashMap<String, WorkflowDefinitionsFolder>();

    private final Map<String, IStatus> checkApiStatuses = new HashMap<String, IStatus>();

    public WorkflowDefinitionsCustomContentProvider()
    {
        super();
    }

    public void dispose()
    {
    }

    public Object[] getChildren( Object parentElement )
    {
        if( parentElement instanceof IServer )
        {
            return null;
        }
        else if( parentElement instanceof WorkflowDefinitionsFolder )
        {
            return ( (WorkflowDefinitionsFolder) parentElement ).getChildren();
        }

        return null;
    }

    public Object getParent( Object element )
    {
        if( element instanceof IWorkspaceRoot )
        {
            return null;
        }

        return null;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public void getPipelinedChildren( final Object parent, final Set currentChildren )
    {
        if( parent instanceof IServer )
        {
            final IServer server = (IServer) parent;

//            final IServerListener listener = new IServerListener()
//            {
//                @Override
//                public void serverChanged( ServerEvent event )
//                {
//                    try
//                    {
//                        WorkflowDefinitionsCustomContentProvider.this.checkApiStatuses.remove( server.getId() );
//                        refreshUI( (NavigatorContentService) getConfig().getService(), server );
//                    }
//                    catch( Exception e) {}
//                }
//            };
//            server.addServerListener( listener );

            if( server.getServerState() == IServer.STATE_STARTED )
            {
                final WorkflowDefinitionsFolder definitionsNode = this.workflowDefinitionFolders.get( server.getId() );

                if( definitionsNode == null )
                {
                    final IStatus checkApiStatus = this.checkApiStatuses.get( server.getId() );

                    if( checkApiStatus == null )
                    {
                        Job checkJob = new Job( "Checking for Kaleo Designer API..." )
                        {
                            @Override
                            protected IStatus run( IProgressMonitor monitor )
                            {
                                try
                                {
                                    IKaleoConnection kaleoConnection = KaleoCore.getKaleoConnection( server );
                                    KaleoCore.updateKaleoConnectionSettings(
                                        KaleoUtil.getLiferayServer( server, monitor ), kaleoConnection );

                                    kaleoConnection.getKaleoDefinitions();
                                    kaleoConnection.getKaleoDraftWorkflowDefinitions();

                                    WorkflowDefinitionsCustomContentProvider.this.checkApiStatuses.put(
                                        server.getId(), Status.OK_STATUS );

                                    refreshUI( (NavigatorContentService) getConfig().getService(), server );
                                }
                                catch (Exception e)
                                {
                                    WorkflowDefinitionsCustomContentProvider.this.checkApiStatuses.put(
                                        server.getId(), KaleoUI.createErrorStatus( e ) );
                                }

                                return Status.OK_STATUS;
                            }
                        };

                        checkJob.schedule();
                    }
                    else
                    {
                        if( checkApiStatus.isOK() )
                        {
                            insertDefinitionsNode( server, currentChildren );
                            WorkflowDefinitionsCustomContentProvider.this.checkApiStatuses.put( server.getId(), null );
                        }
                        else
                        {
                            KaleoUI.logInfo("Kaleo Workflow API unavailable.", checkApiStatus);
                        }
                    }
                }
                else
                {
                    if( !currentChildren.contains( definitionsNode ) )
                    {
                        currentChildren.add( definitionsNode );
                        definitionsNode.getChildren(); // make sure children are cached.
                    }
                }
            }
            else
            {
                this.workflowDefinitionFolders.put( server.getId(), null );
                this.checkApiStatuses.put( server.getId(), null );
            }
        }
    }

    protected void refreshUI( final NavigatorContentService s, final IServer server )
    {
        UIUtil.async
        (
            new Runnable()
            {
                public void run()
                {
                    try
                    {
                        final CommonViewer viewer = (CommonViewer) s.getViewer();
                        viewer.refresh( true );
                        viewer.setExpandedState( server, true );
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
        );
    }

    public Object getPipelinedParent( final Object object, final Object suggestedParent )
    {
        if( object instanceof WorkflowDefinitionsFolder )
        {
            return ( (WorkflowDefinitionsFolder) object ).getParent();

            // if ( ProjectUtil.isLiferayProject( project ) && this.definitionsContentNode != null )
            // {
            // return this.definitionsContentNode;
            // }
        }
        // else if ( anObject instanceof DefinitionsContainer && anObject.equals( this.definitionsContentNode ) )
        // {
        // return this.definitionsContentNode.getParent();
        // }

        return null;
    }

    public boolean hasChildren( Object element )
    {
        if( element instanceof IServer )
        {
            IServer server = (IServer) element;

            final WorkflowDefinitionsFolder definitionsNode = this.workflowDefinitionFolders.get( server.getId() );

            if( definitionsNode != null )
            {
                return true;
            }

            if( ServerUtil.isLiferayRuntime( server ) )
            {
                if( server.getServerState() == IServer.STATE_STARTED )
                {
                    return true;
                }
            }
        }
        else if( element instanceof WorkflowDefinitionsFolder )
        {
            return true;
        }

        return false;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private void insertDefinitionsNode( IServer server, Set currentChildren )
    {
        WorkflowDefinitionsFolder node = new WorkflowDefinitionsFolder( this.getConfig(), server );

        this.workflowDefinitionFolders.put( server.getId(), node );

        currentChildren.add( node );
    }

    public boolean interceptRefresh( PipelinedViewerUpdate viewerUpdate )
    {
        for( Object refreshTarget : viewerUpdate.getRefreshTargets() )
        {
            if( refreshTarget instanceof IServer )
            {
                clearStatuses((IServer) refreshTarget);
            }
        }

        return false;
    }

    private void clearStatuses( IServer server )
    {
        if( ServerUtil.isLiferayRuntime( server ) )
        {
            if( server.getServerState() != IServer.STATE_STARTED )
            {
                this.checkApiStatuses.put( server.getId(), null );
            }
        }
    }

    public boolean interceptUpdate( PipelinedViewerUpdate viewerUpdate )
    {
        for( Object updateTarget : viewerUpdate.getRefreshTargets() )
        {
            if( updateTarget instanceof IServer )
            {
                clearStatuses( (IServer) updateTarget );
            }
        }

        return false;
    }

}

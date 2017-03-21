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

package com.liferay.ide.kaleo.ui.editor;

import static com.liferay.ide.kaleo.core.util.KaleoModelUtil.DEFAULT_POINT;
import static java.lang.Math.min;

import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.ConnectionBendpoint;
import com.liferay.ide.kaleo.core.model.Position;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.model.TransitionMetadata;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;
import com.liferay.ide.kaleo.core.model.WorkflowNodeMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.ConnectionAddEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionBendpointsEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionDeleteEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionService;
import org.eclipse.sapphire.ui.diagram.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeAddEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeBounds;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeDeleteEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeMoveEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramPageEvent;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart.PostAutoLayoutEvent;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart.PreAutoLayoutEvent;
import org.eclipse.sapphire.ui.diagram.layout.ConnectionHashKey;
import org.eclipse.sapphire.ui.diagram.layout.DiagramLayoutPersistenceService;
import org.eclipse.sapphire.util.ListFactory;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class WorkflowDefinitionLayoutPersistenceService extends DiagramLayoutPersistenceService
{

    private boolean dirty;
    private HashMap<String, DiagramNodeBounds> nodeBounds;
    private HashMap<ConnectionHashKey, List<Point>> connectionBendPoints;
    private HashMap<ConnectionHashKey, Point> connectionLabelPositions;
    private HashMap<String, List<Transition>> unconnectedTransitions;
    private Listener connectionPartListener;
    private Listener bendpointListener;
    private Listener workflowNodeMetadataListener;
    private Listener workflowNodeListener;
    private Listener diagramPartListener;
    private boolean autoLayout = false;


    @Override
    protected void init()
    {
        super.init();

        this.nodeBounds = new HashMap<String, DiagramNodeBounds>();
        this.connectionBendPoints = new HashMap<ConnectionHashKey, List<Point>>();
        this.connectionLabelPositions = new HashMap<ConnectionHashKey, Point>();
        this.unconnectedTransitions = new HashMap<String, List<Transition>>();
        this.dirty = false;

        this.connectionPartListener = new FilteredListener<ConnectionBendpointsEvent>()
        {
            protected void handleTypedEvent( ConnectionBendpointsEvent event )
            {
                if( event.reset() )
                {
                    if( autoLayout )
                    {
                        addConnectionToPersistenceCache( event.part() );
                        refreshDirtyState();
                    }
                    else
                    {
                        write( event.part() );
                    }

                }
                else
                {
                    write( event.part() );
                }
            }
        };

        load();
        refreshPersistedPartsCache();
        addDiagramPartListener();
        addModelListeners();
    }

    private void setGridVisible( boolean gridVisible )
    {
        part().adapt( WorkflowDefinitionEditor.class ).setGridVisible( gridVisible );
    }

    private void setGuidesVisible( boolean showGuides )
    {
        part().adapt( WorkflowDefinitionEditor.class ).setShowGuides( showGuides );
    }

    private void read( DiagramNodePart nodePart )
    {
        final WorkflowNode workflowNode = nodePart.getLocalModelElement().nearest( WorkflowNode.class );

        if( ! workflowNode.disposed() )
        {
            final WorkflowNodeMetadata metadata = workflowNode.getMetadata().content();

            if( metadata.getPosition().getX().content() != -1 && metadata.getPosition().getY().content() != -1 )
            {
                writeWorkflowNodeMetaDataToBounds( metadata, nodePart );
            }

            final String nodeId = nodePart.getId();

            if( this.nodeBounds.containsKey( nodeId ) && this.nodeBounds.get( nodeId ) != null )
            {
                nodePart.setNodeBounds( this.nodeBounds.get( nodeId ) );
            }
            else
            {
                this.nodeBounds.put( nodeId, nodePart.getNodeBounds() );
            }
        }

        // handle the case where a transition is added and its target node hasn't been. 
        // keep the transitions in a map and every time a new node is added, 
        // check if it is a target of unconnected transitions, reconnect the DiagramConnectionPart
        final SapphireDiagramEditorPagePart part = part();
        final ConnectionService connService = part.service( ConnectionService.class );

        if( workflowNode instanceof CanTransition )
        {
            for( Transition transition : ( (CanTransition) workflowNode ).getTransitions() )
            {
                if( transition.getTarget().target() == null )
                {
                    if( ! unconnectedTransitions.containsKey( transition.getTarget().content() ) )
                    {
                        unconnectedTransitions.put( transition.getTarget().content(), new LinkedList<Transition>( ) );
                    }

                    if( ! transition.disposed() )
                    {
                        unconnectedTransitions.get( transition.getTarget().content() ).add( transition );
                    }
                }
            }
        }

        if( unconnectedTransitions.containsKey( workflowNode.getName().content() ) )
        {
            List<Transition> transitions = unconnectedTransitions.get( workflowNode.getName().content() );

            for( Transition transition : transitions )
            {
                DiagramConnectionPart connPart = getConnectionPart( connService, transition );

                DiagramNodePart nodePart1 = part.getDiagramNodePart( transition.nearest( WorkflowNode.class ) );
                DiagramNodePart nodePart2 = part.getDiagramNodePart( workflowNode );

                connPart.reconnect( nodePart1, nodePart2 );

//                sometimes the reconnect doesn't work, an alternative way is to remove it and create a new one 
//                if( connPart != null )
//                {
//                    connPart.remove();
//                }
//
//                connService.connect( nodePart1, nodePart2, "Transition" );
            }

            unconnectedTransitions.remove( workflowNode.getName().content() );
        }
    }

    private void write( DiagramNodePart nodePart )
    {
        WorkflowNode workflowNode = nodePart.getLocalModelElement().nearest( WorkflowNode.class );

        if( ! workflowNode.disposed() )
        {
            if( isNodeLayoutChanged( nodePart ) )
            {
                removeWorkflowNodeListeners();
                writeWorkflowNodeBoundsToMetaData( workflowNode, nodePart );
                addWorkflowNodeListeners();
            }

            refreshDirtyState();
        }
    }

    @Override
    public DiagramConnectionInfo read( DiagramConnectionPart connPart )
    {
        final ConnectionHashKey key = ConnectionHashKey.createKey( connPart );

        if( this.connectionBendPoints.containsKey( key ) && this.connectionBendPoints.get( key ) != null )
        {
            return new DiagramConnectionInfo( this.connectionBendPoints.get( key ) );
        }
        else
        {
            return null;
        }
    }

    private void write(DiagramConnectionPart connPart)
    {
        final Transition transition = connPart.getLocalModelElement().nearest( Transition.class );

        if( ! transition.disposed() )
        {
            if( isConnectionLayoutChanged( connPart ) )
            {
                this.removeBendpointListeners();
                writeTransitionBendPoints( transition, connPart );
                this.addBendpointListeners();
            }

            refreshDirtyState();
        }
    }

    @Override
    public void dispose()
    {
        if( this.diagramPartListener != null )
        {
            part().detach( this.diagramPartListener );
        }

        this.removeWorkflowNodeListeners();
        this.removeBendpointListeners();

        super.dispose();
    }

    private void load()
    {
        final SapphireDiagramEditorPagePart part = part();
        part.setGridVisible( part.adapt( WorkflowDefinitionEditor.class ).isGridVisible() );
        part.setShowGuides( part.adapt( WorkflowDefinitionEditor.class ).isShowGuides() );

        final ConnectionService connService = part.service( ConnectionService.class );

        for( final WorkflowNode workflowNode : definition().getDiagramNodes() )
        {
            final DiagramNodePart nodePart = part.getDiagramNodePart( workflowNode );

            if( nodePart != null )
            {
                final WorkflowNodeMetadata metadata = workflowNode.getMetadata().content( false );

                final DiagramNodeBounds bounds =
                    new DiagramNodeBounds(
                        metadata.getPosition().getX().content(), metadata.getPosition().getY().content(), -1, -1,
                        false, false );
                nodePart.setNodeBounds(bounds);

                final ListFactory<Transition> transitionsList = ListFactory.start();

                for( Transition transition : workflowNode.nearest( CanTransition.class ).getTransitions() )
                {
                    transitionsList.add( transition );
                }

                final List<Transition> transitions = transitionsList.result();

                for( final Transition transition : transitions )
                {
                    final DiagramConnectionPart connPart = getConnectionPart( connService, transition );

                    if( connPart != null )
                    {
                        final TransitionMetadata transitionMetadata = getTransitionMetadata( transition, metadata );

                        if( transitionMetadata == null )
                        {
                            continue;
                        }

                        final List<ConnectionBendpoint> bendpoints = transitionMetadata.getBendpoints();

                        if( bendpoints.size() > 0 )
                        {
                            int index = 0;

                            for( ConnectionBendpoint bendpoint : bendpoints )
                            {
                                connPart.addBendpoint( index++, bendpoint.getX().content(), bendpoint.getY().content() );
                            }
                        }

                        Position labelPosition = transitionMetadata.getLabelLocation();

                        if( labelPosition != null )
                        {
                            connPart.setLabelPosition( new Point(
                                labelPosition.getX().content(), labelPosition.getY().content() ) );
                        }
                    }
                }
            }
        }

        // Listen on existing connection parts
        for( final DiagramConnectionPart connPart : connService.list() )
        {
            connPart.attach( this.connectionPartListener );
        }
    }

    private void handleNodeLayoutChange( WorkflowNode workflowNode )
    {
        final DiagramNodePart nodePart =
            part().getDiagramNodePart( workflowNode );

        final WorkflowNodeMetadata metadata = workflowNode.getMetadata().content();

        final DiagramNodeBounds nodeBounds =
            new DiagramNodeBounds( metadata.getPosition().getX().content(), metadata.getPosition().getY().content() );

        nodePart.setNodeBounds( nodeBounds );
    }

    private void handleConnectionBendpointChange( Transition transition )
    {
        final ConnectionService connService =
            part().service( ConnectionService.class );
        final DiagramConnectionPart connPart = getConnectionPart( connService, transition );

        if( connPart != null )
        {
            final List<Point> bendpoints = new ArrayList<Point>();

            final WorkflowNodeMetadata metadata =
                transition.nearest( WorkflowNode.class ).getMetadata().content( false );

            final TransitionMetadata transitionMetadata = getTransitionMetadata( transition, metadata );

            for( final ConnectionBendpoint bendpoint : transitionMetadata.getBendpoints() )
            {
                bendpoints.add( new Point( bendpoint.getX().content(), bendpoint.getY().content() ) );
            }

            connPart.resetBendpoints( bendpoints );
        }
    }

    private void handleWorkflowNodeMetaChange( WorkflowNodeMetadata workflowNodeMetadata )
    {
        final WorkflowNode workflowNode = workflowNodeMetadata.nearest( WorkflowNode.class );

        if( workflowNode == null )
        {
            return;
        }

        final DiagramNodePart nodePart = part().getDiagramNodePart( workflowNode );

        if( nodePart != null )
        {
            if( nodePart.getNodeBounds().getX() != workflowNodeMetadata.getPosition().getX().content() ||
                nodePart.getNodeBounds().getY() != workflowNodeMetadata.getPosition().getY().content() )
            {
                nodePart.setNodeBounds
                (
                    new DiagramNodeBounds
                    (
                        workflowNodeMetadata.getPosition().getX().content(),
                        workflowNodeMetadata.getPosition().getY().content()
                    )
                );
            }
        }

        final ConnectionService connService = part().service( ConnectionService.class );

        if( workflowNode instanceof CanTransition )
        {
            ElementList<Transition> transitions = ( (CanTransition) workflowNode ).getTransitions();

            for( Transition transition : transitions )
            {
                final List<Point> bendpoints = new ArrayList<Point>();

                final DiagramConnectionPart connPart = getConnectionPart( connService, transition );

                if( connPart != null )
                {
                    final TransitionMetadata transitionMetadata = getTransitionMetadata( transition, workflowNodeMetadata );

                    if( transitionMetadata != null )
                    {
                        for( final ConnectionBendpoint bendpoint : transitionMetadata.getBendpoints() )
                        {
                            bendpoints.add( new Point( bendpoint.getX().content(), bendpoint.getY().content() ) );
                        }

                        connPart.resetBendpoints( bendpoints );
                    }
                }
            }
        }
    }

    private void addNodeToPersistenceCache( DiagramNodePart nodePart )
    {
        final String nodeId = nodePart.getId();
        this.nodeBounds.put( nodeId, nodePart.getNodeBounds() );
    }

    private void addConnectionToPersistenceCache( DiagramConnectionPart connPart )
    {
        final ConnectionHashKey connKey = ConnectionHashKey.createKey( connPart );
        this.connectionBendPoints.put( connKey, connPart.getBendpoints() );

        if( connPart.getLabelPosition() != null )
        {
            this.connectionLabelPositions.put( connKey, new Point( connPart.getLabelPosition() ) );
        }
        else
        {
            this.connectionLabelPositions.put( connKey, new Point( -1, -1 ) );
        }
    }

    private void addDiagramPartListener()
    {
        this.diagramPartListener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                if( event instanceof DiagramNodeEvent )
                {
                    handleDiagramNodeEvent( (DiagramNodeEvent) event );
                }
                else if( event instanceof DiagramPageEvent )
                {
                    handleDiagramPageEvent( (DiagramPageEvent) event );
                }
                else if( event instanceof PreAutoLayoutEvent )
                {
                    autoLayout = true;
                }
                else if( event instanceof PostAutoLayoutEvent )
                {
                    autoLayout = false;
                }
                else if( event instanceof ConnectionAddEvent )
                {
                    handleConnectionAddEvent( (ConnectionAddEvent) event );
                }
                else if( event instanceof ConnectionDeleteEvent )
                {
                    handleConnectionDeleteEvent( (ConnectionDeleteEvent) event );
                }
            }
        };

        part().attach( this.diagramPartListener );
    }

    private void handleDiagramNodeEvent( final DiagramNodeEvent event )
    {
        final DiagramNodePart nodePart = (DiagramNodePart) event.part();

        if( event instanceof DiagramNodeAddEvent )
        {
            read( nodePart );
        }
        else if( event instanceof DiagramNodeDeleteEvent )
        {
            refreshDirtyState();
        }
        else if( event instanceof DiagramNodeMoveEvent )
        {
            final DiagramNodeBounds nodeBounds = nodePart.getNodeBounds();

            if( nodeBounds.isAutoLayout() )
            {
                // need to add the node bounds to the persistence cache so that "revert" could work
                addNodeToPersistenceCache( nodePart );
                refreshDirtyState();
            }
            else if( !nodeBounds.isDefaultPosition() )
            {
                write( (DiagramNodePart) event.part() );
            }
        }
    }

    protected void handleConnectionAddEvent( ConnectionAddEvent event )
    {
        final DiagramConnectionPart connPart = event.part();

        connPart.attach( this.connectionPartListener );

        final DiagramConnectionInfo connectionInfo = read( connPart );

        if( connectionInfo != null )
        {
            connPart.resetBendpoints( connectionInfo.getBendPoints() );
        }
    }

    protected void handleConnectionDeleteEvent( ConnectionDeleteEvent event )
    {
        refreshDirtyState();
    }

    private void handleDiagramPageEvent( DiagramPageEvent event )
    {
        final SapphireDiagramEditorPagePart diagramPart = event.part().adapt( SapphireDiagramEditorPagePart.class );

        switch( event.getDiagramPageEventType() )
        {
            case GridStateChange:
                setGridVisible( diagramPart.isGridVisible() );
                break;

            case GuideStateChange:
                setGuidesVisible( diagramPart.isShowGuides() );
                break;

            case DiagramSave:
                doSave();
                break;

            default:
                break;
        }
    }

    class WorkflowNodeListener extends FilteredListener<PropertyEvent>
    {
        @Override
        public void handleTypedEvent( final PropertyEvent event )
        {
            if( event != null && event.property() != null )
            {
                WorkflowNode diagramNode = event.property().nearest( WorkflowNode.class );

                if( diagramNode != null )
                {
                    handleNodeLayoutChange( diagramNode );
                }
            }
        }
    }

    class BendpointListener extends FilteredListener<PropertyEvent>
    {
        @Override
        public void handleTypedEvent( PropertyEvent event )
        {
            if( event != null && event.property() != null )
            {
                Transition transition = event.property().nearest( Transition.class );

                if( transition != null )
                {
                    handleConnectionBendpointChange( transition );
                }
            }
        }
    }

    class WorkflowNodeMetadataListener extends FilteredListener<PropertyEvent>
    {
        @Override
        protected void handleTypedEvent( PropertyEvent event )
        {
            if( event != null && event.property() != null && event.property().element() instanceof WorkflowNodeMetadata )
            {
                handleWorkflowNodeMetaChange( (WorkflowNodeMetadata) event.property().element() );
            }
        }
    }

    private void addModelListeners()
    {
        // TODO reenable model listeners?
//      this.workflowNodeListener = new WorkflowNodeListener();
//      this.bendpointListener = new BendpointListener();
        this.workflowNodeMetadataListener = new WorkflowNodeMetadataListener();
        addWorkflowNodeListeners();
        addBendpointListeners();
        addWorkflowNodteMetadataListeners();
    }

    private void addWorkflowNodteMetadataListeners()
    {
        if( this.workflowNodeMetadataListener != null )
        {
            definition().attach( this.workflowNodeMetadataListener, "/*/Metadata" );
        }
    }

    private void addBendpointListeners()
    {
        if( this.bendpointListener != null )
        {
            definition().attach( this.bendpointListener, "/*/Metadata/TransitionsMetadata/Bendpoints/*" );
        }
    }

    private void addWorkflowNodeListeners()
    {
        if( this.workflowNodeListener != null )
        {
            definition().attach( this.workflowNodeListener, "/*/Metadata/Position/*" );
        }
    }

    private void writeWorkflowNodeMetaDataToBounds( WorkflowNodeMetadata metadata, DiagramNodePart nodePart )
    {
        nodePart.setNodeBounds( metadata.getPosition().getX().content(), metadata.getPosition().getY().content() );
    }

    private void writeWorkflowNodeBoundsToMetaData( WorkflowNode workflowNode, DiagramNodePart nodePart )
    {
        final Bounds bounds = nodePart.getNodeBounds();

        final WorkflowNodeMetadata metadata = workflowNode.getMetadata().content( true );

        if( bounds.getX() != metadata.getPosition().getX().content() )
        {
            metadata.getPosition().setX( bounds.getX() );
        }

        if( bounds.getY() != metadata.getPosition().getY().content() )
        {
            metadata.getPosition().setY( bounds.getY() );
        }
    }

    private void writeTransitionBendPoints( Transition transition, DiagramConnectionPart connPart )
    {
        final WorkflowNodeMetadata metadata = transition.nearest( WorkflowNode.class ).getMetadata().content( true );

        final TransitionMetadata transitionMetadata = getTransitionMetadata( transition, metadata, true );

        transitionMetadata.setName( transition.getName().content() );

        final ElementList<ConnectionBendpoint> bendpointsInMetadataList = transitionMetadata.getBendpoints();
        final int bendpointsInMetadataSize = bendpointsInMetadataList.size();

        final List<Point> bendpointsInPartList = connPart.getBendpoints();
        final int bendpointsInPartSize = bendpointsInPartList.size();

        for( int i = 0, n = min( bendpointsInMetadataSize, bendpointsInPartSize ); i < n; i++ )
        {
            final ConnectionBendpoint bendpointInMetadata = bendpointsInMetadataList.get( i );
            final Point bendpointInPart = bendpointsInPartList.get( i );

            if( bendpointInMetadata.getX().content() != bendpointInPart.getX() )
            {
                bendpointInMetadata.setX( bendpointInPart.getX() );
            }

            if( bendpointInMetadata.getY().content() != bendpointInPart.getY() )
            {
                bendpointInMetadata.setY( bendpointInPart.getY() );
            }
        }

        if( bendpointsInMetadataSize < bendpointsInPartSize )
        {
            for( int i = bendpointsInMetadataSize; i < bendpointsInPartSize; i++ )
            {
                final ConnectionBendpoint bendpointInMetadata = bendpointsInMetadataList.insert();
                final Point bendpointInPart = bendpointsInPartList.get( i );

                bendpointInMetadata.setX( bendpointInPart.getX() );
                bendpointInMetadata.setY( bendpointInPart.getY() );
            }
        }
        else if( bendpointsInMetadataSize > bendpointsInPartSize )
        {
            for( int i = bendpointsInMetadataSize - 1; i >= bendpointsInPartSize; i-- )
            {
                bendpointsInMetadataList.remove( i );
            }
        }

        if( connPart.getLabelPosition() != null && !connPart.getLabelPosition().equals( DEFAULT_POINT ) )
        {
            transitionMetadata.getLabelLocation().setX( connPart.getLabelPosition().getX() );
            transitionMetadata.getLabelLocation().setY( connPart.getLabelPosition().getY() );
        }
    }

    private void removeWorkflowNodeListeners()
    {
        if( this.workflowNodeListener != null )
        {
            definition().detach( this.workflowNodeListener, "/*/Metadata/Position/*" );
        }
    }

    private void removeBendpointListeners()
    {
        if( this.bendpointListener != null )
        {
            definition().detach( this.bendpointListener, "/*/Metadata/TransitionsMetadata/Bendpoints/*" );
        }

    }

    private void doSave()
    {
        refreshPersistedPartsCache();

        // For nodes that are placed using default node positions and connection bend points that
        // are calculated using connection router, we don't modify the corresponding model properties
        // in order to allow "revert" in source editor to work correctly.
        // So we need to do an explicit save of the node bounds and connection bend points here.

        this.removeWorkflowNodeListeners();
        this.removeBendpointListeners();

        for( DiagramNodePart nodePart : part().getNodes() )
        {
            final WorkflowNode workflowNode = (WorkflowNode) nodePart.getLocalModelElement();

            if( !workflowNode.disposed() )
            {
                writeWorkflowNodeBoundsToMetaData( workflowNode, nodePart );
                DiagramNodeBounds nodePartBounds = nodePart.getNodeBounds();
                nodePartBounds.setAutoLayout( false );
                nodePart.setNodeBounds( nodePartBounds );
            }
        }

        for( DiagramConnectionPart connPart : part().getConnections() )
        {
            Transition transition = (Transition) connPart.getLocalModelElement();

            if( !transition.disposed() )
            {
                writeTransitionBendPoints( transition, connPart );
            }
        }

        this.addWorkflowNodeListeners();
        this.addBendpointListeners();
    }

    private boolean isNodeLayoutChanged( DiagramNodePart nodePart )
    {
        final DiagramNodeBounds newBounds = nodePart.getNodeBounds();
        boolean changed = false;
        final String nodeId = nodePart.getId();

        if( this.nodeBounds.containsKey( nodeId ) )
        {
            final DiagramNodeBounds oldBounds = this.nodeBounds.get( nodeId );

            if( !newBounds.equals( oldBounds ) )
            {
                changed = true;
            }
        }
        else
        {
            changed = true;
        }

        return changed;
    }

    private boolean isConnectionLayoutChanged( DiagramConnectionPart connPart )
    {
        // Detect whether the connection bendpoints have been changed.
        final List<Point> bendpoints = connPart.getBendpoints();
        final Point labelPosition = connPart.getLabelPosition();
        final ConnectionHashKey key = ConnectionHashKey.createKey( connPart );
        boolean changed = false;

        if( this.connectionBendPoints.containsKey( key ) )
        {
            final List<Point> oldBendpoints = this.connectionBendPoints.get( key );

            if( bendpoints.size() != oldBendpoints.size() )
            {
                changed = true;
            }
            else
            {
                for( int i = 0; i < bendpoints.size(); i++ )
                {
                    Point newPt = bendpoints.get( i );
                    Point oldPt = oldBendpoints.get( i );

                    if( newPt.getX() != oldPt.getX() || newPt.getY() != oldPt.getY() )
                    {
                        changed = true;
                        break;
                    }
                }
            }

            if( !bendpoints.equals( oldBendpoints ) )
            {
                changed = true;
            }
        }
        else
        {
            changed = true;
        }

        if( !changed && this.connectionLabelPositions.containsKey( key ) )
        {
            final Point oldLabelPosition = this.connectionLabelPositions.get( key );

            if( labelPosition != null && !labelPosition.equals( oldLabelPosition ) )
            {
                changed = true;
            }
        }
        else
        {
            changed = true;
        }

        return changed;
    }

    private boolean isDiagramLayoutChanged()
    {
        boolean changed = false;

        final SapphireDiagramEditorPagePart part = part();

        if( !part.disposed() )
        {
            final ConnectionService connService = part.service( ConnectionService.class );

            for( DiagramNodePart nodePart : part.getNodes() )
            {
                if( !nodePart.getLocalModelElement().disposed() && isNodeLayoutChanged( nodePart ) )
                {
                    changed = true;
                    break;
                }
            }

            for( DiagramConnectionPart connPart : connService.list() )
            {
                if( !connPart.getLocalModelElement().disposed() && isConnectionLayoutChanged( connPart ) )
                {
                    changed = true;
                    break;
                }
            }
        }

        return changed;
    }

    @Override
    public boolean dirty()
    {
        return this.dirty;
    }

    private void refreshDirtyState()
    {
        final boolean after = isDiagramLayoutChanged();

        if( this.dirty != after )
        {
            final boolean before = this.dirty;
            this.dirty = after;

            broadcast( new DirtyStateEvent( this, before, after ) );
        }
    }

    private void refreshPersistedPartsCache()
    {
        this.nodeBounds.clear();
        this.connectionBendPoints.clear();
        final ConnectionService connService =
            part().service( ConnectionService.class );

        for( DiagramConnectionPart connPart : connService.list() )
        {
            addConnectionToPersistenceCache( connPart );
        }

        for( DiagramNodePart nodePart : part().getNodes() )
        {
            addNodeToPersistenceCache( nodePart );
        }
    }

    private DiagramConnectionPart getConnectionPart( ConnectionService connService, Element element )
    {
        for( DiagramConnectionPart connPart : connService.list() )
        {
            if( connPart.getLocalModelElement() == element )
            {
                return connPart;
            }
        }

        return null;
    }

    private TransitionMetadata getTransitionMetadata( Transition transition, WorkflowNodeMetadata nodeMetadata )
    {
        return getTransitionMetadata( transition, nodeMetadata, false );
    }

    private TransitionMetadata getTransitionMetadata(
        Transition transition, WorkflowNodeMetadata nodeMetadata, boolean createIfNecessary )
    {
        TransitionMetadata retval = null;

        ElementList<TransitionMetadata> transitionsData = nodeMetadata.getTransitionsMetadata();

        for( TransitionMetadata transitionData : transitionsData )
        {
            if( transition.getName().content() != null &&
                transition.getName().content().equals( transitionData.getName().content() ) )
            {
                retval = transitionData;
                break;
            }
        }

        if( retval == null && createIfNecessary )
        {
            retval = nodeMetadata.getTransitionsMetadata().insert();
        }

        return retval;
    }

    private SapphireDiagramEditorPagePart part()
    {
        return context( SapphireDiagramEditorPagePart.class );
    }

    private WorkflowDefinition definition()
    {
        return part().getLocalModelElement().nearest( WorkflowDefinition.class );
    }

}

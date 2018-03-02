/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.kaleo.ui.editor;

import static com.liferay.ide.kaleo.core.util.KaleoModelUtil.DEFAULT_POINT;

import static java.lang.Math.min;

import com.liferay.ide.core.util.ListUtil;
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
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.Value;
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
public class WorkflowDefinitionLayoutPersistenceService extends DiagramLayoutPersistenceService {

	@Override
	public boolean dirty() {
		return _dirty;
	}

	@Override
	public void dispose() {
		if (_diagramPartListener != null) {
			_part().detach(_diagramPartListener);
		}

		_removeWorkflowNodeListeners();
		_removeBendpointListeners();

		super.dispose();
	}

	@Override
	public DiagramConnectionInfo read(DiagramConnectionPart connPart) {
		ConnectionHashKey key = ConnectionHashKey.createKey(connPart);

		if (_connectionBendPoints.containsKey(key) && (_connectionBendPoints.get(key) != null)) {
			return new DiagramConnectionInfo(_connectionBendPoints.get(key));
		}
		else {
			return null;
		}
	}

	protected void handleConnectionAddEvent(ConnectionAddEvent event) {
		DiagramConnectionPart connPart = event.part();

		connPart.attach(_connectionPartListener);

		DiagramConnectionInfo connectionInfo = read(connPart);

		if (connectionInfo != null) {
			connPart.resetBendpoints(connectionInfo.getBendPoints());
		}
	}

	protected void handleConnectionDeleteEvent(ConnectionDeleteEvent event) {
		_refreshDirtyState();
	}

	@Override
	protected void init() {
		super.init();

		_nodeBounds = new HashMap<>();
		_connectionBendPoints = new HashMap<>();
		_connectionLabelPositions = new HashMap<>();
		_unconnectedTransitions = new HashMap<>();
		_dirty = false;

		_connectionPartListener = new FilteredListener<ConnectionBendpointsEvent>() {

			protected void handleTypedEvent(ConnectionBendpointsEvent event) {
				if (event.reset()) {
					if (_autoLayout) {
						_addConnectionToPersistenceCache(event.part());
						_refreshDirtyState();
					}
					else {
						_write(event.part());
					}
				}
				else {
					_write(event.part());
				}
			}

		};

		_load();
		_refreshPersistedPartsCache();
		_addDiagramPartListener();
		_addModelListeners();
	}

	protected class BendpointListener extends FilteredListener<PropertyEvent> {

		@Override
		public void handleTypedEvent(PropertyEvent event) {
			if ((event != null) && (event.property() != null)) {
				Transition transition = event.property().nearest(Transition.class);

				if (transition != null) {
					_handleConnectionBendpointChange(transition);
				}
			}
		}

	}

	protected class WorkflowNodeListener extends FilteredListener<PropertyEvent> {

		@Override
		public void handleTypedEvent(PropertyEvent event) {
			if ((event != null) && (event.property() != null)) {
				WorkflowNode diagramNode = event.property().nearest(WorkflowNode.class);

				if (diagramNode != null) {
					_handleNodeLayoutChange(diagramNode);
				}
			}
		}

	}

	protected class WorkflowNodeMetadataListener extends FilteredListener<PropertyEvent> {

		@Override
		protected void handleTypedEvent(PropertyEvent event) {
			if ((event != null) && (event.property() != null) &&
				event.property().element() instanceof WorkflowNodeMetadata) {

				_handleWorkflowNodeMetaChange((WorkflowNodeMetadata)event.property().element());
			}
		}

	}

	private void _addBendpointListeners() {
		if (_bendpointListener != null) {
			_definition().attach(_bendpointListener, "/*/Metadata/TransitionsMetadata/Bendpoints/*");
		}
	}

	private void _addConnectionToPersistenceCache(DiagramConnectionPart connPart) {
		ConnectionHashKey connKey = ConnectionHashKey.createKey(connPart);

		_connectionBendPoints.put(connKey, connPart.getBendpoints());

		if (connPart.getLabelPosition() != null) {
			_connectionLabelPositions.put(connKey, new Point(connPart.getLabelPosition()));
		}
		else {
			_connectionLabelPositions.put(connKey, new Point(-1, -1));
		}
	}

	private void _addDiagramPartListener() {
		_diagramPartListener = new Listener() {

			@Override
			public void handle(Event event) {
				if (event instanceof DiagramNodeEvent) {
					DiagramNodeEvent nodeEvent = (DiagramNodeEvent)event;

					_handleDiagramNodeEvent(nodeEvent);
				}
				else if (event instanceof DiagramPageEvent) {
					DiagramPageEvent pageEvent = (DiagramPageEvent)event;

					_handleDiagramPageEvent(pageEvent);
				}
				else if (event instanceof PreAutoLayoutEvent) {
					_autoLayout = true;
				}
				else if (event instanceof PostAutoLayoutEvent) {
					_autoLayout = false;
				}
				else if (event instanceof ConnectionAddEvent) {
					ConnectionAddEvent addEvent = (ConnectionAddEvent)event;

					handleConnectionAddEvent(addEvent);
				}
				else if (event instanceof ConnectionDeleteEvent) {
					handleConnectionDeleteEvent((ConnectionDeleteEvent)event);
				}
			}

		};

		_part().attach(_diagramPartListener);
	}

	private void _addModelListeners() {
		/*
		 * TODO reenable model listeners? workflowNodeListener = new
		 * WorkflowNodeListener(); bendpointListener = new BendpointListener();
		 */
		_workflowNodeMetadataListener = new WorkflowNodeMetadataListener();
		_addWorkflowNodeListeners();
		_addBendpointListeners();
		_addWorkflowNodteMetadataListeners();
	}

	private void _addNodeToPersistenceCache(DiagramNodePart nodePart) {
		String nodeId = nodePart.getId();

		_nodeBounds.put(nodeId, nodePart.getNodeBounds());
	}

	private void _addWorkflowNodeListeners() {
		if (_workflowNodeListener != null) {
			_definition().attach(_workflowNodeListener, "/*/Metadata/Position/*");
		}
	}

	private void _addWorkflowNodteMetadataListeners() {
		if (_workflowNodeMetadataListener != null) {
			_definition().attach(_workflowNodeMetadataListener, "/*/Metadata");
		}
	}

	private WorkflowDefinition _definition() {
		Element localModelElement = _part().getLocalModelElement();

		return localModelElement.nearest(WorkflowDefinition.class);
	}

	private DiagramConnectionPart _getConnectionPart(ConnectionService connService, Element element) {
		for (DiagramConnectionPart connPart : connService.list()) {
			if (connPart.getLocalModelElement() == element) {
				return connPart;
			}
		}

		return null;
	}

	private TransitionMetadata _getTransitionMetadata(Transition transition, WorkflowNodeMetadata nodeMetadata) {
		return _getTransitionMetadata(transition, nodeMetadata, false);
	}

	private TransitionMetadata _getTransitionMetadata(
		Transition transition, WorkflowNodeMetadata nodeMetadata, boolean createIfNecessary) {

		TransitionMetadata retval = null;

		ElementList<TransitionMetadata> transitionsData = nodeMetadata.getTransitionsMetadata();

		for (TransitionMetadata transitionData : transitionsData) {
			if (transition.getName().content() != null) {
				String transitionName = transition.getName().content();

				if (transitionName.equals(transitionData.getName().content())) {
				}

				retval = transitionData;
				break;
			}
		}

		if ((retval == null) && createIfNecessary) {
			retval = nodeMetadata.getTransitionsMetadata().insert();
		}

		return retval;
	}

	private void _handleConnectionBendpointChange(Transition transition) {
		ConnectionService connService = _part().service(ConnectionService.class);

		DiagramConnectionPart connPart = _getConnectionPart(connService, transition);

		if (connPart != null) {
			List<Point> bendpoints = new ArrayList<>();

			WorkflowNode wfNode = transition.nearest(WorkflowNode.class);

			ElementHandle<WorkflowNodeMetadata> nodeMetadata = wfNode.getMetadata();

			WorkflowNodeMetadata metadata = nodeMetadata.content(false);

			TransitionMetadata transitionMetadata = _getTransitionMetadata(transition, metadata);

			for (ConnectionBendpoint bendpoint : transitionMetadata.getBendpoints()) {
				bendpoints.add(new Point(bendpoint.getX().content(), bendpoint.getY().content()));
			}

			connPart.resetBendpoints(bendpoints);
		}
	}

	private void _handleDiagramNodeEvent(DiagramNodeEvent event) {
		DiagramNodePart nodePart = event.part();

		if (event instanceof DiagramNodeAddEvent) {
			_read(nodePart);
		}
		else if (event instanceof DiagramNodeDeleteEvent) {
			_refreshDirtyState();
		}
		else if (event instanceof DiagramNodeMoveEvent) {
			DiagramNodeBounds nodeBounds = nodePart.getNodeBounds();

			if (nodeBounds.isAutoLayout()) {
				/*
				 * need to add the node bounds to the persistence cache so that
				 * "revert" could work
				 */
				_addNodeToPersistenceCache(nodePart);

				_refreshDirtyState();
			}
			else if (!nodeBounds.isDefaultPosition()) {
				DiagramNodePart diagramNodePart = event.part();

				_write(diagramNodePart);
			}
		}
	}

	private void _handleDiagramPageEvent(DiagramPageEvent event) {
		SapphireDiagramEditorPagePart diagramPart = event.part().adapt(SapphireDiagramEditorPagePart.class);

		switch (event.getDiagramPageEventType()) {
			case GridStateChange:
				_setGridVisible(diagramPart.isGridVisible());
				break;

			case GuideStateChange:
				_setGuidesVisible(diagramPart.isShowGuides());
				break;

			case DiagramSave:
				_save();
				break;

			default:
				break;
		}
	}

	private void _handleNodeLayoutChange(WorkflowNode workflowNode) {
		DiagramNodePart nodePart = _part().getDiagramNodePart(workflowNode);

		WorkflowNodeMetadata metadata = workflowNode.getMetadata().content();

		Position position = metadata.getPosition();

		Value<Integer> valueX = position.getX();

		Value<Integer> valueY = position.getY();

		DiagramNodeBounds nodeBounds = new DiagramNodeBounds(valueX.content(), valueY.content());

		nodePart.setNodeBounds(nodeBounds);
	}

	private void _handleWorkflowNodeMetaChange(WorkflowNodeMetadata workflowNodeMetadata) {
		WorkflowNode workflowNode = workflowNodeMetadata.nearest(WorkflowNode.class);

		if (workflowNode == null) {
			return;
		}

		DiagramNodePart nodePart = _part().getDiagramNodePart(workflowNode);

		if (nodePart != null) {
			Position position = workflowNodeMetadata.getPosition();

			Value<Integer> valueX = position.getX();

			Value<Integer> valueY = position.getY();

			DiagramNodeBounds nodeBounds = nodePart.getNodeBounds();

			if ((nodeBounds.getX() != valueX.content()) || (nodeBounds.getY() != valueY.content())) {
				nodePart.setNodeBounds(new DiagramNodeBounds(valueX.content(), valueY.content()));
			}
		}

		ConnectionService connService = _part().service(ConnectionService.class);

		if (workflowNode instanceof CanTransition) {
			CanTransition canTransition = (CanTransition)workflowNode;

			ElementList<Transition> transitions = canTransition.getTransitions();

			for (Transition transition : transitions) {
				List<Point> bendpoints = new ArrayList<>();

				DiagramConnectionPart connPart = _getConnectionPart(connService, transition);

				if (connPart != null) {
					TransitionMetadata transitionMetadata = _getTransitionMetadata(transition, workflowNodeMetadata);

					if (transitionMetadata != null) {
						for (ConnectionBendpoint bendpoint : transitionMetadata.getBendpoints()) {
							bendpoints.add(new Point(bendpoint.getX().content(), bendpoint.getY().content()));
						}

						connPart.resetBendpoints(bendpoints);
					}
				}
			}
		}
	}

	private boolean _isConnectionLayoutChanged(DiagramConnectionPart connPart) {

		// Detect whether the connection bendpoints have been changed.

		List<Point> bendpoints = connPart.getBendpoints();
		Point labelPosition = connPart.getLabelPosition();
		ConnectionHashKey key = ConnectionHashKey.createKey(connPart);
		boolean changed = false;

		if (_connectionBendPoints.containsKey(key)) {
			List<Point> oldBendpoints = _connectionBendPoints.get(key);

			if (bendpoints.size() != oldBendpoints.size()) {
				changed = true;
			}
			else {
				for (int i = 0; i < bendpoints.size(); i++) {
					Point newPt = bendpoints.get(i);
					Point oldPt = oldBendpoints.get(i);

					if ((newPt.getX() != oldPt.getX()) || (newPt.getY() != oldPt.getY())) {
						changed = true;
						break;
					}
				}
			}

			if (!bendpoints.equals(oldBendpoints)) {
				changed = true;
			}
		}
		else {
			changed = true;
		}

		if (!changed && _connectionLabelPositions.containsKey(key)) {
			Point oldLabelPosition = _connectionLabelPositions.get(key);

			if ((labelPosition != null) && !labelPosition.equals(oldLabelPosition)) {
				changed = true;
			}
		}
		else {
			changed = true;
		}

		return changed;
	}

	private boolean _isDiagramLayoutChanged() {
		boolean changed = false;

		SapphireDiagramEditorPagePart part = _part();

		if (!part.disposed()) {
			ConnectionService connService = part.service(ConnectionService.class);

			for (DiagramNodePart nodePart : part.getNodes()) {
				if (!nodePart.getLocalModelElement().disposed() && _isNodeLayoutChanged(nodePart)) {
					changed = true;
					break;
				}
			}

			for (DiagramConnectionPart connPart : connService.list()) {
				if (!connPart.getLocalModelElement().disposed() && _isConnectionLayoutChanged(connPart)) {
					changed = true;
					break;
				}
			}
		}

		return changed;
	}

	private boolean _isNodeLayoutChanged(DiagramNodePart nodePart) {
		DiagramNodeBounds newBounds = nodePart.getNodeBounds();
		boolean changed = false;
		String nodeId = nodePart.getId();

		if (_nodeBounds.containsKey(nodeId)) {
			DiagramNodeBounds oldBounds = _nodeBounds.get(nodeId);

			if (!newBounds.equals(oldBounds)) {
				changed = true;
			}
		}
		else {
			changed = true;
		}

		return changed;
	}

	private void _load() {
		SapphireDiagramEditorPagePart part = _part();

		part.setGridVisible(part.adapt(WorkflowDefinitionEditor.class).isGridVisible());
		part.setShowGuides(part.adapt(WorkflowDefinitionEditor.class).isShowGuides());

		ConnectionService connService = part.service(ConnectionService.class);

		for (WorkflowNode workflowNode : _definition().getDiagramNodes()) {
			DiagramNodePart nodePart = part.getDiagramNodePart(workflowNode);

			if (nodePart != null) {
				WorkflowNodeMetadata metadata = workflowNode.getMetadata().content(false);

				Position position = metadata.getPosition();

				Value<Integer> valueX = position.getX();

				Value<Integer> valueY = position.getY();

				DiagramNodeBounds bounds = new DiagramNodeBounds(
					valueX.content(), valueY.content(), -1, -1, false, false);

				nodePart.setNodeBounds(bounds);

				ListFactory<Transition> transitionsList = ListFactory.start();

				for (Transition transition : workflowNode.nearest(CanTransition.class).getTransitions()) {
					transitionsList.add(transition);
				}

				List<Transition> transitions = transitionsList.result();

				for (Transition transition : transitions) {
					DiagramConnectionPart connPart = _getConnectionPart(connService, transition);

					if (connPart != null) {
						TransitionMetadata transitionMetadata = _getTransitionMetadata(transition, metadata);

						if (transitionMetadata == null) {
							continue;
						}

						List<ConnectionBendpoint> bendpoints = transitionMetadata.getBendpoints();

						if (ListUtil.isNotEmpty(bendpoints)) {
							int index = 0;

							for (ConnectionBendpoint bendpoint : bendpoints) {
								connPart.addBendpoint(index++, bendpoint.getX().content(), bendpoint.getY().content());
							}
						}

						Position labelPosition = transitionMetadata.getLabelLocation();

						if (labelPosition != null) {
							connPart.setLabelPosition(
								new Point(labelPosition.getX().content(), labelPosition.getY().content()));
						}
					}
				}
			}
		}

		// Listen on existing connection parts

		for (DiagramConnectionPart connPart : connService.list()) {
			connPart.attach(_connectionPartListener);
		}
	}

	private SapphireDiagramEditorPagePart _part() {
		return context(SapphireDiagramEditorPagePart.class);
	}

	private void _read(DiagramNodePart nodePart) {
		WorkflowNode workflowNode = nodePart.getLocalModelElement().nearest(WorkflowNode.class);

		if (!workflowNode.disposed()) {
			WorkflowNodeMetadata metadata = workflowNode.getMetadata().content();

			Position position = metadata.getPosition();

			Value<Integer> valueX = position.getX();

			Value<Integer> valueY = position.getY();

			if ((valueX.content() != -1) && (valueY.content() != -1)) {
				_writeWorkflowNodeMetaDataToBounds(metadata, nodePart);
			}

			String nodeId = nodePart.getId();

			if (_nodeBounds.containsKey(nodeId) && (_nodeBounds.get(nodeId) != null)) {
				nodePart.setNodeBounds(_nodeBounds.get(nodeId));
			}
			else {
				_nodeBounds.put(nodeId, nodePart.getNodeBounds());
			}
		}

		/*
		 * handle the case where a transition is added and its target node
		 * hasn't been. keep the transitions in a map and every time a new node
		 * is added, check if it is a target of unconnected transitions,
		 * reconnect the DiagramConnectionPart
		 */
		SapphireDiagramEditorPagePart part = _part();

		ConnectionService connService = part.service(ConnectionService.class);

		if (workflowNode instanceof CanTransition) {
			CanTransition canTransition = (CanTransition)workflowNode;

			for (Transition transition : canTransition.getTransitions()) {
				if (transition.getTarget().target() == null) {
					if (!_unconnectedTransitions.containsKey(transition.getTarget().content())) {
						_unconnectedTransitions.put(transition.getTarget().content(), new LinkedList<Transition>());
					}

					if (!transition.disposed()) {
						_unconnectedTransitions.get(transition.getTarget().content()).add(transition);
					}
				}
			}
		}

		if (_unconnectedTransitions.containsKey(workflowNode.getName().content())) {
			List<Transition> transitions = _unconnectedTransitions.get(workflowNode.getName().content());

			for (Transition transition : transitions) {
				DiagramConnectionPart connPart = _getConnectionPart(connService, transition);

				DiagramNodePart nodePart1 = part.getDiagramNodePart(transition.nearest(WorkflowNode.class));
				DiagramNodePart nodePart2 = part.getDiagramNodePart(workflowNode);

				connPart.reconnect(nodePart1, nodePart2);

				/*
				 * sometimes the reconnect doesn't work, an alternative way is
				 * to remove it and create a new one if( connPart != null ) {
				 * connPart.remove(); }
				 *
				 * connService.connect( nodePart1, nodePart2, "Transition" );
				 */
			}

			_unconnectedTransitions.remove(workflowNode.getName().content());
		}
	}

	private void _refreshDirtyState() {
		boolean after = _isDiagramLayoutChanged();

		if (_dirty != after) {
			boolean before = _dirty;
			_dirty = after;

			broadcast(new DirtyStateEvent(this, before, after));
		}
	}

	private void _refreshPersistedPartsCache() {
		_nodeBounds.clear();
		_connectionBendPoints.clear();
		ConnectionService connService = _part().service(ConnectionService.class);

		for (DiagramConnectionPart connPart : connService.list()) {
			_addConnectionToPersistenceCache(connPart);
		}

		for (DiagramNodePart nodePart : _part().getNodes()) {
			_addNodeToPersistenceCache(nodePart);
		}
	}

	private void _removeBendpointListeners() {
		if (_bendpointListener != null) {
			_definition().detach(_bendpointListener, "/*/Metadata/TransitionsMetadata/Bendpoints/*");
		}
	}

	private void _removeWorkflowNodeListeners() {
		if (_workflowNodeListener != null) {
			_definition().detach(_workflowNodeListener, "/*/Metadata/Position/*");
		}
	}

	private void _save() {
		_refreshPersistedPartsCache();

		/*
		 * For nodes that are placed using default node positions and connection
		 * bend points that are calculated using connection router, we don't
		 * modify the corresponding model properties in order to allow "revert"
		 * in source editor to work correctly. So we need to do an explicit save
		 * of the node bounds and connection bend points here.
		 */
		_removeWorkflowNodeListeners();
		_removeBendpointListeners();

		for (DiagramNodePart nodePart : _part().getNodes()) {
			Element modelElement = nodePart.getLocalModelElement();

			WorkflowNode workflowNode = (WorkflowNode)modelElement;

			if (!workflowNode.disposed()) {
				_writeWorkflowNodeBoundsToMetaData(workflowNode, nodePart);

				DiagramNodeBounds nodePartBounds = nodePart.getNodeBounds();

				nodePartBounds.setAutoLayout(false);

				nodePart.setNodeBounds(nodePartBounds);
			}
		}

		for (DiagramConnectionPart connPart : _part().getConnections()) {
			Element modelElement = connPart.getLocalModelElement();

			Transition transition = (Transition)modelElement;

			if (!transition.disposed()) {
				_writeTransitionBendPoints(transition, connPart);
			}
		}

		_addWorkflowNodeListeners();
		_addBendpointListeners();
	}

	private void _setGridVisible(boolean gridVisible) {
		WorkflowDefinitionEditor workflowEditor = _part().adapt(WorkflowDefinitionEditor.class);

		workflowEditor.setGridVisible(gridVisible);
	}

	private void _setGuidesVisible(boolean showGuides) {
		WorkflowDefinitionEditor workflowEditor = _part().adapt(WorkflowDefinitionEditor.class);

		workflowEditor.setShowGuides(showGuides);
	}

	private void _write(DiagramConnectionPart connPart) {
		Transition transition = connPart.getLocalModelElement().nearest(Transition.class);

		if (!transition.disposed()) {
			if (_isConnectionLayoutChanged(connPart)) {
				_removeBendpointListeners();
				_writeTransitionBendPoints(transition, connPart);
				_addBendpointListeners();
			}

			_refreshDirtyState();
		}
	}

	private void _write(DiagramNodePart nodePart) {
		WorkflowNode workflowNode = nodePart.getLocalModelElement().nearest(WorkflowNode.class);

		if (!workflowNode.disposed()) {
			if (_isNodeLayoutChanged(nodePart)) {
				_removeWorkflowNodeListeners();
				_writeWorkflowNodeBoundsToMetaData(workflowNode, nodePart);
				_addWorkflowNodeListeners();
			}

			_refreshDirtyState();
		}
	}

	private void _writeTransitionBendPoints(Transition transition, DiagramConnectionPart connPart) {
		WorkflowNode workflowNode = transition.nearest(WorkflowNode.class);

		ElementHandle<WorkflowNodeMetadata> nodeMetadata = workflowNode.getMetadata();

		WorkflowNodeMetadata metadata = nodeMetadata.content(true);

		TransitionMetadata transitionMetadata = _getTransitionMetadata(transition, metadata, true);

		transitionMetadata.setName(transition.getName().content());

		ElementList<ConnectionBendpoint> bendpointsInMetadataList = transitionMetadata.getBendpoints();

		int bendpointsInMetadataSize = bendpointsInMetadataList.size();

		List<Point> bendpointsInPartList = connPart.getBendpoints();

		int bendpointsInPartSize = bendpointsInPartList.size();

		for (int i = 0, n = min(bendpointsInMetadataSize, bendpointsInPartSize); i < n; i++) {
			ConnectionBendpoint bendpointInMetadata = bendpointsInMetadataList.get(i);
			Point bendpointInPart = bendpointsInPartList.get(i);

			if (bendpointInMetadata.getX().content() != bendpointInPart.getX()) {
				bendpointInMetadata.setX(bendpointInPart.getX());
			}

			if (bendpointInMetadata.getY().content() != bendpointInPart.getY()) {
				bendpointInMetadata.setY(bendpointInPart.getY());
			}
		}

		if (bendpointsInMetadataSize < bendpointsInPartSize) {
			for (int i = bendpointsInMetadataSize; i < bendpointsInPartSize; i++) {
				ConnectionBendpoint bendpointInMetadata = bendpointsInMetadataList.insert();
				Point bendpointInPart = bendpointsInPartList.get(i);

				bendpointInMetadata.setX(bendpointInPart.getX());
				bendpointInMetadata.setY(bendpointInPart.getY());
			}
		}
		else if (bendpointsInMetadataSize > bendpointsInPartSize) {
			for (int i = bendpointsInMetadataSize - 1; i >= bendpointsInPartSize; i--) {
				bendpointsInMetadataList.remove(i);
			}
		}

		if ((connPart.getLabelPosition() != null) && !connPart.getLabelPosition().equals(DEFAULT_POINT)) {
			transitionMetadata.getLabelLocation().setX(connPart.getLabelPosition().getX());
			transitionMetadata.getLabelLocation().setY(connPart.getLabelPosition().getY());
		}
	}

	private void _writeWorkflowNodeBoundsToMetaData(WorkflowNode workflowNode, DiagramNodePart nodePart) {
		Bounds bounds = nodePart.getNodeBounds();

		ElementHandle<WorkflowNodeMetadata> nodeMetadata = workflowNode.getMetadata();

		WorkflowNodeMetadata metadata = nodeMetadata.content(true);

		Position position = metadata.getPosition();

		Value<Integer> valueX = position.getX();

		if (bounds.getX() != valueX.content()) {
			position.setX(bounds.getX());
		}

		Value<Integer> valueY = position.getY();

		if (bounds.getY() != valueY.content()) {
			position.setY(bounds.getY());
		}
	}

	private void _writeWorkflowNodeMetaDataToBounds(WorkflowNodeMetadata metadata, DiagramNodePart nodePart) {
		Position position = metadata.getPosition();

		Value<Integer> valueX = position.getX();

		Value<Integer> valueY = position.getY();

		nodePart.setNodeBounds(valueX.content(), valueY.content());
	}

	private boolean _autoLayout = false;
	private Listener _bendpointListener;
	private HashMap<ConnectionHashKey, List<Point>> _connectionBendPoints;
	private HashMap<ConnectionHashKey, Point> _connectionLabelPositions;
	private Listener _connectionPartListener;
	private Listener _diagramPartListener;
	private boolean _dirty;
	private HashMap<String, DiagramNodeBounds> _nodeBounds;
	private HashMap<String, List<Transition>> _unconnectedTransitions;
	private Listener _workflowNodeListener;
	private Listener _workflowNodeMetadataListener;

}
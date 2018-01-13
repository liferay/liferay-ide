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

package com.liferay.ide.kaleo.ui.diagram;

import com.liferay.ide.kaleo.ui.editor.WorkflowDefinitionEditor;

import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart.ConnectionPalette;
import org.eclipse.sapphire.ui.swt.gef.ConnectionCreationFactory;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Event;

/**
 * @author Gregory Amerson
 */
public class StartTransitionActionHandler extends SapphireActionHandler {

	@Override
	protected Object run(Presentation context) {
		WorkflowDefinitionEditor editor = context.part().adapt(WorkflowDefinitionEditor.class);

		SapphireDiagramEditor diagramEditor = editor.getDiagramEditor();

		ISapphirePart part = context.part();

		DiagramNodePart diagramPart = part.nearest(DiagramNodePart.class);

		SapphireDiagramEditorPagePart editorPart = part.nearest(SapphireDiagramEditorPagePart.class);

		ConnectionPalette connectionPalette = editorPart.getConnectionPalettes().get(0);

		CreationFactory factory = new ConnectionCreationFactory(connectionPalette.getConnectionDef());

		ConnectionCreationTool tool = new ConnectionCreationTool(factory);

		diagramEditor.getEditDomain().setActiveTool(tool);

		Event e = new Event();

		e.x = diagramPart.getNodeBounds().getX();
		e.y = diagramPart.getNodeBounds().getY();
		e.widget = diagramEditor.getGraphicalViewer().getControl();
		e.button = 1;

		MouseEvent me = new MouseEvent(e);

		tool.mouseDown(me, diagramEditor.getGraphicalViewer());

		return null;
	}

}
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

import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.diagram.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * @author Gregory Amerson
 */
public class ShowDiagramPartActionHandler extends SapphireActionHandler {

	@Override
	protected Object run(Presentation context) {
		SapphireDiagramEditorPagePart diagramPart = context.part().nearest(SapphireDiagramEditorPagePart.class);

		if (diagramPart != null) {
			LabelProvider labelProvider = new LabelProvider() {

				@Override
				public Image getImage(Object element) {
					if (element instanceof DiagramNodePart) {
						DiagramNodePart diagramNodePart = (DiagramNodePart)element;

						Element modelElement = diagramNodePart.getLocalModelElement();

						return diagramPart.getSwtResourceCache().image(modelElement.type().image());
					}
					else if (element instanceof DiagramConnectionPart) {
						return diagramPart.getSwtResourceCache().image(Transition.TYPE.image());
					}
					else {
						return diagramPart.getSwtResourceCache().image(WorkflowNode.TYPE.image());
					}
				}

				@Override
				public String getText(Object element) {
					if (element instanceof DiagramNodePart) {
						return ((DiagramNodePart)element).getId();
					}
					else if (element instanceof DiagramConnectionPart) {
						return ((DiagramConnectionPart)element).getLabel();
					}
					else {
						if (element != null) {
							return element.toString();
						}

						return "";
					}
				}

			};

			ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				((SwtPresentation)context).shell(), labelProvider);

			List<SapphirePart> parts = new ArrayList<>();

			parts.addAll(diagramPart.getNodes());
			parts.addAll(diagramPart.getConnections());

			dialog.setElements(parts.toArray());

			dialog.setMultipleSelection(false);
			dialog.setHelpAvailable(false);
			dialog.setTitle("Find Part");
			dialog.setMessage("Select part:");

			dialog.open();

			Object[] result = dialog.getResult();

			if ((result != null) && (result.length == 1)) {

				// select node in diagram

				ISapphirePart part = (ISapphirePart)result[0];

				if (part instanceof DiagramConnectionPart || part instanceof DiagramNodePart) {
					/*
					 * diagramPart.setSelections( ReadOnlyListFactory.create(
					 * part ) );
					 */
					SapphireDiagramEditor diagramEditor = diagramPart.adapt(SapphireDiagramEditor.class);

					GraphicalViewer viewer = diagramEditor.getGraphicalViewer();

					GraphicalEditPart editpart = diagramEditor.getGraphicalEditPart(part);

					if (editpart != null) {

						// Force a layout first.

						viewer.flush();
						viewer.select(editpart);
						viewer.reveal(editpart);
					}
				}
			}
		}

		return null;
	}

}
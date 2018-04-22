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

package com.liferay.ide.layouttpl.ui.editor;

import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.ui.parts.LayoutTplEditPartFactory;
import com.liferay.ide.layouttpl.ui.parts.LayoutTplRootEditPart;

import java.util.EventObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
public class LayoutTplPreviewEditor extends GraphicalEditor {

	public LayoutTplPreviewEditor(LayoutTplElement layoutTpl) {
		modelElement = layoutTpl;
		setEditDomain(new DefaultEditDomain(this));
	}

	@Override
	public void commandStackChanged(EventObject event) {
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	public void doSaveAs() {
	}

	@Override
	public ActionRegistry getActionRegistry() {
		return super.getActionRegistry();
	}

	@Override
	public DefaultEditDomain getEditDomain() {
		return super.getEditDomain();
	}

	public LayoutTplElement getModelElement() {
		return modelElement;
	}

	@Override
	public SelectionSynchronizer getSelectionSynchronizer() {
		return super.getSelectionSynchronizer();
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void refreshVisualModel(LayoutTplElement layoutTpl) {
		GraphicalViewer viewer = getGraphicalViewer();

		if (viewer != null) {
			viewer.setContents(layoutTpl);
			_refreshViewer(viewer);
		}
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();

		viewer.setEditPartFactory(new LayoutTplEditPartFactory());
		viewer.setRootEditPart(new LayoutTplRootEditPart());
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));
	}

	@Override
	protected void createActions() {
	}

	protected void createGraphicalViewer(Composite parent) {
		GraphicalViewer viewer = new GraphicalViewerImpl();

		viewer.createControl(parent);
		setGraphicalViewer(viewer);

		configureGraphicalViewer();
		hookGraphicalViewer();
		initializeGraphicalViewer();
	}

	protected void initializeGraphicalViewer() {
		GraphicalViewer viewer = getGraphicalViewer();

		viewer.setContents(getModelElement());
		_refreshViewer(viewer);
	}

	protected void setInput(IEditorInput input) {
		super.setInput(input);

		setPartName(input.getName());
	}

	protected LayoutTplElement modelElement;

	private void _refreshViewer(GraphicalViewer viewer) {
		Control control = viewer.getControl();

		control.addPaintListener(
			new PaintListener() {

				public void paintControl(PaintEvent e) {
					EditPart editPage = getGraphicalViewer().getContents();

					editPage.refresh();

					control.removePaintListener(this);
				}

			});
	}

}
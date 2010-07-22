/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/
package com.liferay.ide.eclipse.layouttpl.ui.editor;

import com.liferay.ide.eclipse.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.eclipse.layouttpl.ui.gef.GraphicalEditorWithFlyoutPalette;
import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.parts.LayoutTplEditPartFactory;
import com.liferay.ide.eclipse.layouttpl.ui.parts.LayoutTplRootEditPart;

import java.util.EventObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class LayoutTplEditor extends GraphicalEditorWithFlyoutPalette {

	protected static PaletteRoot PALETTE_MODEL;

	protected LayoutTplDiagram diagram;

	public LayoutTplEditor() {
		super();
		setEditDomain(new DefaultEditDomain(this));
	}
	
	protected void createGraphicalViewer(Composite parent) {
		// GraphicalViewer viewer = new ScrollingGraphicalViewer();
		GraphicalViewer viewer = new GraphicalViewerImpl();
		viewer.createControl(parent);
		setGraphicalViewer(viewer);
		configureGraphicalViewer();
		hookGraphicalViewer();
		initializeGraphicalViewer();
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		
		GraphicalViewer viewer = getGraphicalViewer();

		viewer.setEditPartFactory(new LayoutTplEditPartFactory());

		// viewer.setRootEditPart(new ScalableRootEditPart());
		viewer.setRootEditPart(new LayoutTplRootEditPart());
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));

		// configure the context menu provider
		ContextMenuProvider cmProvider = new LayoutTplContextMenuProvider(viewer, getActionRegistry());
		viewer.setContextMenu(cmProvider);
		getSite().registerContextMenu(cmProvider, viewer);
	}

	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);

		super.commandStackChanged(event);
	}

	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {

			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				viewer.setPaletteViewerPreferences(new LayoutTplPaletteViewerPreferences());
				// create a drag source listener for this palette viewer
				// together with an appropriate transfer drop target listener,
				// this will enable
				// model element creation by dragging a
				// CombinatedTemplateCreationEntries
				// from the palette into the editor
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}

		};
	}

	/**
	 * Create a transfer drop target listener. When using a
	 * CombinedTemplateCreationEntry tool in the palette, this will enable model
	 * element creation by dragging from the palette.
	 * 
	 * @see #createPaletteViewerProvider()
	 */
	protected TransferDropTargetListener createTransferDropTargetListener() {
		return new TemplateTransferDropTargetListener(getGraphicalViewer()) {

			protected CreationFactory getFactory(Object template) {
				return new SimpleFactory((Class<?>) template);
			}

		};
	}

	protected void setInput(IEditorInput input) {
		super.setInput(input);

		try {
			IFile file = ((IFileEditorInput) input).getFile();
			setPartName(file.getName());

			diagram = LayoutTplDiagram.createFromFile(file);
		}
		catch (Exception e) {
			LayoutTplUI.logError(e);
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		IFile file = ((IFileEditorInput) getEditorInput()).getFile();
		try {
			diagram.saveToFile(file, monitor);
			getCommandStack().markSaveLocation();
		}
		catch (Exception e) {
			LayoutTplUI.logError(e);
		}
	}

	public void doSaveAs() {
		// TODO LayoutTplEditor#doSaveAs()
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class type) {
		if (type == IContentOutlinePage.class) {
			return new LayoutTplOutlinePage(this, new TreeViewer());
		}

		return super.getAdapter(type);
	}

	protected PaletteRoot getPaletteRoot() {
		if (PALETTE_MODEL == null) {
			PALETTE_MODEL = LayoutTplEditorPaletteFactory.createPalette();
		}

		return PALETTE_MODEL;
	}
	
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		final GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(getDiagram()); // set the contents of this editor
		viewer.getControl().addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				getGraphicalViewer().getContents().refresh();// rebuild column heights if needed
				viewer.getControl().removePaintListener(this);
			}

		});

		// listen for dropped parts
		viewer.addDropTargetListener(createTransferDropTargetListener());
	}

	public LayoutTplDiagram getDiagram() {
		return diagram;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}

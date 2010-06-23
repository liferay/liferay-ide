package com.liferay.ide.eclipse.layouttpl.ui.editor;

import com.liferay.ide.eclipse.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.eclipse.layouttpl.ui.gef.GraphicalEditorWithFlyoutPalette;
import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.parts.LayoutTplEditPartFactory;

import java.util.EventObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.util.TransferDropTargetListener;
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
	
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new LayoutTplEditPartFactory());
		viewer.setRootEditPart(new ScalableRootEditPart());
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

			if (diagram == null) {
				diagram = new LayoutTplDiagram();
			}

			diagram.loadFromFile(file);
		}
		catch (Exception e) {
			LayoutTplUI.logError(e);
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO LayoutTplEditor#doSave()
	}

	public void doSaveAs() {
		// TODO LayoutTplEditor#doSaveAs()
	}

	@SuppressWarnings("unchecked")
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
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(getDiagram()); // set the contents of this editor

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

package com.liferay.ide.eclipse.layouttpl.ui.editor;

import com.liferay.ide.eclipse.layouttpl.ui.parts.LayoutTplTreeEditPartFactory;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;


public class LayoutTplOutlinePage extends ContentOutlinePage {

	protected LayoutTplEditor layoutTplEditor;

	public LayoutTplOutlinePage(LayoutTplEditor layoutTplEditor, EditPartViewer viewer) {
		super(viewer);

		this.layoutTplEditor = layoutTplEditor;
	}

	public void dispose() {
		// unhook outline viewer
		if (layoutTplEditor != null) {
			layoutTplEditor.getSelectionSynchronizer().removeViewer(getViewer());
		}

		// dispose
		super.dispose();
	}

	public void createControl(Composite parent) {
		// create outline viewer page
		getViewer().createControl(parent);

		if (layoutTplEditor != null) {
			getViewer().setEditDomain(this.layoutTplEditor.getEditDomain());
		}
		// configure outline viewer

		getViewer().setEditPartFactory(new LayoutTplTreeEditPartFactory());
		// configure & add context menu to viewer
		ContextMenuProvider cmProvider =
			new LayoutTplContextMenuProvider(getViewer(), this.layoutTplEditor.getActionRegistry());
		getViewer().setContextMenu(cmProvider);
		getSite().registerContextMenu(
			"com.liferay.ide.eclipse.layouttpl.ui.outline.contextmenu", cmProvider, getSite().getSelectionProvider());
		// hook outline viewer
		this.layoutTplEditor.getSelectionSynchronizer().addViewer(getViewer());
		// initialize outline viewer with model
		getViewer().setContents(this.layoutTplEditor.getDiagram());
		// show outline viewer
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#getControl()
	 */
	public Control getControl() {
		return getViewer().getControl();
	}

	/**
	 * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
	 */
	public void init(IPageSite pageSite) {
		super.init(pageSite);

		if (layoutTplEditor != null) {
			ActionRegistry registry = layoutTplEditor.getActionRegistry();

			String undoId = ActionFactory.UNDO.getId();
			String redoId = ActionFactory.REDO.getId();
			String deleteId = ActionFactory.DELETE.getId();

			IActionBars bars = pageSite.getActionBars();
			bars.setGlobalActionHandler(undoId, registry.getAction(undoId));
			bars.setGlobalActionHandler(redoId, registry.getAction(redoId));
			bars.setGlobalActionHandler(deleteId, registry.getAction(deleteId));
		}

	}
}

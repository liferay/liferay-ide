package com.liferay.ide.eclipse.layouttpl.ui.editor;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.html.ui.internal.edit.ui.ActionContributorHTML;
import org.eclipse.wst.sse.ui.internal.ISourceViewerActionBarContributor;


@SuppressWarnings("restriction")
public class LayoutTplMultiPageEditorActionBarContributor extends MultiPageEditorActionBarContributor {

	protected IEditorActionBarContributor sourceEditorContributor;
	protected IEditorActionBarContributor visualEditorContributor;
	protected MultiPageEditorPart multiPageEditor;
	protected boolean needsMultiInit;

	public LayoutTplMultiPageEditorActionBarContributor() {
		super();
				this.sourceEditorContributor = new ActionContributorHTML();
	}

	public void init(IActionBars actionBars) {
		super.init(actionBars);

		if (actionBars != null) {
			initDesignViewerActionBarContributor(actionBars);
			initSourceViewerActionContributor(actionBars);
		}

		needsMultiInit = true;
	}

	protected void initDesignViewerActionBarContributor(IActionBars actionBars) {
		if (visualEditorContributor != null) {
			visualEditorContributor.init(actionBars, getPage());
		}
	}

	protected void initSourceViewerActionContributor(IActionBars actionBars) {
		if (sourceEditorContributor != null) {
			sourceEditorContributor.init(actionBars, getPage());
		}
	}

	public void dispose() {
		super.dispose();

		if (visualEditorContributor != null) {
			visualEditorContributor.dispose();
		}

		if (sourceEditorContributor != null) {
			sourceEditorContributor.dispose();
		}

		multiPageEditor = null;
	}

	@Override
	public void setActivePage(IEditorPart activeEditor) {
		if (multiPageEditor != null) {
			if ((activeEditor != null) && (activeEditor instanceof ITextEditor)) {
				activateSourcePage(activeEditor);
			}
			else {
				activateVisualPage(activeEditor);
			}
		}

		IActionBars actionBars = getActionBars();
		if (actionBars != null) {
			// update menu bar and tool bar
			actionBars.updateActionBars();
		}
	}

	@Override
	public void setActiveEditor(IEditorPart part) {
		if (part instanceof MultiPageEditorPart) {
			this.multiPageEditor = (MultiPageEditorPart) part;
		}

		if (needsMultiInit) {
			visualEditorContributor = new LayoutTplEditorActionBarContributor();
			initDesignViewerActionBarContributor(getActionBars());
			needsMultiInit = false;
		}

		super.setActiveEditor(part);
	}

	protected void activateVisualPage(IEditorPart activeEditor) {
		if ((sourceEditorContributor != null) && (sourceEditorContributor instanceof ISourceViewerActionBarContributor)) {
			// if design page is not really an IEditorPart, activeEditor ==
			// null, so pass in multiPageEditor instead (d282414)
			if (activeEditor == null) {
				sourceEditorContributor.setActiveEditor(multiPageEditor);
			}
			else {
				sourceEditorContributor.setActiveEditor(activeEditor);
			}
			((ISourceViewerActionBarContributor) sourceEditorContributor).setViewerSpecificContributionsEnabled(false);
		}
	}

	protected void activateSourcePage(IEditorPart activeEditor) {
		if (visualEditorContributor != null) {
			visualEditorContributor.setActiveEditor(multiPageEditor);
		}

		if ((sourceEditorContributor != null) && (sourceEditorContributor instanceof ISourceViewerActionBarContributor)) {
			sourceEditorContributor.setActiveEditor(activeEditor);
			((ISourceViewerActionBarContributor) sourceEditorContributor).setViewerSpecificContributionsEnabled(true);
		}
	}

}

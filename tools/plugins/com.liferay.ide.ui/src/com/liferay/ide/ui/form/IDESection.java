/*******************************************************************************
 *  Copyright (c) 2003, 2009 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.ui.form;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.liferay.ide.core.model.IBaseModel;
import com.liferay.ide.core.model.IModelChangedEvent;
import com.liferay.ide.core.model.IModelChangedListener;

public abstract class IDESection extends SectionPart implements IModelChangedListener, IContextPart, IAdaptable {

	private IDEFormPage fPage;

	public IDESection(IDEFormPage page, Composite parent, int style) {
		this(page, parent, style, true);
	}

	public IDESection(IDEFormPage page, Composite parent, int style, boolean titleBar) {
		super(parent, page.getManagedForm().getToolkit(), titleBar ? (ExpandableComposite.TITLE_BAR | style) : style);
		fPage = page;
		initialize(page.getManagedForm());
		getSection().clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		getSection().setData("part", this); //$NON-NLS-1$
	}

	protected abstract void createClient(Section section, FormToolkit toolkit);

	public IDEFormPage getPage() {
		return fPage;
	}

	protected IProject getProject() {
		return fPage.getFormEditor().getCommonProject();
	}

	public boolean doGlobalAction(String actionId) {
		return false;
	}

	public void modelChanged(IModelChangedEvent e) {
		if (e.getChangeType() == IModelChangedEvent.WORLD_CHANGED)
			markStale();
	}

	public String getContextId() {
		return null;
	}

	public void fireSaveNeeded() {
		markDirty();
		if (getContextId() != null)
			getPage().getFormEditor().fireSaveNeeded(getContextId(), false);
	}

	public boolean isEditable() {
		// getAggregateModel() can (though never should) return null
		IBaseModel model = getPage().getFormEditor().getModel();
		return model == null ? false : model.isEditable();
	}

	/**
	 * @param selection
	 * @return
	 */
	public boolean canCopy(ISelection selection) {
		// Sub-classes to override
		return false;
	}

	/**
	 * @param selection
	 * @return
	 */
	public boolean canCut(ISelection selection) {
		// Sub-classes to override
		return false;
	}

	/**
	 * @param clipboard
	 * @return
	 */
	public boolean canPaste(Clipboard clipboard) {
		return false;
	}

	public void cancelEdit() {
		super.refresh();
	}

	public Object getAdapter(Class adapter) {
		return null;
	}
}

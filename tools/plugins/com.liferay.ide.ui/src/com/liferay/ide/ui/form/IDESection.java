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

package com.liferay.ide.ui.form;

import com.liferay.ide.core.model.IBaseModel;
import com.liferay.ide.core.model.IModelChangedEvent;
import com.liferay.ide.core.model.IModelChangedListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Gregory Amerson
 */
public abstract class IDESection extends SectionPart implements IAdaptable, IContextPart, IModelChangedListener {

	public IDESection(IDEFormPage page, Composite parent, int style) {
		this(page, parent, style, true);
	}

	public IDESection(IDEFormPage page, Composite parent, int style, boolean titleBar) {
		super(parent, page.getManagedForm().getToolkit(), titleBar ? (ExpandableComposite.TITLE_BAR | style) : style);

		_fPage = page;
		initialize(page.getManagedForm());
		getSection().clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		getSection().setData("part", this);
	}

	public void cancelEdit() {
		super.refresh();
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

	public boolean doGlobalAction(String actionId) {
		return false;
	}

	public void fireSaveNeeded() {
		markDirty();

		if (getContextId() != null) {
			IDEFormEditor formEditor = getPage().getFormEditor();

			formEditor.fireSaveNeeded(getContextId(), false);
		}
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public String getContextId() {
		return null;
	}

	public IDEFormPage getPage() {
		return _fPage;
	}

	public boolean isEditable() {

		// getAggregateModel() can (though never should) return null

		IDEFormEditor formEditor = getPage().getFormEditor();

		IBaseModel model = formEditor.getModel();

		if (model == null) {
			return false;
		}

		return model.isEditable();
	}

	public void modelChanged(IModelChangedEvent e) {
		if (e.getChangeType() == IModelChangedEvent.WORLD_CHANGED) {
			markStale();
		}
	}

	protected abstract void createClient(Section section, FormToolkit toolkit);

	protected IProject getProject() {
		IDEFormEditor formEditor = _fPage.getFormEditor();

		return formEditor.getCommonProject();
	}

	private IDEFormPage _fPage;

}
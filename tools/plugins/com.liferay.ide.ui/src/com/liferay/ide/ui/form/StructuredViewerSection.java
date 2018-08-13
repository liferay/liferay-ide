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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Gregory Amerson
 */
public abstract class StructuredViewerSection extends IDESection {

	/**
	 * Constructor for StructuredViewerSection.
	 *
	 * @param formPage
	 */
	public StructuredViewerSection(
		IDEFormPage formPage, Composite parent, int style, boolean titleBar, String[] buttonLabels) {

		super(formPage, parent, style, titleBar);

		fViewerPart = createViewerPart(buttonLabels);

		fViewerPart.setMinimumSize(50, 50);

		IManagedForm form = formPage.getManagedForm();

		FormToolkit toolkit = form.getToolkit();

		createClient(getSection(), toolkit);

		_fDoSelection = true;
	}

	/**
	 * Constructor for StructuredViewerSection.
	 *
	 * @param formPage
	 */
	public StructuredViewerSection(IDEFormPage formPage, Composite parent, int style, String[] buttonLabels) {
		this(formPage, parent, style, true, buttonLabels);
	}

	/**
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.pde.internal.ui.editor.PDESection#canPaste(org.eclipse.swt.
	 * dnd.Clipboard)
	 */
	public boolean canPaste(Clipboard clipboard) {
		return false;
	}

	public StructuredViewerPart getStructuredViewerPart() {
		return fViewerPart;
	}

	public void setFocus() {
		Control control = fViewerPart.getControl();

		control.setFocus();
	}

	protected void buttonSelected(int index) {
	}

	/**
	 * @param targetObject
	 * @param sourceObjects
	 * @return
	 */
	protected boolean canPaste(Object targetObject, Object[] sourceObjects) {
		return false;
	}

	/**
	 * @return
	 */
	protected boolean canSelect() {
		return _fDoSelection;
	}

	protected Composite createClientContainer(Composite parent, int span, FormToolkit toolkit) {
		Composite container = toolkit.createComposite(parent);

		container.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, span));

		return container;
	}

	protected abstract StructuredViewerPart createViewerPart(String[] buttonLabels);

	protected void createViewerPartControl(Composite parent, int style, int span, FormToolkit toolkit) {
		fViewerPart.createControl(parent, style, span, toolkit);
		MenuManager popupMenuManager = new MenuManager();
		IMenuListener listener = new IMenuListener() {

			public void menuAboutToShow(IMenuManager mng) {
				fillContextMenu(mng);
			}

		};

		popupMenuManager.addMenuListener(listener);

		popupMenuManager.setRemoveAllWhenShown(true);
		Control control = fViewerPart.getControl();

		Menu menu = popupMenuManager.createContextMenu(control);

		control.setMenu(menu);
	}

	protected void doPaste() {

		// ISelection selection = getViewerSelection();
		// IStructuredSelection ssel = (IStructuredSelection) selection;
		// if (ssel.size() > 1)
		// return;

		//

		// Object target = ssel.getFirstElement();

		//

		// Clipboard clipboard = getPage().getPDEEditor().getClipboard();
		// ModelDataTransfer modelTransfer = ModelDataTransfer.getInstance();
		// Object[] objects = (Object[]) clipboard.getContents(modelTransfer);
		// if (objects != null) {
		// doPaste(target, objects);
		// }

	}

	/**
	 * @param targetObject
	 * @param sourceObjects
	 */
	protected void doPaste(Object targetObject, Object[] sourceObjects) {

		// NO-OP
		// Children will override to provide fuctionality

	}

	/**
	 * @param select
	 */
	protected void doSelect(boolean select) {
		_fDoSelection = select;
	}

	protected void fillContextMenu(IMenuManager manager) {
	}

	protected int getArrayIndex(Object[] array, Object object) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(object)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * <p>Given the index of TreeViewer item and the size of the array of its
	 * immediate siblings, gets the index of the desired new selection as
	 * follows: <ul><li>if this is the only item, return -1 (meaning select the
	 * parent)</li> <li>if this is the last item, return the index of the
	 * predecessor</li> <li>otherwise, return the index of the
	 * successor</li></ul></p>
	 *
	 * @param thisIndex
	 *            the item's index
	 * @param length
	 *            the array length
	 * @return new selection index or -1 for parent
	 */
	protected int getNewSelectionIndex(int thisIndex, int length) {
		if (thisIndex == (length - 1)) {
			return thisIndex - 1;
		}

		return thisIndex + 1;
	}

	protected ISelection getViewerSelection() {
		StructuredViewer viewer = fViewerPart.getViewer();

		return viewer.getSelection();
	}

	/**
	 * @return
	 */
	protected boolean isDragAndDropEnabled() {
		return false;
	}

	protected StructuredViewerPart fViewerPart;

	private boolean _fDoSelection;

}
/*******************************************************************************
 *  Copyright (c) 2000, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.ui.form;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.widgets.FormToolkit;


public abstract class StructuredViewerSection extends IDESection {

	protected StructuredViewerPart fViewerPart;

	private boolean fDoSelection;

	/**
	 * Constructor for StructuredViewerSection.
	 * @param formPage
	 */
	public StructuredViewerSection(IDEFormPage formPage, Composite parent, int style, String[] buttonLabels) {
		this(formPage, parent, style, true, buttonLabels);
	}

	/**
	 * Constructor for StructuredViewerSection.
	 * @param formPage
	 */
	public StructuredViewerSection(IDEFormPage formPage, Composite parent, int style, boolean titleBar, String[] buttonLabels) {
		super(formPage, parent, style, titleBar);
		fViewerPart = createViewerPart(buttonLabels);
		fViewerPart.setMinimumSize(50, 50);
		FormToolkit toolkit = formPage.getManagedForm().getToolkit();
		createClient(getSection(), toolkit);
		fDoSelection = true;
	}

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

	protected Composite createClientContainer(Composite parent, int span, FormToolkit toolkit) {
		Composite container = toolkit.createComposite(parent);
		container.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, span));
		return container;
	}

	protected abstract StructuredViewerPart createViewerPart(String[] buttonLabels);

	protected void fillContextMenu(IMenuManager manager) {
	}

	protected void buttonSelected(int index) {
	}

	protected void doPaste() {
//		ISelection selection = getViewerSelection();
//		IStructuredSelection ssel = (IStructuredSelection) selection;
//		if (ssel.size() > 1)
//			return;
//
//		Object target = ssel.getFirstElement();
//
//		Clipboard clipboard = getPage().getPDEEditor().getClipboard();
//		ModelDataTransfer modelTransfer = ModelDataTransfer.getInstance();
//		Object[] objects = (Object[]) clipboard.getContents(modelTransfer);
//		if (objects != null) {
//			doPaste(target, objects);
//		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.PDESection#canPaste(org.eclipse.swt.dnd.Clipboard)
	 */
	public boolean canPaste(Clipboard clipboard) {
//		ISelection selection = getViewerSelection();
//		IStructuredSelection ssel = (IStructuredSelection) selection;
//		if (ssel.size() > 1)
//			return false;
//
//		Object target = ssel.getFirstElement();
//		ModelDataTransfer modelTransfer = ModelDataTransfer.getInstance();
//		Object[] objects = (Object[]) clipboard.getContents(modelTransfer);
//		if (objects != null && objects.length > 0) {
//			return canPaste(target, objects);
//		}
		return false;
	}

	protected ISelection getViewerSelection() {
		return fViewerPart.getViewer().getSelection();
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
	 * @param targetObject
	 * @param sourceObjects
	 * @return
	 */
	protected boolean canPaste(Object targetObject, Object[] sourceObjects) {
		return false;
	}

	public void setFocus() {
		fViewerPart.getControl().setFocus();
	}

	public StructuredViewerPart getStructuredViewerPart() {
		return this.fViewerPart;
	}

	/**
	 * <p>Given the index of TreeViewer item and the size of the array of its immediate
	 * siblings, gets the index of the desired new selection as follows:
	 * <ul><li>if this is the only item, return -1 (meaning select the parent)</li>
	 * <li>if this is the last item, return the index of the predecessor</li>
	 * <li>otherwise, return the index of the successor</li></p>
	 * 
	 * @param thisIndex
	 * 			the item's index
	 * @param length
	 * 			the array length
	 * @return
	 * 			new selection index or -1 for parent
	 */
	protected int getNewSelectionIndex(int thisIndex, int length) {
		if (thisIndex == length - 1)
			return thisIndex - 1;
		return thisIndex + 1;
	}

	protected int getArrayIndex(Object[] array, Object object) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(object))
				return i;
		}
		return -1;
	}

	/**
	 * @return
	 */
	protected boolean isDragAndDropEnabled() {
		return false;
	}

	/**
	 * @param select
	 */
	protected void doSelect(boolean select) {
		fDoSelection = select;
	}

	/**
	 * @return
	 */
	protected boolean canSelect() {
		return fDoSelection;
	}

}

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

package com.liferay.ide.project.ui.dialog;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.ui.LiferayUIPlugin;

import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

/**
 * @author Andy Wu
 * @author Lovett Li
 * @author Simon Jiang
 */
public abstract class ProjectSelectionDialog extends SelectionStatusDialog {

	/**
	 * Constructor
	 *
	 * @param parentShell
	 * @param projectsWithSpecifics
	 */
	public ProjectSelectionDialog(Shell parentShell, ViewerFilter filter) {
		super(parentShell);

		_fFilter = filter;
	}

	// sizing constants

	protected void addSelectionButtons(Composite composite) {
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();

		layout.numColumns = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		buttonComposite.setLayout(layout);

		buttonComposite.setLayoutData(new GridData(SWT.END, SWT.TOP, true, false));
		Button selectButton = createButton(buttonComposite, IDialogConstants.SELECT_ALL_ID, "Select All", false);

		SelectionListener listener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectAllAction();
			}

		};

		selectButton.addSelectionListener(listener);

		Button deselectButton = createButton(buttonComposite, IDialogConstants.DESELECT_ALL_ID, "Deselect All", false);

		listener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				deSelectAllAction();
			}

		};

		deselectButton.addSelectionListener(listener);
	}

	protected  void selectAllAction() {
		fTableViewer.setAllChecked(true);
		getOkButton().setEnabled(true);
	}

	protected  void deSelectAllAction() {
		fTableViewer.setAllChecked(false);
		getOkButton().setEnabled(false);
	}

	// initial _fTableViewer status

	@Override
	public void create() {
		super.create();
		Object[] checkedElements = fTableViewer.getCheckedElements();

		if (ListUtil.isEmpty(checkedElements)) {
			getOkButton().setEnabled(false);
		}
	}

	protected abstract boolean checkProject(IJavaProject project);

	/**
	 * (non-Javadoc)
	 *
	 * @see SelectionStatusDialog#computeResult()
	 */
	protected void computeResult() {
	}

	/**
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite parent) {

		// page group

		Composite composite = (Composite)super.createDialogArea(parent);

		Font font = parent.getFont();

		composite.setFont(font);

		createMessageArea(composite);

		fTableViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);

		fTableViewer.addPostSelectionChangedListener(
			new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					updateOKButtonState(event);
				}

			});

		addSelectionButtons(composite);

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);

		data.heightHint = _sizingSelectionWidgetHeight;
		data.widthHint = _sizingSelectionWidgetWidth;
		fTableViewer.getTable().setLayoutData(data);

		fTableViewer.setLabelProvider(new JavaElementLabelProvider());
		fTableViewer.setContentProvider(getContentProvider());
		fTableViewer.setComparator(new JavaElementComparator());
		fTableViewer.getControl().setFont(font);

		updateFilter(true);

		IJavaModel input = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());

		fTableViewer.setInput(input);

		initialize();

		_selectionChanged(new Object[0]);
		Dialog.applyDialogFont(composite);

		return composite;
	}

	// the visual selection widget group

	protected IContentProvider getContentProvider() {
		return new JavaProjectProvider();
	}

	protected void initialize() {
		fTableViewer.setAllChecked(true);
	}

	@Override
	protected void okPressed() {
		Object[] checkedElements = fTableViewer.getCheckedElements();

		setSelectionResult(checkedElements);

		super.okPressed();
	}

	/**
	 * Updates the viewer filter based on the selection of the 'show project
	 * with...' button
	 *
	 * @param selected
	 */
	protected void updateFilter(boolean selected) {
		if (_fFilter == null) {
			return;
		}

		if (selected) {
			fTableViewer.addFilter(_fFilter);
		}
		else {
			fTableViewer.removeFilter(_fFilter);
		}
	}

	protected CheckboxTableViewer fTableViewer;

	/**
	 * Handles the change in selection of the viewer and updates the status of the
	 * dialog at the same time
	 *
	 * @param objects
	 */
	private void _selectionChanged(Object[] objects) {
		updateStatus(new Status(IStatus.OK, LiferayUIPlugin.PLUGIN_ID, StringPool.EMPTY));
		setSelectionResult(objects);
	}

	protected void updateOKButtonState(EventObject event) {
		Object element = event.getSource();

		if (element instanceof CheckboxTableViewer) {
			Object[] checkedElements = ((CheckboxTableViewer)element).getCheckedElements();

			if (checkedElements.length == 0) {
				getOkButton().setEnabled(false);
			}
			else {
				getOkButton().setEnabled(true);
			}
		}
	}

	private static final int _sizingSelectionWidgetHeight = 250;
	private static final int _sizingSelectionWidgetWidth = 300;

	/**
	 * The filter for the viewer
	 */
	private ViewerFilter _fFilter;

	private class JavaProjectProvider extends StandardJavaElementContentProvider {

		public Object[] getChildren(Object element) {
			if (element instanceof IJavaModel) {
				final IJavaModel model = (IJavaModel)element;
				final Set<IJavaProject> set = new HashSet<>();

				try {
					final IJavaProject[] projects = model.getJavaProjects();

					for (int i = 0; i < projects.length; i++) {
						if (checkProject(projects[i])) {
							set.add(projects[i]);
						}
					}
				}
				catch (JavaModelException jme) {

					// ignore

				}

				return set.toArray();
			}

			return super.getChildren(element);
		}

	}

}
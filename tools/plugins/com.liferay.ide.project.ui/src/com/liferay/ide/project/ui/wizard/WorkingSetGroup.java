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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.DeviceResourceException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.dialogs.IWorkingSetSelectionDialog;

/**
 * @author Gregory Amerson
 */
public class WorkingSetGroup {

	public WorkingSetGroup(Composite container, List<IWorkingSet> workingSets, Shell shell) {
		_workingSets = workingSets;
		_shell = shell;

		_createControl(container);
	}

	public void dispose() {
		IBaseLabelProvider labelProvider = _workingsetComboViewer.getLabelProvider();

		labelProvider.dispose();
	}

	public boolean selectWorkingSets(List<IWorkingSet> workingSets) {
		Set<IWorkingSet> defaultSets = _getWorkingSets();

		_workingsetComboViewer.setInput(defaultSets);

		if (ListUtil.isNotEmpty(workingSets)) {
			if (workingSets.size() == 1) {
				IWorkingSet workingSet = workingSets.get(0);

				if (defaultSets.contains(workingSet)) {
					_workingsetComboViewer.setSelection(new StructuredSelection(workingSet));
				}
			}
			else {
				_workingsetComboViewer.add(workingSets);
				_workingsetComboViewer.setSelection(new StructuredSelection((Object)workingSets));
			}

			return true;
		}

		return false;
	}

	public Button addToWorkingSetButton;

	protected void updateConfiguration() {
		if (addToWorkingSetButton.getSelection()) {
			IStructuredSelection selection = (IStructuredSelection)_workingsetComboViewer.getSelection();

			Object o = selection.getFirstElement();

			if (o != null) {
				_workingSets.clear();

				if (o instanceof IWorkingSet) {
					_workingSets.add((IWorkingSet)o);
				}
				else if (o instanceof List<?>) {
					@SuppressWarnings("unchecked")
					List<IWorkingSet> l = (List<IWorkingSet>)o;

					_workingSets.addAll(l);
				}
			}
		}
	}

	private void _createControl(Composite container) {
		addToWorkingSetButton = new Button(container, SWT.CHECK);

		GridData gd_addToWorkingSetButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);

		gd_addToWorkingSetButton.verticalIndent = 12;

		addToWorkingSetButton.setLayoutData(gd_addToWorkingSetButton);

		addToWorkingSetButton.setSelection(true);
		addToWorkingSetButton.setData("name", "addToWorkingSetButton");
		addToWorkingSetButton.setText("Add project to working set");
		addToWorkingSetButton.setSelection(false);

		final Label workingsetLabel = new Label(container, SWT.NONE);
		GridData gd_workingsetLabel = new GridData();

		gd_workingsetLabel.horizontalIndent = 10;
		workingsetLabel.setLayoutData(gd_workingsetLabel);

		workingsetLabel.setEnabled(false);
		workingsetLabel.setData("name", "workingsetLabel");
		workingsetLabel.setText("Working set:");

		Combo workingsetCombo = new Combo(container, SWT.READ_ONLY);

		workingsetCombo.setEnabled(false);
		workingsetCombo.setData("name", "workingsetCombo");
		workingsetCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		_workingsetComboViewer = new ComboViewer(workingsetCombo);

		_workingsetComboViewer.setContentProvider(
			new IStructuredContentProvider() {

				public void dispose() {
				}

				public Object[] getElements(Object input) {
					if (input instanceof IWorkingSet[]) {
						return (IWorkingSet[])input;
					}
					else if (input instanceof List<?>) {
						return new Object[] {input};
					}
					else if (input instanceof Set<?>) {
						Set<?> setInput = (Set<?>)input;

						return setInput.toArray();
					}

					return new IWorkingSet[0];
				}

				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}

			});
		_workingsetComboViewer.setLabelProvider(
			new LabelProvider() {

				public void dispose() {
					_images.dispose();
					super.dispose();
				}

				@SuppressWarnings("deprecation")
				public Image getImage(Object element) {
					if (element instanceof IWorkingSet) {
						IWorkingSet workingSetElement = (IWorkingSet)element;

						ImageDescriptor imageDescriptor = workingSetElement.getImage();

						if (imageDescriptor != null) {
							try {
								return (Image)_images.create(imageDescriptor);
							}
							catch (DeviceResourceException dre) {
								return null;
							}
						}
					}

					return super.getImage(element);
				}

				public String getText(Object element) {
					if (element instanceof IWorkingSet) {
						IWorkingSet workingSetElement = (IWorkingSet)element;

						return workingSetElement.getLabel();
					}
					else if (element instanceof List<?>) {
						StringBuffer sb = new StringBuffer();

						for (Object o : (List<?>)element) {
							if (o instanceof IWorkingSet) {
								if (sb.length() > 0) {
									sb.append(", ");
								}

								IWorkingSet workingSetObject = (IWorkingSet)o;

								sb.append(workingSetObject.getLabel());
							}
						}

						return sb.toString();
					}

					return super.getText(element);
				}

				private ResourceManager _images = new LocalResourceManager(JFaceResources.getResources());

			});

		_workingsetComboViewer.setComparator(new ViewerComparator());

		final Button newWorkingSetButton = new Button(container, SWT.NONE);

		newWorkingSetButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		newWorkingSetButton.setData("name", "configureButton");
		newWorkingSetButton.setText("More...");
		newWorkingSetButton.setEnabled(false);
		newWorkingSetButton.addSelectionListener(
			new SelectionAdapter() {

				public void widgetSelected(final SelectionEvent e) {
					IWorkingSetManager workingSetManager = UIUtil.getWorkingSetManager();

					IWorkingSetSelectionDialog dialog = workingSetManager.createWorkingSetSelectionDialog(
						_shell, true, _workingSetIds.toArray(new String[0]));

					if (dialog.open() == Window.OK) {
						IWorkingSet[] workingSets = dialog.getSelection();

						selectWorkingSets(Arrays.asList(workingSets));
					}
				}

			});

		if (selectWorkingSets(_workingSets)) {
			addToWorkingSetButton.setSelection(true);
			workingsetLabel.setEnabled(true);

			Combo combo = _workingsetComboViewer.getCombo();

			combo.setEnabled(true);

			newWorkingSetButton.setEnabled(true);
		}

		addToWorkingSetButton.addSelectionListener(
			new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					boolean addToWorkingingSet = addToWorkingSetButton.getSelection();

					workingsetLabel.setEnabled(addToWorkingingSet);

					Combo combo = _workingsetComboViewer.getCombo();

					combo.setEnabled(addToWorkingingSet);

					newWorkingSetButton.setEnabled(addToWorkingingSet);

					if (addToWorkingingSet) {
						updateConfiguration();
					}
					else {
						_workingSets.clear();
					}
				}

			});

		_workingsetComboViewer.addSelectionChangedListener(
			new ISelectionChangedListener() {

				public void selectionChanged(SelectionChangedEvent event) {
					updateConfiguration();
				}

			});
	}

	private Set<IWorkingSet> _getWorkingSets() {
		Set<IWorkingSet> workingSets = new HashSet<>();

		IWorkingSetManager workingSetManager = UIUtil.getWorkingSetManager();

		for (IWorkingSet workingSet : workingSetManager.getWorkingSets()) {
			if (!workingSet.isEmpty()) {
				IAdaptable[] elements = workingSet.getElements();

				IResource resource = (IResource)elements[0].getAdapter(IResource.class);

				if (resource != null) {
					workingSets.add(workingSet);
				}
			}
			else {
				if (_workingSetIds.contains(workingSet.getId())) {
					workingSets.add(workingSet);
				}
			}
		}

		return workingSets;
	}

	private static final List<String> _workingSetIds = Arrays.asList(
		//
		"org.eclipse.ui.resourceWorkingSetPage", "org.eclipse.jdt.ui.JavaWorkingSetPage");

	private final Shell _shell;
	private ComboViewer _workingsetComboViewer;
	private final List<IWorkingSet> _workingSets;

}
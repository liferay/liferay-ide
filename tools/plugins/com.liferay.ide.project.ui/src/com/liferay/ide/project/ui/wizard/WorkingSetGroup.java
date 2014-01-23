/*******************************************************************************
 * Copyright (c) 2008-2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.project.ui.wizard;

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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetSelectionDialog;

/**
 * Copied from org.eclipse.m2e.core.ui.internal.components.WorkingSetGroup
 */
// TODO reconcile with WorkingSets
public class WorkingSetGroup {

  static final List<String> WORKING_SET_IDS = Arrays.asList( //
      "org.eclipse.ui.resourceWorkingSetPage", "org.eclipse.jdt.ui.JavaWorkingSetPage"); //$NON-NLS-1$ //$NON-NLS-2$

  ComboViewer workingsetComboViewer;

  Button addToWorkingSetButton;

  final List<IWorkingSet> workingSets;

  final Shell shell;

  public WorkingSetGroup(Composite container, List<IWorkingSet> workingSets, Shell shell) {
    this.workingSets = workingSets;
    this.shell = shell;

    createControl(container);
  }

  private void createControl(Composite container) {
    addToWorkingSetButton = new Button(container, SWT.CHECK);
    GridData gd_addToWorkingSetButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
    gd_addToWorkingSetButton.verticalIndent = 12;
    addToWorkingSetButton.setLayoutData(gd_addToWorkingSetButton);
    addToWorkingSetButton.setSelection(true);
    addToWorkingSetButton.setData("name", "addToWorkingSetButton"); //$NON-NLS-1$ //$NON-NLS-2$
    addToWorkingSetButton.setText("Add project to working set");
    addToWorkingSetButton.setSelection(false);

    final Label workingsetLabel = new Label(container, SWT.NONE);
    GridData gd_workingsetLabel = new GridData();
    gd_workingsetLabel.horizontalIndent = 10;
    workingsetLabel.setLayoutData(gd_workingsetLabel);
    workingsetLabel.setEnabled(false);
    workingsetLabel.setData("name", "workingsetLabel"); //$NON-NLS-1$ //$NON-NLS-2$
    workingsetLabel.setText("Working set:");

    Combo workingsetCombo = new Combo(container, SWT.READ_ONLY);
    workingsetCombo.setEnabled(false);
    workingsetCombo.setData("name", "workingsetCombo"); //$NON-NLS-1$ //$NON-NLS-2$
    workingsetCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    workingsetComboViewer = new ComboViewer(workingsetCombo);
    workingsetComboViewer.setContentProvider(new IStructuredContentProvider() {
      public Object[] getElements(Object input) {
        if(input instanceof IWorkingSet[]) {
          return (IWorkingSet[]) input;
        } else if(input instanceof List<?>) {
          return new Object[] {input};
        } else if(input instanceof Set<?>) {
          return ((Set<?>) input).toArray();
        }
        return new IWorkingSet[0];
      }

      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }

      public void dispose() {
      }
    });
    workingsetComboViewer.setLabelProvider(new LabelProvider() {
      private ResourceManager images = new LocalResourceManager(JFaceResources.getResources());

      @SuppressWarnings("deprecation")
      public Image getImage(Object element) {
        if(element instanceof IWorkingSet) {
          ImageDescriptor imageDescriptor = ((IWorkingSet) element).getImage();
          if(imageDescriptor != null) {
            try {
              return (Image) images.create(imageDescriptor);
            } catch(DeviceResourceException ex) {
              return null;
            }
          }
        }
        return super.getImage(element);
      }

      public String getText(Object element) {
        if(element instanceof IWorkingSet) {
          return ((IWorkingSet) element).getLabel();
        } else if(element instanceof List<?>) {
          StringBuffer sb = new StringBuffer();
          for(Object o : (List<?>) element) {
            if(o instanceof IWorkingSet) {
              if(sb.length() > 0) {
                sb.append(", "); //$NON-NLS-1$
              }
              sb.append(((IWorkingSet) o).getLabel());
            }
          }
          return sb.toString();
        }
        return super.getText(element);
      }

      public void dispose() {
        images.dispose();
        super.dispose();
      }
    });

    workingsetComboViewer.setComparator(new ViewerComparator());

    final Button newWorkingSetButton = new Button(container, SWT.NONE);
    newWorkingSetButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    newWorkingSetButton.setData("name", "configureButton"); //$NON-NLS-1$ //$NON-NLS-2$
    newWorkingSetButton.setText("More...");
    newWorkingSetButton.setEnabled(false);
    newWorkingSetButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(final SelectionEvent e) {
        IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
        IWorkingSetSelectionDialog dialog = workingSetManager.createWorkingSetSelectionDialog(shell, true,
            WORKING_SET_IDS.toArray(new String[0]));
        if(dialog.open() == Window.OK) {
          IWorkingSet[] workingSets = dialog.getSelection();
          selectWorkingSets(Arrays.asList(workingSets));
        }
      }
    });

    if(selectWorkingSets(workingSets)) {
      addToWorkingSetButton.setSelection(true);
      workingsetLabel.setEnabled(true);
      workingsetComboViewer.getCombo().setEnabled(true);
      newWorkingSetButton.setEnabled(true);
    }

    addToWorkingSetButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        boolean addToWorkingingSet = addToWorkingSetButton.getSelection();
        workingsetLabel.setEnabled(addToWorkingingSet);
        workingsetComboViewer.getCombo().setEnabled(addToWorkingingSet);
        newWorkingSetButton.setEnabled(addToWorkingingSet);
        if(addToWorkingingSet) {
          updateConfiguration();
        } else {
          workingSets.clear();
        }
      }
    });

    workingsetComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent event) {
        updateConfiguration();
      }
    });
  }

  protected void updateConfiguration() {
    if(addToWorkingSetButton.getSelection()) {
      IStructuredSelection selection = (IStructuredSelection) workingsetComboViewer.getSelection();
      Object o = selection.getFirstElement();
      if(o != null) {
        workingSets.clear();
        if(o instanceof IWorkingSet) {
          workingSets.add((IWorkingSet) o);
        } else if(o instanceof List<?>) {
          @SuppressWarnings("unchecked")
          List<IWorkingSet> l = (List<IWorkingSet>) o;
          workingSets.addAll(l);
        }
      }
    }
  }

  Set<IWorkingSet> getWorkingSets() {
    Set<IWorkingSet> workingSets = new HashSet<IWorkingSet>();

    IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
    for(IWorkingSet workingSet : workingSetManager.getWorkingSets()) {
      if(!workingSet.isEmpty()) {
        IAdaptable[] elements = workingSet.getElements();
        IResource resource = (IResource) elements[0].getAdapter(IResource.class);
        if(resource != null) {
          workingSets.add(workingSet);
        }
      } else {
        if(WORKING_SET_IDS.contains(workingSet.getId())) {
          workingSets.add(workingSet);
        }
      }
    }

    return workingSets;
  }

  public void dispose() {
    workingsetComboViewer.getLabelProvider().dispose();
  }

  public boolean selectWorkingSets(List<IWorkingSet> workingSets) {
    Set<IWorkingSet> defaultSets = getWorkingSets();
    workingsetComboViewer.setInput(defaultSets);

    if(workingSets != null && workingSets.size() > 0) {
      if(workingSets.size() == 1) {
        IWorkingSet workingSet = workingSets.get(0);
        if(defaultSets.contains(workingSet)) {
          workingsetComboViewer.setSelection(new StructuredSelection(workingSet));
        }
      } else {
        workingsetComboViewer.add(workingSets);
        workingsetComboViewer.setSelection(new StructuredSelection((Object) workingSets));
      }
      return true;
    }
    return false;
  }
}

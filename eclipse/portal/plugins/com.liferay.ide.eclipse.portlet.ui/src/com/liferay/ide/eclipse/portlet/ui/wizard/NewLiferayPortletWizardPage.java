/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.ui.wizard;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.ui.util.SWTUtil;
import com.liferay.ide.eclipse.ui.wizard.LiferayDataModelWizardPage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class NewLiferayPortletWizardPage extends LiferayDataModelWizardPage
	implements INewPortletClassDataModelProperties {

	protected Button addToControlPanelButton;
	protected Button allowMultiInstanceButton;
	protected Combo category;
	protected Button createEntryClassButton;
	protected Text cssClassWrapper;
	protected Text cssFile;
	protected Combo entryCategory;
	protected Text entryClassWrapper;
    protected Text entryWeight;
	protected boolean fragment;
	protected Text iconFile;
	protected Text id;
	protected Text javascriptFile;

	// protected Text name;

	public NewLiferayPortletWizardPage(
		IDataModel dataModel, String pageName, String desc, String title, boolean fragment) {

		super(dataModel, pageName, title, null);
		this.fragment = fragment;
		setDescription(desc);
	}

	protected void createLiferayDisplayGroup(Composite composite) {
		Group group = SWTUtil.createGroup(composite, "Liferay Display", 2);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		SWTUtil.createLabel(group, "Display Category:", 1);

		this.category = new Combo(group, SWT.DROP_DOWN);
		this.category.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.synchHelper.synchCombo(category, CATEGORY, null);
        
		SWTUtil.createLabel(group, "", 1);

		this.addToControlPanelButton = SWTUtil.createCheckButton(group, "Add to Control Panel", null, false, 1);
        this.synchHelper.synchCheckbox(this.addToControlPanelButton, ADD_TO_CONTROL_PANEL, null);

        final Label entryCategoryLabel = SWTUtil.createLabel(group, "Entry Catagory:", 1);
        
        this.entryCategory = new Combo(group, SWT.DROP_DOWN);
        this.entryCategory.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.synchHelper.synchCombo(entryCategory, ENTRY_CATEGORY, null);

        final Label entryWeightLabel = SWTUtil.createLabel(group, "Entry Weight:", 1);
        
        this.entryWeight = SWTUtil.createText(group, 1);
        this.synchHelper.synchText(entryWeight, ENTRY_WEIGHT, null);
        
        SWTUtil.createLabel(group, "", 1);
        
        this.createEntryClassButton = SWTUtil.createCheckButton(group, "Create Entry Class", null, false, 1);
        this.createEntryClassButton.setToolTipText( "The control-panel-entry-class value must be a class that "
            + "implements\n com.liferay.portlet.ControlPanelEntry and is called by the Control Panel to\n "
            + "decide whether the portlet should be shown to a specific user in a specific\n context." );
        this.synchHelper.synchCheckbox(createEntryClassButton, CREATE_ENTRY_CLASS, null);
        
        final Label entryClassLabel = SWTUtil.createLabel(group, "Entry Class:", 1);
        
        this.entryClassWrapper = SWTUtil.createText(group, 1);
        this.synchHelper.synchText(entryClassWrapper, ENTRY_CLASS_NAME, null);
        
        addToControlPanelButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                entryCategoryLabel.setEnabled( addToControlPanelButton.getSelection() );
                entryCategory.setEnabled( addToControlPanelButton.getSelection() );

                entryWeightLabel.setEnabled( addToControlPanelButton.getSelection() );
                entryWeight.setEnabled( addToControlPanelButton.getSelection() );
                
                createEntryClassButton.setEnabled( addToControlPanelButton.getSelection() );
                
                entryClassLabel.setEnabled( addToControlPanelButton.getSelection() && createEntryClassButton.getSelection() );
                entryClassWrapper.setEnabled( addToControlPanelButton.getSelection() && createEntryClassButton.getSelection() );
            }
        });
        
        createEntryClassButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                entryClassLabel.setEnabled(createEntryClassButton.getSelection());
                entryClassWrapper.setEnabled(createEntryClassButton.getSelection());
            }
        });
	}
	
	protected void createLiferayPortletInfoGroup(Composite composite) {
		Group group = SWTUtil.createGroup(composite, "Liferay Portlet Info", 3);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;

		group.setLayoutData(gd);

		// we don't need to create the name as it can never be anything different than the portlet name on 2nd page
		// SWTUtil.createLabel(group, SWT.RIGHT, "Name:", 1);
		//
		// this.name = SWTUtil.createText(group, 1);
		// this.synchHelper.synchText(name, LIFERAY_PORTLET_NAME, null);
		// SWTUtil.createLabel(group, "", 1);

		SWTUtil.createLabel(group, SWT.RIGHT, "Icon:", 1);

		this.iconFile = SWTUtil.createText(group, 1);
		this.synchHelper.synchText(iconFile, ICON_FILE, null);

		if (this.fragment) {
			SWTUtil.createLabel(group, "", 1);
		}
		else {
			Button iconFileBrowse = SWTUtil.createPushButton(group, "Browse...", null);
			iconFileBrowse.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					handleFileBrowseButton(
						NewLiferayPortletWizardPage.this.iconFile, "Icon Selection", "Choose an icon file: ");
				}

			});
		}

		SWTUtil.createLabel(group, "", 1);

		this.allowMultiInstanceButton = SWTUtil.createCheckButton(group, "Allow multiple instances", null, true, 2);
		this.synchHelper.synchCheckbox(this.allowMultiInstanceButton, ALLOW_MULTIPLE, null);

		SWTUtil.createLabel(group, SWT.RIGHT, "CSS:", 1);

		this.cssFile = SWTUtil.createText(group, 1);
		this.synchHelper.synchText(cssFile, CSS_FILE, null);

		if (this.fragment) {
			SWTUtil.createLabel(group, "", 1);
		}
		else {
			Button cssFileBrowse = SWTUtil.createPushButton(group, "Browse...", null);
			cssFileBrowse.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					handleFileBrowseButton(
						NewLiferayPortletWizardPage.this.cssFile, "CSS Selection", "Choose a css file: ");
				}

			});
		}

		SWTUtil.createLabel(group, SWT.RIGHT, "JavaScript:", 1);

		this.javascriptFile = SWTUtil.createText(group, 1);
		this.synchHelper.synchText(javascriptFile, JAVASCRIPT_FILE, null);

		if (this.fragment) {
			SWTUtil.createLabel(group, "", 1);
		}
		else {
			Button javascriptFileBrowse = SWTUtil.createPushButton(group, "Browse...", null);
			javascriptFileBrowse.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					handleFileBrowseButton(
						NewLiferayPortletWizardPage.this.javascriptFile, "JavaScript Selection",
						"Choose a javascript file: ");
				}

			});
		}

		SWTUtil.createLabel(group, SWT.RIGHT, "CSS class wrapper:", 1);

		this.cssClassWrapper = SWTUtil.createText(group, 1);
		this.synchHelper.synchText(cssClassWrapper, CSS_CLASS_WRAPPER, null);

		SWTUtil.createLabel(group, "", 1);

		this.synchHelper.getDataModel().addListener(new IDataModelListener() {

			public void propertyChanged(DataModelEvent event) {
				if (INewJavaClassDataModelProperties.CLASS_NAME.equals(event.getPropertyName()) ||
					PORTLET_NAME.equals(event.getPropertyName())) {

					synchHelper.synchAllUIWithModel();
				}
			}
		});
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite composite = SWTUtil.createTopComposite(parent, 3);

		createLiferayPortletInfoGroup(composite);

		createLiferayDisplayGroup(composite);

		return composite;
	}

    @Override
    protected void enter() {
        super.enter();

        if (entryCategory != null && !entryCategory.isDisposed()) {
            entryCategory.setEnabled(addToControlPanelButton.getSelection());
        }
        if (entryWeight != null && !entryWeight.isDisposed()) {
            entryWeight.setEnabled(addToControlPanelButton.getSelection());
        }
        if (createEntryClassButton != null && !createEntryClassButton.isDisposed()) {
            createEntryClassButton.setEnabled( addToControlPanelButton.getSelection() );
        }
        if (entryClassWrapper != null && !entryClassWrapper.isDisposed()) {
            entryClassWrapper.setEnabled( createEntryClassButton.getSelection() && createEntryClassButton.getEnabled() );
        }
    }

	protected ISelectionStatusValidator getContainerDialogSelectionValidator() {
		return new ISelectionStatusValidator() {

			public IStatus validate(Object[] selection) {
				if (selection != null && selection.length > 0 && selection[0] != null &&
					!(selection[0] instanceof IProject) && !(selection[0] instanceof IFolder)) {
					return Status.OK_STATUS;
				}

				return PortletUIPlugin.createErrorStatus("Choose a valid project file");
			}
		};
	}

	protected ViewerFilter getContainerDialogViewerFilter() {
		return new ViewerFilter() {

			public boolean select(Viewer viewer, Object parent, Object element) {
				if (element instanceof IProject) {
					IProject project = (IProject) element;

					return project.getName().equals(
						model.getProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME));
				}
				else if (element instanceof IFolder) {
					return true;
				}
				else if (element instanceof IFile) {
					return true;
				}

				return false;
			}
		};
	}

	protected IFolder getDocroot() {
		return CoreUtil.getDocroot(getDataModel().getStringProperty(PROJECT_NAME));
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] {
			LIFERAY_PORTLET_NAME, ICON_FILE, ALLOW_MULTIPLE, CSS_FILE, JAVASCRIPT_FILE, CSS_CLASS_WRAPPER, CATEGORY, ENTRY_WEIGHT, ENTRY_CLASS_NAME
		};
	}

	protected void handleFileBrowseButton(final Text text, String title, String message) {
		ISelectionStatusValidator validator = getContainerDialogSelectionValidator();

		ViewerFilter filter = getContainerDialogViewerFilter();

		ITreeContentProvider contentProvider = new WorkbenchContentProvider();

		ILabelProvider labelProvider =
			new DecoratingLabelProvider(
				new WorkbenchLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), labelProvider, contentProvider);
		dialog.setValidator(validator);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.addFilter(filter);

		IFolder docroot = CoreUtil.getDocroot(getDataModel().getStringProperty(PROJECT_NAME));

		dialog.setInput(docroot);

		if (dialog.open() == Window.OK) {
			Object element = dialog.getFirstResult();

			try {
				if (element instanceof IFile) {
					IFile file = (IFile) element;

					text.setText("/" + file.getFullPath().makeRelativeTo(docroot.getFullPath()).toPortableString());
					// dealWithSelectedContainerResource(container);
				}
			}
			catch (Exception ex) {
				// Do nothing
			}

		}
	}

	protected boolean isProjectValid(IProject project) {
		return ProjectUtil.isPortletProject(project);
	}

}

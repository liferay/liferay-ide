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

package com.liferay.ide.service.ui.wizard;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.wizard.LiferayDataModelWizardPage;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.service.core.operation.INewServiceBuilderDataModelProperties;
import com.liferay.ide.service.ui.ServiceUI;
import com.liferay.ide.ui.util.SWTUtil;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class NewServiceBuilderWizardPage
	extends LiferayDataModelWizardPage implements INewServiceBuilderDataModelProperties {

	public NewServiceBuilderWizardPage(IDataModel dataModel, String pageName, String title, String description) {
		super(
			dataModel, pageName, title,
			ServiceUI.imageDescriptorFromPlugin(ServiceUI.PLUGIN_ID, "/icons/wizban/service_wiz.png"));

		setDescription(description);
	}

	protected void createPackageNamespaceAuthorGroup(Composite parent) {
		Composite group = SWTUtil.createTopComposite(parent, 3);

		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		// package

		packageLabel = new Label(group, SWT.LEFT);

		packageLabel.setText(Msgs.packagePath);
		packageLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		packageText = new Text(group, SWT.SINGLE | SWT.BORDER);

		packageText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		synchHelper.synchText(packageText, PACKAGE_PATH, null);

		IPackageFragment packageFragment = getSelectedPackageFragment();

		String targetProject = model.getStringProperty(PROJECT_NAME);

		if ((packageFragment != null) && packageFragment.exists()) {
			String packageFragmentName = packageFragment.getJavaProject().getElementName();

			if (packageFragmentName.equals(targetProject)) {
				model.setProperty(PACKAGE_PATH, packageFragment.getElementName());
			}
		}

		packageButton = new Button(group, SWT.PUSH);

		packageButton.setText(J2EEUIMessages.BROWSE_BUTTON_LABEL);
		packageButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		packageButton.addSelectionListener(
			new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {

					// Do nothing

				}

				public void widgetSelected(SelectionEvent e) {
					handlePackageButtonPressed();
				}

			});

		SWTUtil.createLabel(group, SWT.LEAD, Msgs.namespace, 1);
		namespace = SWTUtil.createText(group, 1);

		this.synchHelper.synchText(namespace, NAMESPACE, null);

		SWTUtil.createLabel(group, SWT.LEAD, StringPool.EMPTY, 1);

		SWTUtil.createLabel(group, SWT.LEAD, Msgs.author, 1);
		author = SWTUtil.createText(group, 1);

		this.synchHelper.synchText(author, AUTHOR, null);

		SWTUtil.createLabel(group, StringPool.EMPTY, 1);

		SWTUtil.createLabel(group, StringPool.EMPTY, 1);
		Composite checkboxParent = SWTUtil.createComposite(group, 1, 1, SWT.FILL, 0, 3);

		useSampleTemplate = SWTUtil.createCheckButton(checkboxParent, Msgs.includeSampleEntity, null, true, 1);

		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);

		useSampleTemplate.setLayoutData(data);

		this.synchHelper.synchCheckbox(useSampleTemplate, USE_SAMPLE_TEMPLATE, null);
	}

	/**
	 * Add project group
	 */
	protected void createProjectNameServiceFileGroup(Composite parent) {
		projectNameLabel = new Label(parent, SWT.NONE);

		projectNameLabel.setText("Plugin project:");
		projectNameLabel.setLayoutData(new GridData());

		projectNameCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);

		data.widthHint = 300;
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		projectNameCombo.setLayoutData(data);

		synchHelper.synchCombo(projectNameCombo, PROJECT_NAME, null);

		String initialProjectName = initializeProjectList(projectNameCombo, model);

		if ((projectName == null) && (initialProjectName != null)) {
			projectName = initialProjectName;
		}

		SWTUtil.createLabel(parent, SWT.LEAD, Msgs.serviceFile, 1);
		serviceFile = SWTUtil.createText(parent, 1);

		this.synchHelper.synchText(serviceFile, SERVICE_FILE, null);
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite topComposite = SWTUtil.createTopComposite(parent, 2);

		createProjectNameServiceFileGroup(topComposite);

		SWTUtil.createSeparator(topComposite, 2);

		createPackageNamespaceAuthorGroup(topComposite);

		setShellImage();

		return topComposite;
	}

	protected IPackageFragmentRoot getPackageFragmentRoot(IPackageFragment packageFragment) {
		if (packageFragment == null) {
			return null;
		}
		else if (packageFragment.getParent() instanceof IPackageFragment) {
			return getPackageFragmentRoot((IPackageFragment)packageFragment.getParent());
		}
		else if (packageFragment.getParent() instanceof IPackageFragmentRoot) {
			return (IPackageFragmentRoot)packageFragment.getParent();
		}
		else {
			return null;
		}
	}

	protected IPackageFragment getSelectedPackageFragment() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (window == null) {
			return null;
		}

		ISelection selection = window.getSelectionService().getSelection();

		if (selection == null) {
			return null;
		}

		IJavaElement element = getInitialJavaElement(selection);

		if (element != null) {
			if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
				return (IPackageFragment)element;
			}
			else if (element.getElementType() == IJavaElement.COMPILATION_UNIT) {
				IJavaElement parent = ((ICompilationUnit)element).getParent();

				if (parent.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
					return (IPackageFragment)parent;
				}
			}
			else if (element.getElementType() == IJavaElement.TYPE) {
				return ((IType)element).getPackageFragment();
			}
		}

		return null;
	}

	protected IPackageFragmentRoot getSelectedPackageFragmentRoot() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (window == null) {
			return null;
		}

		ISelection selection = window.getSelectionService().getSelection();

		if (selection == null) {
			return null;
		}

		IJavaElement element = getInitialJavaElement(selection);

		if (element != null) {
			if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT) {
				return (IPackageFragmentRoot)element;
			}
		}

		return null;
	}

	protected String[] getValidationPropertyNames() {
		return new String[] {PROJECT_NAME, SERVICE_FILE, PACKAGE_PATH, NAMESPACE, AUTHOR};
	}

	protected void handlePackageButtonPressed() {
		IPackageFragmentRoot packRoot = (IPackageFragmentRoot)model.getProperty(JAVA_PACKAGE_FRAGMENT_ROOT);

		if (packRoot == null) {
			return;
		}

		IJavaElement[] packages = null;

		try {
			packages = packRoot.getChildren();
		}
		catch (JavaModelException jme) {

			// Do nothing

		}

		if (packages == null) {
			packages = new IJavaElement[0];
		}

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
			getShell(), new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT));

		dialog.setTitle(J2EEUIMessages.PACKAGE_SELECTION_DIALOG_TITLE);
		dialog.setMessage(J2EEUIMessages.PACKAGE_SELECTION_DIALOG_DESC);
		dialog.setEmptyListMessage(J2EEUIMessages.PACKAGE_SELECTION_DIALOG_MSG_NONE);
		dialog.setElements(packages);

		if (dialog.open() == Window.OK) {
			IPackageFragment fragment = (IPackageFragment)dialog.getFirstResult();

			if (fragment != null) {
				packageText.setText(fragment.getElementName());
			}
			else {
				packageText.setText(J2EEUIMessages.EMPTY_STRING);
			}
		}
	}

	protected boolean isProjectValid(IProject project) {
		if ((ProjectUtil.isPortletProject(project) || ProjectUtil.isHookProject(project) ||
			 ProjectUtil.isExtProject(project)) &&
			SDKUtil.isSDKProject(project)) {

			return true;
		}

		return false;
	}

	protected void setShellImage() {
		Bundle bundle = ServiceUI.getDefault().getBundle();

		URL url = bundle.getEntry("/icons/e16/service.png");

		Image shellImage = ImageDescriptor.createFromURL(url).createImage();

		getShell().setImage(shellImage);
	}

	protected Text author;
	protected Text namespace;
	protected Button packageButton;
	protected Label packageLabel;
	protected Text packageText;
	protected String projectName;
	protected Combo projectNameCombo;
	protected Label projectNameLabel;
	protected Text serviceFile;
	protected Button useSampleTemplate;

	private static class Msgs extends NLS {

		public static String author;
		public static String includeSampleEntity;
		public static String namespace;
		public static String packagePath;
		public static String serviceFile;

		static {
			initializeMessages(NewServiceBuilderWizardPage.class.getName(), Msgs.class);
		}

	}

}
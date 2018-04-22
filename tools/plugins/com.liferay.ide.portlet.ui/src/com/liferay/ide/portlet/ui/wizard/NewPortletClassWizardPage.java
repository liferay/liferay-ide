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

package com.liferay.ide.portlet.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.ui.dialog.FilteredTypesSelectionDialogEx;
import com.liferay.ide.ui.util.SWTUtil;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.jst.j2ee.internal.wizard.NewJavaClassWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings({"restriction", "deprecation"})
public class NewPortletClassWizardPage extends NewJavaClassWizardPage implements INewPortletClassDataModelProperties {

	public NewPortletClassWizardPage(
		IDataModel model, String pageName, String pageDesc, String pageTitle, boolean fragment) {

		super(model, pageName, pageDesc, pageTitle, IModuleConstants.JST_WEB_MODULE);

		projectName = null;
		this.fragment = fragment;
	}

	public NewPortletClassWizardPage(
		IDataModel model, String pageName, String pageDesc, String pageTitle, boolean fragment,
		boolean initialProject) {

		this(model, pageName, pageDesc, pageTitle, fragment);

		this.initialProject = initialProject;
	}

	protected void createClassnameGroup(Composite parent) {

		// class name

		classLabel = new Label(parent, SWT.LEFT);

		classLabel.setText(Msgs.portletClassLabel);
		classLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		classText = new Text(parent, SWT.SINGLE | SWT.BORDER);

		classText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		synchHelper.synchText(classText, INewJavaClassDataModelProperties.CLASS_NAME, null);

		new Label(parent, SWT.LEFT);
	}

	/**
	 * Add folder group to composite
	 */
	protected void createFolderGroup(Composite composite) {

		// folder

		Label folderLabel = new Label(composite, SWT.LEFT);

		folderLabel.setText(J2EEUIMessages.FOLDER_LABEL);
		folderLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		folderText = new Text(composite, SWT.SINGLE | SWT.BORDER);

		folderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		synchHelper.synchText(folderText, INewJavaClassDataModelProperties.SOURCE_FOLDER, null);

		IPackageFragmentRoot root = getSelectedPackageFragmentRoot();

		String projectName = model.getStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME);

		if ((projectName != null) && (projectName.length() > 0)) {
			IProject targetProject = ProjectUtilities.getProject(projectName);

			if ((root == null) || !root.getJavaProject().equals(JavaCore.create(targetProject))) {
				IFolder folder = getDefaultJavaSourceFolder(targetProject);

				if (folder != null) {
					folderText.setText(folder.getFullPath().toPortableString());
				}
			}
			else {
				folderText.setText(root.getPath().toString());
			}
		}

		folderButton = new Button(composite, SWT.PUSH);

		folderButton.setText(J2EEUIMessages.BROWSE_BUTTON_LABEL);
		folderButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		SelectionListener listener = new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

				// Do nothing

			}

			public void widgetSelected(SelectionEvent e) {
				handleFolderButtonPressed();
			}

		};

		folderButton.addSelectionListener(listener);
	}

	protected void createNewPortletClassGroup(Composite parent) {
		new Label(parent, SWT.LEFT);

		Composite composite = new Composite(parent, SWT.NONE);

		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(2, false));

		createNewClassRadio = new Button(composite, SWT.RADIO);

		createNewClassRadio.setText(Msgs.createNewPortlet);

		useDefaultPortletRadio = new Button(composite, SWT.RADIO);

		useDefaultPortletRadio.setText(Msgs.useDefaultPortlet);

		new Label(parent, SWT.NONE);

		synchHelper.synchRadio(createNewClassRadio, CREATE_NEW_PORTLET_CLASS, null);
		synchHelper.synchRadio(useDefaultPortletRadio, USE_DEFAULT_PORTLET_CLASS, null);

		SelectionAdapter adapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				classText.setEnabled(createNewClassRadio.getSelection());
				packageText.setEnabled(createNewClassRadio.getSelection());
				superCombo.setEnabled(createNewClassRadio.getSelection());
			}

		};

		createNewClassRadio.addSelectionListener(adapter);
	}

	protected void createPackageGroup(Composite parent) {

		// package

		packageLabel = new Label(parent, SWT.LEFT);

		packageLabel.setText(J2EEUIMessages.JAVA_PACKAGE_LABEL);
		packageLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		packageText = new Text(parent, SWT.SINGLE | SWT.BORDER);

		packageText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		synchHelper.synchText(packageText, INewJavaClassDataModelProperties.JAVA_PACKAGE, null);

		IPackageFragment packageFragment = getSelectedPackageFragment();

		String targetProject = model.getStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME);

		if ((packageFragment != null) && packageFragment.exists()) {
			String elementName = packageFragment.getJavaProject().getElementName();

			if (elementName.equals(targetProject)) {
				IPackageFragmentRoot root = getPackageFragmentRoot(packageFragment);

				if (root != null) {
					folderText.setText(root.getPath().toString());
				}

				model.setProperty(INewJavaClassDataModelProperties.JAVA_PACKAGE, packageFragment.getElementName());
			}
		}

		if (fragment) {
			SWTUtil.createLabel(parent, StringPool.EMPTY, 1);
		}
		else {
			packageButton = new Button(parent, SWT.PUSH);

			packageButton.setText(J2EEUIMessages.BROWSE_BUTTON_LABEL);
			packageButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

			SelectionListener listener = new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {

					// Do nothing

				}

				public void widgetSelected(SelectionEvent e) {
					handlePackageButtonPressed();
				}

			};

			packageButton.addSelectionListener(listener);
		}
	}

	protected void createPortletClassGroup(Composite parent) {
		if (!fragment && getDataModel().getBooleanProperty(SHOW_NEW_CLASS_OPTION)) {
			createNewPortletClassGroup(parent);
		}

		createClassnameGroup(parent);
		createPackageGroup(parent);
		createSuperclassGroup(parent);
	}

	/**
	 * Add project group
	 */
	protected void createProjectNameGroup(Composite parent) {

		// set up project name label

		projectNameLabel = new Label(parent, SWT.NONE);

		projectNameLabel.setText("Portlet plugin project:");

		projectNameLabel.setLayoutData(new GridData());

		// set up project name entry field

		projectNameCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);

		data.widthHint = 300;
		data.horizontalSpan = 1;

		projectNameCombo.setLayoutData(data);

		SelectionAdapter adapter = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				IProject project = CoreUtil.getProject(projectNameCombo.getText());

				validateProjectRequirements(project);

				getDataModel().notifyPropertyChange(SUPERCLASS, IDataModel.VALID_VALUES_CHG);
				getDataModel().notifyPropertyChange(ENTRY_CATEGORY, IDataModel.VALID_VALUES_CHG);
			}

		};

		projectNameCombo.addSelectionListener(adapter);

		synchHelper.synchCombo(projectNameCombo, IArtifactEditOperationDataModelProperties.PROJECT_NAME, null);

		initializeProjectList();

		new Label(parent, SWT.NONE);
	}

	protected void createSuperclassGroup(Composite parent) {

		// superclass

		superLabel = new Label(parent, SWT.LEFT);

		superLabel.setText(J2EEUIMessages.SUPERCLASS_LABEL);
		superLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// superText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		// superText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// synchHelper.synchText(superText,
		// INewJavaClassDataModelProperties.SUPERCLASS, null);

		superCombo = new Combo(parent, SWT.DROP_DOWN);

		superCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		synchHelper.synchCombo(superCombo, INewJavaClassDataModelProperties.SUPERCLASS, null);

		if (fragment) {
			SWTUtil.createLabel(parent, StringPool.EMPTY, 1);
		}
		else {
			superButton = new Button(parent, SWT.PUSH);

			superButton.setText(J2EEUIMessages.BROWSE_BUTTON_LABEL);
			superButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

			SelectionListener listener = new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {

					// Do nothing

				}

				public void widgetSelected(SelectionEvent e) {

					// handleSuperButtonPressed();

					handleClassButtonSelected(
						superCombo, "javax.portlet.Portlet", J2EEUIMessages.SUPERCLASS_SELECTION_DIALOG_TITLE,
						J2EEUIMessages.SUPERCLASS_SELECTION_DIALOG_DESC);
				}

			};

			superButton.addSelectionListener(listener);
		}
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite composite = SWTUtil.createTopComposite(parent, 3);

		if (!fragment) {
			createProjectNameGroup(composite);
			createFolderGroup(composite);
			addSeperator(composite, 3);
		}

		createPortletClassGroup(composite);

		setFocusOnClassText();

		setShellImage();

		return composite;
	}

	@Override
	protected void enter() {
		super.enter();

		validatePage();
	}

	protected ViewerFilter getContainerDialogViewerFilter() {
		return new ViewerFilter() {

			public boolean select(Viewer viewer, Object parent, Object element) {
				if (element instanceof IProject) {
					IProject project = (IProject)element;

					return project.getName().equals(
						model.getProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME));
				}
				else if (element instanceof IFolder) {
					IFolder folder = (IFolder)element;

					// only show source folders

					IProject project = ProjectUtilities.getProject(
						model.getStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME));

					IPackageFragmentRoot[] sourceFolders = J2EEProjectUtilities.getSourceContainers(project);

					for (int i = 0; i < sourceFolders.length; i++) {
						if ((sourceFolders[i].getResource() != null) && sourceFolders[i].getResource().equals(folder)) {
							return true;
						}
						else if (ProjectUtil.isParent(folder, sourceFolders[i].getResource())) {
							return true;
						}
					}
				}

				return false;
			}

		};
	}

	protected IFolder getDefaultJavaSourceFolder(IProject project) {
		if (project == null) {
			return null;
		}

		IPackageFragmentRoot[] sources = J2EEProjectUtilities.getSourceContainers(project);

		// Try and return the first source folder

		if (ListUtil.isNotEmpty(sources)) {
			try {
				return (IFolder)sources[0].getCorrespondingResource();
			}
			catch (Exception e) {
				return null;
			}
		}

		return null;
	}

	/**
	 * @return
	 */
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

		// StructuredSelection stucturedSelection = (StructuredSelection)
		// selection;

		IJavaElement element = getInitialJavaElement(selection);

		if (element != null) {
			if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT) {
				return (IPackageFragmentRoot)element;
			}
		}

		return null;
	}

	protected IProject getSelectedProject() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (window == null) {
			return null;
		}

		ISelection selection = window.getSelectionService().getSelection();

		if (selection == null) {
			return null;
		}

		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}

		IJavaElement element = getInitialJavaElement(selection);

		if ((element != null) && (element.getJavaProject() != null)) {
			return element.getJavaProject().getProject();
		}

		IStructuredSelection stucturedSelection = (IStructuredSelection)selection;

		if (stucturedSelection.getFirstElement() instanceof EObject) {
			return ProjectUtilities.getProject(stucturedSelection.getFirstElement());
		}

		return getExtendedSelectedProject(stucturedSelection.getFirstElement());
	}

	protected String[] getValidationPropertyNames() {
		List<String> validationPropertyNames = new ArrayList<>();

		if (fragment) {
			return new String[] {
				IArtifactEditOperationDataModelProperties.COMPONENT_NAME, INewJavaClassDataModelProperties.JAVA_PACKAGE,
				INewJavaClassDataModelProperties.CLASS_NAME, INewJavaClassDataModelProperties.SUPERCLASS
			};
		}
		else {
			validationPropertyNames.add(CREATE_NEW_PORTLET_CLASS);
			validationPropertyNames.add(USE_DEFAULT_PORTLET_CLASS);
			Collections.addAll(validationPropertyNames, super.getValidationPropertyNames());
		}

		return validationPropertyNames.toArray(new String[0]);
	}

	protected void handleClassButtonSelected(Control control, String baseClass, String title, String message) {
		getControl().setCursor(new Cursor(getShell().getDisplay(), SWT.CURSOR_WAIT));

		IPackageFragmentRoot packRoot = (IPackageFragmentRoot)model.getProperty(
			INewJavaClassDataModelProperties.JAVA_PACKAGE_FRAGMENT_ROOT);

		if (packRoot == null) {
			return;
		}

		// this eliminates the non-exported classpath entries
		// IJavaSearchScope scope =
		// TypeSearchEngine.createJavaSearchScopeForAProject(packRoot.getJavaProject(),
		// true, true);

		IJavaSearchScope scope = null;

		try {
			IType type = packRoot.getJavaProject().findType(baseClass);

			if (type == null) {
				return;
			}

			scope = BasicSearchEngine.createHierarchyScope(type);
		}
		catch (JavaModelException jme) {
			PortletUIPlugin.logError(jme);
			return;
		}

		// This includes all entries on the classpath. This behavior is
		// identical
		// to the Super Class Browse Button on the Create new Java Class Wizard
		// IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new
		// IJavaElement[] {root.getJavaProject()} );

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialogEx(
			getShell(), false, getWizard().getContainer(), scope, IJavaSearchConstants.CLASS);

		dialog.setTitle(title);
		dialog.setMessage(message);

		if (dialog.open() == Window.OK) {
			IType type = (IType)dialog.getFirstResult();

			String classFullPath = J2EEUIMessages.EMPTY_STRING;

			if (type != null) {
				classFullPath = type.getFullyQualifiedName();
			}

			if (control instanceof Text) {
				((Text)control).setText(classFullPath);
			}
			else if (control instanceof Combo) {
				((Combo)control).setText(classFullPath);
			}

			getControl().setCursor(null);

			return;
		}

		getControl().setCursor(null);
	}

	protected void handleCustomButtonSelected() {

		// boolean enable = customButton.getSelection();
		// portletClassLabel.setEnabled(!enable);
		// portletClassText.setEnabled(!enable);
		// packageText.setEnabled(enable);
		// packageButton.setEnabled(enable);
		// packageLabel.setEnabled(enable);
		// classText.setEnabled(enable);
		// classLabel.setEnabled(enable);
		// superText.setEnabled(enable);
		// superCombo.setEnabled(enable);
		// superButton.setEnabled(enable);
		// superLabel.setEnabled(enable);

	}

	/**
	 * Browse for a new Destination Folder
	 */
	protected void handleFolderButtonPressed() {
		ISelectionStatusValidator validator = getContainerDialogSelectionValidator();

		ViewerFilter filter = getContainerDialogViewerFilter();

		ITreeContentProvider contentProvider = new WorkbenchContentProvider();

		IWorkbench workbench = PlatformUI.getWorkbench();

		ILabelProvider labelProvider = new DecoratingLabelProvider(
			new WorkbenchLabelProvider(), workbench.getDecoratorManager().getLabelDecorator());

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), labelProvider, contentProvider);

		dialog.setValidator(validator);
		dialog.setTitle(J2EEUIMessages.CONTAINER_SELECTION_DIALOG_TITLE);
		dialog.setMessage(J2EEUIMessages.CONTAINER_SELECTION_DIALOG_DESC);
		dialog.addFilter(filter);

		String projectName = model.getStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME);

		if ((projectName == null) || (projectName.length() == 0)) {
			return;
		}

		IProject project = ProjectUtilities.getProject(projectName);

		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());

		if (project != null) {
			dialog.setInitialSelection(project);
		}

		if (dialog.open() == Window.OK) {
			Object element = dialog.getFirstResult();

			try {
				if (element instanceof IContainer) {
					IContainer container = (IContainer)element;

					folderText.setText(container.getFullPath().toString());

					// dealWithSelectedContainerResource(container);

				}
			}
			catch (Exception ex) {

				// Do nothing

			}

		}
	}

	protected void initializeProjectList() {
		IProject[] workspaceProjects = CoreUtil.getAllProjects();

		List<String> items = new ArrayList<>();

		for (IProject project : workspaceProjects) {
			if (isProjectValid(project)) {
				items.add(project.getName());
			}
		}

		if (ListUtil.isEmpty(items)) {
			return;
		}

		String[] names = new String[items.size()];

		for (int i = 0; i < items.size(); i++) {
			names[i] = (String)items.get(i);
		}

		projectNameCombo.setItems(names);

		IProject selectedProject = null;

		try {
			if (model != null) {
				String projectNameFromModel = model.getStringProperty(
					IArtifactEditOperationDataModelProperties.COMPONENT_NAME);

				if ((projectNameFromModel != null) && (projectNameFromModel.length() > 0)) {
					selectedProject = CoreUtil.getProject(projectNameFromModel);
				}
			}
		}
		catch (Exception e) {
		}

		try {
			if (selectedProject == null) {
				selectedProject = getSelectedProject();
			}

			if ((selectedProject != null) && selectedProject.isAccessible() &&
				selectedProject.hasNature(IModuleConstants.MODULE_NATURE_ID)) {

				projectNameCombo.setText(selectedProject.getName());

				validateProjectRequirements(selectedProject);

				model.setProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME, selectedProject.getName());
			}
		}
		catch (CoreException ce) {

			// Ignore

		}

		if ((projectName == null) && ListUtil.isNotEmpty(names)) {
			projectName = names[0];
		}

		if (((projectNameCombo.getText() == null) || (projectNameCombo.getText().length() == 0)) &&
			(projectName != null)) {

			projectNameCombo.setText(projectName);

			validateProjectRequirements(CoreUtil.getProject(projectName));

			model.setProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME, projectName);
		}

		projectNameLabel.setEnabled(!initialProject);
		projectNameCombo.setEnabled(!initialProject);
	}

	protected boolean isProjectValid(IProject project) {
		if (super.isProjectValid(project) && ProjectUtil.isPortletProject(project)) {
			return true;
		}

		return false;
	}

	protected void setFocusOnClassText() {
		if (classText != null) {
			classText.setFocus();
		}
	}

	protected void setShellImage() {
		PortletUIPlugin plugin = PortletUIPlugin.getDefault();

		URL url = plugin.getBundle().getEntry("/icons/e16/portlet.png");

		Image shellImage = ImageDescriptor.createFromURL(url).createImage();

		getShell().setImage(shellImage);
	}

	protected void validateProjectRequirements(IProject project) {
		super.validateProjectRequirements(project);
	}

	protected Button createNewClassRadio;
	protected Button customButton;
	protected Button folderButton;
	protected Text folderText;
	protected boolean fragment;
	protected boolean initialProject;
	protected Button portletClassButton;
	protected Label portletClassLabel;
	protected Text portletClassText;
	protected String projectName;
	protected Combo projectNameCombo;
	protected Label projectNameLabel;
	protected Combo superCombo;
	protected Button useDefaultPortletRadio;

	private static class Msgs extends NLS {

		public static String createNewPortlet;
		public static String portletClassLabel;
		public static String useDefaultPortlet;

		static {
			initializeMessages(NewPortletClassWizardPage.class.getName(), Msgs.class);
		}

	}

}
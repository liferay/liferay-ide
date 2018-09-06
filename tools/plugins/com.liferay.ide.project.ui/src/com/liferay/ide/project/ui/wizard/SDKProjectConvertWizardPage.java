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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.ISDKProjectsImportDataModelProperties;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.ui.util.SWTUtil;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.InvocationTargetException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.web.ui.internal.wizards.DataModelFacetCreationWizardPage;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class SDKProjectConvertWizardPage
	extends DataModelFacetCreationWizardPage implements ISDKProjectsImportDataModelProperties {

	public static final String METADATA_FOLDER = ".metadata";

	public SDKProjectConvertWizardPage(IDataModel model, String pageName) {
		super(model, pageName);

		setTitle(Msgs.convertProject);

		setDescription(Msgs.convertExistingProject);
	}

	public void updateProjectsList(final String path) {

		// on an empty path empty selectedProjects

		if ((path == null) || (path.length() == 0)) {
			setMessage(Msgs.importProjectsDescription);

			selectedProjects = new ProjectRecord[0];

			projectsList.refresh(true);

			projectsList.setCheckedElements(selectedProjects);

			setPageComplete(ListUtil.isNotEmpty(projectsList.getCheckedElements()));

			lastPath = path;

			return;
		}

		final File directory = new File(path);

		long modified = directory.lastModified();

		if (path.equals(lastPath) && (lastModified == modified)) {

			// since the file/folder was not modified and the path did not
			// change, no refreshing is required

			return;
		}

		lastPath = path;

		lastModified = modified;

		final boolean dirSelected = true;

		try {
			getContainer().run(
				true, true,
				new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor) {
						monitor.beginTask(Msgs.searchingMessage, 100);

						monitor.worked(10);

						if (dirSelected && directory.isDirectory()) {
							ProjectRecord[] projectToConvert =
								(ProjectRecord[])getDataModel().getProperty(SELECTED_PROJECTS);

							IPath dir = new Path(directory.getPath());

							if (dir.isPrefixOf(projectToConvert[0].getProjectLocation())) {
								selectedProjects = projectToConvert;
							}
							else {
								selectedProjects = new ProjectRecord[0];
							}
						}
						else {
							monitor.worked(60);
						}

						monitor.done();
					}

				});
		}
		catch (InvocationTargetException ite) {
			IDEWorkbenchPlugin.log(ite.getMessage(), ite);
		}
		catch (InterruptedException ie) {
		}

		projectsList.refresh(true);

		if (ListUtil.isNotEmpty(selectedProjects)) {
			projectsList.setChecked(selectedProjects[0], true);
		}

		setPageComplete(ListUtil.isNotEmpty(projectsList.getCheckedElements()));

		if (selectedProjects.length == 0) {
			setMessage(Msgs.noProjectsToImport, WARNING);
		}

		Object[] checkedProjects = projectsList.getCheckedElements();

		if (ListUtil.isNotEmpty(checkedProjects)) {
			selectedProjects = new ProjectRecord[checkedProjects.length];

			for (int i = 0; i < checkedProjects.length; i++) {
				selectedProjects[i] = (ProjectRecord)checkedProjects[i];
			}

			getDataModel().setProperty(SELECTED_PROJECTS, selectedProjects);
		}
	}

	protected boolean collectProjectsFromDirectory(
		Collection<File> eclipseProjectFiles, Collection<File> liferayProjectDirs, File directory,
		Set<String> directoriesVisited, boolean recurse, IProgressMonitor monitor) {

		if (monitor.isCanceled()) {
			return false;
		}

		monitor.subTask(NLS.bind(Msgs.checkingMessage, directory.getPath()));

		File[] contents = directory.listFiles();

		if (contents == null) {
			return false;
		}

		// Initialize recursion guard for recursive symbolic links

		if (directoriesVisited == null) {
			directoriesVisited = new HashSet<>();

			try {
				directoriesVisited.add(directory.getCanonicalPath());
			}
			catch (IOException ioe) {
				StatusManager statusManager = StatusManager.getManager();

				statusManager.handle(StatusUtil.newStatus(IStatus.ERROR, ioe.getLocalizedMessage(), ioe));
			}
		}

		// first look for project description files

		final String dotProject = IProjectDescription.DESCRIPTION_FILE_NAME;

		for (File content : contents) {
			if (_isLiferaySDKProjectDir(content)) {

				// recurse to see if it has project file

				int currentSize = eclipseProjectFiles.size();

				collectProjectsFromDirectory(
					eclipseProjectFiles, liferayProjectDirs, content, directoriesVisited, false, monitor);

				int newSize = eclipseProjectFiles.size();

				if (newSize == currentSize) {
					liferayProjectDirs.add(content);
				}
			}
			else if (content.isFile() && dotProject.equals(content.getName())) {
				if (!eclipseProjectFiles.contains(content)) {
					eclipseProjectFiles.add(content);
				}

				// don't search sub-directories since we can't have nested
				// projects

				return true;
			}
		}

		// no project description found, so recurse into sub-directories

		for (File content : contents) {
			if (content.isDirectory()) {
				if (!METADATA_FOLDER.equals(content.getName())) {
					try {
						String canonicalPath = content.getCanonicalPath();

						if (!directoriesVisited.add(canonicalPath)) {

							// already been here --> do not recurse

							continue;
						}
					}
					catch (IOException ioe) {
						StatusManager statusManager = StatusManager.getManager();

						statusManager.handle(StatusUtil.newStatus(IStatus.ERROR, ioe.getLocalizedMessage(), ioe));
					}

					// dont recurse directories that we have already determined
					// are Liferay projects

					if (!liferayProjectDirs.contains(content) && recurse) {
						collectProjectsFromDirectory(
							eclipseProjectFiles, liferayProjectDirs, content, directoriesVisited, recurse, monitor);
					}
				}
			}
		}

		return true;
	}

	protected void createProjectsList(Composite workArea) {
		Label title = new Label(workArea, SWT.NONE);

		title.setText(Msgs.importProjectLabel);
		title.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));

		projectsList = new CheckboxTreeViewer(workArea, SWT.BORDER);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);

		Control control = projectsList.getControl();

		gridData.widthHint = new PixelConverter(control).convertWidthInCharsToPixels(25);
		gridData.heightHint = new PixelConverter(control).convertHeightInCharsToPixels(10);

		control.setLayoutData(gridData);

		projectsList.setContentProvider(
			new ITreeContentProvider() {

				public void dispose() {
				}

				public Object[] getChildren(Object parentElement) {
					return null;
				}

				public Object[] getElements(Object inputElement) {

					// return getProjectRecords();

					return (Object[])getDataModel().getProperty(SELECTED_PROJECTS);
				}

				public Object getParent(Object element) {
					return null;
				}

				public boolean hasChildren(Object element) {
					return false;
				}

				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}

			});

		projectsList.setLabelProvider(new ProjectLabelProvider());
		projectsList.addCheckStateListener(
			new ICheckStateListener() {

				public void checkStateChanged(CheckStateChangedEvent event) {
					ProjectRecord element = (ProjectRecord)event.getElement();

					if (element.hasConflicts()) {
						projectsList.setChecked(element, false);
					}

					getDataModel().setProperty(SELECTED_PROJECTS, projectsList.getCheckedElements());

					setPageComplete(ListUtil.isNotEmpty(projectsList.getCheckedElements()));
				}

			});

		projectsList.setInput(this);
		projectsList.setComparator(new ViewerComparator());
	}

	protected void createSDKLocationField(Composite topComposite) {
		SWTUtil.createLabel(topComposite, SWT.LEAD, Msgs.liferayPluginSDKLocationLabel, 1);

		sdkLocation = SWTUtil.createText(topComposite, 2);

		this.synchHelper.synchText(sdkLocation, SDK_LOCATION, null);
	}

	protected void createSDKVersionField(Composite topComposite) {
		SWTUtil.createLabel(topComposite, SWT.LEAD, Msgs.liferayPluginSDKVersionLabel, 1);

		sdkVersion = SWTUtil.createText(topComposite, 2);

		this.synchHelper.synchText(sdkVersion, SDK_VERSION, null);

		SWTUtil.createLabel(topComposite, StringPool.EMPTY, 1);
	}

	protected void createTargetRuntimeGroup(Composite parent) {
		Label label = new Label(parent, SWT.NONE);

		label.setText(Msgs.liferayTargetRuntimeLabel);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		serverTargetCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);

		serverTargetCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button newServerTargetButton = new Button(parent, SWT.NONE);

		newServerTargetButton.setText(Msgs.newButton);
		newServerTargetButton.addSelectionListener(
			new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					final DataModelPropertyDescriptor[] preAdditionDescriptors = model.getValidPropertyDescriptors(
						FACET_RUNTIME);

					boolean ok = ServerUIUtil.showNewRuntimeWizard(getShell(), getModuleTypeID(), null, "com.liferay.");

					if (ok) {
						DataModelPropertyDescriptor[] postAdditionDescriptors = model.getValidPropertyDescriptors(
							FACET_RUNTIME);

						Object[] preAddition = new Object[preAdditionDescriptors.length];

						for (int i = 0; i < preAddition.length; i++) {
							preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
						}

						Object[] postAddition = new Object[postAdditionDescriptors.length];

						for (int i = 0; i < postAddition.length; i++) {
							postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
						}

						Object newAddition = CoreUtil.getNewObject(preAddition, postAddition);

						if (newAddition != null) {
							model.setProperty(FACET_RUNTIME, newAddition);
						}
					}
				}

			});

		Control[] deps = {newServerTargetButton};

		synchHelper.synchCombo(serverTargetCombo, FACET_RUNTIME, deps);

		if ((serverTargetCombo.getSelectionIndex() == -1) && (serverTargetCombo.getVisibleItemCount() != 0)) {
			serverTargetCombo.select(0);
		}
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite topComposite = SWTUtil.createTopComposite(parent, 1);

		GridLayout gl = new GridLayout(3, false);

		// gl.marginLeft = 5;

		topComposite.setLayout(gl);

		topComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		createSDKLocationField(topComposite);
		createSDKVersionField(topComposite);

		SWTUtil.createVerticalSpacer(topComposite, 1, 3);

		createProjectsList(topComposite);

		createTargetRuntimeGroup(topComposite);

		return topComposite;
	}

	@Override
	protected void enter() {
		super.enter();

		if (sdkLocation.getText() != null) {
			updateProjectsList(sdkLocation.getText());
		}
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] {SDK_LOCATION, SDK_VERSION, SELECTED_PROJECTS, FACET_RUNTIME};
	}

	protected boolean isValidLiferayProjectDir(File dir) {
		String name = dir.getName();

		if (name.endsWith(ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX) ||
			name.endsWith(ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX) ||
			name.endsWith(ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX)) {

			return true;
		}

		return false;
	}

	protected long lastModified;
	protected String lastPath;
	protected CheckboxTreeViewer projectsList;
	protected Text sdkLocation;
	protected Text sdkVersion;
	protected ProjectRecord[] selectedProjects = new ProjectRecord[0];
	protected Combo serverTargetCombo;
	protected IProject[] wsProjects;

	protected final class ProjectLabelProvider extends LabelProvider implements IColorProvider {

		public Color getBackground(Object element) {
			return null;
		}

		public Color getForeground(Object element) {
			ProjectRecord projectRecord = (ProjectRecord)element;

			if (projectRecord.hasConflicts()) {
				Display display = getShell().getDisplay();

				return display.getSystemColor(SWT.COLOR_GRAY);
			}

			return null;
		}

		public String getText(Object element) {
			return ((ProjectRecord)element).getProjectLabel();
		}

	}

	private boolean _isLiferaySDKProjectDir(File file) {
		if ((file != null) && file.isDirectory() && isValidLiferayProjectDir(file)) {

			// check for build.xml and docroot

			File[] contents = file.listFiles();

			boolean hasBuildXml = false;

			boolean hasDocroot = false;

			for (File content : contents) {
				if ("build.xml".equals(content.getName()) ||
					FileUtil.nameEndsWith(file, ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX)) {

					hasBuildXml = true;

					continue;
				}

				if (ISDKConstants.DEFAULT_DOCROOT_FOLDER.equals(content.getName())) {
					hasDocroot = true;

					continue;
				}
			}

			if (hasBuildXml && hasDocroot) {
				return true;
			}
		}

		return false;
	}

	private static class Msgs extends NLS {

		public static String checkingMessage;
		public static String convertExistingProject;
		public static String convertProject;
		public static String importProjectLabel;
		public static String importProjectsDescription;
		public static String liferayPluginSDKLocationLabel;
		public static String liferayPluginSDKVersionLabel;
		public static String liferayTargetRuntimeLabel;
		public static String newButton;
		public static String noProjectsToImport;
		public static String searchingMessage;

		static {
			initializeMessages(SDKProjectConvertWizardPage.class.getName(), Msgs.class);
		}

	}

}
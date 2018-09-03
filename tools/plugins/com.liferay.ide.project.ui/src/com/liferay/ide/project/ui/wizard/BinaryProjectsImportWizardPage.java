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
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.BinaryProjectRecord;
import com.liferay.ide.project.core.ISDKProjectsImportDataModelProperties;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.SWTUtil;

import java.io.File;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@SuppressWarnings("restriction")
public class BinaryProjectsImportWizardPage
	extends SDKProjectsImportWizardPage implements ISDKProjectsImportDataModelProperties {

	public BinaryProjectsImportWizardPage(IDataModel model, String pageName) {
		super(model, pageName);

		setTitle(Msgs.importLiferayBinaryPlugins);
		setDescription(Msgs.selectBinaryPlugins);
	}

	@Override
	public Object[] getProjectRecords() {
		List<BinaryProjectRecord> binaryProjectRecords = new ArrayList<>();

		for (Object project : selectedProjects) {
			BinaryProjectRecord binaryProjectRecord = (BinaryProjectRecord)project;

			if (isProjectInWorkspace(binaryProjectRecord.getLiferayPluginName())) {
				binaryProjectRecord.setConflicts(true);
			}

			binaryProjectRecords.add(binaryProjectRecord);
		}

		return binaryProjectRecords.toArray(new BinaryProjectRecord[binaryProjectRecords.size()]);
	}

	public void updateBinariesList() {
		String path = binariesLocation.getText();

		if (path != null) {
			updateProjectsList(path);
		}
	}

	@Override
	public void updateProjectsList(String path) {

		// on an empty path empty selectedProjects

		if ((path == null) || (path.length() == 0)) {
			setMessage(StringPool.EMPTY);

			selectedProjects = new BinaryProjectRecord[0];

			projectsList.refresh(true);

			projectsList.setCheckedElements(selectedProjects);

			setPageComplete(ListUtil.isNotEmpty(projectsList.getCheckedElements()));

			lastPath = path;

			return;
		}

		// Check if the direcotry is the Plugins SDK folder

		String sdkLocationPath = sdkLocation.getText();

		if ((sdkLocationPath != null) && sdkLocationPath.equals(path)) {
			path = sdkLocationPath + "/dist";
		}

		final File directory = new File(path);

		long modified = directory.lastModified();

		if (path.equals(lastPath) && (lastModified == modified)) {

			// since the file/folder was not modified and the path did not
			// change, no refreshing is required

			if (selectedProjects.length == 0) {
				setMessage(StringPool.EMPTY, WARNING);
			}

			return;
		}

		lastPath = path;

		lastModified = modified;

		final boolean dirSelected = true;

		try {
			getContainer().run(
				true, true,
				new IRunnableWithProgress() {

					/*
					 * (non-Javadoc)
					 *
					 * @see IRunnableWithProgress#run(org .eclipse.core.runtime.IProgressMonitor)
					 */
					public void run(IProgressMonitor monitor) {
						monitor.beginTask(StringPool.EMPTY, 100);

						selectedProjects = new BinaryProjectRecord[0];

						Collection<File> projectBinaries = new ArrayList<>();

						monitor.worked(10);

						if (dirSelected && directory.isDirectory()) {
							if (!ProjectImportUtil.collectBinariesFromDirectory(
									projectBinaries, directory, true, monitor)) {

								return;
							}

							selectedProjects = new BinaryProjectRecord[projectBinaries.size()];

							int index = 0;

							monitor.worked(50);

							monitor.subTask(StringPool.EMPTY);

							for (File binaryFile : projectBinaries) {
								selectedProjects[index++] = new BinaryProjectRecord(binaryFile);
							}

							// for ( File liferayProjectDir : liferayProjectDirs ) {
							// selectedProjects[index++] = new ProjectRecord( liferayProjectDir );
							// }

						}
						else {
							monitor.worked(60);
						}

						monitor.done();
					}

				});
		}
		catch (InvocationTargetException ite) {
			ProjectUI.logError(ite);
		}
		catch (InterruptedException ie) {

			// Nothing to do if the user interrupts.

		}

		projectsList.refresh(true);

		setPageComplete(ListUtil.isNotEmpty(projectsList.getCheckedElements()));

		if (selectedProjects.length == 0) {
			setMessage(StringPool.EMPTY, WARNING);
		}

		Object[] checkedBinaries = projectsList.getCheckedElements();

		if (ListUtil.isNotEmpty(checkedBinaries)) {
			selectedProjects = new BinaryProjectRecord[checkedBinaries.length];

			for (int i = 0; i < checkedBinaries.length; i++) {
				selectedProjects[i] = (BinaryProjectRecord)checkedBinaries[i];
			}

			getDataModel().setProperty(SELECTED_PROJECTS, selectedProjects);
		}
	}

	protected void createBinaryLocationField(Composite parent) {
		Label label = new Label(parent, SWT.NONE);

		label.setText(Msgs.selectPluginsRootDirectoryLabel);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		binariesLocation = SWTUtil.createSingleText(parent, 1);

		binariesLocation.addModifyListener(
			new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					if (binariesLocation.isFocusControl() && CoreUtil.isNullOrEmpty(binariesLocation.getText())) {
						setMessage(null);
					}
				}

			});

		Button browse = SWTUtil.createButton(parent, Msgs.browse);

		browse.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					doBrowse();
				}

			});
	}

	@Override
	protected void createPluginsSDKField(Composite parent) {
		createBinaryLocationField(parent);

		SelectionAdapter selectionAdapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				synchHelper.synchAllUIWithModel();
				validatePage(true);
				updateBinariesList();
			}

		};

		new LiferaySDKField(
			parent, getDataModel(), selectionAdapter, LIFERAY_SDK_NAME, synchHelper, Msgs.selectSDKLabel);
	}

	@Override
	protected void createProjectsList(Composite workArea) {
		super.createProjectsList(workArea);

		labelProjectsList.setText(Msgs.binaryPluginsLabel);
	}

	@Override
	protected IBaseLabelProvider createProjectsListLabelProvider() {
		return new BinaryLabelProvider();
	}

	protected void doBrowse() {
		DirectoryDialog dd = new DirectoryDialog(getShell(), SWT.OPEN);

		String filterPath = binariesLocation.getText();

		if (filterPath != null) {
			dd.setFilterPath(filterPath);
			dd.setText(NLS.bind(Msgs.selectRootDirectoryPath, filterPath));
		}
		else {
			dd.setText(Msgs.selectRootDirectory);
		}

		if (CoreUtil.isNullOrEmpty(binariesLocation.getText())) {
			dd.setFilterPath(binariesLocation.getText());
		}

		String dir = dd.open();

		if (!CoreUtil.isNullOrEmpty(dir)) {
			binariesLocation.setText(dir);
			updateProjectsList(dir);
		}
	}

	@Override
	protected void enter() {
	}

	@Override
	protected void handleCheckStateChangedEvent(CheckStateChangedEvent event) {
		getDataModel().setProperty(SELECTED_PROJECTS, projectsList.getCheckedElements());

		setPageComplete(ListUtil.isNotEmpty(projectsList.getCheckedElements()));
	}

	@Override
	protected void handleDeselectAll(SelectionEvent e) {
		projectsList.setCheckedElements(new Object[0]);

		getDataModel().setProperty(SELECTED_PROJECTS, projectsList.getCheckedElements());

		validatePage(true);

		setPageComplete(false);
	}

	@Override
	protected void handleRefresh(SelectionEvent e) {

		// force a project refresh

		lastModified = -1;

		updateProjectsList(binariesLocation.getText());

		projectsList.setCheckedElements(new Object[0]);

		getDataModel().setProperty(SELECTED_PROJECTS, projectsList.getCheckedElements());

		validatePage(true);

		setPageComplete(false);
	}

	@Override
	protected void handleSelectAll(SelectionEvent e) {
		for (Object project : selectedProjects) {
			BinaryProjectRecord binaryProject = (BinaryProjectRecord)project;

			if (binaryProject.isConflicts()) {
				projectsList.setChecked(binaryProject, false);
			}
			else {
				projectsList.setChecked(binaryProject, true);
			}
		}

		getDataModel().setProperty(SELECTED_PROJECTS, projectsList.getCheckedElements());

		validatePage(true);
	}

	protected Text binariesLocation;

	protected final class BinaryLabelProvider extends StyledCellLabelProvider {

		public BinaryLabelProvider() {
			_colorRegistry.put(_GREY_COLOR, new RGB(128, 128, 128));
			_greyedStyler = StyledString.createColorRegistryStyler(_GREY_COLOR, null);
		}

		public Image getImage() {
			ProjectUI projectUI = ProjectUI.getDefault();

			ImageRegistry imageRegistry = projectUI.getImageRegistry();

			Image image = imageRegistry.get(ProjectUI.WAR_IMAGE_ID);

			return image;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see StyledCellLabelProvider#update(org.eclipse.jface. viewers.ViewerCell)
		 */
		@Override
		public void update(ViewerCell cell) {
			Object obj = cell.getElement();

			BinaryProjectRecord binaryProjectRecord = null;

			if (obj instanceof BinaryProjectRecord) {
				binaryProjectRecord = (BinaryProjectRecord)obj;
			}

			StyledString styledString = null;

			if (binaryProjectRecord.isConflicts()) {

				// TODO:show warning that some project exists, similar to what we get when
				// importing projects with
				// standard import existing project into workspace

				styledString = new StyledString(binaryProjectRecord.getBinaryName(), _greyedStyler);

				styledString.append(" (" + binaryProjectRecord.getFilePath() + ") ", _greyedStyler);
			}
			else {
				styledString = new StyledString(
					binaryProjectRecord.getBinaryName(),
					StyledString.createColorRegistryStyler(
						JFacePreferences.CONTENT_ASSIST_FOREGROUND_COLOR,
						JFacePreferences.CONTENT_ASSIST_BACKGROUND_COLOR));

				styledString.append(" (" + binaryProjectRecord.getFilePath() + ") ", _greyedStyler);
			}

			cell.setImage(getImage());
			cell.setText(styledString.getString());
			cell.setStyleRanges(styledString.getStyleRanges());

			super.update(cell);
		}

		private static final String _GREY_COLOR = "already_exist_element_color";

		private final ColorRegistry _colorRegistry = JFaceResources.getColorRegistry();
		private final Styler _greyedStyler;

	}

	private static class Msgs extends NLS {

		public static String binaryPluginsLabel;
		public static String browse;
		public static String importLiferayBinaryPlugins;
		public static String selectBinaryPlugins;
		public static String selectPluginsRootDirectoryLabel;
		public static String selectRootDirectory;
		public static String selectRootDirectoryPath;
		public static String selectSDKLabel;

		static {
			initializeMessages(BinaryProjectsImportWizardPage.class.getName(), Msgs.class);
		}

	}

}
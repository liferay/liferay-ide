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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.IOUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.IWorkspaceProjectBuilder;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.jobs.InitBundleJob;
import com.liferay.ide.project.core.jobs.JobUtil;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceProjectProvider;
import com.liferay.ide.project.ui.IvyUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageNavigatorListener;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValuePropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValPrefManagerProject;
import org.eclipse.wst.validation.internal.ValidatorMutable;
import org.eclipse.wst.validation.internal.model.ProjectPreferences;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import org.osgi.framework.Bundle;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
@SuppressWarnings({"unused", "restriction", "deprecation"})
public class InitConfigureProjectPage extends Page implements SelectionChangedListener {

	public InitConfigureProjectPage(final Composite parent, int style, LiferayUpgradeDataModel dataModel) {
		super(parent, style, dataModel, initConfigureProjectPageId, false);

		SapphireUtil.attachListener(dataModel.getSdkLocation(), new LiferayUpgradeValidationListener());
		SapphireUtil.attachListener(dataModel.getBundleName(), new LiferayUpgradeValidationListener());
		SapphireUtil.attachListener(dataModel.getBundleUrl(), new LiferayUpgradeValidationListener());
		SapphireUtil.attachListener(dataModel.getBackupLocation(), new LiferayUpgradeValidationListener());
		SapphireUtil.attachListener(dataModel.getUpgradeVersion(), new LiferayUpgradeValidationListener());

		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.V_SCROLL);
		GridData scrolledData = new GridData(SWT.FILL, SWT.FILL, true, true);

		scrolledData.widthHint = DEFAULT_PAGE_WIDTH;
		scrolledComposite.setLayoutData(scrolledData);

		_pageParent = SWTUtil.createComposite(scrolledComposite, getGridLayoutCount(), 1, GridData.FILL_BOTH);

		scrolledComposite.setMinHeight(300);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setContent(_pageParent);

		_dirLabel = createLabel(
			_pageParent, "Project sources(Plugins SDK, Maven parent, or existing Liferay Workspace)");
		_dirField = createTextField(_pageParent, SWT.NONE);

		_dirField.addModifyListener(
			new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					dataModel.setSdkLocation(_dirField.getText());

					if (CoreUtil.isNullOrEmpty(_dirField.getText())) {
						_disposeBundleCheckboxElement();

						_disposeBundleElement();

						_disposeImportElement();

						_disposeUpgradeVersionElement();

						dataModel.setIsLiferayWorkspace(false);
						dataModel.setUpgradeVersion(_upgradeVersionItemValues[1]);

						_createUpgradeVersionElement();

						_createDownloaBundleCheckboxElement();

						_createBundleElement();

						_createImportElement();

						_pageParent.layout();

						_startCheckThread();
					}
				}

			});

		Button createButton = SWTUtil.createButton(_pageParent, "Browse...");

		createButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dd = new DirectoryDialog(getShell());

					dd.setMessage("Plugins SDK top-level directory, Maven or Liferay Workspace project root directory");

					String selectedDir = dd.open();

					if (selectedDir != null) {
						_dirField.setText(selectedDir);
					}
				}

			});

		_createUpgradeVersionElement();

		_createDownloaBundleCheckboxElement();

		_createBundleElement();

		_createImportElement();

		_startCheckThread();
	}

	public void checkAndConfigureIvy(IProject project) {
		if ((project != null) && FileUtil.exists(project.getFile(ISDKConstants.IVY_XML_FILE))) {
			new WorkspaceJob("Configuring project with Ivy dependencies") {

				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
					try {
						IvyUtil.configureIvyProject(project, monitor);
					}
					catch (CoreException ce) {
						return ProjectCore.createErrorStatus(
							ProjectCore.PLUGIN_ID, "Failed to configured ivy project.", ce);
					}

					return StatusBridge.create(Status.createOkStatus());
				}

			}.schedule();
		}
	}

	@Override
	public void createSpecialDescriptor(Composite parent, int style) {
		Composite fillLayoutComposite = SWTUtil.createComposite(parent, 2, 2, GridData.FILL_HORIZONTAL);

		final StringBuilder descriptorBuilder = new StringBuilder(
			"The initial step will be to upgrade your source code to Liferay Workspace environment. ");

		descriptorBuilder.append("For more details, please see <a>dev.liferay.com</a>.");

		String url = "https://dev.liferay.com/develop/tutorials";

		SWTUtil.createHyperLink(fillLayoutComposite, SWT.WRAP, descriptorBuilder.toString(), 1, url);

		StringBuilder extensionDecBuilder = new StringBuilder(
			"The first step will help you convert a Liferay Plugins SDK 6.2");

		extensionDecBuilder.append(" to Liferay Workspace.\n");
		extensionDecBuilder.append(
			"Click the \"Import Projects\" button to import your project into the Eclipse workspace ");
		extensionDecBuilder.append("(this process maybe need 5-10 minutes for bundle initialization).\n");
		extensionDecBuilder.append("Note:\n");
		extensionDecBuilder.append("       To save time, downloading the 7.0 ivy cache locally could be a good choice");
		extensionDecBuilder.append(" when upgrading to Liferay Liferay workspace. \n");
		extensionDecBuilder.append(
			"       Theme and Ext projects will be ignored since this tool does not support them currently. \n");

		Label image = new Label(fillLayoutComposite, SWT.WRAP);

		image.setImage(loadImage("question.png"));

		PopupDialog popupDialog = new PopupDialog(
			fillLayoutComposite.getShell(), PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE, true, false, false, false, false,
			null, null) {

			private static final int _CURSOR_SIZE = 15;

			@Override
			protected Point getInitialLocation(Point initialSize) {
				Display display = getShell().getDisplay();

				Point location = display.getCursorLocation();

				location.x += _CURSOR_SIZE;
				location.y += _CURSOR_SIZE;

				return location;
			}

			@Override
			protected Control createDialogArea(Composite parent) {
				Label label = new Label(parent, SWT.WRAP);

				label.setText(extensionDecBuilder.toString());

				label.setFont(new Font(null, "Times New Roman", 11, SWT.NORMAL));

				GridData gd = new GridData(GridData.BEGINNING | GridData.FILL_BOTH);

				gd.horizontalIndent = PopupDialog.POPUP_HORIZONTALSPACING;
				gd.verticalIndent = PopupDialog.POPUP_VERTICALSPACING;
				label.setLayoutData(gd);

				return label;
			}

		};

		image.addListener(
			SWT.MouseHover,
			new org.eclipse.swt.widgets.Listener() {

				@Override
				public void handleEvent(org.eclipse.swt.widgets.Event event) {
					popupDialog.open();
				}

			});

		image.addListener(
			SWT.MouseExit,
			new org.eclipse.swt.widgets.Listener() {

				@Override
				public void handleEvent(org.eclipse.swt.widgets.Event event) {
					popupDialog.close();
				}

			});
	}

	@Override
	public int getGridLayoutCount() {
		return 2;
	}

	@Override
	public boolean getGridLayoutEqualWidth() {
		return false;
	}

	@Override
	public String getPageTitle() {
		return "Select project(s) to upgrade";
	}

	@Override
	public void onSelectionChanged(int targetSelection) {
		if (targetSelection == 1) {
			_startCheckThread();
		}
	}

	protected void importProject(IProgressMonitor monitor, IProgressMonitor groupMonitor) throws Exception {
		UIUtil.async(
			() -> {
				_customProcessBar = new CustomProcessBar(_pageParent, "Please wait for all work to be completed");

				_pageParent.layout();
			});

		String layout = SapphireUtil.getContent(dataModel.getLayout());

		IPath location = PathBridge.create(SapphireUtil.getContent(dataModel.getSdkLocation()));

		if (_isAlreadyImported(location)) {
			Stream.of(CoreUtil.getAllProjects()).forEach(this::_checkProjectType);

			dataModel.setImportFinished(true);

			return;
		}

		_backup();

		_clearExistingProjects(location, groupMonitor);

		_deleteEclipseConfigFiles(location.toFile());

		groupMonitor.worked(20);

		String locationString = location.toPortableString();

		if (LiferayWorkspaceUtil.isValidWorkspaceLocation(locationString)) {
			ILiferayProjectProvider provider = null;

			ILiferayProjectProvider[] providers = LiferayCore.getProviders("workspace");

			if (LiferayWorkspaceUtil.isValidGradleWorkspaceLocation(locationString)) {
				for (ILiferayProjectProvider projectProvider : providers) {
					String name = projectProvider.getShortName();

					if (name.contains("gradle")) {
						provider = projectProvider;

						break;
					}
				}
			}
			else {
				for (ILiferayProjectProvider projectProvider : providers) {
					String name = projectProvider.getShortName();

					if (name.contains("maven")) {
						provider = projectProvider;

						break;
					}
				}
			}

			if ((provider != null) && (provider instanceof NewLiferayWorkspaceProjectProvider)) {
				((NewLiferayWorkspaceProjectProvider<?>)provider).importProject(location, groupMonitor);

				JobUtil.waitForLiferayProjectJob();

				IProject[] projects = CoreUtil.getAllProjects();

				for (IProject project : projects) {
					_checkProjectType(project);
				}

				IJobManager jobManager = Job.getJobManager();

				jobManager.join("org.eclipse.buildship.core.jobs", null);

				if (SapphireUtil.getContent(dataModel.getDownloadBundle())) {
					_createInitBundle(monitor, groupMonitor);
				}

				dataModel.setConvertLiferayWorkspace(true);
			}
		}
		else if (_isMavenProject(location.toPortableString())) {
			ILiferayProjectImporter importer = LiferayCore.getImporter("maven");

			List<IProject> projects = importer.importProjects(locationString, groupMonitor);

			for (IProject project : projects) {
				_checkProjectType(project);
			}
		}
		else {
			String newPath = "";

			_createLiferayWorkspace(location, groupMonitor);

			_isCanceled(monitor);

			_removeIvyPrivateSetting(location.append("plugins-sdk"));

			newPath = _renameProjectFolder(location);

			IPath sdkLocation = new Path(newPath).append("plugins-sdk");

			_deleteSDKLegacyProjects(sdkLocation);

			ILiferayProjectImporter importer = LiferayCore.getImporter("gradle");

			importer.importProjects(newPath, groupMonitor);

			IJobManager jobManager = Job.getJobManager();

			jobManager.join("org.eclipse.buildship.core.jobs", null);

			if (SapphireUtil.getContent(dataModel.getDownloadBundle())) {
				_createInitBundle(monitor, groupMonitor);
			}

			_importSDKProject(sdkLocation, monitor);
		}

		dataModel.setImportFinished(true);
	}

	private void _backup() {
		Boolean backup = SapphireUtil.getContent(dataModel.getBackupSdk());

		if (!backup) {
			return;
		}

		try {
			org.eclipse.sapphire.modeling.Path originalPath = SapphireUtil.getContent(dataModel.getSdkLocation());

			if (originalPath != null) {
				IPath backupLocation = PathBridge.create(SapphireUtil.getContent(dataModel.getBackupLocation()));

				FileUtil.mkdirs(backupLocation.toFile());

				IPath backupPath = backupLocation.append("backup.zip");

				ZipUtil.zip(originalPath.toFile(), backupPath.toFile());
			}
		}
		catch (Exception e) {
			ProjectUI.logError("Error to backup original project folder.", e);
		}
	}

	private void _checkProjectType(IProject project) {
		if (FileUtil.notExists(project)) {
			return;
		}

		if (ProjectUtil.isGradleProject(project)) {
			dataModel.setHasGradleProject(true);
		}

		if (ProjectUtil.isMavenProject(project)) {
			dataModel.setHasMavenProject(true);
		}

		if (ProjectUtil.isPortletProject(project)) {
			dataModel.setHasPortlet(true);
		}

		if (ProjectUtil.isHookProject(project)) {
			dataModel.setHasHook(true);
		}

		List<IFile> searchFiles = new SearchFilesVisitor().searchFiles(project, "service.xml");

		if (ListUtil.isNotEmpty(searchFiles)) {
			dataModel.setHasServiceBuilder(true);
		}

		if (ProjectUtil.isLayoutTplProject(project)) {
			dataModel.setHasLayout(true);
		}

		if (ProjectUtil.isThemeProject(project)) {
			dataModel.setHasTheme(true);
		}

		if (ProjectUtil.isExtProject(project)) {
			dataModel.setHasExt(true);
		}

		if (ProjectUtil.isWebProject(project)) {
			dataModel.setHasWeb(true);
		}
	}

	private void _clearExistingProjects(IPath location, IProgressMonitor monitor) throws CoreException {
		IProject sdkProject = SDKUtil.getWorkspaceSDKProject();

		if ((sdkProject != null) && location.equals(sdkProject.getLocation())) {
			IProject[] projects = ProjectUtil.getAllPluginsSDKProjects();

			for (IProject project : projects) {
				project.delete(false, true, monitor);
			}

			sdkProject.delete(false, true, monitor);
		}

		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			String projectPortableLocation = FileUtil.getLocationPortableString(project);

			if (projectPortableLocation.startsWith(location.toPortableString())) {
				project.delete(false, true, monitor);
			}
		}
	}

	private boolean _configureProjectValidationExclude(IProject project, boolean disableValidation) {
		boolean retval = false;

		if (project == null) {
			return retval;
		}

		try {
			ValManager valManager = ValManager.getDefault();

			Validator[] vals = valManager.getValidators(project, true);

			ValidatorMutable[] validators = new ValidatorMutable[vals.length];

			for (int i = 0; i < vals.length; i++) {
				validators[i] = new ValidatorMutable(vals[i]);
			}

			ProjectPreferences pp = new ProjectPreferences(project, true, disableValidation, null);

			ValPrefManagerProject vpm = new ValPrefManagerProject(project);

			vpm.savePreferences(pp, validators);
		}
		catch (Exception e) {
		}

		return retval;
	}

	private void _configureUpgradeVersionComb(
		String[] upgradeVersionItemNames, String[] upgradeVersionItemValues, String upgradeVersion,
		String[] initBundleUrls) {

		_upgradeVersionComb.setItems(upgradeVersionItemNames);
		_upgradeVersionComb.select(0);

		for (int i = 0; i < upgradeVersionItemValues.length; i++) {
			if (upgradeVersionItemValues != null) {
				if (upgradeVersionItemValues[i].equals(upgradeVersion)) {
					_upgradeVersionComb.select(i);

					if ((_bundleUrlField != null) && !_bundleUrlField.isDisposed()) {
						dataModel.setBundleUrl(initBundleUrls[i]);
						_bundleUrlField.setText(initBundleUrls[i]);
					}

					break;
				}
			}
		}

		_upgradeVersionComb.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					String upgradeVersion = _upgradeVersionComb.getText();

					for (int i = 0; i < upgradeVersionItemNames.length; i++) {
						if (upgradeVersion.equals(upgradeVersionItemNames[i])) {
							dataModel.setUpgradeVersion(upgradeVersionItemValues[i]);

							if ((_bundleUrlField != null) && !_bundleUrlField.isDisposed()) {
								dataModel.setBundleUrl(initBundleUrls[i]);
								_bundleUrlField.setText(initBundleUrls[i]);
							}

							break;
						}
					}

					_startCheckThread();
				}

			});
	}

	private void _createBundleControl() {
		_disposeImportElement();

		_disposeBundleElement();

		_createDownloaBundleCheckboxElement();

		_createBundleElement();

		_createImportElement();

		_pageParent.layout();
	}

	private void _createBundleElement() {
		_bundleNameLabel = createLabel(_pageParent, "Server Name:");
		_bundleNameField = createTextField(_pageParent, SWT.NONE);

		_bundleNameField.addModifyListener(
			new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					dataModel.setBundleName(_bundleNameField.getText());
				}

			});

		Display pageDdisplay = _pageParent.getDisplay();

		String bundleName = SapphireUtil.getContent(dataModel.getBundleName());

		_bundleNameField.setText(bundleName != null ? bundleName : "");

		_bundleUrlLabel = createLabel(_pageParent, "Liferay Server Bundle Download URL");

		_bundleUrlField = createTextField(_pageParent, SWT.NONE);

		_bundleUrlField.setForeground(pageDdisplay.getSystemColor(SWT.COLOR_DARK_GRAY));

		_bundleUrlField.setText(SapphireUtil.getContent(dataModel.getBundleUrl()));

		_bundleUrlField.addModifyListener(
			new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					dataModel.setBundleUrl(_bundleUrlField.getText());
				}

			});

		_bundleUrlField.addFocusListener(
			new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
					String input = ((Text)e.getSource()).getText();
					String upgradeVersion = SapphireUtil.getContent(dataModel.getUpgradeVersion());

					String defaultBundleUrl = LiferayUpgradeDataModel.DEFAULT_BUNDLE_URL_70;

					if (upgradeVersion.contains("7.1")) {
						defaultBundleUrl = LiferayUpgradeDataModel.DEFAULT_BUNDLE_URL_71;
					}

					if (input.equals(defaultBundleUrl)) {
						_bundleUrlField.setText("");
					}

					_bundleUrlField.setForeground(pageDdisplay.getSystemColor(SWT.COLOR_BLACK));
				}

				@Override
				public void focusLost(FocusEvent e) {
					String input = ((Text)e.getSource()).getText();
					String upgradeVersion = SapphireUtil.getContent(dataModel.getUpgradeVersion());

					String defaultBundleUrl = LiferayUpgradeDataModel.DEFAULT_BUNDLE_URL_70;

					if (upgradeVersion.contains("7.1")) {
						defaultBundleUrl = LiferayUpgradeDataModel.DEFAULT_BUNDLE_URL_71;
					}

					if (CoreUtil.isNullOrEmpty(input)) {
						_bundleUrlField.setForeground(pageDdisplay.getSystemColor(SWT.COLOR_DARK_GRAY));
						_bundleUrlField.setText(defaultBundleUrl);
					}
				}

			});

		dataModel.setBundleUrl(_bundleUrlField.getText());
	}

	private void _createDownloaBundleCheckboxElement() {
		_disposeBundleCheckboxElement();

		_downloadBundleCheckbox = SWTUtil.createCheckButton(
			_pageParent, "Download and Initialize Liferay Server Bundle", null, true, 1);

		GridDataFactory.generate(_downloadBundleCheckbox, 2, 1);

		_downloadBundleCheckbox.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					boolean downloadBundle = _downloadBundleCheckbox.getSelection();

					dataModel.setDownloadBundle(downloadBundle);

					if (downloadBundle) {
						_disposeImportElement();
						_createBundleElement();
						_createImportElement();
						_pageParent.layout();
					}
					else {
						_disposeBundleElement();
						_disposeImportElement();
						_createImportElement();
						_pageParent.layout();
					}

					_startCheckThread();
				}

			});
	}

	private void _createImportElement() {
		_createHorizontalSpacer = createHorizontalSpacer(_pageParent, 3);
		_createSeparator = createSeparator(_pageParent, 3);

		_importButton = SWTUtil.createButton(_pageParent, "Import Projects");

		_importButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Boolean importFinished = SapphireUtil.getContent(dataModel.getImportFinished());

					if (_isPageValidate() && !importFinished) {
						_saveSettings();

						_importButton.setEnabled(false);

						IJobManager jobManager = Job.getJobManager();

						IProgressMonitor groupMonitor = jobManager.createProgressGroup();

						Job importJob = new Job("Importing projects") {

							@Override
							protected IStatus run(IProgressMonitor monitor) {
								try {
									groupMonitor.beginTask("Processing work", 100);

									importProject(monitor, groupMonitor);

									groupMonitor.done();

									return org.eclipse.core.runtime.Status.OK_STATUS;
								}
								catch (OperationCanceledException oce) {
									return org.eclipse.core.runtime.Status.CANCEL_STATUS;
								}
								catch (Exception ce) {
									groupMonitor.done();

									ProjectUI.logError(ce);

									PageValidateEvent pe = new PageValidateEvent();

									pe.setMessage(ce.getMessage());
									pe.setType(PageValidateEvent.error);

									triggerValidationEvent(pe);

									return ProjectUI.createErrorStatus(ce.getMessage(), ce);
								}
							}

						};

						importJob.addJobChangeListener(
							new JobChangeAdapter() {

								@Override
								public void done(IJobChangeEvent changeEvent) {
									UIUtil.async(
										() -> {
											if (_customProcessBar != null) {
												_customProcessBar.dispose();
											}

											if (changeEvent.getResult() ==
													org.eclipse.core.runtime.Status.CANCEL_STATUS) {

												return;
											}

											UpgradeView.resetPages();

											PageNavigateEvent event = new PageNavigateEvent();

											if (UpgradeView.getPageNumber() < 3) {
												Boolean showAllPages = MessageDialog.openQuestion(
													UIUtil.getActiveShell(), "Show All Pages",
													"There is no project need to be upgraded.\n Show all the " +
														"following steps?");

												if (showAllPages) {
													UpgradeView.showAllPages();
												}
												else {
													event.setTargetPage(1);
												}
											}
											else {
												event.setTargetPage(2);
											}

											for (PageNavigatorListener listener : naviListeners) {
												listener.onPageNavigate(event);
											}

											setNextPage(true);

											_importButton.setEnabled(true);

											setSelectedAction(getSelectedAction("PageFinishAction"));
										});
								}

							});

						importJob.setProgressGroup(groupMonitor, IProgressMonitor.UNKNOWN);

						importJob.schedule();
					}
				}

			});
	}

	private void _createInitBundle(IProgressMonitor monitor, IProgressMonitor groupMonitor)
		throws CoreException, InterruptedException {

		_isCanceled(monitor);

		IPath location = PathBridge.create(SapphireUtil.getContent(dataModel.getSdkLocation()));

		IProject project = CoreUtil.getProject(location.lastSegment());

		String serverName = SapphireUtil.getContent(dataModel.getBundleName());

		String bundleUrl = SapphireUtil.getContent(dataModel.getBundleUrl());

		InitBundleJob job = new InitBundleJob(project, serverName, bundleUrl);

		job.setProgressGroup(groupMonitor, IProgressMonitor.UNKNOWN);

		job.schedule();

		job.join();

		groupMonitor.worked(50);

		_isCanceled(monitor);
	}

	private void _createLiferayWorkspace(IPath targetSDKLocation, IProgressMonitor groupMonitor)
		throws InterruptedException {

		Job job = new Job("Initializing Liferay Workspace...") {

			@Override
			protected IStatus run(IProgressMonitor progress) {
				try {
					StringBuilder sb = new StringBuilder();

					sb.append("--base ");

					File targetSdkFile = targetSDKLocation.toFile();

					sb.append("\"" + targetSdkFile.getAbsolutePath() + "\" ");

					sb.append("init -u");

					progress.worked(30);
					BladeCLI.execute(sb.toString());
					progress.worked(100);
				}
				catch (BladeCLIException bclie) {
					ProjectUI.logError(bclie);

					return ProjectUI.createErrorStatus("Faild execute Liferay Workspace Init Command...", bclie);
				}

				return org.eclipse.core.runtime.Status.OK_STATUS;
			}

		};

		job.setProgressGroup(groupMonitor, IProgressMonitor.UNKNOWN);

		job.schedule();

		job.join();
	}

	private void _createServerControl() {
		_disposeImportElement();

		_disposeBundleCheckboxElement();

		_disposeBundleElement();

		_createImportElement();

		_pageParent.layout();
	}

	private void _createUpgradeVersionElement() {
		_upgradeVersionLabel = createLabel(_pageParent, "Upgrade to Liferay Version ");

		_upgradeVersionComb = new Combo(_pageParent, SWT.DROP_DOWN | SWT.READ_ONLY);

		_upgradeVersionComb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		String upgradeVersionValue = SapphireUtil.getContent(dataModel.getUpgradeVersion());

		Boolean inputIsLiferayWorkspaceElement = SapphireUtil.getContent(dataModel.getIsLiferayWorkspace());

		if (inputIsLiferayWorkspaceElement) {
			_configureUpgradeVersionComb(
				_upgradeVersionItemNamesWorkspace, _upgradeVersionItemValuesWorkspace, upgradeVersionValue,
				_upgradeVersionBundleUrlValuesWorkspace);
		}
		else {
			_configureUpgradeVersionComb(
				_upgradeVersionItemNames, _upgradeVersionItemValues, upgradeVersionValue, _initBundleUrlValues);
		}
	}

	private void _deleteEclipseConfigFiles(File project) {
		for (File file : project.listFiles()) {
			if (".classpath".contentEquals(file.getName()) || ".settings".contentEquals(file.getName()) ||
				".project".contentEquals(file.getName())) {

				if (file.isDirectory()) {
					FileUtil.deleteDir(file, true);
				}

				file.delete();
			}
		}
	}

	private void _deleteSDKLegacyProjects(IPath sdkLocation) {
		String[] needDeletedPaths = {"shared/portal-http-service", "webs/resources-importer-web"};

		for (String path : needDeletedPaths) {
			IPath sdkPath = sdkLocation.append(path);

			File file = sdkPath.toFile();

			if (file.exists()) {
				FileUtil.deleteDir(file, true);
			}
		}
	}

	private void _deleteServiceBuilderJarFile(IProject project, IProgressMonitor monitor) {
		try {
			IFolder docrootFolder = CoreUtil.getDefaultDocrootFolder(project);

			if (docrootFolder != null) {
				IFile serviceJarFile = docrootFolder.getFile("WEB-INF/lib/" + project.getName() + "-service.jar");

				if (serviceJarFile.exists()) {
					serviceJarFile.delete(true, monitor);
				}
			}
		}
		catch (CoreException ce) {
			ProjectUI.logError(ce);
		}
	}

	private void _disposeBundleCheckboxElement() {
		if ((_downloadBundleCheckbox != null) && (_downloadBundleCheckbox != null)) {
			_downloadBundleCheckbox.dispose();
		}
	}

	private void _disposeBundleElement() {
		if ((_bundleNameField != null) && (_bundleUrlField != null)) {
			_bundleNameField.dispose();
			_bundleNameLabel.dispose();
			_bundleUrlField.dispose();
			_bundleUrlLabel.dispose();
		}
	}

	private void _disposeImportElement() {
		if (_createSeparator != null) {
			_createSeparator.dispose();
		}

		if (_createHorizontalSpacer != null) {
			_createHorizontalSpacer.dispose();
		}

		if (_importButton != null) {
			_importButton.dispose();
		}
	}

	private void _disposeUpgradeVersionElement() {
		if ((_upgradeVersionLabel != null) && (_upgradeVersionComb != null)) {
			_upgradeVersionLabel.dispose();
			_upgradeVersionComb.dispose();
		}
	}

	private void _getLiferayBundle(IPath targetSDKLocation) throws BladeCLIException {
		StringBuilder sb = new StringBuilder();

		sb.append("--base ");

		File sdkLocation = targetSDKLocation.toFile();

		sb.append("\"" + sdkLocation.getAbsolutePath() + "\" ");

		sb.append("init");

		BladeCLI.execute(sb.toString());
	}

	private IWorkspaceProjectBuilder _getWorkspaceProjectBuilder(IProject project) throws CoreException {
		final ILiferayProject liferayProject = LiferayCore.create(project);

		if (liferayProject == null) {
			throw new CoreException(ProjectUI.createErrorStatus("Can not find Liferay workspace project."));
		}

		final IWorkspaceProjectBuilder builder = liferayProject.adapt(IWorkspaceProjectBuilder.class);

		if (builder == null) {
			throw new CoreException(ProjectUI.createErrorStatus("Can not find Liferay Gradle project builder."));
		}

		return builder;
	}

	private void _importSDKProject(IPath targetSDKLocation, IProgressMonitor monitor) {
		Collection<File> eclipseProjectFiles = new ArrayList<>();

		Collection<File> liferayProjectDirs = new ArrayList<>();

		if (ProjectUtil.collectSDKProjectsFromDirectory(
				eclipseProjectFiles, liferayProjectDirs, targetSDKLocation.toFile(), null, true, monitor)) {

			for (File project : liferayProjectDirs) {
				try {
					_deleteEclipseConfigFiles(project);

					IProject importProject = ProjectImportUtil.importProject(
						new Path(project.getPath()), monitor, null);

					if ((importProject != null) && importProject.isAccessible() && importProject.isOpen()) {
						_checkProjectType(importProject);

						_deleteServiceBuilderJarFile(importProject, monitor);
					}

					if (ProjectUtil.isExtProject(importProject) || ProjectUtil.isThemeProject(importProject)) {
						importProject.delete(false, true, monitor);
					}

					_configureProjectValidationExclude(importProject, true);
				}
				catch (CoreException ce) {
				}
			}

			for (File project : eclipseProjectFiles) {
				try {
					_deleteEclipseConfigFiles(project.getParentFile());

					IProject importProject = ProjectImportUtil.importProject(
						new Path(project.getParent()), monitor, null);

					if ((importProject != null) && importProject.isAccessible() && importProject.isOpen()) {
						_checkProjectType(importProject);

						_deleteServiceBuilderJarFile(importProject, monitor);
					}

					if (ProjectUtil.isExtProject(importProject) || ProjectUtil.isThemeProject(importProject)) {
						importProject.delete(false, true, monitor);
					}

					_configureProjectValidationExclude(importProject, true);
				}
				catch (CoreException ce) {
				}
			}
		}
	}

	private boolean _isAlreadyImported(IPath path) {
		IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

		IContainer[] containers = workspaceRoot.findContainersForLocationURI(FileUtil.toURI(path));

		long projectCount = Stream.of(
			containers
		).filter(
			container -> container instanceof IProject
		).count();

		if (projectCount > 0) {
			return true;
		}

		return false;
	}

	private void _isCanceled(IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			throw new OperationCanceledException("Code upgrade was canceled");
		}
	}

	private boolean _isMavenProject(String path) {
		IStatus buildType = ImportLiferayModuleProjectOpMethods.getBuildType(path);

		return "maven".equals(buildType.getMessage());
	}

	private boolean _isPageValidate() {
		return _validationResult;
	}

	private void _refreshUpgradeVersion() {
		if ((_upgradeVersionLabel != null) && (_upgradeVersionComb != null) && !_upgradeVersionComb.isDisposed()) {
			Boolean liferayWorkspace = SapphireUtil.getContent(dataModel.getIsLiferayWorkspace());

			String[] upgradeVersdionItemNames = null;
			String[] upgradeVersionsValues = null;
			String[] initBundleUrls = null;

			if (!liferayWorkspace) {
				upgradeVersdionItemNames = _upgradeVersionItemNames;
				upgradeVersionsValues = _upgradeVersionItemValues;
				initBundleUrls = _initBundleUrlValues;
			}
			else {
				upgradeVersdionItemNames = _upgradeVersionItemNamesWorkspace;
				upgradeVersionsValues = _upgradeVersionItemNamesWorkspace;
				initBundleUrls = _upgradeVersionBundleUrlValuesWorkspace;
			}

			_upgradeVersionComb.setItems(upgradeVersdionItemNames);

			String upgradeVersion = SapphireUtil.getContent(dataModel.getUpgradeVersion());

			for (int i = 0; i < upgradeVersionsValues.length; i++) {
				if (StringUtil.equals(upgradeVersion, upgradeVersionsValues[i])) {
					int index = i;
					String[] bundleUrls = initBundleUrls;

					UIUtil.async(
						new Runnable() {

							@Override
							public void run() {
								_upgradeVersionComb.select(index);

								if ((_bundleUrlField != null) && !_bundleUrlField.isDisposed()) {
									_bundleUrlField.setText(bundleUrls[index]);
								}
							}

						});

					dataModel.setBundleUrl(initBundleUrls[i]);

					break;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void _removeIvyPrivateSetting(IPath sdkLocation) throws CoreException {
		IPath ivySettingPath = sdkLocation.append("ivy-settings.xml");

		File ivySettingFile = ivySettingPath.toFile();

		SAXBuilder builder = new SAXBuilder(false);

		builder.setValidation(false);
		builder.setFeature("http://xml.org/sax/features/validation", false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

		if (FileUtil.notExists(ivySettingFile)) {
			return;
		}

		try (InputStream ivyInput = Files.newInputStream(ivySettingFile.toPath())) {
			Document doc = builder.build(ivyInput);

			Element itemRem = null;
			Element elementRoot = doc.getRootElement();

			List<Element> resolversElements = elementRoot.getChildren("resolvers");

			for (Iterator<Element> resolversIterator = resolversElements.iterator(); resolversIterator.hasNext();) {
				Element resolversElement = resolversIterator.next();

				List<Element> chainElements = resolversElement.getChildren("chain");

				for (Iterator<Element> chainIterator = chainElements.iterator(); chainIterator.hasNext();) {
					Element chainElement = chainIterator.next();

					List<Element> resolverElements = chainElement.getChildren("resolver");

					Iterator<Element> resolverIterator = resolverElements.iterator();

					while (resolverIterator.hasNext()) {
						Element resolverItem = resolverIterator.next();

						String resolverRefItem = resolverItem.getAttributeValue("ref");

						if (resolverRefItem.equals("liferay-private")) {
							resolverIterator.remove();

							itemRem = resolverItem;
						}
					}
				}

				elementRoot.removeContent(itemRem);

				List<Element> ibiblioElements = resolversElement.getChildren("ibiblio");

				for (Iterator<Element> ibiblioIterator = ibiblioElements.iterator(); ibiblioIterator.hasNext();) {
					Element ibiblioElement = ibiblioIterator.next();

					String liferayPrivateName = ibiblioElement.getAttributeValue("name");

					if (liferayPrivateName.equals("liferay-private")) {
						ibiblioIterator.remove();
						itemRem = ibiblioElement;
					}
				}

				elementRoot.removeContent(itemRem);
			}

			_saveXML(ivySettingFile, doc);
		}
		catch (CoreException | IOException | JDOMException e) {
			ProjectUI.logError(e);

			throw new CoreException(
				StatusBridge.create(
					Status.createErrorStatus(
						"Failed to remove Liferay private url configuration of ivy-settings.xml.", e)));
		}
	}

	private String _renameProjectFolder(IPath targetSDKLocation) throws CoreException {
		return targetSDKLocation.toString();
	}

	private void _saveSettings() {
		dataModel.setHasExt(false);
		dataModel.setHasHook(false);
		dataModel.setHasLayout(false);
		dataModel.setHasPortlet(false);
		dataModel.setHasServiceBuilder(false);
		dataModel.setHasTheme(false);
		dataModel.setHasWeb(false);

		if ((_bundleNameField != null) && !_bundleNameField.isDisposed()) {
			dataModel.setLiferay70ServerName(_bundleNameField.getText());
		}
	}

	private void _saveXML(File templateFile, Document doc) throws CoreException {
		XMLOutputter out = new XMLOutputter();

		try (OutputStream fos = Files.newOutputStream(templateFile.toPath());) {
			out.output(doc, fos);
		}
		catch (Exception e) {
			ProjectUI.logError(e);

			throw new CoreException(
				StatusBridge.create(Status.createErrorStatus("Failed to save change for ivy-settings.xml.", e)));
		}
	}

	private void _startCheckThread() {
		final Thread t = new Thread() {

			@Override
			public void run() {
				_validate();
			}

		};

		t.start();
	}

	private void _validate() {
		UIUtil.async(
			new Runnable() {

				@Override
				public void run() {
					boolean inputValidation = true;
					boolean layoutValidation = true;

					boolean downloadBundle = SapphireUtil.getContent(dataModel.getDownloadBundle());

					String bundUrl = SapphireUtil.getContent(dataModel.getBundleUrl());

					String message = "ok";

					PageValidateEvent pe = new PageValidateEvent();

					pe.setType(PageValidateEvent.error);

					Value<org.eclipse.sapphire.modeling.Path> backupLocationValue = dataModel.getBackupLocation();

					Status validation = backupLocationValue.validation();

					Status sdkValidation = _sdkValidation.compute();

					if (!sdkValidation.ok()) {
						message = sdkValidation.message();

						inputValidation = false;
					}
					else if (!validation.ok()) {
						message = validation.message();

						inputValidation = false;
					}
					else {
						inputValidation = true;
					}

					Status bundleUrlValidation = _bundleUrlValidation.compute();
					Status bundleNameValidation = _bundleNameValidation.compute();

					if (downloadBundle && !bundleNameValidation.ok()) {
						message = bundleNameValidation.message();

						layoutValidation = false;
					}
					else if (downloadBundle && CoreUtil.isNotNullOrEmpty(bundUrl) && !bundleUrlValidation.ok()) {
						message = bundleUrlValidation.message();

						layoutValidation = false;
					}
					else {
						layoutValidation = true;
					}

					Boolean importFinished = SapphireUtil.getContent(dataModel.getImportFinished());

					if (importFinished) {
						message = "Import has finished. If you want to reset, please click reset icon in view toolbar.";

						pe.setType(PageValidateEvent.warning);

						inputValidation = false;
					}

					pe.setMessage(message);

					triggerValidationEvent(pe);

					_validationResult = layoutValidation && inputValidation;

					_importButton.setEnabled(_validationResult);
				}

			});
	}

	private Text _bundleNameField;
	private Label _bundleNameLabel;
	private BundleNameValidationService _bundleNameValidation =
		dataModel.getBundleName().service(BundleNameValidationService.class);
	private Text _bundleUrlField;
	private Label _bundleUrlLabel;
	private BundleUrlValidationService _bundleUrlValidation =
		dataModel.getBundleUrl().service(BundleUrlValidationService.class);
	private Control _createHorizontalSpacer;
	private Control _createSeparator;
	private CustomProcessBar _customProcessBar;
	private Text _dirField;
	private Label _dirLabel;
	private Button _downloadBundleCheckbox;
	private Button _importButton;
	private String[] _initBundleUrlValues = {
		LiferayUpgradeDataModel.DEFAULT_BUNDLE_URL_70, LiferayUpgradeDataModel.DEFAULT_BUNDLE_URL_71
	};
	private Composite _pageParent;
	private ProjectLocationValidationService _sdkValidation =
		dataModel.getSdkLocation().service(ProjectLocationValidationService.class);
	private String[] _upgradeVersionBundleUrlValuesWorkspace = {LiferayUpgradeDataModel.DEFAULT_BUNDLE_URL_71};
	private Combo _upgradeVersionComb;
	private String[] _upgradeVersionItemNames = {"7.0", "7.1"};
	private String[] _upgradeVersionItemNamesWorkspace = {"7.1"};
	private String[] _upgradeVersionItemValues = {"7.0", "7.0,7.1"};
	private String[] _upgradeVersionItemValuesWorkspace = {"7.1"};
	private Label _upgradeVersionLabel;
	private boolean _validationResult;

	private class LiferayUpgradeValidationListener extends Listener {

		@Override
		public void handle(Event event) {
			if (!(event instanceof ValuePropertyContentEvent)) {
				return;
			}

			final Property property = ((ValuePropertyContentEvent)event).property();

			if (!"SdkLocation".equals(property.name())) {
				_startCheckThread();

				return;
			}

			org.eclipse.sapphire.modeling.Path path = SapphireUtil.getContent(dataModel.getSdkLocation());

			Status compute = _sdkValidation.compute();

			if ((path == null) || !compute.ok()) {
				_startCheckThread();

				return;
			}

			if (_isAlreadyImported(PathBridge.create(path))) {
				if (LiferayWorkspaceUtil.isValidWorkspaceLocation(path.toPortableString())) {
					dataModel.setUpgradeVersion(_upgradeVersionItemValuesWorkspace[0]);
					dataModel.setIsLiferayWorkspace(true);
				}
				else {
					dataModel.setIsLiferayWorkspace(false);
					dataModel.setUpgradeVersion(_upgradeVersionItemValues[1]);
				}

				dataModel.setDownloadBundle(false);

				_disposeBundleCheckboxElement();
				_disposeBundleElement();

				_importButton.setText("Continue");
				_pageParent.layout();
			}
			else if (LiferayWorkspaceUtil.isValidWorkspaceLocation(path.toPortableString())) {
				dataModel.setIsLiferayWorkspace(true);
				dataModel.setUpgradeVersion(_upgradeVersionItemValuesWorkspace[0]);

				_disposeBundleCheckboxElement();
				_disposeBundleElement();
				_disposeUpgradeVersionElement();
				_disposeImportElement();

				_createUpgradeVersionElement();
				_createBundleControl();

				_pageParent.layout();
			}
			else if (_isMavenProject(path.toPortableString()) &&
					 !LiferayWorkspaceUtil.isValidWorkspaceLocation(path.toPortableString())) {

				dataModel.setIsLiferayWorkspace(false);
				dataModel.setDownloadBundle(false);
				dataModel.setUpgradeVersion(_upgradeVersionItemValues[1]);

				_disposeBundleCheckboxElement();
				_disposeBundleElement();
				_disposeUpgradeVersionElement();
				_disposeImportElement();

				_createUpgradeVersionElement();
				_createImportElement();
				_pageParent.layout();
			}
			else {
				dataModel.setIsLiferayWorkspace(false);
				dataModel.setUpgradeVersion(_upgradeVersionItemValues[1]);

				_disposeUpgradeVersionElement();

				_createUpgradeVersionElement();

				_createBundleControl();

				_pageParent.layout();
			}

			_refreshUpgradeVersion();
			_startCheckThread();
		}

	}

}
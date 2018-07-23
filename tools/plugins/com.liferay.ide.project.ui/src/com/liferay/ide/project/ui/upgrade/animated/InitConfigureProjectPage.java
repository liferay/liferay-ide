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
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.IWorkspaceProjectBuilder;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.SapphireUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceProjectProvider;
import com.liferay.ide.project.ui.BreakingChangeVersion;
import com.liferay.ide.project.ui.IvyUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageNavigatorListener;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.reflect.InvocationTargetException;

import java.net.URL;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ComboViewer;
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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.ServerUIUtil;
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
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
@SuppressWarnings({"unused", "restriction", "deprecation"})
public class InitConfigureProjectPage extends Page implements IServerLifecycleListener, SelectionChangedListener {

	public InitConfigureProjectPage(final Composite parent, int style, LiferayUpgradeDataModel dataModel) {
		super(parent, style, dataModel, initConfigureProjectPageId, false);

		SapphireUtil.propertyAttachListener(dataModel.getSdkLocation(), new LiferayUpgradeValidationListener());
		SapphireUtil.propertyAttachListener(dataModel.getBundleName(), new LiferayUpgradeValidationListener());
		SapphireUtil.propertyAttachListener(dataModel.getBundleUrl(), new LiferayUpgradeValidationListener());
		SapphireUtil.propertyAttachListener(dataModel.getBackupLocation(), new LiferayUpgradeValidationListener());
		SapphireUtil.propertyAttachListener(dataModel.getBreakingChangeVersion(), new LiferayUpgradeValidationListener());

		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.V_SCROLL);
		GridData scrolledData = new GridData(SWT.FILL, SWT.FILL, true, true);

		scrolledData.widthHint = DEFAULT_PAGE_WIDTH;
		scrolledComposite.setLayoutData(scrolledData);

		_pageParent = SWTUtil.createComposite(scrolledComposite, getGridLayoutCount(), 1, GridData.FILL_BOTH);

		scrolledComposite.setMinHeight(300);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setContent(_pageParent);

		_dirLabel = createLabel(_pageParent, "Plugins SDK or Maven Project Root Location:");
		_dirField = createTextField(_pageParent, SWT.NONE);

		_dirField.addModifyListener(
			new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					dataModel.setSdkLocation(_dirField.getText());

					if (CoreUtil.isNullOrEmpty(_dirField.getText())) {
						_disposeMigrateLayoutElement();

						_disposeBundleCheckboxElement();

						_disposeBundleElement();

						_disposeServerEelment();

						_disposeImportElement();

						_disposeBreakingChangeElement();

						_createMigrateBreakingChangeVersion();

						_createMigrateLayoutElement();

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

					dd.setMessage("Plugins SDK top-level directory or Maven project root directory");

					String selectedDir = dd.open();

					if (selectedDir != null) {
						_dirField.setText(selectedDir);
					}
				}

			});

		_createMigrateBreakingChangeVersion();

		_createMigrateLayoutElement();

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
			"The initial step will be to upgrade to Liferay Workspace or Liferay Plugins SDK 7.0. ");

		descriptorBuilder.append("For more details, please see <a>dev.liferay.com</a>.");

		String url = "https://dev.liferay.com/develop/tutorials";

		SWTUtil.createHyperLink(fillLayoutComposite, SWT.WRAP, descriptorBuilder.toString(), 1, url);

		StringBuilder extensionDecBuilder = new StringBuilder(
			"The first step will help you convert a Liferay Plugins SDK 6.2");

		extensionDecBuilder.append(" to Liferay Plugins SDK 7.0 or to Liferay Workspace.\n");
		extensionDecBuilder.append(
			"Click the \"Import Projects\" button to import your project into the Eclipse workspace ");
		extensionDecBuilder.append("(this process maybe need 5-10 minutes for bundle initialization).\n");
		extensionDecBuilder.append("Note:\n");
		extensionDecBuilder.append("       To save time, downloading the 7.0 ivy cache locally could be a good choice");
		extensionDecBuilder.append(" when upgrading to Liferay Plugins SDK 7. \n");
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

	@Override
	public void serverAdded(IServer server) {
		UIUtil.async(
			new Runnable() {

				@Override
				public void run() {
					boolean serverExisted = false;

					if ((_serverComb != null) && !_serverComb.isDisposed()) {
						String[] serverNames = _serverComb.getItems();

						List<String> serverList = new ArrayList<>(Arrays.asList(serverNames));

						for (String serverName : serverList) {
							if (serverName.equals(server.getName())) {
								serverExisted = true;
							}
						}

						if (!serverExisted) {
							serverList.add(server.getName());

							_serverComb.setItems(serverList.toArray(new String[serverList.size()]));
							_serverComb.select(serverList.size() - 1);
						}

						_startCheckThread();
					}
				}

			});
	}

	@Override
	public void serverChanged(IServer server) {
	}

	@Override
	public void serverRemoved(IServer server) {
		UIUtil.async(
			new Runnable() {

				@Override
				public void run() {
					if ((_serverComb != null) && !_serverComb.isDisposed()) {
						String[] serverNames = _serverComb.getItems();

						List<String> serverList = new ArrayList<>(Arrays.asList(serverNames));

						Iterator<String> serverNameiterator = serverList.iterator();

						while (serverNameiterator.hasNext()) {
							String serverName = serverNameiterator.next();

							if (serverName.equals(server.getName())) {
								serverNameiterator.remove();
							}
						}

						_serverComb.setItems(serverList.toArray(new String[serverList.size()]));
						_serverComb.select(0);

						_startCheckThread();
					}
				}

			});
	}

	protected void importProject() throws CoreException {
		String layout = SapphireUtil.getValue(dataModel.getLayout());

		IPath location = PathBridge.create(SapphireUtil.getValue(dataModel.getSdkLocation()));

		if (_isAlreadyImported(location)) {
			Stream.of(CoreUtil.getAllProjects()).forEach(this::_checkProjectType);

			dataModel.setImportFinished(true);

			return;
		}

		try {
			IWorkbench workbench = PlatformUI.getWorkbench();

			IProgressService progressService = workbench.getProgressService();

			progressService.run(
				true, true,
				new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InterruptedException, InvocationTargetException {
						try {
							String newPath = "";

							_backup(monitor);

							_clearExistingProjects(location, monitor);

							_deleteEclipseConfigFiles(location.toFile());

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
									((NewLiferayWorkspaceProjectProvider<?>)provider).importProject(location, monitor);

									IJobManager jobManager = Job.getJobManager();

									Job[] jobs = jobManager.find(null);

									for (Job job : jobs) {
										if (job.getProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB) != null) {
											job.join();
										}
									}

									IProject[] projects = CoreUtil.getAllProjects();

									for (IProject project : projects) {
										_checkProjectType(project);
									}

									dataModel.setConvertLiferayWorkspace(true);
								}
							}
							else if (_isMavenProject(location.toPortableString())) {
								ILiferayProjectImporter importer = LiferayCore.getImporter("maven");

								List<IProject> projects = importer.importProjects(locationString, monitor);

								for (IProject project : projects) {
									_checkProjectType(project);
								}
							}
							else {
								if (layout.equals("Upgrade to Liferay Workspace")) {
									_createLiferayWorkspace(location, monitor);

									_removeIvyPrivateSetting(location.append("plugins-sdk"));

									newPath = _renameProjectFolder(location);

									IPath sdkLocation = new Path(newPath).append("plugins-sdk");

									_deleteSDKLegacyProjects(sdkLocation);

									ILiferayProjectImporter importer = LiferayCore.getImporter("gradle");

									importer.importProjects(newPath, monitor);

									IJobManager jobManager = Job.getJobManager();

									jobManager.join("org.eclipse.buildship.core.jobs", null);

									if (SapphireUtil.getValue(dataModel.getDownloadBundle())) {
										_createInitBundle(monitor);
									}

									_importSDKProject(sdkLocation, monitor);

									dataModel.setConvertLiferayWorkspace(true);
								}
								else {
									_deleteEclipseConfigFiles(location.toFile());
									_copyNewSDK(location, monitor);

									_removeIvyPrivateSetting(location);

									_deleteSDKLegacyProjects(location);

									String serverName = SapphireUtil.getValue(dataModel.getLiferay70ServerName());

									IServer server = ServerUtil.getServer(serverName);

									newPath = _renameProjectFolder(location);

									SDK sdk = SDKUtil.createSDKFromLocation(new Path(newPath));

									ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(server);

									sdk.addOrUpdateServerProperties(liferayRuntime.getLiferayHome());

									SDKUtil.openAsProject(sdk, monitor);

									_importSDKProject(sdk.getLocation(), monitor);
								}
							}

							dataModel.setImportFinished(true);
						}
						catch (Exception e) {
							ProjectUI.logError(e);

							throw new InvocationTargetException(e, e.getMessage());
						}
					}

				});
		}
		catch (Exception e) {
			ProjectUI.logError(e);

			throw new CoreException(StatusBridge.create(Status.createErrorStatus(e.getMessage(), e)));
		}
	}

	private void _backup(IProgressMonitor monitor) {
		Boolean backup = SapphireUtil.getValue(dataModel.getBackupSdk());

		if (!backup) {
			return;
		}

		SubMonitor progress = SubMonitor.convert(monitor, 100);

		try {
			progress.setTaskName("Backup origial project folder into Eclipse workspace...");

			org.eclipse.sapphire.modeling.Path originalPath = SapphireUtil.getValue(dataModel.getSdkLocation());

			if (originalPath != null) {
				IPath backupLocation = PathBridge.create(SapphireUtil.getValue(dataModel.getBackupLocation()));

				FileUtil.mkdirs(backupLocation.toFile());

				progress.worked(30);

				IPath backupPath = backupLocation.append("backup.zip");

				ZipUtil.zip(originalPath.toFile(), backupPath.toFile());

				progress.setWorkRemaining(70);
			}
		}
		catch (Exception e) {
			ProjectUI.logError("Error to backup original project folder.", e);
		}
		finally {
			progress.done();
		}
	}

	private void _breakingChangeCombSetting(String[] itemNames, String[] itemValues, String breakChangeValue) {
		_breakingChangeVersionComb.setItems(itemNames);

		for (int i = 0; i < itemValues.length; i++) {
			if (itemValues != null) {
				if (breakChangeValue.equals(breakChangeValue)) {
					_breakingChangeVersionComb.select(i);
				}
			}
		}

		_breakingChangeVersionComb.select(0);

		_breakingChangeVersionComb.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				String breakingChangeName = _breakingChangeVersionComb.getText();

				for (int i = 0; i < itemValues.length; i++) {
					if (breakingChangeName.equals(itemNames[i])) {
						dataModel.setBreakingChangeVersion(itemValues[i]);
					}
				}

				_startCheckThread();
			}

		});
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
			String projectPortableLocation = CoreUtil.getReourcePortableString(project);

			if (projectPortableLocation.startsWith(location.toPortableString())) {
				project.delete(false, true, monitor);
			}
		}
	}

	private boolean _configureProjectValidationExclude(IProject project, boolean disableValidation) {
		boolean retval = false;

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

	private void _copyNewSDK(IPath targetSDKLocation, IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);

		try {
			progress.beginTask("Copy new SDK to override target SDK.", 100);

			Bundle projectCoreBundle = Platform.getBundle("com.liferay.ide.project.ui");

			final URL sdkZipUrl = projectCoreBundle.getEntry("resources/sdk70ga2.zip");

			URL fileURL = FileLocator.toFileURL(sdkZipUrl);

			final File sdkZipFile = new File(fileURL.getFile());

			ProjectCore projectCore = ProjectCore.getDefault();

			final IPath stateLocation = projectCore.getStateLocation();

			File stateDir = stateLocation.toFile();

			progress.worked(30);

			ZipUtil.unzip(sdkZipFile, stateDir);

			progress.worked(60);

			IOUtil.copyDirToDir(new File(stateDir, "com.liferay.portal.plugins.sdk-7.0"), targetSDKLocation.toFile());

			progress.worked(100);
		}
		catch (Exception e) {
			ProjectUI.logError(e);

			throw new CoreException(StatusBridge.create(Status.createErrorStatus("Failed copy new SDK..", e)));
		}
		finally {
			progress.done();
		}
	}

	private void _createBundleControl() {
		_disposeServerEelment();

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

		String bundleName = SapphireUtil.getValue(dataModel.getBundleName());

		_bundleNameField.setText(bundleName != null ? bundleName : "");

		_bundleUrlLabel = createLabel(_pageParent, "Bundle URL:");

		_bundleUrlField = createTextField(_pageParent, SWT.NONE);

		_bundleUrlField.setForeground(pageDdisplay.getSystemColor(SWT.COLOR_DARK_GRAY));

		_bundleUrlField.setText(SapphireUtil.getValue(dataModel.getBundleUrl()));

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

					if (input.equals(LiferayUpgradeDataModel.DEFAULT_BUNDLE_URL)) {
						_bundleUrlField.setText("");
					}

					_bundleUrlField.setForeground(pageDdisplay.getSystemColor(SWT.COLOR_BLACK));
				}

				@Override
				public void focusLost(FocusEvent e) {
					String input = ((Text)e.getSource()).getText();

					if (CoreUtil.isNullOrEmpty(input)) {
						_bundleUrlField.setForeground(pageDdisplay.getSystemColor(SWT.COLOR_DARK_GRAY));
						_bundleUrlField.setText(LiferayUpgradeDataModel.DEFAULT_BUNDLE_URL);
					}
				}

			});

		dataModel.setBundleUrl(_bundleUrlField.getText());
	}

	private void _createDownloaBundleCheckboxElement() {
		_disposeBundleCheckboxElement();

		_downloadBundleCheckbox = SWTUtil.createCheckButton(
			_pageParent, "Download Liferay bundle (recommended)", null, true, 1);

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
					try {
						Boolean importFinished = SapphireUtil.getValue(dataModel.getImportFinished());

						if (_isPageValidate() && !importFinished) {
							_saveSettings();

							_importButton.setEnabled(false);

							importProject();

							UpgradeView.resetPages();

							PageNavigateEvent event = new PageNavigateEvent();

							if (UpgradeView.getPageNumber() < 3) {
								Boolean showAllPages = MessageDialog.openQuestion(
									UIUtil.getActiveShell(), "Show All Pages",
									"There is no project need to be upgraded.\n Show all the following steps?");

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
						}
					}
					catch (CoreException ce) {
						ProjectUI.logError(ce);

						PageValidateEvent pe = new PageValidateEvent();

						pe.setMessage(ce.getMessage());
						pe.setType(PageValidateEvent.error);

						triggerValidationEvent(pe);
					}
				}

			});
	}

	private void _createInitBundle(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);

		try {
			progress.beginTask("Execute Liferay Worksapce Bundle Init Command...", 100);

			String layout = SapphireUtil.getValue(dataModel.getLayout());

			if (layout.equals(_layoutNames[0])) {
				IPath sdkLocation = PathBridge.create(SapphireUtil.getValue(dataModel.getSdkLocation()));

				IProject project = CoreUtil.getProject(sdkLocation.lastSegment());

				String bundleUrl = SapphireUtil.getValue(dataModel.getBundleUrl());

				String bundleName = SapphireUtil.getValue(dataModel.getBundleName());

				IWorkspaceProjectBuilder projectBuilder = _getWorkspaceProjectBuilder(project);

				progress.worked(30);

				if ((bundleUrl != null) && (projectBuilder != null)) {
					projectBuilder.initBundle(project, bundleUrl, monitor);
				}

				IPath bundleLocationDir = sdkLocation.append("bundles");

				File bundleFile = bundleLocationDir.toFile();

				if (bundleFile.exists()) {
					progress.worked(60);

					final IPath runtimeLocation = sdkLocation.append(
						LiferayWorkspaceUtil.getHomeDir(sdkLocation.toOSString()));

					ServerUtil.addPortalRuntimeAndServer(bundleName, runtimeLocation, monitor);

					IServer bundleServer = ServerCore.findServer(SapphireUtil.getValue(dataModel.getBundleName()));

					if (bundleServer != null) {
						IPath newIpath = PathBridge.create(SapphireUtil.getValue(dataModel.getSdkLocation()));

						SDK sdk = SDKUtil.createSDKFromLocation(newIpath.append("plugins-sdk"));

						IRuntime runtime = bundleServer.getRuntime();

						IPath bundleLocation = runtime.getLocation();

						sdk.addOrUpdateServerProperties(bundleLocation);
					}

					project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				}
			}

			progress.worked(100);
		}
		catch (Exception e) {
			ProjectUI.logError(e);

			throw new CoreException(
				StatusBridge.create(
					Status.createErrorStatus("Failed to execute Liferay Workspace Bundle Init Command...", e)));
		}
		finally {
			progress.done();
		}
	}

	private void _createLiferayWorkspace(IPath targetSDKLocation, IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);

		try {
			progress.beginTask("Initializing Liferay Workspace...", 100);

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

			throw new CoreException(
				StatusBridge.create(
					Status.createErrorStatus("Faild execute Liferay Workspace Init Command...", bclie)));
		}
		finally {
			progress.done();
		}
	}

	private void _createMigrateBreakingChangeVersion() {
		_breakingChangeVersionLabel = createLabel(_pageParent, "Select Liferay Breaking Change Version: ");

		_breakingChangeVersionComb = new Combo(_pageParent, SWT.DROP_DOWN | SWT.READ_ONLY);

		_breakingChangeVersionComb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		String breakingChangeVersionValue = SapphireUtil.getValue(dataModel.getBreakingChangeVersion());

		Boolean inputIsLiferayWorkspaceElement = SapphireUtil.getValue(dataModel.getInputIsLiferayWorkspace());

		if (inputIsLiferayWorkspaceElement) {
			String[] itemNamesWorkspace = BreakingChangeVersion.getBreakingChangeVersionNames(true);

			String[] breakingChangeVersionsWorkspace = BreakingChangeVersion.getBreakingChangeVersionValues(true);

			_breakingChangeCombSetting(itemNamesWorkspace, breakingChangeVersionsWorkspace, breakingChangeVersionValue);
		}
		else {
			String[] itemNames = BreakingChangeVersion.getBreakingChangeVersionNames(false);

			String[] breakingChangeVersions = BreakingChangeVersion.getBreakingChangeVersionValues(false);

			_breakingChangeCombSetting(itemNames, breakingChangeVersions, breakingChangeVersionValue);
		}

		dataModel.setBreakingChangeVersion(_breakingChangeVersionComb.getText());
	}

	private void _createMigrateLayoutElement() {
		_layoutLabel = createLabel(_pageParent, "Select Migrate Layout:");
		_layoutComb = new Combo(_pageParent, SWT.DROP_DOWN | SWT.READ_ONLY);

		_layoutComb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_layoutComb.setItems(_layoutNames);

		String layoutValue = SapphireUtil.getValue(dataModel.getLayout());

		if (layoutValue != null) {
			for (int i = 0; i < _layoutNames.length; i++) {
				String item = _layoutNames[i];

				if (layoutValue.equals(item)) {
					_layoutComb.select(i);
				}
			}
		}
		else {
			_layoutComb.select(0);
		}

		_layoutComb.addSelectionListener(
			new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					int sel = _layoutComb.getSelectionIndex();

					if (sel == 1) {
						_createServerControl();

						dataModel.setDownloadBundle(false);
					}
					else {
						dataModel.setDownloadBundle(true);
						_createBundleControl();
					}

					dataModel.setLayout(_layoutComb.getText());

					_startCheckThread();
				}

			});

		dataModel.setLayout(_layoutComb.getText());
	}

	private void _createServerControl() {
		_disposeServerEelment();

		_disposeImportElement();

		_disposeBundleCheckboxElement();

		_disposeBundleElement();

		_createServerElement();

		_createImportElement();

		_pageParent.layout();
	}

	private void _createServerElement() {
		_serverLabel = createLabel(_pageParent, "Liferay Server Name:");

		_serverComb = new Combo(_pageParent, SWT.DROP_DOWN | SWT.READ_ONLY);

		_serverComb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		_serverButton = SWTUtil.createButton(_pageParent, "Add Server...");

		_serverButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					ServerUIUtil.showNewServerWizard(_pageParent.getShell(), "liferay.bundle", null, "com.liferay.");
				}

			});

		ServerCore.addServerLifecycleListener(this);

		IServer[] servers = ServerCore.getServers();
		List<String> serverNames = new ArrayList<>();

		if (ListUtil.isNotEmpty(servers)) {
			for (IServer server : servers) {
				IRuntime runtime = server.getRuntime();

				if (LiferayServerCore.newPortalBundle(runtime.getLocation()) != null) {
					serverNames.add(server.getName());
				}
			}
		}

		_serverComb.setItems(serverNames.toArray(new String[serverNames.size()]));
		_serverComb.select(0);
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

	private void _disposeBreakingChangeElement() {
		if ((_breakingChangeVersionLabel != null) && (_breakingChangeVersionComb != null)) {
			_breakingChangeVersionLabel.dispose();
			_breakingChangeVersionComb.dispose();
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

	private void _disposeMigrateLayoutElement() {
		if ((_layoutLabel != null) && (_layoutComb != null)) {
			_layoutLabel.dispose();
			_layoutComb.dispose();
		}
	}

	private void _disposeServerEelment() {
		if ((_serverLabel != null) && (_serverComb != null) && (_serverButton != null)) {
			_serverLabel.dispose();
			_serverComb.dispose();
			_serverButton.dispose();
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
			throw new CoreException(ProjectUI.createErrorStatus("Can't find Liferay workspace project."));
		}

		final IWorkspaceProjectBuilder builder = liferayProject.adapt(IWorkspaceProjectBuilder.class);

		if (builder == null) {
			throw new CoreException(ProjectUI.createErrorStatus("Can't find Liferay Gradle project builder."));
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

		File file = path.toFile();

		IContainer[] containers = workspaceRoot.findContainersForLocationURI(file.toURI());

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

	private boolean _isMavenProject(String path) {
		IStatus buildType = ImportLiferayModuleProjectOpMethods.getBuildType(path);

		return "maven".equals(buildType.getMessage());
	}

	private boolean _isPageValidate() {
		return _validationResult;
	}

	private void _refreshMigrateBreakingChangeVersion() {
		if ((_breakingChangeVersionLabel != null) && (_breakingChangeVersionComb != null) &&
			!_breakingChangeVersionComb.isDisposed()) {

			String[] itemNames = BreakingChangeVersion.getBreakingChangeVersionNames(false);
			String[] breakingChangeVersions = BreakingChangeVersion.getBreakingChangeVersionValues(false);
			_breakingChangeVersionComb.setItems(itemNames);

			Value<String> breakingChangeVersionElement = dataModel.getBreakingChangeVersion();

			String breakingChangeVersion = breakingChangeVersionElement.content();

			for (int i = 0; i < breakingChangeVersions.length; i++) {
				if (breakingChangeVersion != null) {
					if (breakingChangeVersion.equals(breakingChangeVersions[i])) {
						_breakingChangeVersionComb.select(i);

						return;
					}
				}
			}

			_breakingChangeVersionComb.select(0);
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

		if ((_serverComb != null) && !_serverComb.isDisposed()) {
			dataModel.setLiferay70ServerName(_serverComb.getText());
		}

		SDK sdk = SDKUtil.createSDKFromLocation(new Path(_dirField.getText()));

		try {
			if (sdk != null) {
				Map<String, Object> buildPropertiesMap = sdk.getBuildProperties(true);

				final String liferay62ServerLocation =
					(String)(buildPropertiesMap.get(ISDKConstants.PROPERTY_APP_SERVER_PARENT_DIR));

				dataModel.setLiferay62ServerLocation(liferay62ServerLocation);
			}
		}
		catch (Exception xe) {
			ProjectUI.logError(xe);
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

					boolean downloadBundle = SapphireUtil.getValue(dataModel.getDownloadBundle());

					String bundUrl = SapphireUtil.getValue(dataModel.getBundleUrl());

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

					if (!_layoutComb.isDisposed()) {
						if (_layoutComb.getSelectionIndex() == 1) {
							final int itemCount = _serverComb.getItemCount();

							if (itemCount < 1) {
								message = "You should add at least one Liferay 7 portal bundle.";

								layoutValidation = false;
							}
						}
						else if (_layoutComb.getSelectionIndex() == 0) {
							Status bundleUrlValidation = _bundleUrlValidation.compute();
							Status bundleNameValidation = _bundleNameValidation.compute();

							if (downloadBundle && !bundleNameValidation.ok()) {
								message = bundleUrlValidation.message();

								layoutValidation = false;
							}
							else if (downloadBundle && (bundUrl != null) && (bundUrl.length() > 0) &&
									 !bundleUrlValidation.ok()) {

								message = bundleUrlValidation.message();

								layoutValidation = false;
							}
							else {
								layoutValidation = true;
							}
						}
					}

					Boolean importFinished = SapphireUtil.getValue(dataModel.getImportFinished());

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

	private static Color _gray;

	private Composite _blankComposite;
	private String[] _breakingChangeNames = {"7.0_7.0_N", "7.1_7.0,7.1_N", "7.1_7.1_Y"};
	private Combo _breakingChangeVersionComb;
	private ComboViewer _breakingChangeVersionCombViewer;
	private Label _breakingChangeVersionLabel;
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
	private Text _dirField;
	private Label _dirLabel;
	private Button _downloadBundleCheckbox;
	private Button _importButton;
	private Combo _layoutComb;
	private Label _layoutLabel;
	private String[] _layoutNames = {"Upgrade to Liferay Workspace", "Upgrade to Liferay Plugins SDK 7"};
	private Composite _pageParent;

	private ProjectLocationValidationService _sdkValidation =
		dataModel.getSdkLocation().service(ProjectLocationValidationService.class);
	private Button _serverButton;
	private Combo _serverComb;
	private Label _serverLabel;
	private Button _showAllPagesButton;
	private boolean _validationResult;

	private class LiferayUpgradeValidationListener extends Listener {

		@Override
		public void handle(Event event) {
			if (!(event instanceof ValuePropertyContentEvent)) {
				return;
			}

			final Property property = ((ValuePropertyContentEvent)event).property();

			if ("BreakingChangeVersion".equals(property.name())) {
				_refreshMigrateBreakingChangeVersion();
			}

			if (!"SdkLocation".equals(property.name())) {
				_startCheckThread();
				_refreshMigrateBreakingChangeVersion();

				return;
			}

			org.eclipse.sapphire.modeling.Path path = SapphireUtil.getValue(dataModel.getSdkLocation());

			Status compute = _sdkValidation.compute();

			if ((path == null) || !compute.ok()) {
				if (!_layoutComb.isDisposed()) {
					_layoutComb.setEnabled(true);
				}

				dataModel.setInputIsLiferayWorkspace(false);
				_refreshMigrateBreakingChangeVersion();

				_startCheckThread();

				return;
			}

			if (_isAlreadyImported(PathBridge.create(path))) {
				_disposeBundleCheckboxElement();
				_disposeBundleElement();
				_disposeServerEelment();
				_disposeMigrateLayoutElement();
				_disposeBreakingChangeElement();

				if (LiferayWorkspaceUtil.isValidWorkspaceLocation(path.toPortableString())) {
					dataModel.setInputIsLiferayWorkspace(true);
				}
				else {
					dataModel.setInputIsLiferayWorkspace(false);
				}

				_importButton.setText("Continue");
				_pageParent.layout();
			}
			else if (LiferayWorkspaceUtil.isValidWorkspaceLocation(path.toPortableString())) {
				_disposeMigrateLayoutElement();
				_disposeBundleCheckboxElement();
				_disposeBundleElement();
				_disposeServerEelment();
				_disposeBreakingChangeElement();

				dataModel.setInputIsLiferayWorkspace(true);
				_pageParent.layout();
			}
			else if (_isMavenProject(path.toPortableString())) {
				_disposeBundleCheckboxElement();
				_disposeBundleElement();
				_disposeServerEelment();
				_disposeMigrateLayoutElement();
				_disposeBreakingChangeElement();
				_disposeImportElement();

				_createMigrateBreakingChangeVersion();
				_createImportElement();
				dataModel.setInputIsLiferayWorkspace(false);
				_pageParent.layout();
			}
			else {
				_disposeBreakingChangeElement();
				_disposeMigrateLayoutElement();
				_createMigrateBreakingChangeVersion();
				_createMigrateLayoutElement();
				_createBundleControl();

				dataModel.setInputIsLiferayWorkspace(false);
				_pageParent.layout();

				SDK sdk = SDKUtil.createSDKFromLocation(PathBridge.create(path));

				if (sdk != null) {
					String version = sdk.getVersion();

					if ((version != null) &&
						(CoreUtil.compareVersions(new Version(version), new Version("7.0.0")) >= 0)) {

						UIUtil.async(
							new Runnable() {

								@Override
								public void run() {
									if (_layoutComb.getSelectionIndex() != 0) {
										_layoutComb.select(1);
									}

									_layoutComb.setEnabled(false);

									dataModel.setLayout(_layoutComb.getText());
								}

							});
					}
					else {
						_layoutComb.setEnabled(true);
					}
				}
			}

			_startCheckThread();
		}

	}

}
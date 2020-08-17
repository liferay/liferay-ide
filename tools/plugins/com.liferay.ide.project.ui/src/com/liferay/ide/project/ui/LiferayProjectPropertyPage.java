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

package com.liferay.ide.project.ui;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.core.model.LiferayPluginSDKOp;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKCorePlugin;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.DialogDef;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

import org.osgi.framework.Version;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class LiferayProjectPropertyPage
	extends PropertyPage implements IPluginProjectDataModelProperties, IWorkbenchPropertyPage, SapphireContentAccessor {

	public LiferayProjectPropertyPage() {
		setImageDescriptor(ProjectUI.imageDescriptorFromPlugin(ProjectUI.PLUGIN_ID, "/icons/liferay_logo_16.png"));

		noDefaultAndApplyButton();
	}

	@Override
	public boolean performOk() {
		final IProject proj = getProject();

		if (SDKUtil.isSDKProject(proj)) {
			if (_runtimeCombo != null) {
				final String selectedRuntimeName = this._runtimeCombo.getText();

				if (!CoreUtil.isNullOrEmpty(selectedRuntimeName)) {
					final org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime =
						RuntimeManager.getRuntime(selectedRuntimeName);

					if (runtime != null) {
						final IFacetedProject fProject = ProjectUtil.getFacetedProject(getProject());

						if (!runtime.equals(fProject.getPrimaryRuntime())) {
							Job job = new WorkspaceJob("Setting targeted runtime for project.") {

								@Override
								public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
									IStatus retval = Status.OK_STATUS;

									try {
										fProject.setTargetedRuntimes(Collections.singleton(runtime), monitor);
										fProject.setPrimaryRuntime(runtime, monitor);
									}
									catch (Exception e) {
										retval = ProjectUI.createErrorStatus("Could not set targeted runtime", e);
									}

									return retval;
								}

							};

							job.schedule();
						}
					}
					else {
						return false;
					}
				}
			}

			final String sdkName = this._sdkLabel.getText();

			if (!CoreUtil.isNullOrEmpty(sdkName)) {
				try {
					final IEclipsePreferences prefs = new ProjectScope(
						getProject()
					).getNode(
						SDKCorePlugin.PLUGIN_ID
					);

					prefs.put(SDKCorePlugin.PREF_KEY_SDK_NAME, sdkName);
					prefs.flush();
				}
				catch (BackingStoreException bse) {
					ProjectCore.logError("Unable to persist sdk name to project " + getProject(), bse);
				}
			}
		}

		return true;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite top = SWTUtil.createTopComposite(parent, 3);

		createInfoGroup(top);

		return top;
	}

	protected Group createDefaultGroup(Composite parent, String text, int columns) {
		GridLayout gl = new GridLayout(columns, false);

		Group group = new Group(parent, SWT.NONE);

		group.setText(text);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setLayout(gl);

		return group;
	}

	protected void createInfoGroup(final Composite parent) {
		new Label(
			parent, SWT.LEFT
		).setText(
			Msgs.liferayPluginTypeLabel
		);

		Text pluginTypeLabel = new Text(parent, SWT.READ_ONLY | SWT.BORDER);

		pluginTypeLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet(getFacetedProject());

		if (liferayFacet != null) {
			pluginTypeLabel.setText(liferayFacet.getLabel());
		}

		IProject proj = getProject();

		if (ProjectUtil.isLiferayFacetedProject(proj)) {
			try {
				if (!ProjectUtil.isMavenProject(proj)) {
					final SDK projectSdk = SDKUtil.getSDK(getProject());
					new Label(
						parent, SWT.LEFT
					).setText(
						Msgs.liferaySdkLabel
					);

					_sdkLabel = new Text(parent, SWT.READ_ONLY | SWT.BORDER);

					if (projectSdk != null) {
						_sdkLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
						_sdkLabel.setText(projectSdk.getName());
					}
					else {
						_sdkLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
						_sdkLabel.setText("");

						final Hyperlink link = new Hyperlink(parent, SWT.NULL);

						Display display = parent.getDisplay();

						link.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
						link.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
						link.setUnderlined(true);
						link.setText(Msgs.changeliferaySdk);
						link.addHyperlinkListener(
							new HyperlinkAdapter() {

								public void linkActivated(HyperlinkEvent e) {
									String dialogId = new String(
										"com.liferay.ide.project.ui.dialog.SelectPluginsSDKDialog");

									Element instantiate = LiferayPluginSDKOp.TYPE.instantiate();

									LiferayPluginSDKOp op = instantiate.initialize();

									DefinitionLoader loader = DefinitionLoader.context(getClass());

									DefinitionLoader sdefLoader = loader.sdef(dialogId);

									DefinitionLoader.Reference<DialogDef> dialogRef = sdefLoader.dialog(
										"ConfigureLiferaySDK");

									SapphireDialog dialog = new SapphireDialog(UIUtil.getActiveShell(), op, dialogRef);

									dialog.setBlockOnOpen(true);

									int result = dialog.open();

									if (result != SapphireDialog.CANCEL) {
										_sdkLabel.setText(get(op.getPluginsSDKName()));
									}
								}

							});

						return;
					}

					if (CoreUtil.compareVersions(
							Version.parseVersion(projectSdk.getVersion()), ILiferayConstants.V700) < 0) {

						new Label(
							parent, SWT.LEFT
						).setText(
							Msgs.liferayRuntimeLabel
						);

						_runtimeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

						_runtimeCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

						String currentRuntimeName = null;

						try {
							ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(getProject());

							if (liferayRuntime != null) {
								IRuntime runtime = liferayRuntime.getRuntime();

								currentRuntimeName = runtime.getName();
							}
						}
						catch (Exception e) {
							ProjectUI.logError("Could not determine liferay runtime", e);
						}

						List<String> runtimeNames = new ArrayList<>();

						int selectionIndex = -1;

						for (IRuntime runtime : ServerCore.getRuntimes()) {
							if (ServerUtil.isLiferayRuntime(runtime) && FileUtil.exists(runtime.getLocation()) &&
								(LiferayServerCore.newPortalBundle(runtime.getLocation()) == null)) {

								runtimeNames.add(runtime.getName());

								if (StringUtil.equals(currentRuntimeName, runtime.getName())) {
									selectionIndex = runtimeNames.size() - 1;
								}
							}
						}

						if (ListUtil.isEmpty(runtimeNames)) {
							runtimeNames.add("No Liferay runtimes available.");
						}

						_runtimeCombo.setItems(runtimeNames.toArray(new String[0]));

						if (selectionIndex > -1) {
							_runtimeCombo.select(selectionIndex);
						}
					}
				}
			}
			catch (Exception e) {
				ProjectUI.logError("Could not determine whether its a maven project ", e);
			}
		}
	}

	protected IFacetedProject getFacetedProject() {
		IFacetedProject retval = null;

		IProject project = getProject();

		if (project != null) {
			retval = ProjectUtil.getFacetedProject(project);
		}

		return retval;
	}

	protected IProject getProject() {
		IAdaptable adaptable = getElement();

		return adaptable.getAdapter(IProject.class);
	}

	private Combo _runtimeCombo;
	private Text _sdkLabel;

	private static class Msgs extends NLS {

		public static String changeliferaySdk;
		public static String liferayPluginTypeLabel;
		public static String liferayRuntimeLabel;
		public static String liferaySdkLabel;

		static {
			initializeMessages(LiferayProjectPropertyPage.class.getName(), Msgs.class);
		}

	}

}
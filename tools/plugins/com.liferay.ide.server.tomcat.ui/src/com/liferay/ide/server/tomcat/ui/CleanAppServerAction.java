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

package com.liferay.ide.server.tomcat.ui;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.tomcat.core.LiferayTomcatPlugin;
import com.liferay.ide.server.tomcat.core.job.CleanAppServerJob;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.action.AbstractObjectAction;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.TaskWizard;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class CleanAppServerAction extends AbstractObjectAction {

	public CleanAppServerAction() {
	}

	public void run(IAction action) {
		try {
			if (!(fSelection instanceof IStructuredSelection)) {
				return;
			}

			Object elem = ((IStructuredSelection)fSelection).toArray()[0];

			if (!(elem instanceof IProject)) {
				return;
			}

			IProject project = (IProject)elem;

			SDK sdk = SDKUtil.getSDK(project);

			if (sdk == null) {
				return;
			}

			IStatus status = sdk.validate();

			if (!status.isOK()) {
				MessageDialog.openError(null, Msgs.cleanAppServer, status.getChildren()[0].getMessage());
				return;
			}

			Map<String, Object> sdkProperties = sdk.getBuildProperties();

			String bundleZipLocation = (String)sdkProperties.get("app.server.zip.name");

			status = validate(project, bundleZipLocation);

			if (status.isOK()) {
				cleanAppServer(project, bundleZipLocation);
			}
			else {
				MessageDialog.openError(null, Msgs.cleanAppServer, status.getMessage());
				return;
			}
		}
		catch (Exception ex) {
			ProjectUI.logError(ex);
		}
	}

	protected void cleanAppServer(IProject project, String bundleZipLocation) throws CoreException {
		String[] labels = {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL};

		MessageDialog dialog = new MessageDialog(
			getDisplay().getActiveShell(), getTitle(), null, Msgs.deleteEntireTomcatDirectory, MessageDialog.WARNING,
			labels, 1);

		int retval = dialog.open();

		if (retval == MessageDialog.OK) {
			new CleanAppServerJob(project, bundleZipLocation).schedule();
		}
	}

	protected void editRuntime(IRuntime runtime) {
		IRuntimeWorkingCopy runtimeWorkingCopy = runtime.createWorkingCopy();

		if (showWizard(runtimeWorkingCopy) != Window.CANCEL) {
			try {
				runtimeWorkingCopy.save(false, null);
			}
			catch (Exception ex) {
			}
		}
	}

	protected String getTitle() {
		return Msgs.cleanAppServer;
	}

	protected int showWizard(IRuntimeWorkingCopy runtimeWorkingCopy) {
		String title = Msgs.wizEditRuntimeWizardTitle;
		WizardFragment fragment2 = ServerUIPlugin.getWizardFragment(runtimeWorkingCopy.getRuntimeType().getId());

		if (fragment2 == null) {
			return Window.CANCEL;
		}

		TaskModel taskModel = new TaskModel();

		taskModel.putObject(TaskModel.TASK_RUNTIME, runtimeWorkingCopy);

		WizardFragment fragment = new WizardFragment() {

			protected void createChildFragments(List<WizardFragment> list) {
				list.add((WizardFragment)fragment2.getChildFragments().get(0));
				list.add(WizardTaskUtil.SaveRuntimeFragment);
			}

		};

		TaskWizard wizard = new TaskWizard(title, fragment, taskModel);

		wizard.setForcePreviousAndNextButtons(true);
		WizardDialog dialog = new WizardDialog(getDisplay().getActiveShell(), wizard);

		return dialog.open();
	}

	protected IStatus validate(IProject project, String bundleZipLocation) throws CoreException {
		IStatus result = Status.OK_STATUS;

		if (bundleZipLocation == null) {
			return result = LiferayTomcatPlugin.createErrorStatus(Msgs.bundleZipNotdefined);
		}
		else {
			String rootEntryName = null;

			try (InputStream input = Files.newInputStream(Paths.get(bundleZipLocation));
					ZipInputStream zis = new ZipInputStream(input)) {

				ZipEntry rootEntry = zis.getNextEntry();

				rootEntryName = new Path(rootEntry.getName()).segment(0);

				if (rootEntryName.endsWith(StringPool.FORWARD_SLASH)) {
					rootEntryName = rootEntryName.substring(0, rootEntryName.length() - 1);
				}

				boolean foundBundle = false;

				ZipEntry entry = zis.getNextEntry();

				while ((entry != null) && !foundBundle) {
					String entryName = entry.getName();

					if (entryName.startsWith(rootEntryName + "/tomcat-") ||
						entryName.startsWith(rootEntryName + "/jboss-")) {

						foundBundle = true;
					}

					entry = zis.getNextEntry();
				};
			}
			catch (Exception e) {
				return result = LiferayTomcatPlugin.createErrorStatus(Msgs.bundleZipLocationNotValid);
			}

			IPath appServerDir = ServerUtil.getPortalBundle(project).getAppServerDir();

			String bundleDir = appServerDir.removeLastSegments(1).lastSegment();

			if (!bundleDir.equals(rootEntryName)) {
				return result = LiferayTomcatPlugin.createErrorStatus(
					NLS.bind(Msgs.runtimeLocationDirectoryNotMatch, bundleDir, rootEntryName));
			}
		}

		return result;
	}

	private static class Msgs extends NLS {

		public static String bundleZipLocationNotValid;
		public static String bundleZipNotdefined;
		public static String cleanAppServer;
		public static String deleteEntireTomcatDirectory;
		public static String runtimeLocationDirectoryNotMatch;
		public static String wizEditRuntimeWizardTitle;

		static {
			initializeMessages(CleanAppServerAction.class.getName(), Msgs.class);
		}

	}

}
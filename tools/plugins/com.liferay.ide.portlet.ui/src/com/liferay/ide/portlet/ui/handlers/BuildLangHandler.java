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

package com.liferay.ide.portlet.ui.handlers;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.job.BuildLanguageJob;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.ui.util.UIUtil;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class BuildLangHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStatus retval = null;
		IProject project = null;
		IFile langFile = null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)selection;

			Object selected = structuredSelection.getFirstElement();

			if (selected instanceof IResource) {
				project = ((IResource)selected).getProject();
			}
			else if (selected instanceof IJavaElement) {
				project = ((IJavaElement)selected).getJavaProject().getProject();
			}
			else if (selected instanceof PackageFragmentRootContainer) {
				project = ((PackageFragmentRootContainer)selected).getJavaProject().getProject();
			}
		}

		if (project == null) {
			IEditorInput activeInput = HandlerUtil.getActiveEditorInput(event);

			IFile file = (IFile)activeInput.getAdapter(IFile.class);

			if (file != null) {
				project = file.getProject();
			}
		}

		if (project != null) {
			List<IFolder> srcFolders = CoreUtil.getSourceFolders(JavaCore.create(project));

			for (IFolder src : srcFolders) {
				IFile file = src.getFile("content/Language.properties");

				if (file.exists()) {
					langFile = file;
					break;
				}
			}

			if ((langFile != null) && langFile.exists()) {
				try {
					boolean shouldContinue = checkLanguageFileEncoding(langFile);

					if (!shouldContinue) {
						retval = Status.OK_STATUS;
					}

					BuildLanguageJob job = PortletCore.createBuildLanguageJob(langFile);

					job.schedule();
				}
				catch (Exception e2) {
					retval = PortletUIPlugin.createErrorStatus(e2);
				}
			}
		}

		return retval;
	}

	protected boolean checkLanguageFileEncoding(IFile langFile) throws CoreException {
		IProgressMonitor monitor = new NullProgressMonitor();

		try {
			langFile.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch (Exception e) {
			PortletUIPlugin.logError(e);
		}

		String charset = langFile.getCharset(true);

		if (!"UTF-8".equals(charset)) {
			String dialogMessage = NLS.bind(Msgs.languageFileCharacterSet, charset);

			Display display = UIUtil.getActiveShell().getDisplay();

			MessageDialog dialog = new MessageDialog(
				UIUtil.getActiveShell(), Msgs.incompatibleCharacterSet, display.getSystemImage(SWT.ICON_WARNING),
				dialogMessage, MessageDialog.WARNING, new String[] {Msgs.yes, Msgs.no, Msgs.cancel}, 0);

			int retval = dialog.open();

			if (retval == 0) {
				langFile.setCharset("UTF-8", monitor);

				String question = NLS.bind(Msgs.forcedEditFile, langFile.getName());

				if (MessageDialog.openQuestion(UIUtil.getActiveShell(), Msgs.previewFile, question)) {
					IWorkbench workbench = PlatformUI.getWorkbench();

					IDE.openEditor(workbench.getActiveWorkbenchWindow().getActivePage(), langFile);
				}

				return false;
			}
			else if (retval == 2) {
				return false;
			}
		}

		return true;
	}

	private static class Msgs extends NLS {

		public static String cancel;
		public static String forcedEditFile;
		public static String incompatibleCharacterSet;
		public static String languageFileCharacterSet;
		public static String no;
		public static String previewFile;
		public static String yes;

		static {
			initializeMessages(BuildLangHandler.class.getName(), Msgs.class);
		}

	}

}
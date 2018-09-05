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

import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.ui.migration.OpenJavaProjectSelectionDialogAction;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class CompileAction extends OpenJavaProjectSelectionDialogAction {

	public static IConsole getConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();

		IConsoleManager conMan = plugin.getConsoleManager();

		IConsole[] existing = conMan.getConsoles();

		for (IConsole console : existing) {
			if (StringUtil.contains(console.getName(), name)) {
				return console;
			}
		}

		return null;
	}

	public CompileAction(String text, Shell shell) {
		super(text, shell);
	}

	@Override
	public void run() {
		final ISelection selection = getSelectionProjects();

		if ((selection != null) && selection instanceof IStructuredSelection) {
			Object[] projects = ((IStructuredSelection)selection).toArray();

			try {
				SDK sdk = SDKUtil.getWorkspaceSDK();

				IProgressService progressService = UIUtil.getProgressService();

				progressService.busyCursorWhile(
					new IRunnableWithProgress() {

						public void run(IProgressMonitor monitor)
							throws InterruptedException, InvocationTargetException {

							for (Object project : projects) {
								if (project instanceof IProject) {
									IProject p = (IProject)project;

									sdk.war(p, null, false, monitor);

									IDocument document = ((ProcessConsole)getConsole(p.getName())).getDocument();

									if (StringUtil.contains(document.get(), "BUILD FAILED")) {
										return;
									}
								}
							}
						}

					});
			}
			catch (Exception e) {
			}
		}
	}

}
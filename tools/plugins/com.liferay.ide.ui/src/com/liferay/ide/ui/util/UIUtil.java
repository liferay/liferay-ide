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

package com.liferay.ide.ui.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.navigator.NavigatorContentService;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.osgi.framework.Bundle;

import com.liferay.ide.ui.LiferayUIPlugin;

/**
 * @author Greg Amerson
 * @author Cindy Li
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class UIUtil {

	public static void async(Runnable runnable) {
		if (runnable != null) {
			try {
				Display.getDefault().asyncExec(runnable);
			}
			catch (Throwable t) {

				// ignore

			}
		}
	}

	public static void async(Runnable runnable, long delay) {
		Runnable delayer = new Runnable() {

			public void run() {
				try {
					Thread.sleep(delay);
				}
				catch (InterruptedException ie) {
				}

				async(runnable);
			}

		};

		async(delayer);
	}

	public static void executeCommand(String commandId, ISelection selection, Map<String, Object> parameters)
		throws ExecutionException, NotDefinedException, NotEnabledException,
			NotHandledException {

		IEvaluationContext evaluationContext = new EvaluationContext(null, Collections.emptyList());

		evaluationContext.addVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME, selection);

		IWorkbench workbench = PlatformUI.getWorkbench();

		ICommandService commandService = (ICommandService)workbench.getService(ICommandService.class);

		Command migrate = commandService.getCommand(commandId);

		IHandlerService handlerService = (IHandlerService)workbench.getService(IHandlerService.class);

		if (parameters!=null) {
			parameters.keySet().stream().forEach( parma -> evaluationContext.addVariable(parma, parameters.get(parma)));
		}

		handlerService.executeCommandInContext(
			ParameterizedCommand.generateCommand(migrate, null), null, evaluationContext);
	}

	public static IViewPart findView(String viewId) {
		IWorkbench workbench = PlatformUI.getWorkbench();

		for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
			for (IWorkbenchPage page : window.getPages()) {
				IViewPart view = page.findView(viewId);

				if (view != null) {
					return view;
				}
			}
		}

		return null;
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbench workbench = PlatformUI.getWorkbench();

		return workbench.getActiveWorkbenchWindow().getActivePage();
	}

	public static Shell getActiveShell() {
		Shell[] retval = new Shell[1];

		Display.getDefault().syncExec(
			new Runnable() {

				public void run() {
					retval[0] = Display.getDefault().getActiveShell();
				}

			});

		return retval[0];
	}

	public static ImageDescriptor getPluginImageDescriptor(String symbolicName, String imagePath) {
		Bundle bundle = Platform.getBundle(symbolicName);

		if (bundle != null) {
			URL entry = bundle.getEntry(imagePath);

			if (entry != null) {
				return ImageDescriptor.createFromURL(entry);
			}
		}

		return null;
	}

	public static void postInfo(String title, String msg) {
		Display.getDefault().asyncExec(
			new Runnable() {

				public void run() {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), title, msg);
				}

			});
	}

	public static void postInfoWithToggle(
		String title, String msg, String toggleMessage, boolean toggleState, IPersistentPreferenceStore store,
		String key) {

		if ((store == null) || (key == null) || store.getString(key).equals(MessageDialogWithToggle.NEVER)) {
			return;
		}

		Display.getDefault().asyncExec(
			new Runnable() {

				public void run() {
					MessageDialogWithToggle dialog = MessageDialogWithToggle.openInformation(
						Display.getDefault().getActiveShell(), title, msg, toggleMessage, toggleState, store, key);

					try {
						if (dialog.getToggleState()) {
							store.setValue(key, MessageDialogWithToggle.NEVER);
							store.save();
						}
					}
					catch (IOException ioe) {
					}
				}

			});
	}

	public static boolean promptQuestion(String title, String message) {
		boolean[] retval = new boolean[1];

		Display.getDefault().syncExec(
			new Runnable() {

				public void run() {
					retval[0] = MessageDialog.openQuestion(getActiveShell(), title, message);
				}

			});

		return retval[0];
	}

	public static void refreshCommonView(String viewId) {
		try {
			async(
				new Runnable() {

					public void run() {
						IViewPart viewPart = showView(viewId);

						if (viewPart != null) {
							CommonViewer viewer = (CommonViewer)viewPart.getAdapter(CommonViewer.class);

							viewer.refresh(true);
						}
					}

				});
		}
		catch (Exception e) {
			LiferayUIPlugin.logError("Unable to refresh view " + viewId, e);
		}
	}

	public static void refreshContent(ICommonContentExtensionSite site, Object elementOrTreePath) {
		NavigatorContentService s = (NavigatorContentService)site.getService();

		sync(
			new Runnable() {

				public void run() {
					try {
						CommonViewer viewer = (CommonViewer)s.getViewer();

						viewer.refresh(true);
						viewer.setExpandedState(elementOrTreePath, true);
					}
					catch (Exception e) {
					}
				}

			});
	}

	public static IViewPart showView(String viewId) {
		try {
			IWorkbench workbench = PlatformUI.getWorkbench();

			return workbench.getWorkbenchWindows()[0].getActivePage().showView(viewId);
		}
		catch (PartInitException pie) {
			LiferayUIPlugin.logError(pie);
		}

		return null;
	}

	public static void switchToLiferayPerspective(String perspectiveId, boolean confirm) {

		// Retrieve the new project open perspective preference setting

		String perspSetting = PrefUtil.getAPIPreferenceStore().getString(IDE.Preferences.PROJECT_OPEN_NEW_PERSPECTIVE);

		IDEWorkbenchPlugin plugin = IDEWorkbenchPlugin.getDefault();

		String promptSetting = plugin.getPreferenceStore().getString(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE);

		// Return if do not switch perspective setting and are not prompting

		if (!(promptSetting.equals(MessageDialogWithToggle.PROMPT)) &&
			perspSetting.equals(IWorkbenchPreferenceConstants.NO_NEW_PERSPECTIVE)) {

			return;
		}

		// Map perspective id to descriptor.

		IWorkbench workbench = PlatformUI.getWorkbench();

		IPerspectiveRegistry reg = workbench.getPerspectiveRegistry();

		IPerspectiveDescriptor finalPersp = reg.findPerspectiveWithId(perspectiveId);

		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

		if (window != null) {
			IWorkbenchPage page = window.getActivePage();

			if (page != null) {
				IPerspectiveDescriptor currentPersp = page.getPerspective();

				// don't switch if the current perspective is the Liferay
				// perspective

				if (finalPersp.equals(currentPersp)) {
					return;
				}
			}

			// prompt the user to switch

			if (confirm && !_confirmPerspectiveSwitch(window, finalPersp)) {
				return;
			}
		}

		// replace active perspective setting otherwise

		_replaceCurrentPerspective(finalPersp);
	}

	public static void sync(Runnable runnable) {
		if (runnable != null) {
			try {
				Display.getDefault().syncExec(runnable);
			}
			catch (Throwable t) {

				// ignore

			}
		}
	}

	private static boolean _confirmPerspectiveSwitch(IWorkbenchWindow window, IPerspectiveDescriptor finalPersp) {
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();

		String pspm = store.getString(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE);

		if (!IDEInternalPreferences.PSPM_PROMPT.equals(pspm)) {

			// Return whether or not we should always switch

			return IDEInternalPreferences.PSPM_ALWAYS.equals(pspm);
		}

		String desc = finalPersp.getDescription();
		String message;

		if ((desc == null) || (desc.length() == 0)) {
			message = NLS.bind(ResourceMessages.NewProject_perspSwitchMessage, finalPersp.getLabel());
		}
		else {
			message = NLS.bind(
				ResourceMessages.NewProject_perspSwitchMessageWithDesc, new String[] {finalPersp.getLabel(), desc});
		}

		MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(
			window.getShell(), ResourceMessages.NewProject_perspSwitchTitle, message, null, false, store,
			IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE);

		int result = dialog.getReturnCode();

		// If we are not going to prompt anymore propogate the choice.

		if (dialog.getToggleState()) {
			String preferenceValue;

			if (result == IDialogConstants.YES_ID) {

				// Doesn't matter if it is replace or new window
				// as we are going to use the open perspective setting

				preferenceValue = IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_REPLACE;
			}
			else {
				preferenceValue = IWorkbenchPreferenceConstants.NO_NEW_PERSPECTIVE;
			}

			// update PROJECT_OPEN_NEW_PERSPECTIVE to correspond

			PrefUtil.getAPIPreferenceStore().setValue(IDE.Preferences.PROJECT_OPEN_NEW_PERSPECTIVE, preferenceValue);
		}

		if (result == IDialogConstants.YES_ID) {
			return true;
		}

		return false;
	}

	private static void _replaceCurrentPerspective(IPerspectiveDescriptor persp) {

		// Get the active page.

		IWorkbench workbench = PlatformUI.getWorkbench();

		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

		if (window != null) {
			IWorkbenchPage page = window.getActivePage();

			if (page != null) {

				// Set the perspective.

				page.setPerspective(persp);
			}
		}
	}

}
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonFilterDescriptor;
import org.eclipse.ui.navigator.INavigatorActivationService;
import org.eclipse.ui.navigator.INavigatorContentService;
import org.eclipse.ui.navigator.INavigatorFilterService;

/**
 * @author Andy Wu
 */
public class ProjectExplorerLayoutUtil {

	public static void setNested(boolean nested) {
		String commandId = "org.eclipse.ui.navigator.resources.nested.changeProjectPresentation";

		try {
			IWorkbench workbench = PlatformUI.getWorkbench();

			ICommandService commandService = (ICommandService)workbench.getService(ICommandService.class);

			Command command = commandService.getCommand(commandId);

			IHandler hanlder = command.getHandler();

			IWorkbenchPage page = UIUtil.getActivePage();

			IViewPart projectExplorer = page.findView(IPageLayout.ID_PROJECT_EXPLORER);

			if ((hanlder != null) && (projectExplorer != null)) {
				Map<String, String> map = new HashMap<>();

				map.put(_nestParameter, Boolean.toString(nested));

				IEvaluationContext applicationContext = new EvaluationContext(null, new Object());

				applicationContext.addVariable(ISources.ACTIVE_PART_NAME, projectExplorer);

				ExecutionEvent event = new ExecutionEvent(command, map, null, applicationContext);

				_execute(event);
			}
		}
		catch (ExecutionException ee) {

			// ignore errors this is best effort.

		}
	}

	private static void _execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);

		String nestedProjectsContentProviderExtensionId =
			"org.eclipse.ui.navigator.resources.nested.nestedProjectContentProvider";

		String hideTopLevelProjectIfNested = "org.eclipse.ui.navigator.resources.nested.HideTopLevelProjectIfNested";

		String hideFolderWhenProjectIsShownAsNested =
			"org.eclipse.ui.navigator.resources.nested.HideFolderWhenProjectIsShownAsNested";

		if (part instanceof CommonNavigator) {
			CommonNavigator navigator = (CommonNavigator)part;

			INavigatorContentService navigatorContentService = navigator.getNavigatorContentService();

			INavigatorActivationService activationService = navigatorContentService.getActivationService();

			boolean previousNest = activationService.isNavigatorExtensionActive(
				nestedProjectsContentProviderExtensionId);

			String newNestParam = event.getParameter(_nestParameter);
			boolean newNest = false;

			if (newNestParam != null) {
				newNest = Boolean.parseBoolean(newNestParam);
			}

			if (newNest != previousNest) {
				CommonViewer commonViewer = navigator.getCommonViewer();

				ISelection initialSelection = commonViewer.getSelection();

				INavigatorFilterService filterService = navigatorContentService.getFilterService();
				Set<String> filters = new HashSet<>();

				for (ICommonFilterDescriptor desc : filterService.getVisibleFilterDescriptors()) {
					if (filterService.isActive(desc.getId())) {
						filters.add(desc.getId());
					}
				}

				if (newNest) {
					activationService.activateExtensions(
						new String[] {nestedProjectsContentProviderExtensionId}, false);
					filters.add(hideTopLevelProjectIfNested);
					filters.add(hideFolderWhenProjectIsShownAsNested);
				}
				else {
					activationService.deactivateExtensions(
						new String[] {nestedProjectsContentProviderExtensionId}, false);
					filters.remove(hideTopLevelProjectIfNested);
					filters.remove(hideFolderWhenProjectIsShownAsNested);
				}

				filterService.activateFilterIdsAndUpdateViewer(filters.toArray(new String[filters.size()]));

				activationService.persistExtensionActivations();

				commonViewer.refresh();
				commonViewer.setSelection(initialSelection);
			}

			HandlerUtil.updateRadioState(event.getCommand(), Boolean.toString(newNest));
		}
	}

	// copy from
	// org.eclipse.ui.internal.navigator.resources.nested.ProjectPresentationHandler
	// (mars)

	private static String _nestParameter = "org.eclipse.ui.navigator.resources.nested.enabled";

}
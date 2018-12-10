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

package com.liferay.ide.upgrade.task.problem.ui;

import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.task.problem.api.FileProblems;
import com.liferay.ide.upgrade.task.problem.api.Problem;
import com.liferay.ide.upgrade.task.problem.ui.util.MigrationUtil;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Terry Jia
 */
public class UpgradeProblemUI extends AbstractUIPlugin {

	public static final IPath ICONS_PATH = new Path("$nl$/icons/");

	public static final String PLUGIN_ID = "com.liferay.ide.upgrade.task.problem.ui";

	public static final String T_ELCL = "elcl16/";

	public static final String T_OBJ = "obj16/";

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(msg, null);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static ImageDescriptor createImageDescriptor(Bundle bundle, IPath path) {
		URL url = FileLocator.find(bundle, path, null);

		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}

		return null;
	}

	public static UpgradeProblemUI getDefault() {
		return _plugin;
	}

	public static Bundle getDefaultBundle() {
		return _plugin.getBundle();
	}

	public static void logError(Exception e) {
		ILog log = _plugin.getLog();

		log.log(createErrorStatus(e.getMessage(), e));
	}

	public static void logError(String msg, Exception e) {
		ILog log = _plugin.getLog();

		log.log(createErrorStatus(msg, e));
	}

	public Image getImage(String key) {
		Image image = getImageRegistry().get(key);

		return image;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;

		IViewPart projectExplorer = UIUtil.findView("org.eclipse.ui.navigator.ProjectExplorer");

		if (projectExplorer != null) {
			CommonNavigator navigator = (CommonNavigator)projectExplorer;

			CommonViewer commonViewer = navigator.getCommonViewer();

			_doubleClickListener = new IDoubleClickListener() {

				@Override
				public void doubleClick(DoubleClickEvent event) {
					ISelection selection = event.getSelection();

					if (selection instanceof TreeSelection) {
						TreeSelection treeSelection = (TreeSelection)selection;

						Object element = treeSelection.getFirstElement();

						if (element instanceof Problem) {
							MigrationUtil.openEditor((Problem)element);
						}
						else if (element instanceof FileProblems) {
							MigrationUtil.openEditor((FileProblems)element);
						}
					}
				}

			};

			commonViewer.addDoubleClickListener(_doubleClickListener);
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;

		IViewPart projectExplorer = UIUtil.findView("org.eclipse.ui.navigator.ProjectExplorer");

		if (projectExplorer != null) {
			CommonNavigator navigator = (CommonNavigator)projectExplorer;

			CommonViewer commonViewer = navigator.getCommonViewer();

			commonViewer.removeDoubleClickListener(_doubleClickListener);

			_doubleClickListener = null;
		}

		super.stop(context);
	}

	private static UpgradeProblemUI _plugin;

	private IDoubleClickListener _doubleClickListener;

}
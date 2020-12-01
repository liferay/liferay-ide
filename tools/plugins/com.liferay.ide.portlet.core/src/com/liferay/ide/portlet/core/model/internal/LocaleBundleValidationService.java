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

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.core.model.internal.GenericResourceBundlePathService;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.SupportedLocales;
import com.liferay.ide.portlet.core.util.PortletUtil;

import org.apache.commons.lang.StringEscapeUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Kamesh Sampath
 * @author Kuo Zhang
 */
public class LocaleBundleValidationService extends ValidationService implements SapphireContentAccessor {

	public Status compute() {
		Element modelElement = context(Element.class);

		if (!modelElement.disposed() && (modelElement instanceof SupportedLocales)) {
			IProject project = modelElement.adapt(IProject.class);

			IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries(project);

			if (cpEntries == null) {
				return Status.createOkStatus();
			}

			String locale = getText(modelElement.property(context(ValueProperty.class)), false);

			if (locale == null) {
				return Status.createErrorStatus(Resources.localeMustNotEmpty);
			}

			Portlet portlet = modelElement.nearest(Portlet.class);

			IWorkspaceRoot wroot = CoreUtil.getWorkspaceRoot();

			Value<Path> resourceBundle = portlet.getResourceBundle();

			String bundleName = resourceBundle.text();

			if ((resourceBundle != null) && (bundleName != null)) {
				String localeString = PortletUtil.localeString(locale);

				String ioFileName = PortletUtil.convertJavaToIoFileName(
					bundleName, GenericResourceBundlePathService.RB_FILE_EXTENSION, localeString);

				for (IClasspathEntry iClasspathEntry : cpEntries) {
					if (IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind()) {
						IFolder folder = wroot.getFolder(iClasspathEntry.getPath());

						IPath entryPath = folder.getLocation();

						entryPath = entryPath.append(ioFileName);

						IFile resourceBundleFile = wroot.getFileForLocation(entryPath);

						if (FileUtil.exists(resourceBundleFile)) {
							return Status.createOkStatus();
						}

						Object[] objects = {locale, bundleName, localeString};

						return Status.createWarningStatus(
							Resources.bind(StringEscapeUtils.unescapeJava(Resources.noResourceBundle), objects));
					}
				}
			}
		}

		return Status.createOkStatus();
	}

	@Override
	public void dispose() {
		try {
			SupportedLocales supportedLocales = context(SupportedLocales.class);

			Portlet portlet = supportedLocales.nearest(Portlet.class);

			SapphireUtil.detachListener(portlet.getResourceBundle(), _listener);
		}
		catch (Exception ex) {
		}
	}

	public void forceRefresh() {
		SupportedLocales supportedLocales = context(SupportedLocales.class);

		if (!supportedLocales.disposed()) {
			refresh();
		}
	}

	/**
	 * IDE-1132 ,Add a listener to Property ResourceBundle, so LocaleBundle can be
	 * validated once ResourceBundle gets changed.
	 */
	protected void initValidationService() {
		SupportedLocales supportedLocales = context(SupportedLocales.class);

		_listener = new FilteredListener<PropertyContentEvent>() {

			protected void handleTypedEvent(PropertyContentEvent event) {
				if (!supportedLocales.disposed()) {
					refresh();
				}
			}

		};

		Portlet portlet = supportedLocales.nearest(Portlet.class);

		SapphireUtil.attachListener(portlet.getResourceBundle(), _listener);
	}

	private FilteredListener<PropertyContentEvent> _listener;

	private static final class Resources extends NLS {

		public static String localeMustNotEmpty;
		public static String noResourceBundle;

		static {
			initializeMessages(LocaleBundleValidationService.class.getName(), Resources.class);
		}

	}

}
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

import static com.liferay.ide.core.model.internal.GenericResourceBundlePathService.RB_FILE_EXTENSION;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.SupportedLocales;
import com.liferay.ide.portlet.core.util.PortletUtil;

import org.apache.commons.lang.StringEscapeUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
public class LocaleBundleValidationService extends ValidationService {

	public Status compute() {
		Element modelElement = context(Element.class);

		if (!modelElement.disposed() && modelElement instanceof SupportedLocales) {
			IProject project = modelElement.adapt(IProject.class);
			Portlet portlet = modelElement.nearest(Portlet.class);
			IWorkspaceRoot wroot = ResourcesPlugin.getWorkspace().getRoot();
			IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries(project);

			if (cpEntries != null) {
				String locale = modelElement.property(context(ValueProperty.class)).text(false);
				Value<Path> resourceBundle = portlet.getResourceBundle();

				if (locale == null) {
					return Status.createErrorStatus(Resources.localeMustNotEmpty);
				}
				else {
					String bundleName = resourceBundle.text();

					if ((resourceBundle != null) && (bundleName != null)) {
						String localeString = PortletUtil.localeString(locale);

						String ioFileName = PortletUtil.convertJavaToIoFileName(
							bundleName, RB_FILE_EXTENSION, localeString);

						for (IClasspathEntry iClasspathEntry : cpEntries) {
							if (IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind()) {
								IPath entryPath = wroot.getFolder(iClasspathEntry.getPath()).getLocation();

								entryPath = entryPath.append(ioFileName);

								IFile resourceBundleFile = wroot.getFileForLocation(entryPath);

								if ((resourceBundleFile != null) && resourceBundleFile.exists()) {
									return Status.createOkStatus();
								}
								else {
									return Status
										.createWarningStatus(
											Resources.bind(StringEscapeUtils.unescapeJava(Resources.noResourceBundle), new Object[] {
												locale, bundleName, localeString
											}));
								}
							}
						}
					}
				}
			}
		}

		return Status.createOkStatus();
	}

	@Override
	public void dispose() {
		try {
			context(SupportedLocales.class).nearest(Portlet.class).getResourceBundle().detach(this.listener);
		}
		catch (Exception ex) {
		}
			}

	public void forceRefresh() {
		if (!context(SupportedLocales.class).disposed()) {
			refresh();
		}
	}

	/**
	 * IDE-1132 ,Add a listener to Property ResourceBundle, so LocaleBundle can be
	 * validated once ResourceBundle gets changed.
	 */
	protected void initValidationService() {
		this.listener = new FilteredListener<PropertyContentEvent>() {

			protected void handleTypedEvent(PropertyContentEvent event) {
				if (!context(SupportedLocales.class).disposed()) {
					refresh();
				}
			}

		};

		context(SupportedLocales.class).nearest(Portlet.class).getResourceBundle().attach(this.listener);
	}

	private FilteredListener<PropertyContentEvent> listener;

	private static final class Resources extends NLS {

		public static String localeMustNotEmpty;
		public static String noResourceBundle;

		static {
			initializeMessages(LocaleBundleValidationService.class.getName(), Resources.class);
		}

	}

}
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

package com.liferay.ide.ui;

import com.liferay.ide.core.LiferayLanguagePropertiesValidator;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.InvocationTargetException;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IMarkerResolutionGenerator2;

/**
 * @author Kuo Zhang
 */
public class LiferayLanguagePropertiesMarkerResolutionGenerator implements IMarkerResolutionGenerator2 {

	public IMarkerResolution[] getResolutions(IMarker marker) {
		List<IMarkerResolution> resolutions = new ArrayList<>();

		try {
			if (LiferayLanguagePropertiesValidator.ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT.equals(
					marker.getAttribute(IMarker.SOURCE_ID))) {

				resolutions.add(new EncodeAllFilesToDefaultResolution());
				resolutions.add(new EncodeOneFileToDefaultResolution());
			}
		}
		catch (CoreException ce) {
			LiferayUIPlugin.logError(ce);
		}

		return resolutions.toArray(new IMarkerResolution[0]);
	}

	public boolean hasResolutions(IMarker marker) {
		try {
			if (LiferayLanguagePropertiesValidator.LIFERAY_LANGUAGE_PROPERTIES_MARKER_TYPE.equals(marker.getType()) &&
				LiferayLanguagePropertiesValidator.ID_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT.equals(
					marker.getAttribute(IMarker.SOURCE_ID))) {

				return true;
			}
		}
		catch (CoreException ce) {
			LiferayUIPlugin.logError(ce);
		}

		return false;
	}

	private static class Msgs extends NLS {

		public static String encodeAllFilesToDefault;
		public static String encodeThisFileToDefault;

		static {
			initializeMessages(LiferayLanguagePropertiesMarkerResolutionGenerator.class.getName(), Msgs.class);
		}

	}

	private class EncodeAllFilesToDefaultResolution extends EncodeOneFileToDefaultResolution {

		public String getLabel() {
			return Msgs.encodeAllFilesToDefault;
		}

		public void run(IMarker marker) {
			encode(CoreUtil.getLiferayProject(marker.getResource()));
		}

	}

	private class EncodeOneFileToDefaultResolution implements IMarkerResolution2 {

		public String getDescription() {
			return getLabel();
		}

		public Image getImage() {
			LiferayUIPlugin plugin = LiferayUIPlugin.getDefault();

			URL url = plugin.getBundle().getEntry("/icons/e16/encode.png");

			return ImageDescriptor.createFromURL(url).createImage();
		}

		public String getLabel() {
			return Msgs.encodeThisFileToDefault;
		}

		public void run(IMarker marker) {
			if (marker.getResource().getType() == IResource.FILE) {
				encode((IFile)marker.getResource());
			}
		}

		protected void encode(IResource resource) {
			if ((resource != null) && resource.exists()) {
				try {
					ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(UIUtil.getActiveShell());

					IRunnableWithProgress runnable = new IRunnableWithProgress() {

						public void run(IProgressMonitor monitor)
							throws InterruptedException,
								   InvocationTargetException {

							monitor.beginTask("Encoding Liferay language properties files to default (UTF-8)... ", 10);

							PropertiesUtil.encodeLanguagePropertiesFilesToDefault(resource, monitor);

							monitor.done();
						}

					};

					progressMonitorDialog.run(true, false, runnable);
				}
				catch (Exception e) {
					LiferayUIPlugin.logError(e);
				}
			}
		}

	}

}
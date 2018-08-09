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

package com.liferay.ide.xml.search.ui;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.net.URL;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import org.osgi.framework.Bundle;

/**
 * @author Terry Jia
 */
public class AddResourceKeyMarkerResolution extends AbstractResourceBundleMarkerResolution {

	public AddResourceKeyMarkerResolution(IMarker marker, IFile languageFile) {
		super(marker);

		_resourceBundle = languageFile;
	}

	public Image getImage() {
		LiferayXMLSearchUI plugin = LiferayXMLSearchUI.getDefault();

		Bundle bundle = plugin.getBundle();

		URL url = bundle.getEntry("/icons/resource-bundle.png");

		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);

		return imageDescriptor.createImage();
	}

	public String getLabel() {
		StringBuffer sb = new StringBuffer();

		sb.append("Add missing key to ");

		IPath path = _resourceBundle.getProjectRelativePath();

		sb.append(path.toString());

		return sb.toString();
	}

	protected void resolve(IMarker marker) {
		String message = marker.getAttribute(IMarker.MESSAGE, "");

		if ((message == null) || (_resourceBundle == null)) {
			return;
		}

		try {
			String languageKey = getResourceKey(marker);

			if (CoreUtil.isNullOrEmpty(languageKey)) {
				return;
			}

			Properties properties = new Properties();

			try (InputStream is = _resourceBundle.getContents()) {
				properties.load(is);
			}

			if (properties.get(languageKey) != null) {
				return;
			}

			String resourceValue = getDefaultResourceValue(languageKey);

			String resourcePropertyLine = languageKey + "=" + resourceValue;

			String contents = CoreUtil.readStreamToString(_resourceBundle.getContents());

			StringBuffer contentSb = new StringBuffer();

			contentSb.append(contents);

			if (!contents.endsWith("\n")) {
				contentSb.append("\n");
			}

			contentSb.append(resourcePropertyLine);

			String string = StringUtil.trim(contentSb);

			byte[] bytes = string.getBytes("UTF-8");

			int contentOffset = bytes.length;

			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
				_resourceBundle.setContents(inputStream, IResource.FORCE, new NullProgressMonitor());
			}

			byte[] lineBytes = resourcePropertyLine.getBytes();

			int resourcePropertyLineOffset = lineBytes.length;

			openEditor(_resourceBundle, contentOffset - resourcePropertyLineOffset, contentOffset - 1);
		}
		catch (Exception e) {
			LiferayXMLSearchUI.logError(e);
		}
	}

	private IFile _resourceBundle = null;

}
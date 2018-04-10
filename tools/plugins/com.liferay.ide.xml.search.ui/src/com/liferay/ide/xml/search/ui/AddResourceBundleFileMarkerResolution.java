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

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Terry Jia
 */
public class AddResourceBundleFileMarkerResolution extends AbstractResourceBundleMarkerResolution {

	public AddResourceBundleFileMarkerResolution(IMarker marker, String portletName) {
		super(marker);

		_portletName = portletName;
	}

	public Image getImage() {
		LiferayXMLSearchUI plugin = LiferayXMLSearchUI.getDefault();

		URL url = plugin.getBundle().getEntry("/icons/resource-bundle-new.png");

		return ImageDescriptor.createFromURL(url).createImage();
	}

	@Override
	public String getLabel() {
		return "Create a new default resource bundle add it to " + _portletName + " portlet";
	}

	@Override
	protected void resolve(IMarker marker) {
		IProject project = marker.getResource().getProject();

		if ((getResourceKey(marker) == null) || (project == null)) {
			return;
		}

		try {
			_checkResourceBundleElement(project);

			ILiferayProject liferayProject = LiferayCore.create(project);

			if (liferayProject == null) {
				return;
			}

			IFolder folder = liferayProject.getSourceFolder("resources").getFolder(_resourceBundlePackage);

			if (!folder.exists()) {
				CoreUtil.makeFolders(folder);
			}

			IFile resourceBundle = folder.getFile(_resourceBundleName + ".properties");

			String resourceKey = getResourceKey(marker);

			if (CoreUtil.isNullOrEmpty(resourceKey)) {
				return;
			}

			String resourceValue = getDefaultResourceValue(resourceKey);

			String resourcePropertyLine = resourceKey + "=" + resourceValue + "\n";

			int contentOffset = 0;
			int resourcePropertyLineOffset = resourcePropertyLine.getBytes().length;

			if (!resourceBundle.exists()) {
				IFolder parent = (IFolder)resourceBundle.getParent();

				CoreUtil.prepareFolder(parent);

				try(InputStream inputStream = new ByteArrayInputStream(resourcePropertyLine.getBytes("UTF-8"))){
					resourceBundle.create(inputStream, IResource.FORCE, null);
				}

				contentOffset = resourcePropertyLineOffset;
			}
			else {
				String contents = CoreUtil.readStreamToString(resourceBundle.getContents());

				StringBuffer sb = new StringBuffer();

				sb.append(contents);
				sb.append(resourcePropertyLine);

				String string = sb.toString();

				byte[] bytes = string.trim().getBytes("UTF-8");

				contentOffset = bytes.length;

				try(InputStream inputStream = new ByteArrayInputStream(bytes)){
					resourceBundle.setContents(inputStream, IResource.FORCE, new NullProgressMonitor());
				}
			}

			openEditor(resourceBundle, contentOffset - resourcePropertyLineOffset, contentOffset - 1);
		}
		catch (Exception e) {
			LiferayXMLSearchUI.logError(e);
		}
	}

	private void _checkResourceBundleElement(IProject project) {
		PortletDescriptorHelper portletDescriptorHelper = new PortletDescriptorHelper(project);

		String[] resouceBundles = portletDescriptorHelper.getAllResourceBundles();

		if (resouceBundles.length == 0) {
			portletDescriptorHelper.addResourceBundle(_resourceBundlePackage + "." + _resourceBundleName, _portletName);
		}
		else {
			for (String resouceBundle : resouceBundles) {
				if (!CoreUtil.isNullOrEmpty(resouceBundle)) {
					String[] paths = resouceBundle.split("\\.");

					if (paths.length > 2) {
						StringBuffer sb = new StringBuffer();

						for (int i = 0; i < (paths.length - 1); i++) {
							sb.append(paths[i]);
							sb.append("/");
						}

						_resourceBundlePackage = sb.toString();
						_resourceBundleName = paths[paths.length - 1];
					}
					else if (paths.length == 2) {
						_resourceBundlePackage = paths[0];
						_resourceBundleName = paths[1];
					}
					else if (paths.length == 1) {
						_resourceBundlePackage = "";
						_resourceBundleName = paths[0];
					}

					break;
				}
			}
		}
	}

	private String _portletName = "";
	private String _resourceBundleName = "Language";
	private String _resourceBundlePackage = "content";

}
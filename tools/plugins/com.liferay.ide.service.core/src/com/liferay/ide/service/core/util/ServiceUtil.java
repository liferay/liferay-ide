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

package com.liferay.ide.service.core.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
public class ServiceUtil {

	public static IJavaProject[] getAllServiceProjects() {
		List<IJavaProject> serviceProjects = new ArrayList<>();

		for (IProject project : CoreUtil.getAllProjects()) {
			if (project.isAccessible()) {
				IJavaProject jp = JavaCore.create(project);

				if (jp != null) {
					List<IFile> serviceXmls = new SearchFilesVisitor().searchFiles(project, "service.xml");

					if (ListUtil.isNotEmpty(serviceXmls)) {
						serviceProjects.add(jp);
					}
				}
			}
		}

		return serviceProjects.toArray(new IJavaProject[0]);
	}

	public static String getDTDVersion(Document document) {
		String dtdVersion = null;
		DocumentType docType = document.getDoctype();

		if (docType != null) {
			String publicId = docType.getPublicId();
			String systemId = docType.getSystemId();

			if ((publicId != null) && (systemId != null)) {
				if (publicId.contains("6.0.0") || systemId.contains("6.0.0")) {
					dtdVersion = "6.0.0";
				}
				else if (publicId.contains("6.1.0") || systemId.contains("6.1.0")) {
					dtdVersion = "6.1.0";
				}
			}
		}

		return dtdVersion;
	}

	public static boolean isChar(char c) {
		int x = c;

		if (((x >= 97) && (x <= 122)) || ((x >= 65) && (x <= 90))) {
			return true;
		}

		return false;
	}

	public static boolean isValidNamespace(String namespace) {
		if (namespace == null) {
			return false;
		}

		for (char c : namespace.toCharArray()) {
			if ((c != '_') && !isChar(c)) {
				return false;
			}
		}

		return true;
	}

}
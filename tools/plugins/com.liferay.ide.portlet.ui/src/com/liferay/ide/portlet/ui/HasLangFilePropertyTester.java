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

package com.liferay.ide.portlet.ui;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Gregory Amerson
 */
public class HasLangFilePropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IProject) {
			IProject project = (IProject)receiver;

			boolean liferayProject = ProjectUtil.isLiferayFacetedProject(project);

			if (liferayProject) {
				try {
					List<IFolder> srcFolders = CoreUtil.getSourceFolders(JavaCore.create(project));

					for (IFolder src : srcFolders) {
						IResource[] members = src.members();

						for (IResource member : members) {
							if ((member.getType() == IResource.FOLDER) && member.getName().equals("content")) {
								IResource[] content = ((IFolder)member).members();

								for (IResource res : content) {
									if ((res.getType() == IResource.FILE) &&
										res.getName().matches("Language.*\\.properties")) {

										return true;
									}
								}
							}
						}
					}
				}
				catch (Throwable t) {

					// ignore

				}
			}
		}

		return false;
	}

}
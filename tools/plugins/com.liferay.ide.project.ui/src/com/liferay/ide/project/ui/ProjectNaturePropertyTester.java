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

package com.liferay.ide.project.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;

/**
 * @author Gregory Amerson
 */
public class ProjectNaturePropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IProject) {
			IProject project = (IProject)receiver;

			try {
				IProjectDescription desc = project.getDescription();

				if (desc != null) {
					for (String natureId : desc.getNatureIds()) {
						if (natureId.equals(expectedValue)) {
							return true;
						}
					}
				}
			}
			catch (Throwable t) {

				// ignore

			}
		}

		return false;
	}

}
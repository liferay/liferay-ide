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

package com.liferay.ide.service.ui;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;

/**
 * @author Gregory Amerson
 */
public class HasServiceFilePropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IResource) {
			IResource resource = (IResource)receiver;

			IWebProject webproject = LiferayCore.create(IWebProject.class, resource.getProject());

			if (webproject != null) {
				try {

					// IDE-110 IDE-648

					IResource serviceResource = webproject.findDocrootResource(
						new Path("WEB-INF/" + ILiferayConstants.SERVICE_XML_FILE));

					if (FileUtil.exists(serviceResource)) {
						return true;
					}
				}
				catch (Throwable t) {
				}
			}
		}

		return false;
	}

}
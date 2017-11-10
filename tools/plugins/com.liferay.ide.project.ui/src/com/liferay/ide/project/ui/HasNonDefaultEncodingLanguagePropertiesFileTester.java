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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.PropertiesUtil;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Kuo Zhang
 */
public class HasNonDefaultEncodingLanguagePropertiesFileTester extends PropertyTester {

	public HasNonDefaultEncodingLanguagePropertiesFileTester() {
	}

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		boolean retval = false;

		if (receiver instanceof IProject) {
			retval = PropertiesUtil.hasNonDefaultEncodingLanguagePropertiesFile((IProject)receiver);
		}
		else if (receiver instanceof IFile) {
			try {
				if (!ILiferayConstants.LANGUAGE_PROPERTIES_FILE_ENCODING_CHARSET.equals(
						((IFile)receiver).getCharset()) &&
					PropertiesUtil.isLanguagePropertiesFile((IFile)receiver)) {

					retval = true;
				}
			}
			catch (CoreException ce) {
				LiferayCore.logError(ce);
			}
		}

		return retval;
	}

}
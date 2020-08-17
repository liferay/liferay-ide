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

package com.liferay.ide.core.describer;

import com.liferay.ide.core.util.PropertiesUtil;

import org.eclipse.core.resources.IFile;

/**
 * @author Gregory Amerson
 */
public class LiferayLanguagePropertiesFileDescriber extends LiferayPropertiesFileDescriber {

	public LiferayLanguagePropertiesFileDescriber() {
	}

	@Override
	protected boolean isPropertiesFile(Object file) {
		if ((file instanceof IFile) && PropertiesUtil.isLanguagePropertiesFile((IFile)file)) {
			return true;
		}

		return false;
	}

}
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

package com.liferay.ide.xml.search.ui.descriptors;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.xml.search.core.queryspecifications.requestor.ContentTypeXMLSearchRequestor;

/**
 * @author Gregory Amerson
 */
public abstract class LiferayContentTypeXMLSearchRequestor extends ContentTypeXMLSearchRequestor {

	@Override
	public boolean accept(IFile file, IResource rootResource) {
		if (super.accept(file, rootResource) && CoreUtil.isLiferayProject(file.getProject())) {
			return true;
		}

		return false;
	}

}
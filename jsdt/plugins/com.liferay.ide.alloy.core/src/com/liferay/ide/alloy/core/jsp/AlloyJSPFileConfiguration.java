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

package com.liferay.ide.alloy.core.jsp;

import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IFile;

import tern.ITernFile;

import tern.eclipse.ide.core.ITernFileConfiguration;

import tern.server.protocol.html.ScriptTagRegion;

/**
 * @author Gregory Amerson
 */
public class AlloyJSPFileConfiguration implements ITernFileConfiguration {

	@Override
	public ScriptTagRegion[] getScriptTags(ITernFile ternFile) {
		if (ternFile == null) {
			return null;
		}

		Object file = ternFile.getAdapter(IFile.class);

		IFile iFile = (IFile)file;

		if ((file instanceof IFile) && ProjectUtil.isPortletProject(iFile.getProject())) {
			return _tags;
		}

		return null;
	}

	private static final ScriptTagRegion[] _tags = {ScriptTagRegion.SCRIPT_TAG, new ScriptTagRegion("aui:script")};

}
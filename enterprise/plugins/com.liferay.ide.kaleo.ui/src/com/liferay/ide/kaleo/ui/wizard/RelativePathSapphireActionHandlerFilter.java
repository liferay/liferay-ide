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

package com.liferay.ide.kaleo.ui.wizard;

import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFilter;
import org.eclipse.sapphire.ui.forms.swt.RelativePathBrowseActionHandler;
import org.eclipse.sapphire.workspace.ui.ProjectRelativePathBrowseActionHandler;

/**
 * @author Gregory Amerson
 */
public class RelativePathSapphireActionHandlerFilter extends SapphireActionHandlerFilter {

	@Override
	public boolean check(SapphireActionHandler handler) {
		if ((handler instanceof ProjectRelativePathBrowseActionHandler) ||
			 !(handler instanceof RelativePathBrowseActionHandler)) {

			return true;
		}

		return false;
	}

}
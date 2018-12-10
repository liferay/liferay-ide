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

package com.liferay.ide.upgrade.plan.base;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author Terry Jia
 */
public abstract class FileUpgradeTaskStep extends AbstractUpgradeTaskStep {

	public abstract IStatus execute(File file, IProgressMonitor progressMonitor);

	public IStatus execute(IProgressMonitor progressMonitor) {
		FileDialog dialog = new FileDialog(UIUtil.getActiveShell());

		dialog.setFilterExtensions(getExtensions());

		String result = dialog.open();

		File file = new File(result);

		if (FileUtil.exists(file)) {
			execute(file, progressMonitor);
		}

		return Status.CANCEL_STATUS;
	}

	protected String[] getExtensions() {
		return new String[] {"*"};
	}

}
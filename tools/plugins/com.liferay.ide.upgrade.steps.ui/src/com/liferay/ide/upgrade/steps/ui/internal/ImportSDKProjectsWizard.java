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

package com.liferay.ide.upgrade.steps.ui.internal;

import com.liferay.ide.upgrade.steps.core.ImportSDKProjectsOp;

import java.io.File;

import java.nio.file.Path;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Terry Jia
 */
public class ImportSDKProjectsWizard extends SapphireWizard<ImportSDKProjectsOp> implements INewWizard {

	public ImportSDKProjectsWizard(ImportSDKProjectsOp sdkProjectsImportOp, Path currentProjectLocation) {
		super(
			_createDefaultOp(sdkProjectsImportOp, currentProjectLocation),
			DefinitionLoader.sdef(ImportSDKProjectsWizard.class).wizard());
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	private static ImportSDKProjectsOp _createDefaultOp(ImportSDKProjectsOp sdkProjectsImportOp, Path path) {
		File file = path.toFile();

		IPath sdkPath = new org.eclipse.core.runtime.Path(file.getPath());

		sdkProjectsImportOp.setSdkLocation(PathBridge.create(sdkPath));

		return sdkProjectsImportOp;
	}

}
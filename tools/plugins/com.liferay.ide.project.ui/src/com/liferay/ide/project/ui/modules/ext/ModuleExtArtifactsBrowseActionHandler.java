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

package com.liferay.ide.project.ui.modules.ext;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.project.core.modules.ext.NewModuleExtOp;

import java.io.File;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.forms.BrowseActionHandler;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;

import org.osgi.framework.Version;

/**
 * @author Charles Wu
 */
public final class ModuleExtArtifactsBrowseActionHandler extends BrowseActionHandler {

	public static final String ID = "Module.Ext.Browse.Possible";

	public ModuleExtArtifactsBrowseActionHandler() {
		setId(ID);
	}

	@Override
	protected String browse(final Presentation presentation) {
		Value<?> property = property();

		NewModuleExtOp newModuleExtOp = property.nearest(NewModuleExtOp.class);

		final ModuleExtBrowseDialog moduleExtBrowseDialog = new ModuleExtBrowseDialog(
			((FormComponentPresentation)presentation).shell(), property);

		moduleExtBrowseDialog.open();

		final Object[] result = moduleExtBrowseDialog.getResult();

		if ((result != null) && (result.length == 1)) {
			Artifact artifact = (Artifact)result[0];

			newModuleExtOp.setOriginalModuleVersion(new Version(artifact.getVersion()));

			File sourceFile = artifact.getSourceFile();

			newModuleExtOp.setSourceFileUri(sourceFile.toURI());

			return artifact.getArtifact();
		}

		return null;
	}

}
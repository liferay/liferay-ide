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
	protected String browse(final Presentation context) {
		Value<?> property = property();

		NewModuleExtOp op = property.nearest(NewModuleExtOp.class);

		final ModuleExtBrowseDialog dialog = new ModuleExtBrowseDialog(
			((FormComponentPresentation)context).shell(), property);

		dialog.open();

		final Object[] result = dialog.getResult();

		if ((result != null) && (result.length == 1)) {
			Artifact artifact = (Artifact)result[0];

			op.setOriginalModuleVersion(new Version(artifact.getVersion()));

			return artifact.getArtifact();
		}

		return null;
	}

}
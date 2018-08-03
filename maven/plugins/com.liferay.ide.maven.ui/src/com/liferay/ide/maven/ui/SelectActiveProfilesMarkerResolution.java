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

package com.liferay.ide.maven.ui;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.ui.wizard.NewLiferayPluginProjectWizard;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.DefinitionLoader.Reference;
import org.eclipse.sapphire.ui.forms.DialogDef;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;

/**
 * @author Gregory Amerson
 */
public class SelectActiveProfilesMarkerResolution extends AbstractProjectMarkerResolution {

	public String getLabel() {
		return "Select existing maven profiles to attach to the current project.";
	}

	protected int promptUser(IProject project, NewLiferayPluginProjectOp op) {
		DefinitionLoader definitionLoader = DefinitionLoader.sdef(NewLiferayPluginProjectWizard.class);

		Reference<DialogDef> dialogRef = definitionLoader.dialog("SelectActiveProfiles");

		SapphireDialog dialog = new SapphireDialog(UIUtil.getActiveShell(), op, dialogRef);

		dialog.setBlockOnOpen(true);

		return dialog.open();
	}

}
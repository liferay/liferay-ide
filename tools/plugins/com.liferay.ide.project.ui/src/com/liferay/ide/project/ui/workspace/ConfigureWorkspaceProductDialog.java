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

package com.liferay.ide.project.ui.workspace;

import com.liferay.ide.project.core.workspace.ConfigureWorkspaceProductOp;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Simon Jiang
 */
public class ConfigureWorkspaceProductDialog extends SapphireDialog {

	public ConfigureWorkspaceProductDialog() {
		super(
			UIUtil.getActiveShell(), ConfigureWorkspaceProductOp.TYPE,
			DefinitionLoader.sdef(ConfigureWorkspaceProductDialog.class).dialog());
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Update", true);
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

}
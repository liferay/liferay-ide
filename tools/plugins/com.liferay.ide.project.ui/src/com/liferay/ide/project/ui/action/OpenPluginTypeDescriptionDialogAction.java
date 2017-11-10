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

package com.liferay.ide.project.ui.action;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.ui.dialog.PluginTypeDescriptionDialog;
import com.liferay.ide.project.ui.wizard.NewLiferayPluginProjectWizard;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;

/**
 * @author Kuo Zhang
 */
public class OpenPluginTypeDescriptionDialogAction extends SapphireActionHandler {

	@Override
	protected Object run(Presentation context) {
		if (context instanceof SwtPresentation) {
			final SwtPresentation swt = (SwtPresentation)context;

			final NewLiferayPluginProjectOp op = getModelElement().nearest(NewLiferayPluginProjectOp.class);

			final PluginTypeDescriptionDialog dialog = new PluginTypeDescriptionDialog(
				swt.shell(), op,
				DefinitionLoader.sdef(NewLiferayPluginProjectWizard.class).dialog("PluginTypeDescription"));

			dialog.setBlockOnOpen(false);

			dialog.open();
		}

		return null;
	}

}
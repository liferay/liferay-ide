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

package com.liferay.ide.studio.ui;

import com.liferay.ide.project.ui.workspace.NewLiferayWorkspaceWizard;

import java.util.Properties;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.internal.intro.impl.IntroPlugin;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

/**
 * @author Joye Luo
 */
@SuppressWarnings("restriction")
public class NewLiferayWorkspaceAction implements IIntroAction {

	@Override
	public void run(IIntroSite site, Properties properties) {
		NewLiferayWorkspaceWizard wizard = new NewLiferayWorkspaceWizard();

		WizardDialog dialog = new WizardDialog(site.getShell(), wizard);

		if (dialog.open() == Window.OK) {
			IntroPlugin.closeIntro();
		}
	}

}
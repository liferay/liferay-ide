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

package com.liferay.ide.portlet.ui.editor.internal;

import com.liferay.ide.portlet.ui.editor.PortletXmlEditor;
import com.liferay.ide.portlet.ui.wizard.NewPortletWizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;

/**
 * @author Kamesh Sampath
 */
public class CreateLiferayPortletActionHandler extends SapphireActionHandler {

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * SapphireActionHandler#run(org.eclipse.sapphire.ui.
	 * SapphireRenderingContext)
	 */
	@Override
	protected Object run(Presentation context) {
		IProject currentProject = null;

		if (context.part().parent() instanceof PortletXmlEditor) {
			PortletXmlEditor portletXmlEditor = (PortletXmlEditor)context.part().parent();

			currentProject = portletXmlEditor.getProject();
		}

		NewPortletWizard newPortletWizard = new NewPortletWizard(currentProject);

		WizardDialog wizardDialog = new WizardDialog(((SwtPresentation)context).shell(), newPortletWizard);

		wizardDialog.create();
		wizardDialog.open();

		return null;
	}

}
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

package com.liferay.ide.ui.snippets.wizard;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;

/**
 * @author Gregory Amerson
 */
public class AddModelEntityWizardPage extends AbstractModelWizardPage {

	public AddModelEntityWizardPage(String pageName, IEditorPart editor) {
		super(pageName, editor);
		setTitle(Msgs.addModelEntity);
		setDescription(Msgs.insertCode);
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		varNameLabel.setVisible(false);
		varNameText.setVisible(false);
	}

	public String getVarName() {
		return getModel().toLowerCase();
	}

	private static class Msgs extends NLS {

		public static String addModelEntity;
		public static String insertCode;

		static {
			initializeMessages(AddModelEntityWizardPage.class.getName(), Msgs.class);
		}

	}

}
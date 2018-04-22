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
import org.eclipse.ui.IEditorPart;

/**
 * @author Gregory Amerson
 */
public class AddModelEntityWizard extends AbstractModelWizard {

	public AddModelEntityWizard(IEditorPart fEditorPart) {
		super(fEditorPart);
		setWindowTitle(Msgs.addModelEntity);
	}

	@Override
	protected AbstractModelWizardPage createModelWizardPage(IEditorPart editorPart) {
		return new AddModelEntityWizardPage("addModelEntityWizardPage", editorPart);
	}

	private static class Msgs extends NLS {

		public static String addModelEntity;

		static {
			initializeMessages(AddModelEntityWizard.class.getName(), Msgs.class);
		}

	}

}
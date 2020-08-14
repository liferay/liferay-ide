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

package com.liferay.ide.ui.snippets;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.ui.snippets.wizard.AbstractModelWizard;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public abstract class ModelSnippetInsertion extends AbstractSnippetInsertion {

	public ModelSnippetInsertion() {
	}

	@Override
	public void insert(IEditorPart editorPart) {
		if (fEditorPart == null) {
			fEditorPart = editorPart;
		}

		super.insert(editorPart);
	}

	protected abstract AbstractModelWizard createModelWizard(IEditorPart fEditorPart);

	protected String getPreparedText(AbstractModelWizard wizard) {
		String text = fItem.getContentString();

		text = StringUtils.replace(text, "${model}", wizard.getModel());
		text = StringUtils.replace(text, "${varName}", wizard.getVarName());

		// Update EOLs (bug 80231)

		String systemEOL = System.getProperty("line.separator");
		text = StringUtils.replace(text, "\r\n", "\n");
		text = StringUtils.replace(text, "\r", "\n");

		if (!systemEOL.equals("\n") && (systemEOL != null)) {
			text = StringUtils.replace(text, "\n", systemEOL);
		}

		return text;
	}

	@Override
	protected String getResolvedString(Shell host) {
		AbstractModelWizard wizard = createModelWizard(fEditorPart);

		WizardDialog dialog = new WizardDialog(host, wizard);

		dialog.setBlockOnOpen(true);

		int retval = dialog.open();

		if (retval == Window.OK) {
			return getPreparedText(wizard);
		}

		return StringPool.EMPTY;
	}

}
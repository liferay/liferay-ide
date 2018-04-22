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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.ui.snippets.wizard.AbstractModelWizard;
import com.liferay.ide.ui.snippets.wizard.AddModelEntityWizard;

import org.apache.commons.lang.StringUtils;

import org.eclipse.ui.IEditorPart;

/**
 * @author Gregory Amerson
 */
public class AddModelEntitySnippetInsertion extends ModelSnippetInsertion {

	public AddModelEntitySnippetInsertion() {
	}

	@Override
	protected AbstractModelWizard createModelWizard(IEditorPart fEditorPart) {
		return new AddModelEntityWizard(fEditorPart);
	}

	protected String getPreparedText(AbstractModelWizard wizard) {
		String text = super.getPreparedText(wizard);

		StringBuffer fields = new StringBuffer();
		String[] propColumns = wizard.getPropertyColumns();
		String var = wizard.getVarName();

		if (ListUtil.isNotEmpty(propColumns)) {
			for (String prop : propColumns) {
				fields.append(var + ".set" + StringUtils.capitalize(prop) + "(" + prop + ");\n");
			}
		}

		String fieldsVal = fields.toString();

		text = StringUtils.replace(text, "${fields}", CoreUtil.isNullOrEmpty(fieldsVal) ? StringPool.EMPTY : fieldsVal);

		return text;
	}

}
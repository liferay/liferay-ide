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
import com.liferay.ide.ui.snippets.wizard.LiferayUISearchContainerWizard;

import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class SearchContainerSnippetInsertion extends ModelSnippetInsertion {

	public SearchContainerSnippetInsertion() {
	}

	@Override
	protected AbstractModelWizard createModelWizard(IEditorPart fEditorPart) {
		return new LiferayUISearchContainerWizard(this.fEditorPart);
	}

	protected String getPreparedText(AbstractModelWizard wizard) {
		String text = super.getPreparedText(wizard);

		text = StringUtils.replace(text, "${modelClass}", ((LiferayUISearchContainerWizard)wizard).getModelClass());

		StringBuffer columns = new StringBuffer();
		String[] propColumns = wizard.getPropertyColumns();

		if (ListUtil.isNotEmpty(propColumns)) {
			for (String prop : propColumns) {
				columns.append("<liferay-ui:search-container-column-text property=\"");
				columns.append(prop);
				columns.append("\" />\n\n\t\t");
			}
		}

		String columnsVal = columns.toString();

		text = StringUtils.replace(
			text, "${columns}", CoreUtil.isNullOrEmpty(columnsVal) ? StringPool.EMPTY : columnsVal);

		return text;
	}

}
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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.ui.wizard.StringArrayTableWizardSection.StringArrayDialogCallback;

import org.eclipse.swt.widgets.Text;

/**
 * Implementation of the <code>StringArrayDialogCallback</code> interface for
 * both "Initialization Parameters" and "URL Mappings" table views.
 *
 * @author Gregory Amerson
 */
public class StringArrayTableWizardSectionCallback implements StringArrayDialogCallback {

	/**
	 * Trims the text values.
	 */
	public String[] retrieveResultStrings(Text[] texts) {
		int n = texts.length;

		String[] result = new String[n];

		for (int i = 0; i < n; i++) {
			result[i] = texts[i].getText().trim();
		}

		return result;
	}

	/**
	 * The first text field should not be empty.
	 */
	public boolean validate(Text[] texts) {
		if (ListUtil.isNotEmpty(texts)) {
			String text = texts[0].getText().trim();

			if (text.length() > 0) {
				return true;
			}

			return false;
		}

		return true;
	}

}
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

package com.liferay.ide.functional.liferay;

import com.liferay.ide.functional.liferay.page.LiferayIDE;
import com.liferay.ide.functional.swtbot.UI;
import com.liferay.ide.functional.swtbot.page.Shell;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import org.junit.Assert;

/**
 * @author Terry Jia
 */
public class UIAction implements UI {

	public UIAction(SWTWorkbenchBot bot) {
		this.bot = bot;

		ide = LiferayIDE.getInstance(bot);
	}

	public void assertTitle(Shell shell1, Shell shell2) {
		String label1 = shell1.getLabel();
		String label2 = shell2.getLabel();

		Assert.assertFalse("Need to set label for shell1", label1.equals(""));
		Assert.assertFalse("Need to set label for shell2", label2.equals(""));

		Assert.assertTrue("Now under \"" + label1 + "\" but expect \"" + label2 + "\"", label1.equals(label2));
	}

	public void assertTitleStartBy(Shell shell1, Shell shell2) {
		String label1 = shell1.getLabel();
		String label2 = shell2.getLabel();

		Assert.assertFalse("Need to set label for shell1", label1.equals(""));
		Assert.assertFalse("Need to set label for shell2", label2.equals(""));

		Assert.assertTrue(
			"Now under \"" + label1 + "\" but expect \"" + label2 + "\"",
			label2.startsWith(label1) || label1.startsWith(label2));
	}

	protected SWTWorkbenchBot bot;
	protected LiferayIDE ide;

}
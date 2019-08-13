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

package com.liferay.ide.functional.liferay.action;

import com.liferay.ide.functional.liferay.UIAction;
import com.liferay.ide.functional.swtbot.page.AbstractWidget;
import com.liferay.ide.functional.swtbot.page.CTabItem;
import com.liferay.ide.functional.swtbot.page.CheckBox;
import com.liferay.ide.functional.swtbot.page.ComboBox;
import com.liferay.ide.functional.swtbot.page.Editor;
import com.liferay.ide.functional.swtbot.page.Table;
import com.liferay.ide.functional.swtbot.page.View;

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBotAssert;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import org.junit.Assert;

/**
 * @author Terry Jia
 * @author Ashely Yuan
 */
public class ValidationAction extends UIAction {

	public static ValidationAction getInstance(SWTWorkbenchBot bot) {
		if (_validationAction == null) {
			_validationAction = new ValidationAction(bot);
		}

		return _validationAction;
	}

	public void assertActiveFalse(AbstractWidget widget) {
		Assert.assertFalse(widget.isActive());
	}

	public void assertActiveTrue(AbstractWidget widget) {
		Assert.assertTrue(widget.isActive());
	}

	public void assertCheckedFalse(CheckBox checkBox) {
		Assert.assertFalse(checkBox.isChecked());
	}

	public void assertCheckedTrue(CheckBox checkBox) {
		Assert.assertTrue(checkBox.isChecked());
	}

	public void assertContains(String expect, String stack) {
		SWTBotAssert.assertContains(expect, stack);
	}

	public void assertDoesNotContains(String expect, String stack) {
		SWTBotAssert.assertDoesNotContain(expect, stack);
	}

	public void assertEditorVisible(String label) {
		Editor editor = new Editor(bot, label);

		Assert.assertTrue(editor.isActive());
	}

	public void assertEnabledFalse(AbstractWidget widget) {
		Assert.assertFalse(widget.isEnabled());
	}

	public void assertEnabledTrue(AbstractWidget widget) {
		Assert.assertTrue(widget.isEnabled());
	}

	public void assertEquals(String expect, String actual) {
		Assert.assertEquals(expect, actual);
	}

	public void assertEquals(String[] expects, ComboBox comboBox) {
		String[] items = comboBox.items();

		Assert.assertEquals("", expects.length, items.length);

		for (int i = 0; i < items.length; i++) {
			Assert.assertEquals(expects[i], items[i]);
		}
	}

	public void assertEquals(String[] expects, String[] actual) {
		for (int i = 0; i < actual.length; i++) {
			Assert.assertEquals(expects[i], actual[i]);
		}
	}

	public void assertGogoShellVisible() {
		CTabItem gogoShell = new CTabItem(bot, LIFERAY_GOGO_SHELL);

		Assert.assertTrue(gogoShell.isActive());
	}

	public void assertLengthEquals(Object[] a1, Object[] a2) {
		Assert.assertEquals(Arrays.toString(a2), a1.length, a2.length);
	}

	public void assertTableContains(Table table, String expect) {
		Assert.assertTrue(table.containsItem(expect));
	}

	public void assertTextEquals(String expect, AbstractWidget widget) {
		Assert.assertEquals(expect, widget.getText());
	}

	public void assertTreeItemCollapsed(SWTBotTreeItem treeItems) {
		Assert.assertFalse(treeItems.isExpanded());
	}

	public void assertTreeItemExpanded(SWTBotTreeItem treeItems) {
		Assert.assertTrue(treeItems.isExpanded());
	}

	public void assertViewVisible(String label) {
		View view = new View(bot, label);

		Assert.assertTrue(view.isActive());
	}

	private ValidationAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	private static ValidationAction _validationAction;

}
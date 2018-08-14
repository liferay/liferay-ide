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
import com.liferay.ide.core.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IStorage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.wst.common.snippets.core.ISnippetVariable;
import org.eclipse.wst.common.snippets.internal.VariableInsertionDialog;
import org.eclipse.wst.common.snippets.internal.palette.SnippetVariable;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class TaglibVariableInsertionDialog extends VariableInsertionDialog {

	public TaglibVariableInsertionDialog(Shell parentShell, IEditorPart editor, boolean clearModality) {
		super(parentShell, clearModality);

		this.editor = editor;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);

		if (control instanceof Composite) {
			Composite composite = (Composite)control;

			replaceUIText(composite, Msgs.variableLowercase, Msgs.attributeLowercase);
			replaceUIText(composite, Msgs.variableUppercase, Msgs.attributeUppercase);
		}

		Table table = fTableViewer.getTable();

		TableColumn column = table.getColumns()[0];

		column.setText(Msgs.attributeName);

		table.redraw();

		return control;
	}

	@Override
	protected void prepareText() {

		// check the editor, if it is freemarker then prepare freemarker, else
		// use JSP

		String text = _prepareJSPText();

		if (_isFreemarkerEditor(editor)) {
			Matcher m1 = _p1.matcher(text);

			while (m1.matches()) {
				text = m1.replaceFirst(m1.group(1) + StringUtil.toUpperCase(m1.group(2)) + m1.group(3));

				m1 = _p1.matcher(text);
			}

			text = text.replaceAll("<([a-zA-Z]+):", "<@$1\\.");
			text = text.replaceAll("</([a-zA-Z]+):", "</@$1\\.");

			setPreparedText(text);

			return;
		}

		setPreparedText(text);
	}

	protected void replaceUIText(Composite parent, String search, String replace) {
		if (parent == null) {
			return;
		}

		if (ListUtil.isEmpty(parent.getChildren())) {
			return;
		}

		for (Control child : parent.getChildren()) {
			if (child instanceof Label) {
				Label label = (Label)child;

				String labelContent = label.getText();

				if (labelContent != null) {
					label.setText(labelContent.replaceAll(search, replace));
				}
			}
			else if (child instanceof Text) {
				Text text = (Text)child;

				String textContent = text.getText();

				if (textContent != null) {
					text.setText(textContent.replaceAll(search, replace));
				}
			}
			else if (child instanceof Composite) {
				replaceUIText((Composite)child, search, replace);
			}
		}
	}

	protected IEditorPart editor;

	private static boolean _isFreemarkerEditor(IEditorPart editorPart) {
		try {
			IStorageEditorInput input = (IStorageEditorInput)editorPart.getEditorInput();

			IStorage storage = input.getStorage();

			if (StringUtil.endsWith(storage.getName(), ".ftl")) {
				return true;
			}
		}
		catch (Exception e) {

			// ignore just return false

		}

		return false;
	}

	private String _prepareJSPText() {

		// this could be horribly inefficient

		String text = fItem.getContentString();
		ISnippetVariable[] variables = fItem.getVariables();

		for (ISnippetVariable variable : variables) {
			String value = (String)fTableViewer.getColumnData()[1].get(((SnippetVariable)variable).getId());

			if (!CoreUtil.isNullOrEmpty(value)) {
				value = StringPool.SPACE + variable.getName() + "=\"" + value + StringPool.DOUBLE_QUOTE;

				text = StringUtils.replace(text, "${" + variable.getName() + "}", value);
			}
		}

		// remove all cursor markers

		text = StringUtils.replace(text, "${cursor}", "");

		// Update EOLs (bug 80231)

		String systemEOL = System.getProperty("line.separator");
		text = StringUtils.replace(text, "\r\n", "\n");
		text = StringUtils.replace(text, "\r", "\n");

		if (!"\n".equals(systemEOL) && (systemEOL != null)) {
			text = StringUtils.replace(text, "\n", systemEOL);
		}

		return text;
	}

	private Pattern _p1 = Pattern.compile("(.*)-([a-z])(.*)");

	private static class Msgs extends NLS {

		public static String attributeLowercase;
		public static String attributeName;
		public static String attributeUppercase;
		public static String variableLowercase;
		public static String variableUppercase;

		static {
			initializeMessages(TaglibVariableInsertionDialog.class.getName(), Msgs.class);
		}

	}

}
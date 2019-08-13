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

package com.liferay.ide.functional.liferay.page.dialog;

import com.liferay.ide.functional.swtbot.page.Button;
import com.liferay.ide.functional.swtbot.page.ComboBox;
import com.liferay.ide.functional.swtbot.page.Dialog;
import com.liferay.ide.functional.swtbot.page.Text;
import com.liferay.ide.functional.swtbot.page.Tree;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 */
public class NewClassDialog extends Dialog {

	public NewClassDialog(SWTBot bot) {
		super(bot);

		_className = new Text(bot, CLASSNAME);
		_javaPackage = new Text(bot, JAVA_PACKAGE);
		_createBtn = new Button(bot, CREATE);
		_browseBtn = new Button(bot, BROWSE_WITH_DOT);
		_paths = new Tree(bot);
		_superClasses = new ComboBox(bot, SUPERCLASS);
	}

	public Button getBrowseBtn() {
		return _browseBtn;
	}

	public Text getClassName() {
		return _className;
	}

	public Button getCreateBtn() {
		return _createBtn;
	}

	public Text getJavaPackage() {
		return _javaPackage;
	}

	public Tree getPaths() {
		return _paths;
	}

	public ComboBox getSuperClass() {
		return _superClasses;
	}

	private Button _browseBtn;
	private Text _className;
	private Button _createBtn;
	private Text _javaPackage;
	private Tree _paths;
	private ComboBox _superClasses;

}
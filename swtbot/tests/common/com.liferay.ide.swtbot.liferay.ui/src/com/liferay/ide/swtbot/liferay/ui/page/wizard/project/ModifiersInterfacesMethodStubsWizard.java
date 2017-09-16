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

package com.liferay.ide.swtbot.liferay.ui.page.wizard.project;

import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ashley Yuan
 */
public class ModifiersInterfacesMethodStubsWizard extends Wizard {

	public ModifiersInterfacesMethodStubsWizard(SWTWorkbenchBot bot) {
		super(bot, 0);

		_isPublic = new CheckBox(bot, PUBLIC);
		_isAbstract = new CheckBox(bot, ABSTRACT);
		_isFinal = new CheckBox(bot, FINAL);
		_interfaces = new Table(bot, INTERFACES);
		_addBtn = new Button(bot, ADD_WITH_DOT);
		_removeBtn = new Button(bot, REMOVE);
		_constrcutFromSuperClass = new CheckBox(bot, CONSTRUCTORS_FROM_SUPERCLASS);
		_inheritedAbstractMethods = new CheckBox(bot, INHERITED_ABSTRACT_METHODS);
		_init = new CheckBox(bot, INIT);
		_destory = new CheckBox(bot, DESTROY);
		_doView = new CheckBox(bot, DOVIEW);
		_doEdit = new CheckBox(bot, DOEDIT);
		_doHelp = new CheckBox(bot, DOHELP);
		_doAbout = new CheckBox(bot, DOABOUT);
		_doConfig = new CheckBox(bot, DOCONFIG);
		_doEditDefaults = new CheckBox(bot, DOEDITDEFAULTS);
		_doEditGuest = new CheckBox(bot, DOEDITGUEST);
		_doPreview = new CheckBox(bot, DOPREVIEW);
		_doPrint = new CheckBox(bot, DOPRINT);
		_processAction = new CheckBox(bot, PROCESSACTION);
		_serveResource = new CheckBox(bot, SERVERESOURCE);
	}

	public Button getAddBtn() {
		return _addBtn;
	}

	public CheckBox getConstrcutFromSuperClass() {
		return _constrcutFromSuperClass;
	}

	public CheckBox getDestory() {
		return _destory;
	}

	public CheckBox getDoAbout() {
		return _doAbout;
	}

	public CheckBox getDoConfig() {
		return _doConfig;
	}

	public CheckBox getDoEdit() {
		return _doEdit;
	}

	public CheckBox getDoEditDefaults() {
		return _doEditDefaults;
	}

	public CheckBox getDoEditGuest() {
		return _doEditGuest;
	}

	public CheckBox getDoHelp() {
		return _doHelp;
	}

	public CheckBox getDoPreview() {
		return _doPreview;
	}

	public CheckBox getDoPrint() {
		return _doPrint;
	}

	public CheckBox getDoView() {
		return _doView;
	}

	public CheckBox getInheritedAbstractMethods() {
		return _inheritedAbstractMethods;
	}

	public CheckBox getInit() {
		return _init;
	}

	public Table getInterfaces() {
		return _interfaces;
	}

	public CheckBox getIsAbstract() {
		return _isAbstract;
	}

	public CheckBox getIsFinal() {
		return _isFinal;
	}

	public CheckBox getIsPublic() {
		return _isPublic;
	}

	public CheckBox getProcessAction() {
		return _processAction;
	}

	public Button getRemoveBtn() {
		return _removeBtn;
	}

	public CheckBox getServeResource() {
		return _serveResource;
	}

	private Button _addBtn;
	private CheckBox _constrcutFromSuperClass;
	private CheckBox _destory;
	private CheckBox _doAbout;
	private CheckBox _doConfig;
	private CheckBox _doEdit;
	private CheckBox _doEditDefaults;
	private CheckBox _doEditGuest;
	private CheckBox _doHelp;
	private CheckBox _doPreview;
	private CheckBox _doPrint;
	private CheckBox _doView;
	private CheckBox _inheritedAbstractMethods;
	private CheckBox _init;
	private Table _interfaces;
	private CheckBox _isAbstract;
	private CheckBox _isFinal;
	private CheckBox _isPublic;
	private CheckBox _processAction;
	private Button _removeBtn;
	private CheckBox _serveResource;

}
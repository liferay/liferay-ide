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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Radio;
import com.liferay.ide.swtbot.ui.page.Text;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Li Lu
 */
public class CreateJSFPortletWizard extends CreateLiferayPortletWizard {

	public CreateJSFPortletWizard(SWTWorkbenchBot bot) {
		super(bot, 4);

		_createViewFiles = new CheckBox(bot, CREATE_VIRW_FILES);
		_viewFolder = new Text(bot, VIEW_FOLDER);
		_standardJsf = new Radio(bot, STANDARD_JSF);
		_iceFaces = new Radio(bot, ICEFACES);
		_liferayFacesAlloy = new Radio(bot, LIFERAY_FACES_ALLOY);
		_primeFaces = new Radio(bot, PRIMEFACES);
		_richFaces = new Radio(bot, RICHFACES);
	}

	public void deSelectCreateViewFiles() {
		_createViewFiles.deselect();
	}

	public String getViewFolderText() {
		return _viewFolder.getText();
	}

	public boolean isCreateViewFilesChecked() {
		return _createViewFiles.isChecked();
	}

	public boolean isICEfacesEnabled() {
		return _iceFaces.isEnabled();
	}

	public boolean isICEfacesSelected() {
		return _iceFaces.isSelected();
	}

	public boolean isliferayFacesAlloyEnbled() {
		return _liferayFacesAlloy.isEnabled();
	}

	public boolean isLiferayFacesAlloySelected() {
		return _liferayFacesAlloy.isSelected();
	}

	public boolean isPrimeFacesEnbled() {
		return _primeFaces.isEnabled();
	}

	public boolean isPrimeFacesSelected() {
		return _primeFaces.isSelected();
	}

	public boolean isRichFacesEnbled() {
		return _richFaces.isEnabled();
	}

	public boolean isRichFacesSelected() {
		return _richFaces.isSelected();
	}

	public boolean isStandardJSFEnabled() {
		return _standardJsf.isEnabled();
	}

	public boolean isStandardJSFSelected() {
		return _standardJsf.isSelected();
	}

	public boolean isViewFolderEnabled() {
		return _viewFolder.isEnabled();
	}

	public void selectCreateViewFiles() {
		_createViewFiles.select();
	}

	public void selectViewTemplate(String lable) {
		switch (lable) {
			case STANDARD_JSF:
				_standardJsf.click();
			case ICEFACES:
				_iceFaces.click();
			case LIFERAY_FACES_ALLOY:
				_liferayFacesAlloy.click();
			case PRIMEFACES:
				_primeFaces.click();
			case RICHFACES:
				_richFaces.click();
		}
	}

	public void setViewFolderText(String text) {
		_viewFolder.setText(text);
	}

	private CheckBox _createViewFiles;
	private Radio _iceFaces;
	private Radio _liferayFacesAlloy;
	private Radio _primeFaces;
	private Radio _richFaces;
	private Radio _standardJsf;
	private Text _viewFolder;

}
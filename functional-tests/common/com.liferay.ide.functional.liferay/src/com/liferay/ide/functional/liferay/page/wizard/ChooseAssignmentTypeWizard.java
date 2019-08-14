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

package com.liferay.ide.functional.liferay.page.wizard;

import com.liferay.ide.functional.swtbot.page.Radio;
import com.liferay.ide.functional.swtbot.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Haoyi Sun
 */
public class ChooseAssignmentTypeWizard extends Wizard {

	public ChooseAssignmentTypeWizard(SWTBot bot) {
		super(bot);
	}

	public void clickAssignCreator() {
		getAssignCreator().click();
	}

	public void clickAssignResourceActions() {
		getAssignResourceActions().click();
	}

	public void clickAssignRoleById() {
		getAssignRoleById().click();
	}

	public void clickAssignRoleType() {
		getAssignRoleType().click();
	}

	public void clickAssignScriptedAssignment() {
		getAssignScriptedAssignment().click();
	}

	public void clickAssignUser() {
		getAssignUser().click();
	}

	public Radio getAssignCreator() {
		return new Radio(getShell().bot(), 0);
	}

	public Radio getAssignResourceActions() {
		return new Radio(getShell().bot(), 5);
	}

	public Radio getAssignRoleById() {
		return new Radio(getShell().bot(), 2);
	}

	public Radio getAssignRoleType() {
		return new Radio(getShell().bot(), 1);
	}

	public Radio getAssignScriptedAssignment() {
		return new Radio(getShell().bot(), 3);
	}

	public Radio getAssignUser() {
		return new Radio(getShell().bot(), 4);
	}

}
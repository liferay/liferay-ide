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

import com.liferay.ide.functional.swtbot.page.Text;
import com.liferay.ide.functional.swtbot.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewRuntime7Wizard extends Wizard {

	public NewRuntime7Wizard(SWTBot bot) {
		super(bot, 3);
	}

	public Text detectedPortalBundleType() {
		return new Text(getShell().bot(), DETECTED_PORTAL_BUNDLE_TYPE);
	}

	public String getDetectedPortalBundleType() {
		return detectedPortalBundleType().getText();
	}

	public String getName() {
		return name().getText();
	}

	public Text liferayPortalBundleDirectory() {
		return new Text(getShell().bot(), LIFERAY_PORTAL_BUNDLE_DIRECTORY);
	}

	public Text name() {
		return new Text(getShell().bot(), NAME);
	}

	public void setLiferayPortalBundleDirectory(String location) {
		liferayPortalBundleDirectory().setText(location);
	}

	public void setName(String name) {
		name().setText(name);
	}

}
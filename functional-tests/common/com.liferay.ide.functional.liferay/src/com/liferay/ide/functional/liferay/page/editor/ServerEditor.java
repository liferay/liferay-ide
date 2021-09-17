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

package com.liferay.ide.functional.liferay.page.editor;

import com.liferay.ide.functional.swtbot.page.CheckBox;
import com.liferay.ide.functional.swtbot.page.Editor;
import com.liferay.ide.functional.swtbot.page.Radio;
import com.liferay.ide.functional.swtbot.page.Text;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.forms.finder.widgets.SWTBotHyperlink;
import org.eclipse.ui.forms.widgets.Hyperlink;

import org.hamcrest.Matcher;

/**
 * @author Terry Jia
 * @author Rui Wang
 */
public class ServerEditor extends Editor {

	public ServerEditor(SWTWorkbenchBot bot) {
		super(bot);
	}

	public void clickCustomLaunchSettings() {
		getCustomLaunchSettings().click();
	}

	public void clickDefalutLaunchSettings() {
		getDefaultLaunchSettings().click();
	}

	public void clickHyperLink(String label, int hyperLinkIndex) {
		Matcher<Hyperlink> hyperLink = WidgetMatcherFactory.allOf(
			WidgetMatcherFactory.widgetOfType(Hyperlink.class), WidgetMatcherFactory.withMnemonic(label));

		Hyperlink link = (Hyperlink)bot.widget(hyperLink, hyperLinkIndex);

		SWTBotHyperlink selectHyperLink = new SWTBotHyperlink(link);

		selectHyperLink.click();
	}

	public void clickUseDeveloperMode() {
		getUseDeveloperMode().select();
	}

	public Radio getCustomLaunchSettings() {
		return new Radio(getPart().bot(), "Custom Launch Settings");
	}

	public Radio getDefaultLaunchSettings() {
		return new Radio(getPart().bot(), "Default Launch Settings");
	}

	public Text getExternalProperties() {
		return new Text(getPart().bot(), "External properties:");
	}

	public Text getHostName() {
		return new Text(getPart().bot(), "Host name:");
	}

	public Text getHttpPort() {
		return new Text(getPart().bot(), "Http Port:");
	}

	public boolean getHyperLink(String label) {
		Matcher<Hyperlink> hyperLink = WidgetMatcherFactory.allOf(
			WidgetMatcherFactory.widgetOfType(Hyperlink.class), WidgetMatcherFactory.withMnemonic(label));

		Hyperlink link = (Hyperlink)bot.widget(hyperLink);

		SWTBotHyperlink getHyperLink = new SWTBotHyperlink(link);

		getHyperLink.isEnabled();

		return getHyperLink.isEnabled();
	}

	public Text getMemoryArgs() {
		return new Text(getPart().bot(), "Memory args:");
	}

	public Text getPassword() {
		return new Text(getPart().bot(), "Password:");
	}

	public Text getServerName() {
		return new Text(getPart().bot(), "Server name:");
	}

	public CheckBox getUseDeveloperMode() {
		return new CheckBox(getPart().bot(), "Use developer mode");
	}

	public Text getUserName() {
		return new Text(getPart().bot(), "Username:");
	}

	public void setHttpPort(String port) {
		getHttpPort().setText(port);
	}

	public void setMemoryArgs(String memoryArgs) {
		getMemoryArgs().setText(memoryArgs);
	}

	public void setPassword(String password) {
		getPassword().setText(password);
	}

}
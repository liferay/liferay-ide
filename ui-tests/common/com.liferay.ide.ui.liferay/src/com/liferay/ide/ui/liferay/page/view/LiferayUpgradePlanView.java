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

package com.liferay.ide.ui.liferay.page.view;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withMnemonic;

import com.liferay.ide.ui.swtbot.page.Tree;
import com.liferay.ide.ui.swtbot.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.forms.finder.widgets.SWTBotImageHyperlink;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import org.hamcrest.Matcher;

/**
 * @author Lily Li
 */
public class LiferayUpgradePlanView extends View {

	public LiferayUpgradePlanView(SWTWorkbenchBot bot) {
		super(bot, LIFERAY_UPGRADE_PLAN);
	}

	public void click(String label) {
		Matcher matcherImageHyperLink = allOf(widgetOfType(Hyperlink.class), withMnemonic(label));

		ImageHyperlink link = (ImageHyperlink)bot.widget(matcherImageHyperLink);

		SWTBotImageHyperlink imageHyperLink = new SWTBotImageHyperlink(link);

		imageHyperLink.click();

		try {
			imageHyperLink.click();
		}
		catch (Exception e) {
		}
	}

	public Tree getSteps() {
		return new Tree(getPart().bot());
	}

}
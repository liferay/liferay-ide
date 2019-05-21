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

import com.liferay.ide.ui.swtbot.page.ToolbarButtonWithTooltip;
import com.liferay.ide.ui.swtbot.page.Tree;
import com.liferay.ide.ui.swtbot.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.forms.finder.widgets.SWTBotImageHyperlink;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import org.hamcrest.Matcher;

/**
 * @author Lily Li
 */
public class LiferayUpgradePlanView extends View {

	public LiferayUpgradePlanView(SWTWorkbenchBot bot) {
		super(bot, LIFERAY_UPGRADE_PLAN);

		SWTBotShell activeShell = bot.activeShell();

		_activeShellLabel = activeShell.getText();
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

	public void collapseAll() {
		getCollapseAll().click();
	}

	public void expandAll() {
		getExpandAll().click();
	}

	public ToolbarButtonWithTooltip getCollapseAll() {
		return new ToolbarButtonWithTooltip(_getShell().bot(), COLLAPSE_ALL);
	}

	public ToolbarButtonWithTooltip getExpandAll() {
		return new ToolbarButtonWithTooltip(_getShell().bot(), EXPAND_ALL);
	}

	public ToolbarButtonWithTooltip getRestartUpgradePlan() {
		return new ToolbarButtonWithTooltip(_getShell().bot(), RESTART_UPGRADE_PLAN);
	}

	public ToolbarButtonWithTooltip getShowProgressView() {
		return new ToolbarButtonWithTooltip(_getShell().bot(), SHOW_PROGRESS_VIEW);
	}

	public ToolbarButtonWithTooltip getShowUpgradePlanInfoView() {
		return new ToolbarButtonWithTooltip(_getShell().bot(), SHOW_UPGRADE_PLAN_INFO_VIEW);
	}

	public Tree getSteps() {
		return new Tree(getPart().bot());
	}

	public ToolbarButtonWithTooltip getSwitchUpgradePlan() {
		return new ToolbarButtonWithTooltip(_getShell().bot(), SWITCH_UPGRADE_PLAN);
	}

	public ToolbarButtonWithTooltip getUpgradePlanDetails() {
		return new ToolbarButtonWithTooltip(_getShell().bot(), UPGRADE_PLAN_DETAILS);
	}

	public boolean isVisible(String label) {
		Matcher matcherImageHyperLink = allOf(widgetOfType(Hyperlink.class), withMnemonic(label));

		ImageHyperlink link = (ImageHyperlink)bot.widget(matcherImageHyperLink);

		SWTBotImageHyperlink imageHyperLink = new SWTBotImageHyperlink(link);

		return imageHyperLink.isVisible();
	}

	public void restartUpgradePlan() {
		getRestartUpgradePlan().click();
	}

	public void showProgressView() {
		getShowProgressView().click();
	}

	public void showUpgradePlanInfoView() {
		getShowUpgradePlanInfoView().click();
	}

	public void switchUpgradePlan() {
		getSwitchUpgradePlan().click();
	}

	public void upgradePlanDetails() {
		getUpgradePlanDetails().click();
	}

	private SWTBotShell _getShell() {
		return bot.shell(_activeShellLabel);
	}

	private String _activeShellLabel;

}
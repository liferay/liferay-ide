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

package com.liferay.ide.upgrade.plan.ui.internal;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradePlanInfoView extends PageBookView {

	public static final String ID = "com.liferay.ide.upgrade.plan.info.view";

	@Override
	protected IPage createDefaultPage(PageBook pageBook) {
		MessagePage page = new MessagePage();

		initPage(page);

		page.createControl(pageBook);

		StringBuilder sb = new StringBuilder();

		sb.append("This view displays information about the selected item in the Liferay Upgrade Plan outline. Begin ");
		sb.append("a new upgrade plan by using \"Project > New Liferay Upgrade Plan...\" action. If an upgrade plan ");
		sb.append("is already active, then select any item in the outline view to display more information.");

		page.setMessage(sb.toString());

		return page;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart workbenchPart) {
		UpgradePlanInfoPage upgradeInfoPage = new UpgradePlanInfoPage(workbenchPart);

		initPage(upgradeInfoPage);

		if (workbenchPart instanceof ProjectExplorer) {
			upgradeInfoPage.setMessage("Information View for the ProjectExplorer");
		}
		else if (workbenchPart instanceof UpgradePlanView) {
			upgradeInfoPage.setMessage("Information View for the current Upgrade Plan");
		}

		upgradeInfoPage.createControl(getPageBook());

		return new PageRec(workbenchPart, upgradeInfoPage);
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart workbenchPart, PageRec pageRec) {
		IPage page = pageRec.page;

		page.dispose();

		pageRec.dispose();
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();

		if (page != null) {
			return page.getActiveEditor();
		}

		return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		if (part instanceof ProjectExplorer) {
			return true;
		}
		else if (part instanceof UpgradePlanView) {
			return true;
		}

		return false;
	}

}
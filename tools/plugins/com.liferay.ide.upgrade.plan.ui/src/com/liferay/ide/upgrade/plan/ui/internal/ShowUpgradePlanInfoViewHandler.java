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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.handlers.ShowViewHandler;

/**
 * @author Terry Jia
 */
public class ShowUpgradePlanInfoViewHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Map<String, String> parameters = new HashMap<>();

		parameters.put(IWorkbenchCommandConstants.VIEWS_SHOW_VIEW_PARM_ID, UpgradePlanInfoView.ID);

		ExecutionEvent executionEvent = new ExecutionEvent(
			null, parameters, event.getTrigger(), event.getApplicationContext());

		ShowViewHandler showViewHandler = new ShowViewHandler();

		showViewHandler.execute(executionEvent);

		return null;
	}

}
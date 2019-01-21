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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Gregory Amerson
 */
public class NewUpgradePlanHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEvaluationContext evaluationContext = (IEvaluationContext)event.getApplicationContext();

		Object variable = evaluationContext.getVariable("activeShell");

		if (variable instanceof Shell) {
			Shell shell = (Shell)variable;

			NewUpgradePlanDialog newUpgradePlanDialog = new NewUpgradePlanDialog(shell);

			return newUpgradePlanDialog.open();
		}

		return null;
	}

}
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

package com.liferay.ide.upgrade.plan.ui.dialog;

import com.liferay.ide.upgrade.plan.ui.model.NewUpgradePlanOp;

import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Terry Jia
 */
public class NewUpgradePlanDialog extends SapphireDialog {

	public NewUpgradePlanDialog(Shell shell) {
		super(shell, _op(), DefinitionLoader.sdef(NewUpgradePlanDialog.class).dialog());
	}

	private static NewUpgradePlanOp _op() {
		return NewUpgradePlanOp.TYPE.instantiate();
	}

}
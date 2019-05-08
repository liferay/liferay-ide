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

import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlansOp;

import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class SwitchUpgradePlanDialog extends SapphireDialog {

	public SwitchUpgradePlanDialog() {
		super(
			UIUtil.getActiveShell(), UpgradePlansOp.TYPE,
			DefinitionLoader.sdef(SwitchUpgradePlanDialog.class).dialog());
	}

	@Override
	protected Control createButtonBar(final Composite parent) {
		return null;
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

}
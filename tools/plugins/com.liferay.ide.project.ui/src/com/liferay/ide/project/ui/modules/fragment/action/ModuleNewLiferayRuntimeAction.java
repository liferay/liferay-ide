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

package com.liferay.ide.project.ui.modules.fragment.action;

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.wst.server.ui.ServerUIUtil;

/**
 * @author Terry Jia
 */
public class ModuleNewLiferayRuntimeAction extends SapphireActionHandler {

	@Override
	protected Object run(Presentation context) {
		SapphirePart part = context.part();

		Element element = part.getModelElement();

		NewModuleFragmentOp op = element.nearest(NewModuleFragmentOp.class);

		boolean oK = ServerUIUtil.showNewRuntimeWizard(
			((SwtPresentation)context).shell(), "liferay.bundle", null, "com.liferay.");

		if (oK) {
			SapphireUtil.refresh(op.property(NewModuleFragmentOp.PROP_LIFERAY_RUNTIME_NAME));
		}

		return Status.createOkStatus();
	}

}
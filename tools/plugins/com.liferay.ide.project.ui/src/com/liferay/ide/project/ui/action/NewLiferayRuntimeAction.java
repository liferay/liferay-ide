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

package com.liferay.ide.project.ui.action;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.wst.server.ui.ServerUIUtil;

/**
 * @author Gregory Amerson
 */
public class NewLiferayRuntimeAction extends SapphireActionHandler {

	public NewLiferayRuntimeAction() {
	}

	@Override
	protected Object run(Presentation context) {
		SwtPresentation presentationContext = (SwtPresentation)context;

		ServerUIUtil.showNewRuntimeWizard(presentationContext.shell(), "jst.web", null, "com.liferay.");

		return Status.createOkStatus();
	}

}
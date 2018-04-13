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

package com.liferay.ide.server.ui.view;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.ui.LiferayServerUI;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.provisional.IServerToolTip;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class BundleToolTip implements IServerToolTip {

	@Override
	public void createContent(Composite parent, IServer server) {
		StringBuffer sb = new StringBuffer();

		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			if (project.isOpen()) {
				try {
					IMarker[] markers = project.findMarkers(
						LiferayServerCore.BUNDLE_OUTPUT_ERROR_MARKER_TYPE, false, 0);

					if (ListUtil.isNotEmpty(markers)) {
						sb.append("Error \"Could not create output jar\" happened on " + project.getName() + "\n");
					}
				}
				catch (CoreException ce) {
					LiferayServerUI.logError(ce);
				}
			}
		}

		String s = sb.toString();

		if (!s.equals("")) {
			StyledText sText = new StyledText(parent, SWT.NONE);

			Display display = parent.getDisplay();

			sText.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));

			sText.setEditable(false);
			sText.setBackground(parent.getBackground());
			sText.setText(sb.toString());
		}
	}

}
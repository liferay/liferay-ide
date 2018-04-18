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

package com.liferay.ide.server.ui.navigator;

import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.ui.internal.navigator.NavigatorContentService;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.INavigatorContentService;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class NavigatorUIUtil {

	public static CommonViewer getViewer(ICommonContentExtensionSite config) {
		return (CommonViewer)((NavigatorContentService)config.getService()).getViewer();
	}

	public static void refreshUI(INavigatorContentService service, IServer server) {
		UIUtil.async(
			new Runnable() {

				public void run() {
					try {
						CommonViewer viewer = (CommonViewer)((NavigatorContentService)service).getViewer();

						viewer.refresh(true);
						viewer.setExpandedState(server, true);
					}
					catch (Exception e) {
					}
				}

			});
	}

}
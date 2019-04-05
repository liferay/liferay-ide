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

package com.liferay.ide.server.ui.handlers;

import com.liferay.ide.server.core.ILiferayServer;

import java.net.URL;

import org.eclipse.osgi.util.NLS;

/**
 * @author Eric Min
 */
public class OpenPortalHomeHandler extends OpenPortalURLHandler {

	protected URL getPortalURL(Object selected) {
		ILiferayServer liferayServer = getLiferayServer(selected);

		return liferayServer.getPortalHomeUrl();
	}

	@Override
	protected String getPortalURLTitle() {
		return Msgs.liferayPortal;
	}

	private static class Msgs extends NLS {

		public static String liferayPortal;

		static {
			initializeMessages(OpenPortalHomeHandler.class.getName(), Msgs.class);
		}

	}

}
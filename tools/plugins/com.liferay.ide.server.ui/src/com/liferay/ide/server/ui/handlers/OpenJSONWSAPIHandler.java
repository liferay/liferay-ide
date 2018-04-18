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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.osgi.util.NLS;

/**
 * @author Eric Min
 */
public class OpenJSONWSAPIHandler extends OpenPortalURLHandler {

	protected URL getPortalURL(Object selected) {
		try {
			return new URL(getLiferayServer(selected).getPortalHomeUrl(), "/api/jsonws");
		}
		catch (MalformedURLException murle) {
		}

		return null;
	}

	@Override
	protected String getPortalURLTitle() {
		return Msgs.jsonWsApi;
	}

	private static class Msgs extends NLS {

		public static String jsonWsApi;

		static {
			initializeMessages(OpenJSONWSAPIHandler.class.getName(), Msgs.class);
		}

	}

}
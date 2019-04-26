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

package com.liferay.portal.apache.bridges.struts;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portlet.PortletURLImplWrapper;

import org.apache.portals.bridges.struts.StrutsPortletURL;

/**
 * @author Michael Young
 */
public class LiferayStrutsPortletURLImpl extends PortletURLImplWrapper {

	public LiferayStrutsPortletURLImpl(
		PortletResponseImpl portletResponseImpl, long plid, String lifecycle) {

		super(portletResponseImpl, plid, lifecycle);
	}

	@Override
	public void setParameter(String name, String value) {
		super.setParameter(name, value);

		// Add parameters from the query string because bridges passes these
		// through instead of setting them on the portlet URL

		try {
			if (name.equals(StrutsPortletURL.PAGE)) {
				String decodedValue = HttpUtil.decodeURL(value);

				String[] urlComponents = decodedValue.split("\\?", 2);

				if (urlComponents.length != 2) {
					return;
				}

				String[] nameValueArray = urlComponents[1].split("\\&");

				for (String nameValue : nameValueArray) {
					String[] nameValuePair = nameValue.split("\\=", 2);

					if (nameValuePair.length == 2) {
						super.setParameter(nameValuePair[0], nameValuePair[1]);
					}
					else if (nameValuePair.length == 1) {
						super.setParameter(nameValuePair[0], "true");
					}
				}
			}
		}
		catch (Throwable t) {
			_log.error("Could not parse Struts page query string " + value, t);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayStrutsPortletURLImpl.class);

}
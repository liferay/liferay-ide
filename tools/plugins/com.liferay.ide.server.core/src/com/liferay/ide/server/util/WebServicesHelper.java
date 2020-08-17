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

package com.liferay.ide.server.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gregory Amerson
 */
public class WebServicesHelper {

	public WebServicesHelper(URL webServicesListURL) {
		this.webServicesListURL = webServicesListURL;
	}

	public String[] getWebServiceNames() {
		if (wsdlNameURLMap == null) {
			initMap();
		}

		Set<String> keySet = wsdlNameURLMap.keySet();

		return keySet.toArray(new String[0]);
	}

	public String getWebServiceWSDLURLByName(String serviceName) {
		if (wsdlNameURLMap == null) {
			initMap();
		}

		return wsdlNameURLMap.get(serviceName);
	}

	protected void initMap() {
		try {
			wsdlNameURLMap = new HashMap<>();

			String webServicesString = CoreUtil.readStreamToString(webServicesListURL.openStream());

			List<String> wsdlUrls = _pullLinks(webServicesString);

			for (String url : wsdlUrls) {
				String name = _pullServiceName(url);

				if (!CoreUtil.isNullOrEmpty(name)) {
					wsdlNameURLMap.put(name, url);
				}
			}
		}
		catch (IOException ioe) {
			LiferayServerCore.logError("Unable to initial web services list.");
		}
	}

	protected URL webServicesListURL;
	protected Map<String, String> wsdlNameURLMap = null;

	private List<String> _pullLinks(String text) {
		List<String> links = new ArrayList<>();

		String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";

		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(text);

		while (m.find()) {
			String urlStr = m.group();

			if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
				urlStr = urlStr.substring(1, urlStr.length() - 1);
			}

			links.add(urlStr);
		}

		return links;
	}

	private String _pullServiceName(String wsdlUrl) {
		String regex = "axis/(\\w+)\\?wsdl$";

		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(wsdlUrl);

		if (m.find()) {
			return m.group(1);
		}

		return null;
	}

}
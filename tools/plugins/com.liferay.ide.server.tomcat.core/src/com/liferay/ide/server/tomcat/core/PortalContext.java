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

package com.liferay.ide.server.tomcat.core;

import java.util.Locale;

/**
 * @author Seiphon Wang
 */
public class PortalContext {

	public PortalContext(String name) {
		String baseName = name;

		if (baseName.startsWith("/")) {
			baseName = baseName.substring(1);
		}

		baseName = baseName.replaceAll("/", "#");

		if (baseName.startsWith("##") || "".equals(baseName)) {
			baseName = "ROOT" + baseName;
		}

		String lowerCaseBaseName = baseName.toLowerCase(Locale.ENGLISH);

		if (lowerCaseBaseName.endsWith(".war") || lowerCaseBaseName.endsWith(".xml")) {
			baseName = baseName.substring(0, baseName.length() - 4);
		}

		_baseName = baseName;

		String path;

		int versionIndex = _baseName.indexOf("##");

		if (versionIndex > -1) {
			_version = _baseName.substring(versionIndex + 2);

			path = _baseName.substring(0, versionIndex);
		}
		else {
			_version = "";

			path = _baseName;
		}

		if ("ROOT".equals(path)) {
			_path = "";
		}
		else {
			_path = "/" + path.replaceAll("#", "/");
		}

		if (versionIndex > -1) {
			_name = _path + "##" + _version;
		}
		else {
			_name = _path;
		}
	}

	public String getBaseName() {
		return _baseName;
	}

	public String getName() {
		return _name;
	}

	public String getPath() {
		return _path;
	}

	public String getVersion() {
		return _version;
	}

	private final String _baseName;
	private final String _name;
	private final String _path;
	private final String _version;

}
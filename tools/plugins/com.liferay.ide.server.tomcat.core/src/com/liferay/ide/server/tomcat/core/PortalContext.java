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

	public static final String FWD_SLASH_REPLACEMENT = "#";

	public static final String ROOT_NAME = "ROOT";

	public static final String VERSION_MARKER = "##";

	public static PortalContext extractFromPath(String path) {
		path = path.replaceAll("\\\\", "/");

		while (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		int lastSegment = path.lastIndexOf('/');

		if (lastSegment > 0) {
			path = path.substring(lastSegment + 1);
		}

		return new PortalContext(path, true);
	}

	public PortalContext(String name, boolean stripFileExtension) {
		String tmp1 = name;

		if (tmp1.startsWith("/")) {
			tmp1 = tmp1.substring(1);
		}

		tmp1 = tmp1.replaceAll("/", FWD_SLASH_REPLACEMENT);

		if (tmp1.startsWith(VERSION_MARKER) || "".equals(tmp1)) {
			tmp1 = ROOT_NAME + tmp1;
		}

		String localeTmp1 = tmp1.toLowerCase(Locale.ENGLISH);

		if (stripFileExtension && (localeTmp1.endsWith(".war") || localeTmp1.endsWith(".xml"))) {
			tmp1 = tmp1.substring(0, tmp1.length() - 4);
		}

		_baseName = tmp1;

		String tmp2;

		int versionIndex = _baseName.indexOf(VERSION_MARKER);

		if (versionIndex > -1) {
			_version = _baseName.substring(versionIndex + 2);

			tmp2 = _baseName.substring(0, versionIndex);
		}
		else {
			_version = "";

			tmp2 = _baseName;
		}

		if (ROOT_NAME.equals(tmp2)) {
			_path = "";
		}
		else {
			_path = "/" + tmp2.replaceAll(FWD_SLASH_REPLACEMENT, "/");
		}

		if (versionIndex > -1) {
			_name = _path + VERSION_MARKER + _version;
		}
		else {
			_name = _path;
		}
	}

	public PortalContext(String path, String version) {
		if ((path == null) || "/".equals(path) || "/ROOT".equals(path)) {
			_path = "";
		}
		else {
			_path = path;
		}

		if (version == null) {
			_version = "";
		}
		else {
			_version = version;
		}

		if ("".equals(_version)) {
			_name = _path;
		}
		else {
			_name = _path + VERSION_MARKER + _version;
		}

		StringBuilder tmp = new StringBuilder();

		if ("".equals(_path)) {
			tmp.append(ROOT_NAME);
		}
		else {
			String substringedPath = _path.substring(1);

			tmp.append(substringedPath.replaceAll("/", FWD_SLASH_REPLACEMENT));
		}

		if (this._version.length() > 0) {
			tmp.append(VERSION_MARKER);
			tmp.append(_version);
		}

		_baseName = tmp.toString();
	}

	public String getBaseName() {
		return _baseName;
	}

	public String getDisplayName() {
		StringBuilder tmp = new StringBuilder();

		if ("".equals(_path)) {
			tmp.append('/');
		}
		else {
			tmp.append(_path);
		}

		if (!"".equals(_version)) {
			tmp.append(VERSION_MARKER);
			tmp.append(_version);
		}

		return tmp.toString();
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

	@Override
	public String toString() {
		return getDisplayName();
	}

	private final String _baseName;
	private final String _name;
	private final String _path;
	private final String _version;

}
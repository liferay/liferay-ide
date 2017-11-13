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

package com.liferay.ide.project.core;

import static com.liferay.ide.sdk.core.ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX;
import static com.liferay.ide.sdk.core.ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX;
import static com.liferay.ide.sdk.core.ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
import static com.liferay.ide.sdk.core.ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
import static com.liferay.ide.sdk.core.ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX;
import static com.liferay.ide.sdk.core.ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX;

import java.io.File;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Terry Jia
 */
public class BinaryProjectRecord {

	public BinaryProjectRecord(File binaryFile) {
		_binaryFile = binaryFile;

		_setNames();
	}

	public File getBinaryFile() {
		return _binaryFile;
	}

	public String getBinaryName() {
		return _binaryName;
	}

	public String getDisplayName() {
		return _displayName;
	}

	public String getFilePath() {
		return _filePath;
	}

	public String getLiferayPluginName() {
		if (hook) {
			return getDisplayName() + HOOK_PLUGIN_PROJECT_SUFFIX;
		}
		else if (layoutTpl) {
			return getDisplayName() + LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
		}
		else if (portlet) {
			return getDisplayName() + PORTLET_PLUGIN_PROJECT_SUFFIX;
		}
		else if (theme) {
			return getDisplayName() + THEME_PLUGIN_PROJECT_SUFFIX;
		}
		else if (ext) {
			return getDisplayName() + EXT_PLUGIN_PROJECT_SUFFIX;
		}
		else if (web) {
			return getDisplayName() + WEB_PLUGIN_PROJECT_SUFFIX;
		}

		return null;
	}

	public String getLiferayVersion() {
		return _liferayVersion;
	}

	public boolean isConflicts() {
		return conflicts;
	}

	public boolean isExt() {
		return ext;
	}

	public boolean isHook() {
		return hook;
	}

	public boolean isLayoutTpl() {
		return layoutTpl;
	}

	public boolean isPortlet() {
		return portlet;
	}

	public boolean isTheme() {
		return theme;
	}

	public boolean isWeb() {
		return web;
	}

	public void setBinaryFile(File binaryFile) {
		_binaryFile = binaryFile;
	}

	public void setBinaryName(String binaryName) {
		_binaryName = binaryName;
	}

	public void setConflicts(boolean hasConflicts) {
		conflicts = hasConflicts;
	}

	public void setDisplayName(String liferayPluginName) {
		_displayName = liferayPluginName;
	}

	public void setFilePath(String label) {
		_filePath = label;
	}

	protected boolean conflicts;
	protected boolean ext;
	protected boolean hook;
	protected boolean layoutTpl;
	protected boolean portlet;
	protected boolean theme;
	protected boolean web;

	private void _setNames() {
		if (_binaryFile == null) {
			return;
		}

		_binaryName = _binaryFile.getName();

		_filePath = _binaryFile.getAbsolutePath();

		_setPluginProperties();
	}

	private void _setPluginProperties() {
		if (_binaryName == null) {
			return;
		}

		int index = -1;

		if (_binaryName.contains(HOOK_PLUGIN_PROJECT_SUFFIX)) {
			index = _binaryName.indexOf(HOOK_PLUGIN_PROJECT_SUFFIX);

			hook = index != -1 ? true : false;
		}
		else if (_binaryName.contains(THEME_PLUGIN_PROJECT_SUFFIX)) {
			index = _binaryName.indexOf(THEME_PLUGIN_PROJECT_SUFFIX);

			theme = index != -1 ? true : false;
		}
		else if (_binaryName.contains(PORTLET_PLUGIN_PROJECT_SUFFIX)) {
			index = _binaryName.indexOf(PORTLET_PLUGIN_PROJECT_SUFFIX);

			portlet = index != -1 ? true : false;
		}
		else if (_binaryName.contains(LAYOUTTPL_PLUGIN_PROJECT_SUFFIX)) {
			index = _binaryName.indexOf(LAYOUTTPL_PLUGIN_PROJECT_SUFFIX);

			layoutTpl = index != -1 ? true : false;
		}
		else if (_binaryName.contains(EXT_PLUGIN_PROJECT_SUFFIX)) {
			index = _binaryName.indexOf(EXT_PLUGIN_PROJECT_SUFFIX);

			ext = index != -1 ? true : false;
		}
		else if (_binaryName.contains(WEB_PLUGIN_PROJECT_SUFFIX)) {
			index = _binaryName.indexOf(WEB_PLUGIN_PROJECT_SUFFIX);

			web = index != -1 ? true : false;
		}

		if (index != -1) {
			_displayName = _binaryName.substring(0, index);
		}

		index = _binaryName.lastIndexOf("-");

		if (index != -1) {
			_liferayVersion = _binaryName.substring(index + 1, _binaryName.lastIndexOf("."));
		}
	}

	private File _binaryFile;
	private String _binaryName;
	private String _displayName;
	private String _filePath;
	private String _liferayVersion;

}
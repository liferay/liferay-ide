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

package com.liferay.ide.sdk.core;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.internal.core.XMLMemento;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public final class SDKManager {

	public static SDKManager getInstance() {
		if (_instance == null) {
			_instance = new SDKManager();
		}

		return _instance;
	}

	public static Version getLeastValidVersion() {
		return ISDKConstants.LEAST_SUPPORTED_SDK_VERSION;
	}

	public synchronized void addSDK(SDK sdk) {
		if (sdk == null) {
			throw new IllegalArgumentException("sdk cannot be null");
		}

		if (!_initialized) {
			_initialize();
		}

		if (!containsSDK(sdk)) {
			_sdkList.add(sdk);
		}

		if (_sdkList.size() == 1) {
			sdk.setDefault(true);
		}

		_saveSDKs();

		_fireSDKEvent(new SDK[] {sdk}, _EVENT_ADDED);
	}

	public void addSDKListener(ISDKListener listener) {
		synchronized (sdkListeners) {
			sdkListeners.add(listener);
		}
	}

	public synchronized void clearSDKs() {
		this._sdkList.clear();

		_saveSDKs();

		_fireSDKEvent(new SDK[0], _EVENT_REMOVED);
	}

	public boolean containsSDK(SDK theSDK) {
		if ((theSDK != null) && ListUtil.isNotEmpty(getSDKs())) {
			for (SDK sdk : getSDKs()) {
				if (theSDK.getName().equals(sdk.getName()) && theSDK.getLocation().equals(sdk.getLocation())) {
					return true;
				}
			}
		}

		return false;
	}

	public SDK getDefaultSDK() {
		for (SDK sdk : getSDKs()) {
			if (sdk.isDefault()) {
				return sdk;
			}
		}

		return null;
	}

	// current list of available sdks

	public SDK getSDK(IPath sdkLocation) {
		for (SDK sdk : getSDKs()) {
			if (sdk.getLocation().equals(sdkLocation)) {
				return sdk;
			}
		}

		return null;
	}

	public SDK getSDK(String sdkName) {
		if (sdkName == null) {
			return null;
		}

		for (SDK sdk : getSDKs()) {
			if (sdkName.equals(sdk.getName())) {
				return sdk;
			}
		}

		return null;
	}

	public synchronized SDK[] getSDKs() {
		if (!_initialized) {
			_initialize();
		}

		return _sdkList.toArray(new SDK[0]);
	}

	public void removeSDKListener(ISDKListener listener) {
		synchronized (sdkListeners) {
			sdkListeners.remove(listener);
		}
	}

	public synchronized void setSDKs(SDK[] sdks) {
		if (ListUtil.isEmpty(sdks)) {
			throw new IllegalArgumentException("sdk array cannot be null or empty");
		}

		if (CoreUtil.containsNullElement(sdks)) {
			throw new IllegalArgumentException("sdk array contains null element");
		}

		this._sdkList.clear();

		for (SDK sdk : sdks) {
			_sdkList.add(sdk);
		}

		_saveSDKs();

		_fireSDKEvent(sdks, _EVENT_CHANGED);
	}

	protected List<ISDKListener> sdkListeners = new ArrayList<>(3);

	private SDKManager() {
		_instance = this;
	}

	private void _fireSDKEvent(SDK[] sdks, byte event) {
		if (!sdkListeners.isEmpty()) {
			List<ISDKListener> clone = new ArrayList<>();

			clone.addAll(sdkListeners);

			for (ISDKListener listener : clone) {
				try {
					if (event == _EVENT_ADDED) {
						listener.sdksAdded(sdks);
					}
					else if (event == _EVENT_CHANGED) {
						listener.sdksChanged(sdks);
					}
					else if (event == _EVENT_REMOVED) {
						listener.sdksRemoved(sdks);
					}
				}
				catch (Exception e) {
					SDKCorePlugin.logError("error in sdk listener.", e);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private IEclipsePreferences _getPrefs() {
		return new InstanceScope().getNode(SDKCorePlugin.PREFERENCE_ID);
	}

	private void _initialize() {
		_loadSDKs();

		_initialized = true;
	}

	private void _loadSDKs() {
		_sdkList = new ArrayList<>();

		IEclipsePreferences prefs = _getPrefs();

		String sdksXmlString = prefs.get("sdks", null);

		if (!CoreUtil.isNullOrEmpty(sdksXmlString)) {
			try(InputStream inputStream = new ByteArrayInputStream(sdksXmlString.getBytes("UTF-8"));
					InputStreamReader reader = new InputStreamReader(inputStream)) {
				XMLMemento root = XMLMemento.createReadRoot(reader, "UTF-8");

				String defaultSDKName = root.getString("default");

				XMLMemento[] children = root.getChildren("sdk");

				SDK defaultSDK = null;

				for (XMLMemento sdkElement : children) {
					SDK sdk = new SDK();

					sdk.loadFromMemento(sdkElement);

					boolean def = false;

					if ((sdk.getName() != null) && sdk.getName().equals(defaultSDKName) && (defaultSDK == null)) {
						def = true;
					}

					if (def) {
						sdk.setDefault(def);
						defaultSDK = sdk;
					}

					_sdkList.add(sdk);
				}
			}
			catch (Exception e) {
				SDKCorePlugin.logError(e);
			}
		}
	}

	private void _saveSDKs() {
		XMLMemento root = XMLMemento.createWriteRoot("sdks");

		for (SDK sdk : _sdkList) {
			XMLMemento child = root.createChild("sdk");

			if (FileUtil.notExists(sdk.getLocation())) {
				continue;
			}

			sdk.saveToMemento(child);

			if (sdk.isDefault()) {
				root.putString("default", sdk.getName());
			}
		}

		try(StringWriter writer = new StringWriter()) {
			root.save(writer);

			_getPrefs().put("sdks", writer.toString());

			_getPrefs().flush();
		}
		catch (Exception e) {
			LiferayCore.logError(e);
		}
	}

	private static final byte _EVENT_ADDED = 0;

	private static final byte _EVENT_CHANGED = 1;

	private static final byte _EVENT_REMOVED = 2;

	private static SDKManager _instance;

	private boolean _initialized = false;
	private List<SDK> _sdkList;

}
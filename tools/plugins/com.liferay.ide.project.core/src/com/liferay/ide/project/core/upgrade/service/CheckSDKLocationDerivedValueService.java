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

package com.liferay.ide.project.core.upgrade.service;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.upgrade.CodeUpgradeOp;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Andy Wu
 */
public class CheckSDKLocationDerivedValueService extends DerivedValueService {

	@Override
	protected String compute() {
		CodeUpgradeOp op = _op();

		final Path path = SapphireUtil.getContent(op.getSdkLocation());

		SDK sdk = SDKUtil.createSDKFromLocation(PathBridge.create(path));

		String liferay62ServerLocation = null;

		try {
			if (sdk != null) {
				Map<String, Object> buildProperties = sdk.getBuildProperties(true);

				liferay62ServerLocation = (String)(buildProperties.get(ISDKConstants.PROPERTY_APP_SERVER_PARENT_DIR));

				_checkProjects(op, sdk);
			}
			else {
				_setAllStateFalse(op);
			}
		}
		catch (CoreException ce) {
		}

		return liferay62ServerLocation;
	}

	@Override
	protected void initDerivedValueService() {
		super.initDerivedValueService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		Value<Object> sdkLocation = _op().property(CodeUpgradeOp.PROP_SDK_LOCATION);

		sdkLocation.attach(_listener);
	}

	private void _checkProjects(CodeUpgradeOp op, SDK sdk) {
		File[] portlets = _getFiles(sdk, "portlet");

		if (portlets != null) {
			for (File file : portlets) {
				if (file.isDirectory()) {
					op.setHasPortlet("true");

					Path path = new Path(file.getPath()).append("docroot/WEB-INF/service.xml");

					File serviceXml = path.toFile();

					if (FileUtil.exists(serviceXml)) {
						op.setHasServiceBuilder("true");
					}
				}
			}
		}

		File[] hooks = _getFiles(sdk, "hook");

		if (hooks != null) {
			for (File file : hooks) {
				if (file.isDirectory()) {
					op.setHasHook("true");

					Path path = new Path(file.getPath()).append("docroot/WEB-INF/service.xml");

					File serviceXml = path.toFile();

					if (FileUtil.exists(serviceXml)) {
						op.setHasServiceBuilder("true");
					}
				}
			}
		}

		File[] exts = _getFiles(sdk, "ext");

		if (exts != null) {
			for (File file : exts) {
				if (file.isDirectory()) {
					op.setHasExt("true");

					break;
				}
			}
		}

		File[] layouttpls = _getFiles(sdk, "layouttpl");

		if (layouttpls != null) {
			for (File file : layouttpls) {
				if (file.isDirectory()) {
					op.setHasLayout("true");

					break;
				}
			}
		}

		File[] themes = _getFiles(sdk, "theme");

		if (themes != null) {
			for (File file : themes) {
				if (file.isDirectory()) {
					op.setHasTheme("true");

					break;
				}
			}
		}

		File[] webs = _getFiles(sdk, "web");

		if (webs != null) {
			for (File file : webs) {
				if (file.isDirectory()) {
					op.setHasWeb("true");

					break;
				}
			}
		}
	}

	private File[] _getFiles(SDK sdk, String projectType) {
		IPath location = sdk.getLocation();

		IPath folderPath = location.append(sdk.getPluginFolder(projectType));

		File folder = folderPath.toFile();

		if (FileUtil.notExists(folder)) {
			return new File[0];
		}

		return folder.listFiles();
	}

	private CodeUpgradeOp _op() {
		return context(CodeUpgradeOp.class);
	}

	private void _setAllStateFalse(CodeUpgradeOp op) {
		op.setHasPortlet(false);
		op.setHasServiceBuilder(false);
		op.setHasHook(false);
		op.setHasExt(false);
		op.setHasLayout(false);
		op.setHasTheme(false);
		op.setHasWeb(false);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}
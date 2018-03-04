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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.core.model.Hook;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.EnablementService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Gregory Amerson
 */
public class CustomJspsEnablementService extends EnablementService {

	@Override
	protected Boolean compute() {
		boolean enablement = true;

		ElementHandle<CustomJspDir> elementHandle = _hook().getCustomJspDir();

		CustomJspDir customJspDir = elementHandle.content();

		if (customJspDir != null) {
			IProject project = _hook().adapt(IProject.class);
			Path customJspDirPath = customJspDir.getValue().content(true);

			if ((project != null) && (customJspDirPath != null)) {
				IWebProject lrproject = LiferayCore.create(IWebProject.class, project);

				if (lrproject != null) {
					IFolder defaultWebappDir = lrproject.getDefaultDocrootFolder();

					if (FileUtil.exists(defaultWebappDir)) {
						IFolder customJspFolder = defaultWebappDir.getFolder(PathBridge.create(customJspDirPath));

						enablement = FileUtil.exists(customJspFolder);
					}
				}
			}
		}

		return enablement;
	}

	@Override
	protected void initEnablementService() {
		Listener listener = new FilteredListener<PropertyEvent>() {

			protected void handleTypedEvent(PropertyEvent event) {
				refresh();
			};

		};

		ElementHandle<Element> element = _hook().property(Hook.PROP_CUSTOM_JSP_DIR);

		element.attach(listener, CustomJspDir.PROP_VALUE.name());
	}

	private Hook _hook() {
		return context(Hook.class);
	}

}
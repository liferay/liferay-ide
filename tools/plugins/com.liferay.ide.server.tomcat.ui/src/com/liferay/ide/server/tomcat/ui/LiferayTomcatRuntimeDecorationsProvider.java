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

package com.liferay.ide.server.tomcat.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.ui.IDecorationsProvider;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("rawtypes")
public class LiferayTomcatRuntimeDecorationsProvider implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IDecorationsProvider.class.equals(adapterType)) {
			return new LiferayDecorationsProvider((IRuntime)adaptableObject);
		}
		else {
			return null;
		}
	}

	public Class<?>[] getAdapterList() {
		return _ADAPTER_TYPES;
	}

	public class LiferayDecorationsProvider implements IDecorationsProvider {

		// private IRuntime runtime;

		public LiferayDecorationsProvider(IRuntime adaptableObject) {

			// runtime = adaptableObject;

		}

		public ImageDescriptor getIcon() {
			return LiferayTomcatUIPlugin.imageDescriptorFromPlugin(
				LiferayTomcatUIPlugin.PLUGIN_ID, "icons/liferay_logo_16.png");
		}

	}

	private static final Class<?>[] _ADAPTER_TYPES = {IDecorationsProvider.class};

}
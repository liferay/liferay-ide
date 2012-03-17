/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.server.jboss.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.ui.IDecorationsProvider;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("rawtypes")
public class LiferayJBossRuntimeDecorationsProvider implements IAdapterFactory {

	public class LiferayDecorationsProvider implements IDecorationsProvider {

		// private IRuntime runtime;

		public LiferayDecorationsProvider(IRuntime adaptableObject) {
			// runtime = adaptableObject;
		}

		public ImageDescriptor getIcon() {
			return LiferayJBossUIPlugin.imageDescriptorFromPlugin(
				LiferayJBossUIPlugin.PLUGIN_ID, "icons/e16/server.png");
		}

	}

	private static final Class<?>[] ADAPTER_TYPES = {
		IDecorationsProvider.class
	};

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IDecorationsProvider.class.equals(adapterType)) {
			return new LiferayDecorationsProvider((IRuntime) adaptableObject);
		}
		else {
			return null;
		}
	}

	public Class<?>[] getAdapterList() {
		return ADAPTER_TYPES;
	}

}

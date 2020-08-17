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

package com.liferay.ide.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Gregory Amerson
 */
public class LiferayProjectProviderReader extends ExtensionReader<ILiferayProjectProvider> {

	public LiferayProjectProviderReader() {
		super(LiferayCore.PLUGIN_ID, _EXTENSION, _PROVIDER_ELEMENT);
	}

	public ILiferayProjectProvider[] getProviders() {
		return getExtensions().toArray(new ILiferayProjectProvider[0]);
	}

	public ILiferayProjectProvider[] getProviders(Class<?> type) {
		List<ILiferayProjectProvider> providers = new ArrayList<>();

		for (ILiferayProjectProvider provider : getExtensions()) {
			if (provider.provides(type)) {
				providers.add(provider);
			}
		}

		return providers.toArray(new ILiferayProjectProvider[0]);
	}

	public ILiferayProjectProvider[] getProviders(String projectType) {
		List<ILiferayProjectProvider> retval = new ArrayList<>();

		ILiferayProjectProvider[] providers = getProviders();

		for (ILiferayProjectProvider provider : providers) {
			String type = provider.getProjectType();

			if (type.equals(projectType)) {
				retval.add(provider);
			}
		}

		return retval.toArray(new ILiferayProjectProvider[0]);
	}

	@Override
	protected ILiferayProjectProvider initElement(
		IConfigurationElement configElement, ILiferayProjectProvider provider) {

		String shortName = configElement.getAttribute(_ATTRIBUTE_SHORTNAME);
		String displayName = configElement.getAttribute(_ATTRIBUTE_DISPLAYNAME);
		String priority = configElement.getAttribute(_ATTRIBUTE_PRIORITY);
		String type = configElement.getAttribute(_ATTRIBUTE_PROJECTTYPE);
		boolean isDefault = Boolean.parseBoolean(configElement.getAttribute(_ATTRIBUTE_DEFAULT));

		AbstractLiferayProjectProvider projectProvider = (AbstractLiferayProjectProvider)provider;

		projectProvider.setShortName(shortName);
		projectProvider.setDisplayName(displayName);
		projectProvider.setProjectType(type);

		int priorityValue = 10;

		if (Objects.equals("lowest", priority)) {
			priorityValue = 1;
		}
		else if (Objects.equals("low", priority)) {
			priorityValue = 2;
		}
		else if (Objects.equals("normal", priority)) {
			priorityValue = 3;
		}
		else if (Objects.equals("high", priority)) {
			priorityValue = 4;
		}
		else if (Objects.equals("highest", priority)) {
			priorityValue = 5;
		}

		projectProvider.setPriority(priorityValue);
		projectProvider.setDefault(isDefault);

		return provider;
	}

	private static final String _ATTRIBUTE_DEFAULT = "default";

	private static final String _ATTRIBUTE_DISPLAYNAME = "displayName";

	private static final String _ATTRIBUTE_PRIORITY = "priority";

	private static final String _ATTRIBUTE_PROJECTTYPE = "projectType";

	private static final String _ATTRIBUTE_SHORTNAME = "shortName";

	private static final String _EXTENSION = "liferayProjectProviders";

	private static final String _PROVIDER_ELEMENT = "liferayProjectProvider";

}
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

package com.liferay.ide.portlet.core;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;

/**
 * @author Greg Amerson
 */
public class PluginPropertiesConfiguration extends PropertiesConfiguration {

	public PluginPropertiesConfiguration() throws ConfigurationException {
		setDelimiterParsingDisabled(true);
	}

	@Override
	protected PropertiesConfigurationLayout createLayout() {
		return new PluginPropertiesConfigurationLayout(this);
	}

}
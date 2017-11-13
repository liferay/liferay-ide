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

import com.liferay.ide.core.ExtensionReader;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Simon Jiang
 */
public class UpgradeProjectHandlerReader extends ExtensionReader<AbstractUpgradeProjectHandler> {

	public UpgradeProjectHandlerReader() {
		super(ProjectCore.PLUGIN_ID, _EXTENSION, _UPGRADEACTION_ELEMENT);
	}

	public List<AbstractUpgradeProjectHandler> getUpgradeActions() {
		return getExtensions();
	}

	@Override
	protected AbstractUpgradeProjectHandler initElement(
		IConfigurationElement configElement, AbstractUpgradeProjectHandler upgradeHandler) {

		upgradeHandler.setName(configElement.getAttribute(_UPGRADEHANDLERNAME_ELEMENT));

		upgradeHandler.setDescription(configElement.getAttribute(_UPGRADEHANDLERDESCRIPTION_ELEMENT));

		return upgradeHandler;
	}

	private static final String _EXTENSION = "upgradeProjectHandlers";

	private static final String _UPGRADEACTION_ELEMENT = "upgradeProjectHandler";

	private static final String _UPGRADEHANDLERDESCRIPTION_ELEMENT = "description";

	private static final String _UPGRADEHANDLERNAME_ELEMENT = "name";

}
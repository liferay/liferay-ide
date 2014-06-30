/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.project.core;

import com.liferay.ide.core.ExtensionReader;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;


/**
 * @author Simon Jiang
 */
public class UpgradeProjectHandlerReader extends ExtensionReader<AbstractUpgradeProjectHandler>
{
    private static final String EXTENSION = "upgradeProjectHandlers";
    private static final String UPGRADEACTION_ELEMENT = "upgradeProjectHandler";
    private static final String UPGRADEHANDLERNAME_ELEMENT = "name";
    private static final String UPGRADEHANDLERDESCRIPTION_ELEMENT = "description";

    public UpgradeProjectHandlerReader()
    {
        super( ProjectCore.PLUGIN_ID, EXTENSION, UPGRADEACTION_ELEMENT );
    }

    @Override
    protected AbstractUpgradeProjectHandler initElement( IConfigurationElement configElement, AbstractUpgradeProjectHandler upgradeHandler )
    {
        upgradeHandler.setName(  configElement.getAttribute( UPGRADEHANDLERNAME_ELEMENT ) );
        upgradeHandler.setDescription( configElement.getAttribute( UPGRADEHANDLERDESCRIPTION_ELEMENT ) );

        return upgradeHandler;
    }

    public List<AbstractUpgradeProjectHandler> getUpgradeActions()
    {
        return getExtensions();
    }
}

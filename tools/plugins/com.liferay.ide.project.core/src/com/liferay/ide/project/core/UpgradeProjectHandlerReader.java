/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
public class UpgradeProjectHandlerReader extends ExtensionReader<UpgradeProjectHandler>
{
    private static final String EXTENSION = "upgradeProjectHandlers";
    private static final String UPGRADEACTION_ELEMENT = "upgradeProjectHandler";
    private static final String UPGRADEHANDLERNAME_ELEMENT = "name";
    private static final String UPGRADEHANDLERDESCRIPTION_ELEMENT = "description";

    public UpgradeProjectHandlerReader()
    {
        super( LiferayProjectCore.PLUGIN_ID, EXTENSION, UPGRADEACTION_ELEMENT );
    }

    @Override
    protected UpgradeProjectHandler initElement( IConfigurationElement configElement, UpgradeProjectHandler upgradeHandler )
    {
        upgradeHandler.setName(  configElement.getAttribute( UPGRADEHANDLERNAME_ELEMENT ) );
        upgradeHandler.setDescription( configElement.getAttribute( UPGRADEHANDLERDESCRIPTION_ELEMENT ) );

        return upgradeHandler;
    }

    public List<UpgradeProjectHandler> getUpgradeActions()
    {
        return getExtensions();
    }
}

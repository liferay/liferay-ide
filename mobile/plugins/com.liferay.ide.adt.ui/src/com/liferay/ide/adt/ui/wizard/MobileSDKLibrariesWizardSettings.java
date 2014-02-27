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
package com.liferay.ide.adt.ui.wizard;

import com.liferay.ide.adt.core.model.ServerInstance;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.modeling.annotations.Type;


/**
 * @author Kuo Zhang
 */
public interface MobileSDKLibrariesWizardSettings extends Element
{
    ElementType TYPE = new ElementType( MobileSDKLibrariesWizardSettings.class );

    // *** PreviousServerInstances ***

    @Type( base = ServerInstance.class )
    ListProperty PROP_PREVIOUS_SERVER_INSTANCES = new ListProperty( TYPE, "PreviousServerInstances" );

    ElementList<ServerInstance> getPreviousServerInstances();
}

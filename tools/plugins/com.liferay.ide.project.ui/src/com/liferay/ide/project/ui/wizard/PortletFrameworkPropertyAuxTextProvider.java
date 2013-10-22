/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.LiferayProjectCore;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.PropertyDef;


/**
 * @author Gregory Amerson
 */
public class PortletFrameworkPropertyAuxTextProvider extends PossibleValuesAuxTextProvider
{

    @Override
    public String getAuxText( Element modelElement, PropertyDef property, String possibleValue )
    {
        final IPortletFramework framework = LiferayProjectCore.getPortletFramework( possibleValue );
        return framework != null ? getAuxText( framework ) : null;
    }

    private String getAuxText( IPortletFramework framework )
    {
        return framework.getDescription();
    }

}

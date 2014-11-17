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
 * Contributors:
 *      Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
public interface CustomPortletMode extends Element, Describeable, Identifiable
{

    ElementType TYPE = new ElementType( CustomPortletMode.class );

    // *** PortletMode ***

    @Required
    @Unique
    @Label( standard = "Portlet Mode" )
    @XmlBinding( path = "portlet-mode" )
    ValueProperty PROP_PORTLET_MODE = new ValueProperty( TYPE, "PortletMode" ); //$NON-NLS-1$

    Value<String> getPortletMode();

    void setPortletMode( String value );

    // void setPortletMode( IPortletMode value );

    /*
     * Portlet Managed
     */

    @Type( base = Boolean.class )
    @Label( standard = "Portal managed" )
    @DefaultValue( text = "true" )
    @XmlBinding( path = "portal-managed" )
    ValueProperty PROP_PORTAL_MANAGED = new ValueProperty( TYPE, "PortalManaged" ); //$NON-NLS-1$


    Value<Boolean> getPortalManaged();

    void setPortalManaged( String value );

    void setPortalManaged( Boolean value );
}

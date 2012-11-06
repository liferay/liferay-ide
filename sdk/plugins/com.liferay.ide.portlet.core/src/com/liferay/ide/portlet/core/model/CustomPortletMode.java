/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.portlet.core.model.internal.InvertingBooleanXmlValueBinding;
import com.liferay.ide.portlet.core.model.internal.PortletModePossibleValueService;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@GenerateImpl
public interface CustomPortletMode extends IModelElement, Describeable, Identifiable
{

    ModelElementType TYPE = new ModelElementType( CustomPortletMode.class );

    // *** PortletMode ***

    @Required
    @NoDuplicates
    @Label( standard = "Portlet Mode" )
    @XmlBinding( path = "portlet-mode" )
    @Service( impl = PortletModePossibleValueService.class )
    ValueProperty PROP_PORTLET_MODE = new ValueProperty( TYPE, "PortletMode" );

    Value<String> getPortletMode();

    void setPortletMode( String value );

    // void setPortletMode( IPortletMode value );

    /*
     * Portlet Managed
     */

    @Type( base = Boolean.class )
    @Label( standard = "Portlet managed" )
    @CustomXmlValueBinding( impl = InvertingBooleanXmlValueBinding.class, params = "portal-managed" )
    ValueProperty PROP_PORTLET_MANAGED = new ValueProperty( TYPE, "PortletManaged" );

    Value<Boolean> getPortletManaged();

    void setPortletManaged( String value );

    void setPortletManaged( Boolean value );
}
